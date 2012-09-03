/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.PersistenceService;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import no.ntnu.kpro.core.service.interfaces.PersistenceService;

/**
 *
 * @author aleksandersjafjell
 */
public class LocalPrivateStorage implements PersistenceService {

    /**
     * Saves a new file if it does not exist, and overwrites old if it does.
     * @param fileName Name of file to be saved.
     * @param content File content.
     * @returns true if file saved successfully, else false.
     */
    public boolean saveToStorage(String fileName, String content, Context context) {
        try{
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
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
    public BufferedReader readFromStorage(String fileName) {
        return null;   
             
             
        
    }

    public boolean authorize(String userName, String password) {
        return true;
    }

    public boolean isAuthorized() {
        return true;
    }

    public boolean removeFile(String fileName) {
        return true;
    }
}
