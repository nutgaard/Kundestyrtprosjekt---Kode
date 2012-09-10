/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import no.ntnu.kpro.core.service.interfaces.PersistenceService;

/**
 *
 * @author Nicklas
 */
public class ServiceProvider extends Service {

    private IBinder mBinder = new LocalBinder();
    private static final String TAG = "KPRO";
    public static ThreadPoolExecutor threadpool;
    private Activity currentActivity;
    
    @Override
    public void onCreate() {
        super.onCreate();
        threadpool = new ThreadPoolExecutor(MODE_PRIVATE, MODE_PRIVATE, START_STICKY, TimeUnit.DAYS, null);
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
            this.mBinder = new LocalBinder();
        }
        return this.mBinder;
    }
    public class LocalBinder extends Binder {
        public ServiceProvider getService(){
            return ServiceProvider.this;
        }
    }
    public PersistenceService getPersistenceService() {
        return null;
    }
}
