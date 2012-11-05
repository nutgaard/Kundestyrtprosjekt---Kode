/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 *
 * @author Nicklas
 */
public class ShareWithActivity extends Activity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        System.out.println("ShareWithActivity starting");

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String action = intent.getAction();
        if (Intent.ACTION_SEND.equals(action)) {
            if (extras.containsKey(Intent.EXTRA_STREAM)) {
                Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
                Intent i = new Intent("ShareWith");
                i.putExtra("uri", uri);
                sendBroadcast(i);
            }
        }
        System.out.println("ShareWithActivity complete, finishing");
        finish();
    }
}
