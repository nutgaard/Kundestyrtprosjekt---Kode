/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 *
 * @author Nicklas
 */
public class ServiceProvider extends Service {

    private IBinder mBinder = new LocalBinder();
    private static final String TAG = "KPRO";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service starting");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        if (this.mBinder == null){
            
        }
        return this.mBinder;
    }
    public class LocalBinder extends Binder {
        public ServiceProvider getService(){
            return ServiceProvider.this;
        }
    }
}
