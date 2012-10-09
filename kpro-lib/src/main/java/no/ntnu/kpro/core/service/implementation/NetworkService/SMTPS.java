/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import com.sun.mail.smtp.SMTPSSLTransport;
import com.sun.mail.smtp.SMTPTransport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.MimeMessage;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas
 */
public class SMTPS {

    private List<XOMessage> msgList;
    private final SMTPSender sender;
    private final List<NetworkService.Callback> listeners;

    public SMTPS(final String address, final String username, final String password, final Properties properties, final List<NetworkService.Callback> callback) {
        this.msgList = Collections.synchronizedList(new ArrayList<XOMessage>());
        this.sender = new SMTPSender(properties, address, username, password);
        this.listeners = callback;
    }

    void send(XOMessage msg) {
        if (!sender.isRunning()) {
            sender.run = true;
            new Thread(this.sender).start();
        }
        int index = Collections.binarySearch(msgList, msg);
        if (index < 0) {
            msgList.add(~index, msg);
            synchronized (sender) {
                sender.notifyAll();
            }
        }
    }

    private class SMTPSender implements Runnable {

        private final Properties props;
        private final Authenticator auth;
        private boolean run = false;
        private TransportConnectionListener listener = new TransportConnectionListener();
        private final String address, username, password;

        public SMTPSender(Properties props, final String address, final String username, final String password) {
            this.props = props;
            this.address = address;
            this.username = username;
            this.password = password;
            this.auth = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            };
        }

        @Override
        public void run() {
            while (run) {
                XOMessage msg = null;
                try {
                    Session session = Session.getInstance(props, auth);
                    while (session == null) {
                        Thread.yield();
                    }

                    SMTPTransport transport = (SMTPSSLTransport) session.getTransport("smtps");

                    transport.connect(props.getProperty("mail.smtps.host"), address, password);
                    System.out.println("CONNNNNNNNNNNNNECTED");

                    System.out.println("Transport: " + transport);
                    transport.removeTransportListener(listener);
                    transport.addTransportListener(listener);
//                    System.out.println("Queue: " + msgList.size());
                    while (msgList.isEmpty()) {
                        synchronized (this) {
                            this.wait();
//                            System.out.println("Waiting");
                        }
                    }
//                    System.out.println("Sending");
                    msg = msgList.remove(0);

                    MimeMessage message = NetworkServiceImp.convertToMime(msg);
//                  transport.connect(imp.getSettings().getAttribute("mail.smtps.host"), imp.getUsername(), imp.getPassword());
//                    if (!transport.isConnected()) {
//                        System.out.println("Connection " + imp.getSettings().getProperty("mail.smtps.host") + " " + imp.getUserMail() + " ");
                    transport.connect(props.getProperty("mail.smtps.host"), address, password);
//                        transport.connect();
//                        System.out.println("Connected: " + transport.isConnected());
//                    }
                    transport.sendMessage(message, message.getAllRecipients());
                    if (msgList.isEmpty()) {
//                        transport.close();
//                        System.out.println("Closing");
                        synchronized (this) {
                            this.wait();
                        }
                    }
                    if (!run) {
                        transport.close();
                    }

                } catch (Exception ex) {
                    for (NetworkService.Callback c : listeners) {
                        if (msg == null) {
                            break;
                        }
                        c.mailSentError(msg);
                    }
                    throw new RuntimeException(ex);
                }

            }
        }

        public void stop() {
            this.run = false;
        }

        public boolean isRunning() {
            return this.run;
        }

        private class TransportConnectionListener implements TransportListener {

            public void messageDelivered(TransportEvent te) {
                //Callback
                for (NetworkService.Callback c : listeners) {
                    c.mailSent(NetworkServiceImp.convertToXO((MimeMessage) te.getMessage()), te.getInvalidAddresses());
                }
            }

            public void messageNotDelivered(TransportEvent te) {
                XOMessage msg = NetworkServiceImp.convertToXO((MimeMessage) te.getMessage());
                //Retry sending
                send(msg);
                for (NetworkService.Callback c : listeners) {
                    c.mailSentError(msg);
                }
            }

            public void messagePartiallyDelivered(TransportEvent te) {
                //SMTP cannot partially deliver messages
            }
        }
    }
}
