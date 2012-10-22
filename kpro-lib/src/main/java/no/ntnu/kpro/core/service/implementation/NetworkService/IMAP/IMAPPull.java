/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.search.ReceivedDateTerm;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAPStrategy;
import no.ntnu.kpro.core.service.implementation.NetworkService.NetworkServiceImp;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas
 */
public class IMAPPull extends IMAPStrategy {

    private IMAPStorage storage;
    private int intervalInMillies;
    private long lastPull;
    private boolean run = true;
    
    IMAPPull(final Properties props, final Authenticator auth, List<NetworkService.Callback> listeners, int intervalInSeconds, final IMAPStorage store) {
        this.storage = store;
        this.intervalInMillies = intervalInSeconds * 1000; 
    }

    public IMAPPull(final Properties props, final Authenticator auth, List<NetworkService.Callback> listeners, int intervalInSeconds) {
        this(props, auth, listeners, intervalInSeconds, new IMAPStorage(props, auth, listeners));
    }

    public void run() {
        while (run) {
            long diff = 0;
            while ((diff = System.currentTimeMillis() - lastPull) < intervalInMillies) {
                try {
                    synchronized (this) {
                        System.out.println("Waiting for: "+(intervalInMillies-diff));
                        wait(intervalInMillies-diff);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(IMAPPull.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("Pulling");
            storage.getAllMessages(NetworkServiceImp.BoxName.INBOX, new ReceivedDateTerm(1, new Date(lastPull)));
            lastPull = System.currentTimeMillis();
        }
    }

    public void halt() {
        this.run = false;
    }
}
