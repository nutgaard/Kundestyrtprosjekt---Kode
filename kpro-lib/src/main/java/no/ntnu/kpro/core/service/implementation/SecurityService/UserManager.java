/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.SecurityService;

import android.content.Context;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.ntnu.kpro.core.model.ModelProxy.IUser;
import no.ntnu.kpro.core.model.User;
import no.ntnu.kpro.core.service.implementation.PersistenceService.PersistentWriteThroughStorage.PersistentWriteThroughStorage;

/**
 *
 * @author Nicklas
 */
public class UserManager {
    private PersistentWriteThroughStorage storage;
    
    public UserManager(Context c){
        try {
            this.storage = new PersistentWriteThroughStorage(new User("globals", ""), FileCryptoFactory.getProcessor(FileCryptoFactory.Crypto.NONE), c.getDir("", c.MODE_PRIVATE));
        } catch (Exception ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void createUser(IUser user){
        try {
            this.storage.save(user);
        } catch (Exception ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public Boolean authorize(IUser user){
        try {
            Object[] usersRaw = storage.findAll(User.class);
            if (usersRaw == null || usersRaw.length == 0){
                //Nothing found
                return null;
            }
            IUser[] users = PersistentWriteThroughStorage.castTo(usersRaw, IUser[].class);
            for (IUser u : users){
                if (u.authorize(user)){
                    return Boolean.TRUE;
                }else if (user.getName().equals(u.getName())) {
                    return Boolean.FALSE;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return null;
    }
}
