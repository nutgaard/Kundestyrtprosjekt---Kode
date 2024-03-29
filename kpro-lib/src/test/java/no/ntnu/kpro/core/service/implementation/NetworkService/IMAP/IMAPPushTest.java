/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;

import com.sun.mail.smtp.SMTPTransport;
import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessageType;
import no.ntnu.kpro.core.service.implementation.NetworkService.SMTP.SMTPSender;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.service.interfaces.PersistenceService;
import no.ntnu.kpro.core.utilities.Converter;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Nicklas
 */
public class IMAPPushTest {

    private static final String USER_PASSWORD = "kprothales2012";
    private static final String USER_NAME = "kprothales";
    private static final String EMAIL_USER_ADDRESS = "kprothales@gmail.com";
    private static final String EMAIL_TO = "kprothales@gmail.com";
    private static final String EMAIL_SUBJECT = "Test E-Mail";
    private static final String EMAIL_TEXT = "This is a test e-mail";
    private static final String LOCALHOST = "127.0.0.1";
    private Thread pusherThread;
    private IMAPPush pusher;
    private SMTPSender sender;
    private Properties props;
    private PersistenceService persistence;

    public IMAPPushTest() {
    }

    @Before
    public void setUp() {
        props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", "smtp.gmail.com");
        props.put("mail.smtps.socketFactory.port", "465");
        props.put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.port", "465");
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.socketFactory.port", "993");
        props.put("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.imaps.auth", "true");
        props.put("mail.imaps.port", "993");
        when(persistence.createOutputFile(anyString())).thenReturn(new File("/."));
        Converter.setup(persistence);
        sender = new SMTPSender(USER_NAME, USER_PASSWORD, EMAIL_USER_ADDRESS, props, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER_NAME, USER_PASSWORD);
            }
        }, null);
        pusher = new IMAPPush(props, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER_NAME, USER_PASSWORD);
            }
        }, new Date(0), new LinkedList<NetworkService.Callback>(), new IMAPCache(props, USER_NAME, USER_PASSWORD));
        pusherThread = new Thread(pusher);
        pusherThread.start();
    }

    @After
    public void tearDown() {
        pusher.halt();
        pusher = null;
        pusherThread = null;

    }

//    @Test
    public void mailReceived() throws Exception {
        final List<IXOMessage> m = new LinkedList<IXOMessage>();
        pusher.addCallback(new NetworkService.Callback() {

            public void mailSent(IXOMessage message, Address[] invalidAddress) {
            }

            public void mailSentError(IXOMessage message, Exception ex) {
            }

            public void mailReceived(IXOMessage message) {
                m.add(message);
            }

            public void mailReceivedError(Exception ex) {
            }
        });
        
        //Sending mail
        XOMessage s = new XOMessage(EMAIL_USER_ADDRESS, EMAIL_TO, EMAIL_SUBJECT, EMAIL_TEXT, XOMessageSecurityLabel.UGRADERT, XOMessagePriority.DEFERRED, XOMessageType.DRILL, new Date());
        Session session = Session.getInstance(this.props, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER_NAME, USER_PASSWORD);
            }
        });
        MimeMessage message = Converter.getInstance().convertToMime(session, s);

        SMTPTransport t = (SMTPTransport) session.getTransport("smtps");
        t.connect(this.props.getProperty("mail.smtps.host"), EMAIL_USER_ADDRESS, USER_PASSWORD);
        t.sendMessage(message, message.getAllRecipients());
        t.close();

        //Verify
        synchronized (this) {
            wait(10000);
        }

        assertEquals(1, m.size());
        
        
    }
}
