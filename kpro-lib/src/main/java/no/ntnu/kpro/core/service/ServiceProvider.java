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
import java.security.Security;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import no.ntnu.kpro.core.service.factories.HALServiceFactory;
import no.ntnu.kpro.core.service.factories.NetworkServiceFactory;
import no.ntnu.kpro.core.service.factories.PersistenceServiceFactory;
import no.ntnu.kpro.core.service.factories.SecurityServiceFactory;
import no.ntnu.kpro.core.service.implementation.NetworkService.crypto.CryptoHandler;
import no.ntnu.kpro.core.service.interfaces.HALService;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.service.interfaces.PersistenceService;
import no.ntnu.kpro.core.service.interfaces.SecurityService;

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
    private SecurityService securityService;

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceProvider.instance = this;
        threadpool = new ThreadPoolExecutor(3, 100, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        this.persistenceService = PersistenceServiceFactory.createService();
        this.HALService = HALServiceFactory.createService();
        this.networkService = NetworkServiceFactory.createService();
        this.securityService = SecurityServiceFactory.createService();
        this.networkService.startIMAPIdle();
        this.networkService.getAllMessages();
        CryptoHandler.setDefaultMailcap(); //tell java mail how to handle security
        Log.i(TAG, "Service starting");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ServiceProvider.instance = null;
        this.networkService.stopIMAPIdle();
        Log.i(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (this.mBinder == null) {
            this.mBinder = new LocalBinder();
        }
        Log.d(this.getClass().getName(), "OnBind");
        return this.mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        boolean b = super.onUnbind(intent);
        this.currentActivity = null;
        Log.d(this.getClass().getName(), "OnUnbind");
        return b;
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

//    public void register(Activity activity) {
//        this.currentActivity = activity;
//        
//        if (activity instanceof NetworkService.Callback) {
//            this.getNetworkService().clearListeners();
//            this.getNetworkService().addListener((NetworkService.Callback)activity);
//        }
//        if (activity instanceof HALService.callback) {
//            this.getHALService().clearListeners();
//            this.getHALService().addListener((HALService.callback)activity);
//        }
//        if (activity instanceof PersistenceService.callback) {
//            this.getPersistenceService().clearListeners();
//            this.getPersistenceService().addListener((PersistenceService.callback)activity);
//        }
//    }
    //Can to this because Service is implicit singleton
    public static ServiceProvider getInstance() {
        return instance;
    }
}
