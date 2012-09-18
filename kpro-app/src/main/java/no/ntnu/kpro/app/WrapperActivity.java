/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import no.ntnu.kpro.core.service.ServiceProvider;

/**
 *
 * @author Nicklas
 */
public class WrapperActivity extends Activity {

    protected ServiceProvider mServiceProvider;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName cn, IBinder ib) {
            ServiceProvider.LocalBinder lb = (ServiceProvider.LocalBinder) ib;
            WrapperActivity.this.onServiceConnected(lb.getService());
        }

        public void onServiceDisconnected(ComponentName cn) {
            WrapperActivity.this.onServiceDisconnected(mServiceProvider);
        }
    };
    public boolean isConnected() {
        return this.mServiceProvider != null;    
    }
    public ServiceProvider getServiceProvider() {
        if (!isConnected())return null;
        return mServiceProvider;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Needs some intent to bind to serice
        bindService(new Intent(this, ServiceProvider.class), mConnection, Service.BIND_AUTO_CREATE);
    }
    public void onServiceConnected(ServiceProvider serviceProvider) {
        Log.d(this.getClass().getName(), "ServiceConnected");
        this.mServiceProvider = serviceProvider;
    }
    public void onServiceDisconnected(ServiceProvider serviceProvider) {
        Log.d(this.getClass().getName(), "ServiceDisconnected");
        this.mServiceProvider = null;
    }
}
