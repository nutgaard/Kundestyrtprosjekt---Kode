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

    public SMTPS(NetworkServiceImp imp) {
        this.imp = imp;
        this.msgList = Collections.synchronizedList(new ArrayList<XOMessage>());
    }

    void send(XOMessage msg) {
        int index = Collections.binarySearch(msgList, msg);
        if (index < 0) {
            //Not found, so add it to queue
            msgList.add(~index, msg);
            sender.notifyAll();
        }
    }

    private class SMTPSender implements Runnable {

        private boolean run = true;
        private TransportConnectionListener listener = new TransportConnectionListener();

        @Override
        public void run() {
            while (run) {
                XOMessage msg = null;
                try {
                    SMTPTransport transport = (SMTPSSLTransport) imp.getSession().getTransport("smtps");
                    transport.addTransportListener(listener);
                    while (msgList.isEmpty()) {
                        this.wait();
                    }
                    msg = msgList.remove(0);
                    MimeMessage message = NetworkServiceImp.convertToMime(msg);
//                  transport.connect(imp.getSettings().getAttribute("mail.smtps.host"), imp.getUsername(), imp.getPassword());
                    if (!transport.isConnected()) {
                        transport.connect();
                    }
                    transport.sendMessage(message, message.getAllRecipients());
                    if (msgList.isEmpty()) {
                        transport.close();
                    }

                } catch (Exception ex) {
                    for (NetworkService.Callback c : imp.getListeners()) {
                        if (msg == null) {
                            break;
                        }
                        c.mailSentError(msg);
                    }
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
