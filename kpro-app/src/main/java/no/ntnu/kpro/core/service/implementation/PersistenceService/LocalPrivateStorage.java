/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.PersistenceService;

import android.content.*;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    public boolean saveToStorage(String fileName, String content) {
        try{
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
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
}
