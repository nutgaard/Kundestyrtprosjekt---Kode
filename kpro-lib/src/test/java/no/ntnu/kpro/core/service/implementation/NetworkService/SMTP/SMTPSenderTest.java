/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.SMTP;

import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import java.io.File;
import java.security.Security;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessageType;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.service.interfaces.PersistenceService;
import no.ntnu.kpro.core.utilities.Converter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Nicklas
 */
public class SMTPSenderTest {

    SMTPSender sender;
    GreenMail server;
    PersistenceService persistence;
    private static final String USER_PASSWORD = "kprothales2012";
    private static final String USER_NAME = "kprothales";
    private static final String EMAIL_USER_ADDRESS = "kprothales@gmail.com";
    private static final String EMAIL_TO = "kprothales@gmail.com";
    private static final String EMAIL_SUBJECT = "Test E-Mail";
    private static final String EMAIL_TEXT = "This is a test e-mail";
    private static final String LOCALHOST = "127.0.0.1";

    @Before
    public void setup() {
        Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());
        Properties props = new Properties();
        props.put("mail.smtps.host", LOCALHOST);
        props.put("mail.smtps.auth", "true");
        props.put("mail.debug", "false");
        props.put("mail.smtps.port", ServerSetupTest.SMTPS.getPort());
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER_NAME, USER_PASSWORD);
            }
        };
        
        persistence = mock(PersistenceService.class);
        when(persistence.createOutputFile(anyString())).thenReturn(new File("/."));
        Converter.setup(persistence);
        sender = new SMTPSender(USER_NAME, USER_PASSWORD, EMAIL_USER_ADDRESS, props, auth, new LinkedList<NetworkService.Callback>());

        this.server = new GreenMail(ServerSetupTest.SMTPS);
        this.server.start();
        this.server.setUser(EMAIL_USER_ADDRESS, USER_NAME, USER_PASSWORD);
    }

    @After
    public void teardown() {
        sender = null;
        server.stop();
        server = null;
    }

    @Test
    public void sendMailTest() {
        IXOMessage m = new XOMessage(EMAIL_USER_ADDRESS, EMAIL_TO, EMAIL_SUBJECT, EMAIL_TEXT, XOMessageSecurityLabel.UGRADERT, XOMessagePriority.DEFERRED, XOMessageType.DRILL, new Date());
        sender.addListener(new NetworkService.Callback() {

            public void mailSent(IXOMessage message, Address[] invalidAddress) {
                
            }

            public void mailSentError(IXOMessage message, Exception ex) {
                
            }

            public void mailReceived(IXOMessage message) {
                
            }

            public void mailReceivedError(Exception ex) {
                
            }
        });
        assertTrue(sender.sendMail(m));
        assertEquals(1, server.getReceivedMessages().length);
    }

    @Test
    public void callbackTest() throws InterruptedException {
        XOMessage m = new XOMessage(EMAIL_USER_ADDRESS, EMAIL_TO, EMAIL_SUBJECT, EMAIL_TEXT, XOMessageSecurityLabel.UGRADERT, XOMessagePriority.DEFERRED, XOMessageType.DRILL, new Date());
        final List<IXOMessage> ml = new LinkedList<IXOMessage>();
        sender.addListener(new NetworkService.Callback() {
            public void mailSent(IXOMessage message, Address[] invalidAddress) {
                ml.add(message);
            }

            public void mailSentError(IXOMessage message, Exception ex) {
                ml.add(message);
            }

            public void mailReceived(IXOMessage message) {
//                ml.add(message);
            }

            public void mailReceivedError(Exception ex) {
                ml.add(null);
            }
        });
        sender.sendMail(m);
        //Need to wait inorder for list to be updated
        synchronized (this) {
            wait(100);
        }
        assertEquals(1, ml.size());
        IXOMessage r = ml.get(0);
        assertEquals(m.getFrom(), r.getFrom());
        assertEquals(m.getTo(), r.getTo());
        assertEquals(m.getHtmlBody(), r.getHtmlBody());
        assertEquals(m.getStrippedBody(), r.getStrippedBody());
        assertEquals(m.getSubject(), r.getSubject());
        assertEquals(m.getGrading(), r.getGrading());
    }
}
