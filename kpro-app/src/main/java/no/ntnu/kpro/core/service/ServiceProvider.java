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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import no.ntnu.kpro.core.service.factories.HALServiceFactory;
import no.ntnu.kpro.core.service.factories.NetworkServiceFactory;
import no.ntnu.kpro.core.service.factories.PersistenceServiceFactory;
import no.ntnu.kpro.core.service.interfaces.HALService;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.service.interfaces.PersistenceService;

/**
 *
 * @author Nicklas
 */
public class ServiceProvider extends Service {
    private static ServiceProvider instance;
    
    private IBinder mBinder = new LocalBinder();
    private static final String TAG = "KPRO";
    public static ThreadPoolExecutor threadpool;
    private Activity currentActivity;
    private PersistenceService persistenceService;
    private HALService HALService;
    private NetworkService networkService;
            

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceProvider.instance = this;
        threadpool = new ThreadPoolExecutor(3, 100, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        this.persistenceService = PersistenceServiceFactory.createService();
        this.HALService = HALServiceFactory.createService();
        this.networkService = NetworkServiceFactory.createService();
        Log.i(TAG, "Service starting");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (this.mBinder == null) {
            this.mBinder = new LocalBinder();
        }

        return this.mBinder;
    }

    public class LocalBinder extends Binder {

        public ServiceProvider getService() {
            return ServiceProvider.this;
        }
    }

    public PersistenceService getPersistenceService() {
        return persistenceService;
    }

    public HALService getHALService() {
        return HALService;
    }

    public NetworkService getNetworkService() {
        return networkService;
    }

    public void register(Activity activity) {
        this.currentActivity = activity;
    }

    public void unregister(Activity activity) {
        if (activity == this.currentActivity) {
            this.currentActivity = null;
        }
    }
    public Activity getCurrentActivity() {
        return this.currentActivity;
    }
    public static ServiceProvider getInstance() {
        return instance;
    }
}
