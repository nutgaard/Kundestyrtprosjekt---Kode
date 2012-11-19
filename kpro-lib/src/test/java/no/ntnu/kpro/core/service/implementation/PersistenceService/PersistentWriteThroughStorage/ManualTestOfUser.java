/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.PersistenceService.PersistentWriteThroughStorage;

import no.ntnu.kpro.core.model.ModelProxy.IUser;
import no.ntnu.kpro.core.model.User;
import no.ntnu.kpro.core.service.factories.PersistenceServiceFactory;
import no.ntnu.kpro.core.service.interfaces.PersistenceService;

/**
 *
 * @author Nicklas
 */
public class ManualTestOfUser {
    public static void main(String[] args) throws Exception {
        //Should just write to base dir
        PersistenceService p = PersistenceServiceFactory.createMessageStorage(null, null);
        
        IUser user = new User("Nicklas", "123451");
        p.save(user);
    }
}
