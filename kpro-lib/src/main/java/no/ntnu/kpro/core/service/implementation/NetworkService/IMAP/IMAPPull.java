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
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.ComparisonTerm;
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
    private Date lastReceived = new Date(0);
    private boolean run = true;

    IMAPPull(final Properties props, final Authenticator auth, int intervalInSeconds, final IMAPStorage store) {
        this.storage = store;
        this.intervalInMillies = intervalInSeconds * 1000;
    }

    public IMAPPull(final Properties props, final Authenticator auth, NetworkService.InternalCallback listener, int intervalInSeconds) {
        this(props, auth, intervalInSeconds, new IMAPStorage(props, auth, listener));
    }

    public void run() {
        while (run) {
            long diff = 0;
            while ((diff = System.currentTimeMillis() - lastPull) < intervalInMillies) {
                try {
                    synchronized (this) {
//                        System.out.println("Waiting for: "+(intervalInMillies-diff));
                        wait(intervalInMillies - diff);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(IMAPPull.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("Searching at: "+lastReceived);
            Message[] messagesReceived = storage.getAllMessages(NetworkServiceImp.BoxName.INBOX, new ReceivedDateTerm(ComparisonTerm.GT, lastReceived));
            System.out.println("Pull seen message: "+messagesReceived);
            if (messagesReceived != null) {
                System.out.println("Finding newest mail");
                for (Message m : messagesReceived) {
                    try {
                        System.out.println("DateCompare: "+lastReceived+" vs "+m.getReceivedDate());
                        if (lastReceived.before(m.getReceivedDate())) {
                            System.out.println("Updating last seen mail");
                            lastReceived = m.getReceivedDate();
                        }
                    } catch (MessagingException ex) {
                        Logger.getLogger(IMAPPull.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            lastPull = System.currentTimeMillis();
        }
    }

    public void halt() {
        this.run = false;
    }
}
