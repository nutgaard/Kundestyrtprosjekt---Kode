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
    private boolean removeFile(String fileName, Context context) {
        return context.deleteFile(fileName);
    }
    
    private boolean saveFile(Object fileToSave, String dir, String fileName){
        String data = xs.toXML(fileToSave);
        return saveToStorage(fileName, dir, data);
    }
    
    private Object loadFile(String fileName){
        try{
            BufferedReader br = readFromStorage(fileName,"");
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
    private String[] getFileList(){
        File dir = c.getFilesDir();
        return dir.list();
    }
    private String[] fileSearch(FilenameFilter filter){
        File dir = c.getFilesDir();
        return dir.list(filter);
    }
    /*****************************************************
     *          HERE COMES THE OUTSIDE METHODS
     *****************************************************/
    
    
    public void save(String fileName, Object objectToSave) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void save(String fileName, Object objectToSave, String folder) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void save(String fileName, String content) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void save(String fileName, String content, String folder) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void loadObject(String fileName, callback receiver) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void loadObject(String fileName, String folder, callback receiver) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void loadString(String fileName, callback receiver) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void loadString(String fileName, String folder, callback receiver) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeFile(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeFile(String fileName, String folder) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void authorize(String userName, String password) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void authorize(String userName, String password, callback receiver) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void isAuthorized(callback receiver) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getFileList(callback receiver) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getFileList(String folder, callback receiver) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getFileList(FilenameFilter filter, callback receiver) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getFileList(String folder, FilenameFilter filter, callback receiver) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void giveContext(Context context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
