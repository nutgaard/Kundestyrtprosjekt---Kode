/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import java.security.Security;
import java.util.LinkedList;
import java.util.Properties;
import javax.mail.internet.MimeMessage;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import static org.junit.Assert.*;
/**
 *
 * @author Nicklas
 */
//@RunWith(Suite.class)
//@Suite.SuiteClasses({SMTPSenderTest.SMTPSenderConnection.class})
public class SMTPSenderTest {

    private static final String USER_PASSWORD = "kprothales2012";
    private static final String USER_NAME = "kprothales";
    private static final String EMAIL_USER_ADDRESS = "kprothales@gmail.com";
    private static final String EMAIL_TO = "kprothales@gmail.com";
    private static final String EMAIL_SUBJECT = "Test E-Mail";
    private static final String EMAIL_TEXT = "This is a test e-mail.";
    private static final String LOCALHOST = "127.0.0.1";

    public static class SMTPSenderConnection {
        private GreenMail mailServer;
        private SMTPS.SMTPSender mailClient;
        private Properties props;

        @Before
        public void setUp() {
            Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());
            this.props = new Properties();
            this.props.put("mail.smtps.host", LOCALHOST);
            this.props.put("mail.smtps.auth", "true");
            this.props.put("mail.debug", "false");
            this.props.put("mail.smtps.port", ServerSetupTest.SMTPS.getPort());

            this.mailServer = new GreenMail(ServerSetupTest.SMTPS);
            this.mailServer.start();
            this.mailServer.setUser(EMAIL_USER_ADDRESS, USER_NAME, USER_PASSWORD);
            SMTPS s = new SMTPS(EMAIL_USER_ADDRESS, USER_NAME, USER_PASSWORD, props, new LinkedList<NetworkServiceImp.Callback>());
            this.mailClient = s.getSender();
        }

        @After
        public void tearDown() {
            this.mailClient.stop();
            this.mailServer.stop();
        }
        // TODO add test methods here.
        // The methods must be annotated with annotation @Test. For example:
        //
        // @Test
        // public void hello() {}
        @Test
        public void test() {
            mailClient.msgList.add(new XOMessage(EMAIL_USER_ADDRESS, EMAIL_TO, EMAIL_SUBJECT, EMAIL_TEXT, XOMessageSecurityLabel.UGRADERT));
            mailClient.pullAndSend();
            MimeMessage[] r = mailServer.getReceivedMessages();
            assertNotNull(r);
            assertEquals(1, r.length);
        }
    }
}
