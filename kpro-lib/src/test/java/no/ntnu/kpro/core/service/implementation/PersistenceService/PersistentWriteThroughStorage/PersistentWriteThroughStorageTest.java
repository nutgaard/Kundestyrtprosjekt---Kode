/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.PersistenceService.PersistentWriteThroughStorage;

import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import no.ntnu.kpro.core.model.ModelProxy.IUser;
import no.ntnu.kpro.core.model.User;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.InputSource;

/**
 *
 * //@author Nicklas
 */
public class PersistentWriteThroughStorageTest {

    static User good = new User("GoodGuy");
    static User bad = new User("BadGuy");
    static PersistentWriteThroughStorage em;
    static File baseDir;

    public PersistentWriteThroughStorageTest() {
        
    }

    //@BeforeClass
    public static void setUpClass() {
        try {
            baseDir = new File("/test");
            System.out.println("Base: "+baseDir.getAbsolutePath());
//            em = PersistentWriteThroughStorage.create(good, FileCryptoFactory.getProcessor(FileCryptoFactory.Crypto.NONE), baseDir);

        } catch (Exception ex) {
            Logger.getLogger(PersistentWriteThroughStorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //@AfterClass
    public static void tearDownClass() {
        delete(baseDir);
    }

    //@Before
    public void setUp() {
        try {
            good = new User(new BigInteger(130, new SecureRandom()).toString(32));
            em = new PersistentWriteThroughStorage(good, FileCryptoFactory.getProcessor(FileCryptoFactory.Crypto.NONE), baseDir);
//            em = PersistentWriteThroughStorage.create(good, FileCryptoFactory.getProcessor(FileCryptoFactory.Crypto.NONE), baseDir);
        } catch (Exception ex) {
            Logger.getLogger(PersistentWriteThroughStorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //@After
    public void tearDown() {
            em.close();
            good = null;
            delete(baseDir);
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation //@Test. For example:
    //
    // //@Test
    // public void hello() {}

    //@Test
    public void noSavingFile() {
        System.out.println("Test:noSavingFile");
        try {
            User u = new User("GoodGuysFriend");

            //Base dir not created, cannot be saved
            Object[] users = em.findAll(User.class);
            assertNull(users);
        } catch (Exception ex) {
            Logger.getLogger(PersistentWriteThroughStorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //@Test
    public void savingUnmanagedObjects() {
        System.out.println("Test:savingUnmanagedObjects");
        try {
            User u = new User("GoodGuysFriend");
            em.save(u);
            IUser[] users = em.castTo(em.findAll(User.class), IUser[].class);
            assertEquals(1, users.length);
            em.save(u);
            users = em.castTo(em.findAll(User.class), IUser[].class);
            assertEquals(1, users.length);
        } catch (Exception ex) {
            Logger.getLogger(PersistentWriteThroughStorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //@Test
    public void verifiedSavedContent() {
        System.out.println("Test:verifiedSavedContent");
        try {
            User u = new User("GoodGuysFriend");
            em.save(u);
            IUser[] users = em.castTo(em.findAll(User.class), IUser[].class);
            assertEquals(1, users.length);
            
            XPath v = XPathFactory.newInstance().newXPath();
            XPathExpression e = v.compile(User.class.getName()+"/name");
            File f = new File(baseDir, "/"+good.getName()+"/"+User.class.getSimpleName()+"/0");
            System.out.println("Reading from file: "+f.getAbsolutePath());
            String result = e.evaluate(new InputSource(new FileReader(f)));
            
            assertEquals(result, u.getName());
        } catch (Exception ex) {
            Logger.getLogger(PersistentWriteThroughStorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //@Test
    public void savingManagedObjects() {
        try {
            IUser u = (IUser) em.manage(new User("GoodGuysFriend"));
            IUser[] users = em.castTo(em.findAll(User.class), IUser[].class);
            assertEquals(1, users.length);
            em.save(u);
            users = em.castTo(em.findAll(User.class), IUser[].class);
            assertEquals(1, users.length);
        } catch (Exception ex) {
            Logger.getLogger(PersistentWriteThroughStorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //@Test
    public void fetchUnsaved() {
        try {
            IUser u = (IUser)em.find(User.class, Integer.MAX_VALUE);
            assertNull(u);
        } catch (Exception ex) {
            Logger.getLogger(PersistentWriteThroughStorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
    //@Test
    public void updatingFile() {
        try {
            IUser u = (IUser) em.manage(new User("GoodGuysFriend"));
            String nowName = u.getName();
            
            u.setName("ABCDEFG");
            XPath v = XPathFactory.newInstance().newXPath();
            XPathExpression e = v.compile(User.class.getName()+"/name");
            File f = new File(baseDir, "/"+good.getName()+"/"+User.class.getSimpleName()+"/0");
            System.out.println("Reading from file: "+f.getAbsolutePath());
            String result = e.evaluate(new InputSource(new FileReader(f)));
            
            assertEquals(result, u.getName());
            
        } catch (Exception e) {
            Logger.getLogger(PersistentWriteThroughStorageTest.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    //@Test
    public void readingFile() {
        try {
            User user = new User("Nicklas");
            em.save(user);
            IUser readUser = (IUser)em.find(User.class, 0);
            assertEquals(user.getName(), readUser.getName());
        } catch (Exception ex) {
            Logger.getLogger(PersistentWriteThroughStorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //@Test
    public void deleteFile() {
        try {
            User user = new User("Nicklas");
            em.save(user);
            IUser[] users = em.castTo(em.findAll(User.class), IUser[].class);
            assertEquals(1, users.length);
            em.delete(users[0]);
            users = em.castTo(em.findAll(User.class), IUser[].class);
            assertEquals(0, users.length);
        } catch (Exception ex) {
            Logger.getLogger(PersistentWriteThroughStorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private static void delete(File f){
        if (f.isFile()){
            f.deleteOnExit();
        }else {
            for (File file : f.listFiles()){
                delete(file);
            }
            f.deleteOnExit();
        }
    }
}
