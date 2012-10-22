package no.ntnu.kpro.core.service.implementation.NetworkService.SMTP;

import com.sun.mail.smtp.SMTPTransport;
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
import no.ntnu.kpro.core.service.interfaces.NetworkService.Callback;
import no.ntnu.kpro.core.utilities.Converter;

public class SMTPSender {

    private final String password;
    private final String mailAdr;
    private Properties props;
    private Authenticator auth;
    private List<NetworkService.Callback> listener;

    public SMTPSender(final String username, final String password, final String mailAdr, final Properties props, final Authenticator auth, List<NetworkService.Callback> listener) {
        this.password = password;
        this.mailAdr = mailAdr;
        this.props = props;
        this.auth = auth;
        this.listener = listener;
    }

    public boolean sendMail(XOMessage msg) {
        try {
            Session session = Session.getInstance(this.props, this.auth);
            MimeMessage message = Converter.convertToMime(session, msg);

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

    public void addListener(Callback callback) {
        this.listener.add(callback);
    }
    public void removeListener(Callback callback) {
        this.listener.remove(callback);
    }

    class LocalTransportListener implements TransportListener {

        public void messageDelivered(TransportEvent te) {
            try {
                //Callback
                for (Callback cb : listener) {
                    cb.mailSent(Converter.convertToXO(te.getMessage()), te.getInvalidAddresses());
                }
            } catch (Exception ex) {
                Logger.getLogger(SMTPSender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void messageNotDelivered(TransportEvent te) {
            XOMessage msg;
            try {
                msg = Converter.convertToXO(te.getMessage());
                sendMail(msg);
                //Retry sending
                for (Callback cb : listener) {
                    cb.mailSentError(msg, new Exception("Could not be sent"));
                }
            } catch (Exception ex) {
                Logger.getLogger(SMTPSender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void messagePartiallyDelivered(TransportEvent te) {
            //SMTP cannot partially deliver messages
        }
    }
}