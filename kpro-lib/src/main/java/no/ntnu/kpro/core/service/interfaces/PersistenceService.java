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
public abstract class PersistenceService extends ServiceInterface<PersistenceService.callback> {

    public interface callback {
        public void loadObjectReturn(String fileName, Object file);
        public void loadStringReturn(String fileName, String file);
        public void authorizationConfirmation(boolean isAutorized);
        public void fileListReturn(String[] fileList);
    }
    //Saving files
    public abstract void save(String fileName, Object objectToSave);
    public abstract void save(String fileName, Object objectToSave, String folder);
    public abstract void save(String fileName, String content);
    public abstract void save(String fileName, String content, String folder);
    
    //Retrieving files
    public abstract void loadObject(String fileName, callback receiver);
    public abstract void loadObject(String fileName, String folder, callback receiver);
    public abstract void loadString(String fileName, callback receiver);
    public abstract void loadString(String fileName, String folder, callback receiver);
    
    //Deleting files
    public abstract void removeFile(String fileName);
    public abstract void removeFile(String fileName, String folder);
    
    //Authorization
    public abstract void authorize(String userName, String password);
    public abstract void authorize(String userName, String password, callback receiver);
    public abstract void isAuthorized(callback receiver);
    
    //Utility methodes
    public abstract void getFileList(callback receiver);
    public abstract void getFileList(String folder, callback receiver);
    public abstract void getFileList(FilenameFilter filter, callback receiver);
    public abstract void getFileList(String folder, FilenameFilter filter, callback receiver);
    
    //setup requirements (temp, in case we don't standardize the constructor)
    public abstract void giveContext(Context context);
}
