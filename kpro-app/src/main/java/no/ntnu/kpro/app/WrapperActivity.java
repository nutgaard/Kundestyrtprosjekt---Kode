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
import no.ntnu.kpro.core.service.ServiceProvider;

/**
 *
 * @author Nicklas
 */
public class WrapperActivity extends Activity {

    private ServiceProvider serviceProvider;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName cn, IBinder ib) {
            ServiceProvider.LocalBinder lb = (ServiceProvider.LocalBinder) ib;
            serviceProvider = lb.getService();
            serviceProvider.register(WrapperActivity.this);
        }

        public void onServiceDisconnected(ComponentName cn) {
            serviceProvider = null;
            serviceProvider.unregister(WrapperActivity.this);
            
        }
    };

    public ServiceProvider getServiceProvider() {
        if (serviceProvider == null) {
            throw new RuntimeException("The fuck?? ServiceProvider == null");
        }
        return serviceProvider;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Needs some intent to bind to serice
        bindService(new Intent(this, ServiceProvider.class), mConnection, Service.BIND_AUTO_CREATE);
    }
}
