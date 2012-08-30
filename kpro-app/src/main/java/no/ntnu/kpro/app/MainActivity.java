/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import no.ntnu.kpro.core.service.ServiceProvider;

/**
 *
 * @author Nicklas
 */
public class MainActivity extends Activity {
    private static final String TAG = "KPRO";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Activity started");
        setContentView(R.layout.main);
        
        Log.i(TAG, "Calling new intent to start service");
        Intent serviceIntent = new Intent("no.ntnu.kpro.core.service.ServiceProvider");
        Log.i(TAG, "ServiceAction: "+serviceIntent.getAction());
        Log.i(TAG, "Service: "+serviceIntent.toString());
        Log.i(TAG, "StartService: "+startService(serviceIntent));
        Log.i(TAG, "No errors, service should be running");
    }
}
