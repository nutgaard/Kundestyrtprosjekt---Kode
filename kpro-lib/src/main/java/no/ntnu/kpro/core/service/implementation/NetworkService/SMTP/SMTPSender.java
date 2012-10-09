package no.ntnu.kpro.core.service.implementation.NetworkService.SMTP;

import com.sun.mail.smtp.SMTPTransport;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import no.ntnu.kpro.core.model.XOMessage;


public class SMTPSender {

    public static String LABEL = "XOMailLabel";
    public static String PRIORITY = "XOMailPriority";
    public static String TYPE = "XOMailType";
    private final String password;
    private final String mailAdr;
    private Properties props;
    private Authenticator auth;

    public SMTPSender(final String username, final String password, final String mailAdr, final Properties props, final Authenticator auth) {
        this.props = new Properties();
        this.password = password;
        this.mailAdr = mailAdr;
        this.props = props;
        this.auth = auth;
    }

    public boolean sendMail(XOMessage msg) {
        try {
            Session session = Session.getInstance(this.props, this.auth);
            MimeMessage message = XOMessage.convertToMime(session, msg);

            SMTPTransport t = (SMTPTransport) session.getTransport("smtps");
            t.connect(this.props.getProperty("mail.smtps.host"), this.mailAdr, this.password);
            t.sendMessage(message, message.getAllRecipients());

            t.close();
            return true;
        } catch (Exception ex) {
            Logger.getLogger(SMTPSender.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}