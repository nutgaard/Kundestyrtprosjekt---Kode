/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.interfaces;

import android.content.Context;
import java.io.FilenameFilter;

/**
 *
 * @author Aleksander Sj�fjell
 */
public interface PersistenceService extends ServiceInterface {

    public interface callback {
        public void loadObjectReturn(String fileName, Object file);
        public void loadStringReturn(String fileName, String file);
        public void authorizationConfirmation(boolean isAutorized);
        public void fileListReturn(String[] fileList);
    }
    //Saving files
    public void save(String fileName, Object objectToSave);
    public void save(String fileName, Object objectToSave, String folder);
    public void save(String fileName, String content);
    public void save(String fileName, String content, String folder);
    
    //Retrieving files
    public void loadObject(String fileName, callback receiver);
    public void loadObject(String fileName, String folder, callback receiver);
    public void loadString(String fileName, callback receiver);
    public void loadString(String fileName, String folder, callback receiver);
    
    //Deleting files
    public void removeFile(String fileName);
    public void removeFile(String fileName, String folder);
    
    //Authorization
    public void authorize(String userName, String password);
    public void authorize(String userName, String password, callback receiver);
    public void isAuthorized(callback receiver);
    
    //Utility methodes
    public void getFileList(callback receiver);
    public void getFileList(String folder, callback receiver);
    public void getFileList(FilenameFilter filter, callback receiver);
    public void getFileList(String folder, FilenameFilter filter, callback receiver);
    
    //setup requirements (temp, in case we don't standardize the constructor)
    public void giveContext(Context context);
}
