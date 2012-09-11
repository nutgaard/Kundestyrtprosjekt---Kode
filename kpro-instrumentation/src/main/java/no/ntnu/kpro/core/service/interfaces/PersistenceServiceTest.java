/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.interfaces;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import no.ntnu.kpro.core.service.interfaces.PersistenceService;
import no.ntnu.kpro.core.service.interfaces.PersistenceService.callback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author magnus
 */
public class PersistenceServiceTest {
    
    public PersistenceServiceTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of save method, of class PersistenceService.
     */
    @Test
    public void testSave_Name_Object() {
        System.out.println("save");
        String fileName = "test";
        Object objectToSave = new Object();
        PersistenceService instance = new PersistenceServiceImpl();
        instance.save(fileName, objectToSave);
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader output = new BufferedReader(fr); 
        } catch (FileNotFoundException e) {
           fail("no such file");
        }
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of save method, of class PersistenceService.
     */
    @Test
    public void testSave_Name_Object_Folder() {
        System.out.println("save");
        String fileName = "";
        Object objectToSave = null;
        String folder = "";
        PersistenceService instance = new PersistenceServiceImpl();
        instance.save(fileName, objectToSave, folder);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of save method, of class PersistenceService.
     */
    @Test
    public void testSave_Name_Content()  {
        System.out.println("save");
        String fileName = "filename";
        String content = "I am a proxy for proper data";
        PersistenceService instance = new PersistenceServiceImpl();
        instance.save(fileName, content);
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader output = new BufferedReader(fr); 
            assertTrue(content.equals(output.readLine()));
        } catch (FileNotFoundException e) {
           fail("no such file");
        } catch (IOException e){
            fail("a wild IO error occurs");
        }
    }

    /**
     * Test of save method, of class PersistenceService.
     */
    @Test
    public void testSave_Name_Content_Folder() {
        System.out.println("save");
        String folder = "testfolder";
        String fileName = "filename";
        String content = "I am a proxy for proper data";
        PersistenceService instance = new PersistenceServiceImpl();
        instance.save(fileName, content);
        try {
            FileReader fr = new FileReader(new File(folder,fileName));
            BufferedReader output = new BufferedReader(fr); 
            assertTrue(content.equals(output.readLine()));
        } catch (FileNotFoundException e) {
           fail("no such file");
        } catch (IOException e){
            fail("a wild IO error occurs");
        }
    }

    /**
     * Test of loadObject method, of class PersistenceService.
     */
    @Test
    public void testLoadObject_name_callback() {
        System.out.println("loadObject");
        String fileName = "";
        callback receiver = null;
        PersistenceService instance = new PersistenceServiceImpl();
        instance.loadObject(fileName, receiver);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of loadObject method, of class PersistenceService.
     */
    @Test
    public void testLoadObject_name_Folder_callback() {
        System.out.println("loadObject");
        String fileName = "";
        String folder = "";
        callback receiver = null;
        PersistenceService instance = new PersistenceServiceImpl();
        instance.loadObject(fileName, folder, receiver);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of loadString method, of class PersistenceService.
     */
    @Test
    public void testLoadString_name_callback() {
        System.out.println("loadString");
        String fileName = "";
        callback receiver = null;
        PersistenceService instance = new PersistenceServiceImpl();
        instance.loadString(fileName, receiver);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of loadString method, of class PersistenceService.
     */
    @Test
    public void testLoadString_name_folder_callback() {
        System.out.println("loadString");
        String fileName = "";
        String folder = "";
        callback receiver = null;
        PersistenceService instance = new PersistenceServiceImpl();
        instance.loadString(fileName, folder, receiver);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeFile method, of class PersistenceService.
     */
    @Test
    public void testRemoveFile_fileName() {
        System.out.println("removeFile");
        String fileName = "";
        PersistenceService instance = new PersistenceServiceImpl();
        instance.removeFile(fileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeFile method, of class PersistenceService.
     */
    @Test
    public void testRemoveFile_name_folder() {
        System.out.println("removeFile");
        String fileName = "";
        String folder = "";
        PersistenceService instance = new PersistenceServiceImpl();
        instance.removeFile(fileName, folder);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of authorize method, of class PersistenceService.
     */
    @Test
    public void testAuthorize_username_passord() {
        System.out.println("authorize");
        String userName = "";
        String password = "";
        PersistenceService instance = new PersistenceServiceImpl();
        instance.authorize(userName, password);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of authorize method, of class PersistenceService.
     */
    @Test
    public void testAuthorize_username_passord_callback() {
        System.out.println("authorize");
        String userName = "";
        String password = "";
        callback receiver = null;
        PersistenceService instance = new PersistenceServiceImpl();
        instance.authorize(userName, password, receiver);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isAuthorized method, of class PersistenceService.
     */
    @Test
    public void testIsAuthorized_callback() {
        System.out.println("isAuthorized");
        callback receiver = null;
        PersistenceService instance = new PersistenceServiceImpl();
        instance.isAuthorized(receiver);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFileList method, of class PersistenceService.
     */
    @Test
    public void testGetFileList_callback() {
        System.out.println("getFileList");
        callback receiver = null;
        PersistenceService instance = new PersistenceServiceImpl();
        instance.getFileList(receiver);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFileList method, of class PersistenceService.
     */
    @Test
    public void testGetFileList_folder_callback() {
        System.out.println("getFileList");
        String folder = "";
        callback receiver = null;
        PersistenceService instance = new PersistenceServiceImpl();
        instance.getFileList(folder, receiver);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFileList method, of class PersistenceService.
     */
    @Test
    public void testGetFileList_Filter_callback() {
        System.out.println("getFileList");
        FilenameFilter filter = null;
        callback receiver = null;
        PersistenceService instance = new PersistenceServiceImpl();
        instance.getFileList(filter, receiver);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFileList method, of class PersistenceService.
     */
    @Test
    public void testGetFileList_folder_Filter_callback() {
        System.out.println("getFileList");
        String folder = "";
        FilenameFilter filter = null;
        callback receiver = null;
        PersistenceService instance = new PersistenceServiceImpl();
        instance.getFileList(folder, filter, receiver);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of giveContext method, of class PersistenceService.
     */
    @Test
    public void testGiveContext() {
        System.out.println("giveContext");
        Context context = null;
        PersistenceService instance = new PersistenceServiceImpl();
        instance.giveContext(context);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public class PersistenceServiceImpl implements PersistenceService {

        public void save(String fileName, Object objectToSave) {
        }

        public void save(String fileName, Object objectToSave, String folder) {
        }

        public void save(String fileName, String content) {
        }

        public void save(String fileName, String content, String folder) {
        }

        public void loadObject(String fileName, callback receiver) {
        }

        public void loadObject(String fileName, String folder, callback receiver) {
        }

        public void loadString(String fileName, callback receiver) {
        }

        public void loadString(String fileName, String folder, callback receiver) {
        }

        public void removeFile(String fileName) {
        }

        public void removeFile(String fileName, String folder) {
        }

        public void authorize(String userName, String password) {
        }

        public void authorize(String userName, String password, callback receiver) {
        }

        public void isAuthorized(callback receiver) {
        }

        public void getFileList(callback receiver) {
        }

        public void getFileList(String folder, callback receiver) {
        }

        public void getFileList(FilenameFilter filter, callback receiver) {
        }

        public void getFileList(String folder, FilenameFilter filter, callback receiver) {
        }

        public void giveContext(Context context) {
        }
    }
}
