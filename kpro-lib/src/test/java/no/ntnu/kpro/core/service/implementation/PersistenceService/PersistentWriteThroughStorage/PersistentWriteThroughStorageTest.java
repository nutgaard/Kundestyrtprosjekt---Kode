/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.PersistenceService.PersistentWriteThroughStorage;

import java.io.File;
import no.ntnu.kpro.core.model.Interface.IUser;
import no.ntnu.kpro.core.model.User;
import no.ntnu.kpro.core.service.interfaces.PersistencePostProcessor;

/**
 *
 * @author Nicklas
 */
public class PersistentWriteThroughStorageTest {
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
        }, new File("/"));
        IUser d = (IUser)s.manage(new User("Nicklas"));
        
        d.setName("Ida");
        d.setName("Aleksander");
        
        IUser dl = (IUser)s.find(User.class, 0);
        dl.setName("Magnus");
        
//        DummyInterface d = (DummyInterface) s.find(DummyObject.class, 0);
//        System.out.println("D: "+d);
//        System.out.println("Cls: "+d.getClass());
//        System.out.println("Name: "+d.getName());
//        System.out.println("Age: "+d.getAge());
//        System.out.println("Work: "+d.getWorkTitle());
//        d.setAge(30);
    }
}
