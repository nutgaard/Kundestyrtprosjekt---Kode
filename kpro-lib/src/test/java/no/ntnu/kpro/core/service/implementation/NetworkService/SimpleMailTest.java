package no.ntnu.kpro.core.service.implementation.NetworkService;

import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import java.security.Security;
import java.util.Properties;
import javax.mail.internet.MimeMessage;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({SimpleMailTest.Mock.class, SimpleMailTest.Real.class})
public class SimpleMailTest {

    private static final String USER_PASSWORD = "kprothales2012";
    private static final String USER_NAME = "kprothales";
    private static final String EMAIL_USER_ADDRESS = "kprothales@gmail.com";
    private static final String EMAIL_TO = "kprothales@gmail.com";
    private static final String EMAIL_SUBJECT = "Test E-Mail";
    private static final String EMAIL_TEXT = "This is a test e-mail.";
    private static final String LOCALHOST = "127.0.0.1";;

    public static class Mock {

        private GreenMail mailServer;
        private SimpleMail mailClient;
        private Properties props;

        @Before
        public void setup() {
            Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());
            this.props = new Properties();
            this.props.put("mail.smtps.host", LOCALHOST);
            this.props.put("mail.smtps.auth", "true");
            this.props.put("mail.smtps.port", ServerSetupTest.SMTPS.getPort());

            this.mailServer = new GreenMail(ServerSetupTest.SMTPS);
            this.mailServer.start();
            this.mailServer.setUser(EMAIL_USER_ADDRESS, USER_NAME, USER_PASSWORD);
            this.mailClient = new SimpleMail(USER_NAME, USER_PASSWORD, EMAIL_USER_ADDRESS);
            this.mailClient.setProps(props);
            this.mailClient.setAuth(null);
//            System.out.println("Setup complete");
        }

        @After
        public void tearDown() {
            this.mailServer.stop();
//            System.out.println("Teardown complete");
        }

        @Test
        public void sendMail_Mock() throws Exception {
//            System.out.println("Test: getMails");
            boolean b = mailClient.sendMail(EMAIL_TO, EMAIL_SUBJECT, EMAIL_TEXT);
//            System.out.println("B: " + b);
            assertTrue(b);

            MimeMessage[] messages = mailServer.getReceivedMessages();
            assertNotNull(messages);
            assertEquals(1, messages.length);
            MimeMessage message = messages[0];
            assertEquals(EMAIL_SUBJECT, message.getSubject());
            assertTrue(String.valueOf(message.getContent()).contains(EMAIL_TEXT));
            assertEquals(EMAIL_USER_ADDRESS, message.getFrom()[0].toString());
//            System.out.println("Test: getMails Complete");
        }
    }
    public static class Real {
        private SimpleMail mailClient;
        
        @Before
        public void setup() {
            this.mailClient = new SimpleMail(USER_NAME, USER_PASSWORD, EMAIL_USER_ADDRESS);
//            System.out.println("Setup complete");
        }

        @After
        public void tearDown() {
//            System.out.println("Teardown complete");
        }
        @Test
        public void sendMail_Real() {
            boolean b = mailClient.sendMail(EMAIL_TO, EMAIL_SUBJECT, EMAIL_TEXT);
            assertTrue(b);
        }
    }
}