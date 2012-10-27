/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

import android.net.Uri;
import android.util.Log;
import java.util.ArrayList;
//import java.util.LinkedHashMap;
import no.ntnu.kpro.core.model.attachment.Attachment;

/**
 *
 * @author aleksandersjafjell
 */
public class Attachments extends ArrayList<Attachment> {
    public Attachments() {
        super();
    }
 
    
    /**
     * Adds an attachment to the list of attachments.
     * @param fileLocation
     * @param type
     * @return the string representation of the added attachment
     */
    public String addAttachment(Attachment a) {
        add(a);
        return a.toString();
        
    }

    public void removeAttachment(Uri filePath) {
        for (Attachment obj : this) {
            Uri uri = obj.getUri();
            if (uri.getPath().equals(filePath.getPath())) {
                remove(obj);
            }
        }
    }
    public void removeAttachment(Attachment a){
        remove(a);
    }

    /**
     * Fetches wanted attachment
     * @param index The index of the attachment you want to fetch
     * @return an Object[2] array, where first argument is the full URI file path, and 
     * the second argument is the AttachmentType type
     */
    public Attachment getAttachment(int index) {
        return get(index);
    }
   
    private void logMe(String message) {
        Log.d("SendMessage", message);
    }
}
