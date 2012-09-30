/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.PersistenceService.PersistentWriteThroughStorage;

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
                for (int i = 0;i < b.length; i++) {
                    b[i]--;
                }
                return b;
            }

            public byte[] unprocess(byte[] b) {
                for (int i = 0;i < b.length; i++) {
                    b[i]++;
                }
                return b;
            }
        });
//        DummyInterface d = (DummyInterface)s.manage(new DummyObject("Nicklas", 22, "student"));
//        System.out.println("DDDD: "+d.getClass());
//        d.setAge(23);
//        d.setAge(24);
        DummyInterface d = (DummyInterface) s.find(DummyObject.class, 0);
        System.out.println("D: "+d);
        System.out.println("Cls: "+d.getClass());
        System.out.println("Name: "+d.getName());
        System.out.println("Age: "+d.getAge());
        System.out.println("Work: "+d.getWorkTitle());
        d.setAge(30);
    }
}
