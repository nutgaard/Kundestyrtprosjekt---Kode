/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import no.ntnu.kpro.core.model.ModelProxy.IUser;
import no.ntnu.kpro.core.model.User;
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
        startForeground(START_STICKY, new Notification());
        ServiceProvider.instance = this;
//        threadpool = new ThreadPoolExecutor(3, 100, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        this.persistenceService = PersistenceServiceFactory.createService();
        this.HALService = HALServiceFactory.createService();
        this.networkService = NetworkServiceFactory.createService(getApplicationContext());
        this.securityService = SecurityServiceFactory.createService();
        CryptoHandler.setDefaultMailcap(); //tell java mail how to handle security
        Log.i(TAG, "Service starting");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ServiceProvider.instance = null;
        this.networkService.close();
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
    public void addListener(Activity activity){
        for (Class cls : activity.getClass().getInterfaces()){
            if (cls.equals(NetworkService.Callback.class) && networkService != null){
                networkService.addListener((NetworkService.Callback)activity);
            }
            if (cls.equals(PersistenceService.Callback.class) && persistenceService != null){
                persistenceService.addListener((PersistenceService.Callback)activity);
            }
            if (cls.equals(SecurityService.Callback.class) && securityService != null){
                securityService.addListener((SecurityService.Callback)activity);
            }
            if (cls.equals(HALService.Callback.class) && HALService != null){
                HALService.addListener((HALService.Callback)activity);
            }
        }
    }
    public void removeListener(Activity activity){
        for (Class cls : activity.getClass().getInterfaces()){
            if (cls.equals(NetworkService.Callback.class) && networkService != null){
                networkService.removeListener((NetworkService.Callback)activity);
            }
            if (cls.equals(PersistenceService.Callback.class) && persistenceService != null){
                persistenceService.removeListener((PersistenceService.Callback)activity);
            }
            if (cls.equals(SecurityService.Callback.class) && securityService != null){
                securityService.removeListener((SecurityService.Callback)activity);
            }
            if (cls.equals(HALService.Callback.class) && HALService != null){
                HALService.removeListener((HALService.Callback)activity);
            }
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

    //Can to this because Service is implicit singleton
    public static ServiceProvider getInstance() {
        return instance;
    }
    
    public IUser login(IUser user){
        return user;
    }
}
