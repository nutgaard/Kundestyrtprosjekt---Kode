/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;



import com.sun.mail.imap.IMAPMessage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.search.SearchTerm;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.implementation.NetworkService.NetworkServiceImp;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.utilities.Pair;
import org.junit.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Nicklas
 */
public class IMAPPullTest {

    IMAPStorage store;
    IMAPPull puller;
    Thread pullerThread;

    @Before
    public void setUp() {
        store = mock(IMAPStorage.class);
        when(store.getAllMessages(any(NetworkServiceImp.BoxName.class), any(SearchTerm.class)))
                .thenReturn(null);
        Properties p = new Properties();
        Authenticator a = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("kprothales", "kprothales2012");
            }
        };
        LinkedList<NetworkService.Callback> l = new LinkedList<NetworkService.Callback>();
        Map<String, Pair<IMAPMessage, XOMessage>> c = new HashMap<String, Pair<IMAPMessage, XOMessage>>();
        puller = new IMAPPull(p, a, 1, store, c);
        
        pullerThread = new Thread(puller);
    }

    @After
    public void tearDown() {
        store = null;
        puller = null;
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    @Test
    public void recurringpull() throws Exception {
        pullerThread.start();

        synchronized (this) {
            this.wait(1000);
        }
        verify(store, atLeastOnce()).getAllMessages(any(NetworkServiceImp.BoxName.class), any(SearchTerm.class));

        synchronized (this) {
            this.wait(1000);
        }
        verify(store, atLeast(2)).getAllMessages(any(NetworkServiceImp.BoxName.class), any(SearchTerm.class));
    }
}
