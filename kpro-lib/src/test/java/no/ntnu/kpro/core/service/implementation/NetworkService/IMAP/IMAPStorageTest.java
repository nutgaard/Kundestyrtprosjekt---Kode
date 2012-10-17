/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;

import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.user.UserException;
import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;
import java.io.IOException;
import java.security.Security;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;
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

//    @Before
//    public void setUp() {
//        Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());
//        this.props = new Properties();
//        this.props.put("mail.imaps.host", LOCALHOST);
//        this.props.put("mail.imaps.auth", "true");
//        this.props.put("mail.debug", "true");
//        this.props.put("mail.imaps.port", ServerSetupTest.IMAPS.getPort());
//
//        this.server = new GreenMail(ServerSetup.IMAPS);
//        this.server.start();
//        this.user = this.server.setUser(EMAIL_USER_ADDRESS, USER_NAME, null);
//
//        this.store = new IMAPStorage(props, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(USER_NAME, USER_PASSWORD);
//            }
//        }, new LinkedList<NetworkService.Callback>());
////        this.store = new IMAPStorage(props, null , new LinkedList<NetworkService.Callback>());
//    }
//
//    @After
//    public void tearDown() {
//        store = null;
//        server.stop();
//        server = null;
//    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
//    @Test
//    public void fetchMail() {
//        try {
//            MimeMessage message = new MimeMessage((Session) null);
//            message.setFrom(new InternetAddress(EMAIL_USER_ADDRESS));
//            message.addRecipient(Message.RecipientType.TO, new InternetAddress(EMAIL_TO));
//            message.setSubject(EMAIL_SUBJECT);
//            message.setText(EMAIL_TEXT);
//
//            //Use greenmailuser to deliver message
//            this.user.deliver(message);
//
//            Message[] m = store.getAllMessages(NetworkServiceImp.BoxName.INBOX, new SearchTerm() {
//                @Override
//                public boolean match(Message msg) {
//                    return true;
//                }
//            });
//            assertNotNull(m);
//        } catch (Exception ex) {
//            Logger.getLogger(IMAPStorageTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    @Before
    public void setUp() {
        server = new GreenMail(ServerSetupTest.IMAP);
        server.start();
    }

    @After
    public void tearDown() {
        server.stop();
    }

    @Test
    public void getMails() throws IOException, MessagingException,
            UserException, InterruptedException {
        // create user on mail server
        GreenMailUser user = server.setUser(EMAIL_USER_ADDRESS, USER_NAME,
                USER_PASSWORD);

        // create an e-mail message using javax.mail ..
        MimeMessage message = new MimeMessage((Session) null);
        message.setFrom(new InternetAddress(EMAIL_TO));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                EMAIL_USER_ADDRESS));
        message.setSubject(EMAIL_SUBJECT);
        message.setText(EMAIL_TEXT);

        // use greenmail to store the message
        user.deliver(message);

        // fetch the e-mail via imap using javax.mail ..
        Properties props = new Properties();
        Session session = Session.getInstance(props);
        URLName urlName = new URLName("imap", LOCALHOST,
                ServerSetupTest.IMAP.getPort(), null, user.getLogin(),
                user.getPassword());
        Store store = session.getStore(urlName);
        store.connect();

        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        Message[] messages = folder.getMessages();
        assertNotNull(messages);
        assertEquals(1, messages.length);
        assertEquals(EMAIL_SUBJECT, messages[0].getSubject());
        assertTrue(String.valueOf(messages[0].getContent())
                .contains(EMAIL_TEXT));
        assertEquals(EMAIL_TO, messages[0].getFrom()[0].toString());
    }
}
