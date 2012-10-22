/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;

import java.util.LinkedList;
import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.search.SearchTerm;
import no.ntnu.kpro.core.service.implementation.NetworkService.NetworkServiceImp;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
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
        puller = new IMAPPull(new Properties(), new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("kprothales", "kprothales2012");
            }
        }, new LinkedList<NetworkService.Callback>(), 1, store);
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
         
         synchronized (this){
             this.wait(1000);
         }
         verify(store, atLeastOnce()).getAllMessages(any(NetworkServiceImp.BoxName.class), any(SearchTerm.class));
         
         synchronized (this){
             this.wait(1000);
         }
         verify(store, atLeast(2)).getAllMessages(any(NetworkServiceImp.BoxName.class), any(SearchTerm.class));
     }
}

