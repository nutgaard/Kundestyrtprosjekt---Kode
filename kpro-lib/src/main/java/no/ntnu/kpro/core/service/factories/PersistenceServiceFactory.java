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
            File f = (c!=null)?c.getFilesDir():new File("./");
            System.out.println("Opening Persistencelayer at: "+f);
            return new PersistentWriteThroughStorage(user, FileCryptoFactory.getProcessor(FileCryptoFactory.Crypto.NONE), f);
        } catch (Exception ex) {
            Logger.getLogger(PersistenceServiceFactory.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    public static PersistenceService createImageStore(Context c) {
        try {
            File f = (c!=null)?c.getExternalFilesDir(null) :new File("./");
            return new PersistenceService(f) {

                @Override
                public void close() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Object manage(Object o) throws Exception {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Object unmanage(Object o) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void save(Object o) throws Exception {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void delete(Object o) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Object[] findAll(Class cls) throws Exception {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Object find(Class cls, int id) throws Exception {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
        }catch(Exception e) {
            Logger.getLogger(PersistenceServiceFactory.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }
}
