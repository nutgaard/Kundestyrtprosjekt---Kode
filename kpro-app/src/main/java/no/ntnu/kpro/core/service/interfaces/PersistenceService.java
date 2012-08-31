/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.interfaces;

import android.content.SharedPreferences;
import java.io.BufferedReader;

/**
 *
 * @author Aleksander Sjåfjell
 */
public interface PersistenceService extends ServiceInterface {
    
    /**
     * Authorize the user for saving to persistence
     * @param userName
     * @param password
     * @return true if user was successfully authorized, else false.
     */
    public boolean authorize(String userName, String password);
    
    public boolean isAuthorized();
    
    public boolean saveToStorage(String fileName, String content);
    
    public BufferedReader readFromStorage(String fileName);
    
    public boolean removeFile(String fileName);
    
}
