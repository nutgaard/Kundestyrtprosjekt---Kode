/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

import android.net.Uri;
import android.util.Log;
import no.ntnu.kpro.core.utilities.FileHelper;


/**
 *
 * @author aleksandersjafjell
 */
public class ExpandableListChild {

    private String name;
    private Uri uri;

    public ExpandableListChild(String name, Uri uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        String fileName = name + "(" + FileHelper.getImageFileLastPathSegmentFromImage(uri) + ")";
        Log.i("ListChild", "I is in getName, and name is: " + name);
        return fileName;
    }

    public Uri getUri() {
        return this.uri;

    }
}
