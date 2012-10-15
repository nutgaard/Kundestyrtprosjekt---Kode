package no.ntnu.kpro.core.service.implementation.NetworkService.SMTP;

import java.util.Date;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessageType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;


public class SMTPTest {
    private static final String EMAIL_USER_ADDRESS = "kprothales@gmail.com";
    private static final String EMAIL_TO = "kprothales@gmail.com";
    private static final String EMAIL_SUBJECT = "Test E-Mail";
    private static final String EMAIL_TEXT = "This is a test e-mail";
    private SMTPSender mock;
    private SMTP sender;

    @Before
    public void setup() {
        mock = mock(SMTPSender.class);
        when(mock.sendMail(any(XOMessage.class))).thenReturn(Boolean.TRUE);
        sender = new SMTP(mock);
    }
    @After
    public void teardown() {
        mock = null;
        sender = null;
    }
    @Test
    public void test() throws InterruptedException {
        int n = 10;
        XOMessage[] ml = new XOMessage[n];
        for (int i = 0;i < n; i++){
            ml[i] = new XOMessage(EMAIL_USER_ADDRESS, EMAIL_TO, EMAIL_SUBJECT, EMAIL_TEXT+i, XOMessageSecurityLabel.UGRADERT, XOMessagePriority.DEFERRED, XOMessageType.DRILL, new Date());
        }
        for (XOMessage m : ml){
            sender.send(m);
        }
        synchronized (this){
            wait(200);
        }
        verify(mock, times(n)).sendMail(any(XOMessage.class));
    }
}