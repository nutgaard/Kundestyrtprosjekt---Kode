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
import java.util.Map;
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

    private NetworkServiceImp imp;
    private List<XOMessage> msgList;
    private SMTPSender sender;
    private Authenticator auth;
    private Session session;
    private final String username, password;

    public SMTPS(NetworkServiceImp imp, final String username, final String password) {
        this.imp = imp;
        this.msgList = Collections.synchronizedList(new ArrayList<XOMessage>());
        this.sender = new SMTPSender();
        System.out.println("SenderCOnst: " + sender);
        this.username = username;
        this.password = password;
        this.auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
        this.session = Session.getInstance(imp.getSettings(), auth);
        new Thread(this.sender).start();
    }

    void send(XOMessage msg) {
        int index = Collections.binarySearch(msgList, msg);
        System.out.println("Message Index: " + index);
        if (index < 0) {
            //Not found, so add it to queue
            msgList.add(~index, msg);
            System.out.println("SenderSend: " + sender);
            synchronized (sender) {
                sender.notifyAll();
                System.out.println("Notify");
            }
        }
    }

    private class SMTPSender implements Runnable {

        private boolean run = true;
        private TransportConnectionListener listener = new TransportConnectionListener();

        @Override
        public void run() {
            while (run) {
                XOMessage msg = null;
                System.out.println("Running");
                try {
                    while (session == null) {
//                        System.out.println("Yield");
                        Thread.yield();
                    }
                    System.out.println("Fetching socket");
                    System.out.println("IMP: " + imp);
                    System.out.println("SESSION: " + session);

                    System.out.println("Props");
                    for (Map.Entry<Object, Object> s : imp.getSettings().entrySet()) {
                        System.out.println("    " + s.getKey().toString() + ": " + s.getValue().toString());
                    }


                    SMTPTransport transport = (SMTPSSLTransport) session.getTransport("smtps");
                    System.out.println("Transport " + transport);
                    System.out.println("Adding listeners");
                    transport.removeTransportListener(listener);
                    transport.addTransportListener(listener);
                    System.out.println("Queue: " + msgList.size());
                    while (msgList.isEmpty()) {
                        synchronized (this) {
                            this.wait();
                            System.out.println("Waiting");
                        }
                    }
                    System.out.println("Sending");
                    msg = msgList.remove(0);
                    MimeMessage message = NetworkServiceImp.convertToMime(msg);
//                  transport.connect(imp.getSettings().getAttribute("mail.smtps.host"), imp.getUsername(), imp.getPassword());
                    if (!transport.isConnected()) {
                        System.out.println("Connection " + imp.getSettings().getProperty("mail.smtps.host") + " " + imp.getUserMail() + " ");
                        transport.connect(imp.getSettings().getProperty("mail.smtps.host"), imp.getUserMail(), password);
//                        transport.connect();
                        System.out.println("Connected: " + transport.isConnected());
                    }
                    transport.sendMessage(message, message.getAllRecipients());
                    if (msgList.isEmpty()) {
//                        transport.close();
                        System.out.println("Closing");
                        synchronized (this) {
                            this.wait();
                        }
                    }
                    if (!run) {
                        transport.close();
                    }

                } catch (Exception ex) {
                    for (NetworkService.Callback c : imp.getListeners()) {
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
                for (NetworkService.Callback c : imp.getListeners()) {
                    c.mailSent(NetworkServiceImp.convertToXO((MimeMessage) te.getMessage()), te.getInvalidAddresses());
                }
            }

            public void messageNotDelivered(TransportEvent te) {
                XOMessage msg = NetworkServiceImp.convertToXO((MimeMessage) te.getMessage());
                //Retry sending
                send(msg);
                for (NetworkService.Callback c : imp.getListeners()) {
                    c.mailSentError(msg);
                }
            }

            public void messagePartiallyDelivered(TransportEvent te) {
                //SMTP cannot partially deliver messages
            }
        }
    }
}
