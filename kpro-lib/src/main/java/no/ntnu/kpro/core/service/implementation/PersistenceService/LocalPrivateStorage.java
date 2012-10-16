/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.PersistenceService;

import android.content.Context;
import android.util.Log;
import com.thoughtworks.xstream.XStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import no.ntnu.kpro.core.service.interfaces.PersistenceService;

/**
 *
 * @author aleksandersjafjell
 */
public class LocalPrivateStorage extends PersistenceService {

    XStream xs;
    Context c;
    
    public LocalPrivateStorage(Context context) {
        xs = new XStream();
        c = context;
    }
      
    private boolean saveToStorage(String fileName, String folder, String content) {
        try{
            File file = new File(folder, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();
            return true;
        }catch (java.io.IOException e){
            Log.d("LocalPrivateStorage", e.getStackTrace().toString());
            return false;
        }
    }  

    /**
     * 
     * @param fileName
     * @return 
     */
   
    private BufferedReader readFromStorage(String fileName, String folder) {
        try {
            File dir = c.getDir(folder, c.MODE_PRIVATE);
            File file = new File(dir, fileName);
            FileReader fr = new FileReader(file);
            BufferedReader output = new BufferedReader(fr);
            return output;
        } catch (FileNotFoundException e) {
           Log.d("LocalPrivateStorage", e.getStackTrace().toString());
        }
        return null;
    }


    private boolean isAuthorized() {
        return true;
    }
    
    /**
     * deletes a given file from storage. 
     * @param fileName name of the file to delete
     * @return true if the file was successfully deleted, else false.
     */
    private boolean removeTheFile(String fileName, String dir) {
        File zeFile = new File(dir,fileName);
        return c.deleteFile(zeFile.getPath());
    }
    
    private boolean saveFile(Object fileToSave, String dir, String fileName){
        String data = xs.toXML(fileToSave);
        return saveToStorage(fileName, dir, data);
    }
    
    private Object loadFile(String fileName, String dir){
        try{
            BufferedReader br = readFromStorage(fileName,dir);
            String nextLine = br.readLine();
            String xml = null;
            while (nextLine != null){
                xml = xml + nextLine;
                nextLine = br.readLine();
        }
            return xs.fromXML(xml);
        }catch(Exception e){
            Log.d("StorageWrpper", e.getStackTrace().toString());
            return null;
        }
    }
    /**
     * provides a list of all existing files in the base folder
     * @return 
     */
    private String[] getFileList(String Folder){
        File dir = c.getDir(Folder,c.MODE_PRIVATE);
        return dir.list();
    }
    private String[] fileSearch(String Folder, FilenameFilter filter){
        File dir = c.getDir(Folder,c.MODE_PRIVATE);
        return dir.list(filter);
    }
    /*****************************************************
     *          HERE COMES THE OUTSIDE METHODS
     *****************************************************/
    
    
    public void save(String fileName, Object objectToSave) {
        saveFile(objectToSave, "", fileName);
    }

    public void save(String fileName, Object objectToSave, String folder) {
        saveFile(objectToSave, folder, fileName);
    }

    public void save(String fileName, String content) {
        save(fileName, content, "");
    }

    public void save(String fileName, String content, String folder) {
        saveToStorage(fileName, folder, content);
    }

    public void loadObject(String fileName, Callback receiver) {
        loadObject(fileName, "", receiver);
    }

    public void loadObject(String fileName, String folder, Callback receiver) {
        Object object = loadFile(fileName, "");
        receiver.loadObjectReturn(fileName,object);
    }

    public void loadString(String fileName, Callback receiver) {
        loadString(fileName, "", receiver);
    }

    public void loadString(String fileName, String folder, Callback receiver) {
        try{
            BufferedReader br = readFromStorage(fileName, folder);
            String nextLine = br.readLine();
            String result = null;
            while (nextLine != null){
                result = result + nextLine;
                nextLine = br.readLine();
            }
            receiver.loadStringReturn(fileName, result);
        } catch (Exception e){
            Log.d("LocalPrivateStorage", e.getStackTrace().toString());
//            receiver.someThingWentWrong(e);
        }
            
    }

    public void removeFile(String fileName) {
        removeFile(fileName,"");
    }

    public void removeFile(String fileName, String folder) {
        removeTheFile(fileName, folder);
    }

    public void authorize(String userName, String password) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void authorize(String userName, String password, Callback receiver) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void isAuthorized(Callback receiver) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getFileList(Callback receiver) {
        getFileList("", receiver);
    }

    public void getFileList(String folder, Callback receiver) {
        receiver.fileListReturn(getFileList(folder));
    }

    public void getFileList(FilenameFilter filter, Callback receiver) {
       getFileList("", filter, receiver);
    }

    public void getFileList(String folder, FilenameFilter filter, Callback receiver) {
        receiver.fileListReturn(fileSearch(folder, filter));
    }

    public void giveContext(Context context) {
        c = context;
    }


}
