/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.factories;

import android.content.Context;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.ntnu.kpro.core.model.User;
import no.ntnu.kpro.core.service.implementation.PersistenceService.PersistentWriteThroughStorage.FileCryptoFactory;
import no.ntnu.kpro.core.service.implementation.PersistenceService.PersistentWriteThroughStorage.PersistentWriteThroughStorage;
import no.ntnu.kpro.core.service.interfaces.PersistenceService;

/**
 *
 * @author Nicklas
 */
public class PersistenceServiceFactory {
    public static PersistenceService createService() {
        return null;
    }
    public static PersistenceService createLoginService() {
        return null;
    }
    public static PersistenceService createMessageStorage(User user, Context c){
        try {
            return new PersistentWriteThroughStorage(user, FileCryptoFactory.getProcessor(FileCryptoFactory.Crypto.NONE), (c != null)?c.getDir("/", c.MODE_PRIVATE):new File("/"));
        } catch (Exception ex) {
            Logger.getLogger(PersistenceServiceFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
