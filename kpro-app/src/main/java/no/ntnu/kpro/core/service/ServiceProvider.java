/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Nicklas
 */
public class ServiceProvider extends Service {

//    private static final String TAG = ServiceProvider.class.getSimpleName();
    private static final String TAG = "KPRO";
    private Timer timer;
    private TimerTask updateTask = new TimerTask() {

        @Override
        public void run() {
            Log.i(TAG, "Timer task doing work");
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service starting");
        
        timer = new Timer("TweetCollectorTimer");
        timer.schedule(updateTask, 1000L, 5 * 1000L);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
        timer.cancel();
        timer = null;
    }
}
