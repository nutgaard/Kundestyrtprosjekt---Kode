/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;

import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas
 */
public class IMAP {
    private IMAPStorage storage;
    private IMAPIdle idleListener;
  
    public IMAPStorage getStorage() {
        return this.storage;
    }
    public IMAPIdle getIdleHandler() {
        return this.idleListener;
    }
}
