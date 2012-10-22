/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import com.sun.mail.imap.IMAPMessage;
import java.util.Map;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.utilities.Pair;

/**
 *
 * @author Nicklas
 */
public abstract class IMAPStrategy implements Runnable {
    protected Map<String, Pair<IMAPMessage, XOMessage>> cache;
    public IMAPStrategy(Map<String, Pair<IMAPMessage, XOMessage>> cache) {
        this.cache = cache;
    }
    
    public abstract void halt();
}
