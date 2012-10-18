/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;

import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.user.UserException;
import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import java.io.IOException;
import java.security.Security;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.implementation.NetworkService.NetworkServiceImp;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Nicklas
 */
public class IMAPStorageTest {

    private static final String USER_PASSWORD = "kprothales2012";
    private static final String USER_NAME = "kprothales";
    private static final String EMAIL_USER_ADDRESS = "kprothales@gmail.com";
    private static final String EMAIL_TO = "kprothales@gmail.com";
    private static final String EMAIL_SUBJECT = "Test E-Mail";
    private static final String EMAIL_TEXT = "This is a test e-mail.";
    private static final String LOCALHOST = "127.0.0.1";
    private GreenMail server;
    private IMAPStorage store;
    private Properties props;
    private GreenMailUser user;

    public IMAPStorageTest() {
    }

    @Before
    public void setUp() {
        server = new GreenMail(ServerSetupTest.IMAPS);
        Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", LOCALHOST);
        props.put("mail.imaps.port", ServerSetupTest.IMAPS.getPort());
        props.put("mail.imaps.auth", "true");

        server.start();
        user = server.setUser(EMAIL_USER_ADDRESS, USER_NAME, USER_PASSWORD);
        store = new IMAPStorage(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER_NAME, USER_PASSWORD);
            }
        }, new LinkedList<NetworkService.Callback>());
    }

    @After
    public void tearDown() {
        server.stop();
        store = null;
    }

    @Test
    public void getMails() throws Exception {
        MimeMessage message = new MimeMessage((Session) null);
        message.setFrom(new InternetAddress(EMAIL_USER_ADDRESS));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(EMAIL_TO));
        message.setSubject(EMAIL_SUBJECT);
        message.setText(EMAIL_TEXT);

        user.deliver(message);
        Message[] m = store.getAllMessages(NetworkServiceImp.BoxName.INBOX, new SearchTerm() {
            @Override
            public boolean match(Message msg) {
                return true;
            }
        });
        System.out.println("Got message: " + m);
        assertNotNull(m);
    }

    @Test
    public void callbackTest() throws Exception {
        final List<XOMessage> m = new LinkedList<XOMessage>();
        store.addCallback(new NetworkService.Callback() {
            public void mailSent(XOMessage message, Address[] invalidAddress) {
            }

            public void mailSentError(XOMessage message, Exception ex) {
            }

            public void mailReceived(XOMessage message) {
                m.add(message);
            }

            public void mailReceivedError(Exception ex) {
            }
        });
        //Sending a mail
        MimeMessage message = new MimeMessage((Session) null);
        message.setFrom(new InternetAddress(EMAIL_USER_ADDRESS));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(EMAIL_TO));
        message.setSubject(EMAIL_SUBJECT);
        message.setText(EMAIL_TEXT);
        user.deliver(message);

        //Fetch
        store.getAllMessages(NetworkServiceImp.BoxName.INBOX, new SearchTerm() {
            @Override
            public boolean match(Message msg) {
                return true;
            }
        });


        //Verify
        synchronized (this) {
            wait(100);
        }
        assertEquals(1, m.size());
    }
//    @Test
    public void errorCallbackTest() throws Exception {
        final List<Exception> m = new LinkedList<Exception>();
        store.addCallback(new NetworkService.Callback() {
            public void mailSent(XOMessage message, Address[] invalidAddress) {
            }

            public void mailSentError(XOMessage message, Exception ex) {
            }

            public void mailReceived(XOMessage message) {
                
            }

            public void mailReceivedError(Exception ex) {
                m.add(ex);
            }
        });
        //Sending a mail
        MimeMessage message = new MimeMessage((Session) null);
        message.setFrom(new InternetAddress(EMAIL_USER_ADDRESS));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(EMAIL_TO));
        message.setSubject(EMAIL_SUBJECT);
        message.setText(EMAIL_TEXT);
        user.deliver(message);

        //Fetch
        store.getAllMessages(NetworkServiceImp.BoxName.INBOX, new SearchTerm() {
            @Override
            public boolean match(Message msg) {
                return true;
            }
        });
        //Verify
        synchronized (this) {
            wait(100);
        }
        assertEquals(1, m.size());
    }
}
