/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.interfaces;

/**
 *
 * @author magnus
 */
public class PersistenceServiceTest {
//    XStream xs;
//    public PersistenceServiceTest() {
//    }
//    
//    @Before
//    public void setUp() {
//        xs = new XStream();
//    }
//    
//    @After
//    public void tearDown() {
//    }
//
//    /**
//     * Test of save method, of class PersistenceService.
//     */
//    @Test
//    public void testSave_Name_Object() {
//        //this class just calls the next one with the folder:"" so really doesn't need much testing
//        System.out.println("save");
//        String fileName = "test";
//        Object objectToSave = new Object();
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.save(fileName, objectToSave);
//        try {
//            FileReader fr = new FileReader(fileName);
//            Object loadedObject = xs.fromXML(fr);
//            assertTrue("checking that objects can be saved successfully", loadedObject.equals(objectToSave));
//        } catch (FileNotFoundException e) {
//           fail("no such file");
//        } catch (IOException  e){
//            fail("IO error");
//        }
//       
//    }
//
//    /**
//     * Test of save method, of class PersistenceService.
//     */
//    @Test
//    public void testSave_Name_Object_Folder() {
//        //this class just calls the next one with the folder:"" so really doesn't need much testing
//        System.out.println("save");
//        String fileName = "test";
//        String folder = "TestFolder";
//        Object objectToSave = new Object();
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.save(fileName, objectToSave, folder);
//        try {
//            FileReader fr = new FileReader(fileName);
//            Object loadedObject = xs.fromXML(fr);
//            assertTrue("checking that objects can be saved successfully", loadedObject.equals(objectToSave));
//        } catch (FileNotFoundException e) {
//           fail("no such file");
//        } catch (IOException  e){
//            fail("IO error");
//        }
//    }
//
//    /**
//     * Test of save method, of class PersistenceService.
//     */
//    @Test
//    public void testSave_Name_Content()  {
//        System.out.println("save");
//        String fileName = "filename";
//        String content = "I am a proxy for proper data";
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.save(fileName, content);
//        try {
//            FileReader fr = new FileReader(fileName);
//            BufferedReader output = new BufferedReader(fr); 
//            assertTrue(content.equals(output.readLine()));
//        } catch (FileNotFoundException e) {
//           fail("no such file");
//        } catch (IOException e){
//            fail("a wild IO error occurs");
//        }
//    }
//
//    /**
//     * Test of save method, of class PersistenceService.
//     */
//    @Test
//    public void testSave_Name_Content_Folder() {
//        System.out.println("save");
//        String folder = "testfolder";
//        String fileName = "filename";
//        String content = "I am a proxy for proper data";
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.save(fileName, content);
//        try {
//            FileReader fr = new FileReader(new File(folder,fileName));
//            BufferedReader output = new BufferedReader(fr); 
//            assertTrue(content.equals(output.readLine()));
//        } catch (FileNotFoundException e) {
//           fail("no such file");
//        } catch (IOException e){
//            fail("a wild IO error occurs");
//        }
//    }
//
//    /**
//     * Test of loadObject method, of class PersistenceService.
//     */
//    @Test
//    public void testLoadObject_name_callback() {
//        System.out.println("loadObject");
//        String fileName = "";
//        Callback receiver = null;
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.loadObject(fileName, receiver);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of loadObject method, of class PersistenceService.
//     */
//    @Test
//    public void testLoadObject_name_Folder_callback() {
//        System.out.println("loadObject");
//        String fileName = "";
//        String folder = "";
//        Callback receiver = null;
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.loadObject(fileName, folder, receiver);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of loadString method, of class PersistenceService.
//     */
//    @Test
//    public void testLoadString_name_callback() {
//        System.out.println("loadString");
//        String fileName = "";
//        Callback receiver = null;
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.loadString(fileName, receiver);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of loadString method, of class PersistenceService.
//     */
//    @Test
//    public void testLoadString_name_folder_callback() {
//        System.out.println("loadString");
//        String fileName = "";
//        String folder = "";
//        Callback receiver = null;
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.loadString(fileName, folder, receiver);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of removeFile method, of class PersistenceService.
//     */
//    @Test
//    public void testRemoveFile_fileName() {
//        System.out.println("removeFile");
//        String fileName = "";
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.removeFile(fileName);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of removeFile method, of class PersistenceService.
//     */
//    @Test
//    public void testRemoveFile_name_folder() {
//        System.out.println("removeFile");
//        String fileName = "";
//        String folder = "";
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.removeFile(fileName, folder);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of authorize method, of class PersistenceService.
//     */
//    @Test
//    public void testAuthorize_username_passord() {
//        System.out.println("authorize");
//        String userName = "";
//        String password = "";
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.authorize(userName, password);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of authorize method, of class PersistenceService.
//     */
//    @Test
//    public void testAuthorize_username_passord_callback() {
//        System.out.println("authorize");
//        String userName = "";
//        String password = "";
//        Callback receiver = null;
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.authorize(userName, password, receiver);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of isAuthorized method, of class PersistenceService.
//     */
//    @Test
//    public void testIsAuthorized_callback() {
//        System.out.println("isAuthorized");
//        Callback receiver = null;
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.isAuthorized(receiver);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getFileList method, of class PersistenceService.
//     */
//    @Test
//    public void testGetFileList_callback() {
//        System.out.println("getFileList");
//        Callback receiver = null;
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.getFileList(receiver);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getFileList method, of class PersistenceService.
//     */
//    @Test
//    public void testGetFileList_folder_callback() {
//        System.out.println("getFileList");
//        String folder = "";
//        Callback receiver = null;
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.getFileList(folder, receiver);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getFileList method, of class PersistenceService.
//     */
//    @Test
//    public void testGetFileList_Filter_callback() {
//        System.out.println("getFileList");
//        FilenameFilter filter = null;
//        Callback receiver = null;
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.getFileList(filter, receiver);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getFileList method, of class PersistenceService.
//     */
//    @Test
//    public void testGetFileList_folder_Filter_callback() {
//        System.out.println("getFileList");
//        String folder = "";
//        FilenameFilter filter = null;
//        Callback receiver = null;
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.getFileList(folder, filter, receiver);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of giveContext method, of class PersistenceService.
//     */
//    @Test
//    public void testGiveContext() {
//        System.out.println("giveContext");
//        Context context = null;
//        PersistenceService instance = new PersistenceServiceImpl();
//        instance.giveContext(context);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    public class PersistenceServiceImpl extends PersistenceService {
//
//        public void save(String fileName, Object objectToSave) {
//        }
//
//        public void save(String fileName, Object objectToSave, String folder) {
//        }
//
//        public void save(String fileName, String content) {
//        }
//
//        public void save(String fileName, String content, String folder) {
//        }
//
//        public void loadObject(String fileName, Callback receiver) {
//        }
//
//        public void loadObject(String fileName, String folder, Callback receiver) {
//        }
//
//        public void loadString(String fileName, Callback receiver) {
//        }
//
//        public void loadString(String fileName, String folder, Callback receiver) {
//        }
//
//        public void removeFile(String fileName) {
//        }
//
//        public void removeFile(String fileName, String folder) {
//        }
//
//        public void authorize(String userName, String password) {
//        }
//
//        public void authorize(String userName, String password, Callback receiver) {
//        }
//
//        public void isAuthorized(Callback receiver) {
//        }
//
//        public void getFileList(Callback receiver) {
//        }
//
//        public void getFileList(String folder, Callback receiver) {
//        }
//
//        public void getFileList(FilenameFilter filter, Callback receiver) {
//        }
//
//        public void getFileList(String folder, FilenameFilter filter, Callback receiver) {
//        }
//
//        public void giveContext(Context context) {
//        }
//    }
}
