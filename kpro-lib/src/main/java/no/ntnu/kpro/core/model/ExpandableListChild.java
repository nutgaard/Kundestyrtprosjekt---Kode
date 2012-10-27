/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

import android.net.Uri;

/**
 *
 * @author aleksandersjafjell
 */
public class ExpandableListChild {

	private String name;
	private Uri uri;
        
        public ExpandableListChild(String name, Uri uri){
            this.name = name;
            this.uri = uri;
        }
	
	public String getName() {
		return name + " ( " + uri.getLastPathSegment() + ")";
	}
	
	public Uri getUri() {
		return this.uri;
                
	}
	
}

