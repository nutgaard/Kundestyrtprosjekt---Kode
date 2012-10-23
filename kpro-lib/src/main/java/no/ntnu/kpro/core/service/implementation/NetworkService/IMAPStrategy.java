/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAPCache;

/**
 *
 * @author Nicklas
 */
public abstract class IMAPStrategy implements Runnable {
    protected IMAPCache cache;
    public IMAPStrategy(IMAPCache cache) {
        this.cache = cache;
    }
    
    public abstract void halt();
}
