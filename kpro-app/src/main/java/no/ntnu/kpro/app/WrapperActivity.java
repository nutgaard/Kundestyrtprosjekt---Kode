/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import no.ntnu.kpro.core.service.ServiceProvider;
import org.omg.CosNaming.Binding;

/**
 *
 * @author Nicklas
 */
public class WrapperActivity extends Activity {
    private ServiceProvider serviceProvider;
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName cn, IBinder ib) {
            ServiceProvider.LocalBinder lb = (ServiceProvider.LocalBinder)ib;
            serviceProvider = lb.getService();
        }

        public void onServiceDisconnected(ComponentName cn) {
            serviceProvider = null;
        }
    };
    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Needs some intent to bind to serice
//        bindService(new Intent(Binding.this, ServiceProvider.class), mConnection, Context.BIND_AUTO_CREATE);
    }
}
