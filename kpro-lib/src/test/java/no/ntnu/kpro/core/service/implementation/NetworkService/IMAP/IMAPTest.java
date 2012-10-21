/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;

import no.ntnu.kpro.core.service.implementation.NetworkService.IMAPStrategy;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Nicklas
 */
public class IMAPTest {

    IMAP imap;
    IMAPStrategy puller, pusher;

    public IMAPTest() {
    }

    @Before
    public void setUp() {
        puller = spy(new IMAPStrategyDummy());
        pusher = spy(new IMAPStrategyDummy());
        imap = new IMAP(puller);
    }

    @After
    public void tearDown() {
        imap.halt();
        imap = null;
        puller = null;
        pusher = null;
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    @Test
    public void startNstop() {
        imap.start();
        imap.halt();
        verify(puller).run();
        verify(puller).halt();
    }

    @Test
    public void strategyRestart() throws Exception {
        imap.start();
        puller.halt();//IMAP should automaticly restart the strategy
        synchronized (this) {
            wait(100);
        }
        imap.halt();
        synchronized (this) {
            wait(100);
        }
        verify(puller, atLeast(2)).run();
        verify(puller, atLeast(2)).halt();
    }
    @Test
    public void changeStrategy() throws Exception{
        imap.start();
        synchronized (this) {
            wait(100);
        }
        verify(puller).run(); //Puller should be started
        imap.changeStrategy(pusher);
        synchronized (this) {
            wait(100);
        }
        verify(puller).halt(); //Puller should be automaticly stopped
        verify(pusher).run(); //Pusher should be started
        
    }
}
class IMAPStrategyDummy extends IMAPStrategy {
    public boolean run = true;

    @Override
    public void halt() {
        run = false;
    }

    public void run() {
        while (run){
            Thread.yield();
        }
    }
    
}
