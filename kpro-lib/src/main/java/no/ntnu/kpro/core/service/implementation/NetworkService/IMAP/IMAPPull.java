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

    public IMAPPull(final Properties props, final Authenticator auth, List<NetworkService.Callback> listeners, int intervalInSeconds) {
        this.storage = new IMAPStorage(props, auth, listeners);
        this.intervalInMillies = intervalInSeconds * 1000;
    }

    public void run() {
        while (run) {
            long diff = System.currentTimeMillis() - lastPull;
            while (diff < intervalInMillies) {
                try {
                    synchronized (this) {
                        wait(diff);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(IMAPPull.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            storage.getAllMessages(NetworkServiceImp.BoxName.INBOX, new ReceivedDateTerm(1, new Date(lastPull)));
        }
    }

    public void halt() {
        this.run = false;
    }
}
