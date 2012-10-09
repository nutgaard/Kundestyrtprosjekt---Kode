package no.ntnu.kpro.core.service.implementation.NetworkService.SMTP;

import com.sun.mail.smtp.SMTPTransport;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.MimeMessage;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

public class SMTPSender {

    public static String LABEL = "XOMailLabel";
    public static String PRIORITY = "XOMailPriority";
    public static String TYPE = "XOMailType";
    private final String password;
    private final String mailAdr;
    private Properties props;
    private Authenticator auth;
    private List<NetworkService.Callback> listeners;

    public SMTPSender(final String username, final String password, final String mailAdr, final Properties props, final Authenticator auth) {
        this.props = new Properties();
        this.password = password;
        this.mailAdr = mailAdr;
        this.props = props;
        this.auth = auth;
        this.listeners = Collections.synchronizedList(new LinkedList<NetworkService.Callback>());
    }
    public void addCallback(NetworkService.Callback l) {
        if (!listeners.contains(l)){
            this.listeners.add(l);
        }
    }
    public void removeCallback(NetworkService.Callback l) {
        if (listeners.contains(l)){
            this.listeners.remove(l);
        }
    }
    public boolean sendMail(XOMessage msg) {
        try {
            Session session = Session.getInstance(this.props, this.auth);
            MimeMessage message = XOMessage.convertToMime(session, msg);

            SMTPTransport t = (SMTPTransport) session.getTransport("smtps");
            t.addTransportListener(new LocalTransportListener());
            t.connect(this.props.getProperty("mail.smtps.host"), this.mailAdr, this.password);
            t.sendMessage(message, message.getAllRecipients());

            t.close();
            return true;
        } catch (Exception ex) {
            Logger.getLogger(SMTPSender.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    class LocalTransportListener implements TransportListener {

        public void messageDelivered(TransportEvent te) {
            //Callback
            for (NetworkService.Callback c : listeners) {
                c.mailSent(XOMessage.convertToXO((MimeMessage) te.getMessage()), te.getInvalidAddresses());
            }
        }

        public void messageNotDelivered(TransportEvent te) {
            XOMessage msg = XOMessage.convertToXO((MimeMessage) te.getMessage());
            //Retry sending
            sendMail(msg);
            for (NetworkService.Callback c : listeners) {
                c.mailSentError(msg);
            }
        }

        public void messagePartiallyDelivered(TransportEvent te) {
            //SMTP cannot partially deliver messages
        }
    }
}