/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model.attachment;

import java.net.URI;



/**
 *
 * @author Nicklas
 */
public class ImageAttachment extends Attachment {
     public ImageAttachment(URI uri){
        super(uri);
    }
    
    public String toString() {
        return "Image: "+uri.getPath();
    }
}
