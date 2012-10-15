/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.SecurityService;

import android.content.Context;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.ntnu.kpro.core.model.User;
import no.ntnu.kpro.core.service.implementation.PersistenceService.PersistentWriteThroughStorage.FileCryptoFactory;
import no.ntnu.kpro.core.service.implementation.PersistenceService.PersistentWriteThroughStorage.PersistentWriteThroughStorage;

/**
 *
 * @author Nicklas
 */
public class UserManager {
    private PersistentWriteThroughStorage storage;
    
    public UserManager(Context c){
        try {
            this.storage = new PersistentWriteThroughStorage(new User("globals"), FileCryptoFactory.getProcessor(FileCryptoFactory.Crypto.NONE), c.getDir("/", c.MODE_PRIVATE));
        } catch (Exception ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void createUser(User user){
        try {
            this.storage.save(user);
        } catch (Exception ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public User authorize(User user){
        try {
            User[] users = (User[])storage.findAll(User.class);
            for (User u : users){
                if (u.authorize(user)){
                    return u;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
    }
}
