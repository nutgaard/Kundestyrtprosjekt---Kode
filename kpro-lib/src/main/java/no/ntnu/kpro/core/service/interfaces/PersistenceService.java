/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.interfaces;

import java.util.Arrays;

/**
 *
 * @author Aleksander Sj�fjell
 */
public abstract class PersistenceService extends ServiceInterface<PersistenceService.Callback> {

    public interface Callback {
//        public void loadObjectReturn(String fileName, Object file);
//        public void loadStringReturn(String fileName, String file);
//        public void authorizationConfirmation(boolean isAutorized);
//        public void fileListReturn(String[] fileList);
//        public void someThingWentWrong(Exception e);
    }
//    //Saving files
//    public abstract void save(String fileName, Object objectToSave);
//    public abstract void save(String fileName, Object objectToSave, String folder);
//    public abstract void save(String fileName, String content);
//    public abstract void save(String fileName, String content, String folder);
//    
//    //Retrieving files
//    public abstract void loadObject(String fileName, Callback receiver);
//    public abstract void loadObject(String fileName, String folder, Callback receiver);
//    public abstract void loadString(String fileName, Callback receiver);
//    public abstract void loadString(String fileName, String folder, Callback receiver);
//    
//    //Deleting files
//    public abstract void removeFile(String fileName);
//    public abstract void removeFile(String fileName, String folder);
//    
//    //Authorization
//    public abstract void authorize(String userName, String password);
//    public abstract void authorize(String userName, String password, Callback receiver);
//    public abstract void isAuthorized(Callback receiver);
//    
//    //Utility methodes
//    public abstract void getFileList(Callback receiver);
//    public abstract void getFileList(String folder, Callback receiver);
//    public abstract void getFileList(FilenameFilter filter, Callback receiver);
//    public abstract void getFileList(String folder, FilenameFilter filter, Callback receiver);
//    
//    //setup requirements (temp, in case we don't standardize the constructor)
//    public abstract void giveContext(Context context);
    public abstract void close();
    public abstract Object manage(Object o)throws Exception;
    public abstract Object unmanage(Object o);
    public abstract void save(Object o)throws Exception;
    public abstract void delete(Object o);
    public abstract Object[] findAll(Class cls)throws Exception;
    public abstract Object find(Class cls, int id)throws Exception;
    public static  <T> T[] castTo(Object[] l, Class<? extends T[]> cls) {
        System.out.println("ListLength: "+l.length);
        if (l == null || l.length == 0){
            return null;
        }
        return Arrays.copyOf(l, l.length, cls);
    }
}
