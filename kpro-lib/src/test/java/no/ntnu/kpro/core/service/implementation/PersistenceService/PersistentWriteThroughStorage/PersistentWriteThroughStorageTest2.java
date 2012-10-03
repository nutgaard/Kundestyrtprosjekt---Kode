/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.PersistenceService.PersistentWriteThroughStorage;

import java.io.File;
import java.util.Arrays;
import no.ntnu.kpro.core.model.ModelProxy.IUser;
import no.ntnu.kpro.core.model.User;
import no.ntnu.kpro.core.service.interfaces.PersistencePostProcessor;

/**
 *
 * @author Nicklas
 */
public class PersistentWriteThroughStorageTest2 {

    public static void main(String[] args) throws Exception {
        PersistentWriteThroughStorage s = PersistentWriteThroughStorage.create(new User("test"), new PersistencePostProcessor() {
            public byte[] process(byte[] b) {
//                for (int i = 0;i < b.length; i++) {
//                    b[i]--;
//                }                
                return b;
            }

            public byte[] unprocess(byte[] b) {
//                for (int i = 0;i < b.length; i++) {
//                    b[i]++;
//                }
                return b;
            }
        }, new File("/test"));
        User user = new User("Nicklas");
        IUser d = (IUser)s.manage(user);
        
//        d.setName("Ida");
//        d.setName("Aleksander");
//        
//        
//        
//        IUser dl = (IUser)s.find(User.class, -1);
//        dl.setName("Magnus");

        Object[] users = s.findAll(User.class);
        IUser U = (IUser)users[0];
        System.out.println("Users: "+users.getClass());
        System.out.println("User: "+users);
        IUser[] a = s.castTo(users, IUser[].class);

//        DummyInterface d = (DummyInterface) s.find(DummyObject.class, 0);
//        System.out.println("D: "+d);
//        System.out.println("Cls: "+d.getClass());
//        System.out.println("Name: "+d.getName());
//        System.out.println("Age: "+d.getAge());
//        System.out.println("Work: "+d.getWorkTitle());
//        d.setAge(30);
    }
}
