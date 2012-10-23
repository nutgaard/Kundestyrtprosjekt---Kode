/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author aleksandersjafjell
 */
public class Attachments {

    private List<Object[]> attachments;

    public Attachments() {
        attachments = new ArrayList<Object[]>();
    }
    
    private String getAttachmentToString(Uri fileLocation, AttachmentType type){
        String s = "";
        switch (type) {
                case IMAGE:
                    s += "Image ";
                    break;
                case VIDEO:
                    s += "Video ";
                    break;
                case GPS_LOCATION:
                    s += "GPS ";
                    break;
            }
        s+= "( " + fileLocation.getPath() + " )";
        return s;
    }
    
    /**
     * Adds an attachment to the list of attachments.
     * @param fileLocation
     * @param type
     * @return the string representation of the added attachment
     */
    public String addAttachment(Uri fileLocation, AttachmentType type) {
        attachments.add(new Object[]{fileLocation, type});
        String attachmentString = getAttachmentToString(fileLocation, type);
        Log.d("Attachments", "Added Attachment:" + fileLocation.getPath());
        return attachmentString;
        
    }

    public void removeAttachment(Uri filePath) {
        for (Object[] obj : attachments) {
            Uri uri = (Uri) obj[0];
            if (uri.getPath().equals(filePath.getPath())) {
                attachments.remove(obj);
            }
        }
    }

    /**
     * Fetches wanted attachment
     * @param index The index of the attachment you want to fetch
     * @return an Object[2] array, where first argument is the full URI file path, and 
     * the second argument is the AttachmentType type
     */
    public Object[] getAttachment(int index) {
        return attachments.get(index);
    }

//    /**
//     * 
//     * @return A list of all attachments, named by type and count,
//     * e.g. "Image 1", "Video 2" or "GPS Location 1"
//     */
//    public List<String> getAttachments() {
//        List<String> attachmentStrings = new ArrayList<String>();
//        int imageCount = 1;
//        int videoCount = 1;
//        int gpsCount = 1;
//        for (Object[] obj : attachments) {
//            AttachmentType type = (AttachmentType) obj[1];
//            Uri path = (Uri) obj[0];
//            switch (type) {
//                case IMAGE:
//                    attachmentStrings.add("Image " + imageCount++ + "( " + path.getPath() + " )");
//                    break;
//                case VIDEO:
//                    attachmentStrings.add("Video " + videoCount++);
//                    break;
//                case GPS_LOCATION:
//                    attachmentStrings.add("GPS Location " + gpsCount++);
//                    break;
//            }
//        }
//        return attachmentStrings;
//    }
//    
    private void logMe(String message) {
        Log.d("SendMessage", message);
    }
}
