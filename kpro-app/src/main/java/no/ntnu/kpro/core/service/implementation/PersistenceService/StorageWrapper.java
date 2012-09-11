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
import java.io.FilenameFilter;
import no.ntnu.kpro.core.service.interfaces.PersistenceService;

/**
 * a class that provides assistance to people saving and loading, abstracting
 * away some more horrors of hardware programming.
 *
 * @author magnus
 */
public class StorageWrapper {
    XStream xs;
    PersistenceService ps;
    Context c;
    
    public StorageWrapper(PersistenceService storageService, Context context) {
        xs = new XStream();
        ps = storageService;
        c = context;
    }
    
    public boolean saveFile(Object fileToSave, String fileName){
        String data = xs.toXML(fileToSave);
        return ps.saveToStorage(fileName, data, c);
    }
    
    public Object loadFile(String fileName){
        try{
            BufferedReader br = ps.readFromStorage(fileName);
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
    public String[] getFileList(){
        File dir = c.getFilesDir();
        return dir.list();
    }
    public String[] fileSearch(FilenameFilter filter){
        File dir = c.getFilesDir();
        return dir.list(filter);
    }
}
