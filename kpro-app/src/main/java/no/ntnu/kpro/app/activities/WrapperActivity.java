/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import javax.mail.Address;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.ServiceProvider;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas & Ida
 */
public abstract class WrapperActivity extends ActivityGroup implements NetworkService.Callback{

    protected ServiceProvider mServiceProvider;
    private ServiceConnection mConnection;

    public boolean isConnected() {
        return this.mServiceProvider != null;
    }

    public ServiceProvider getServiceProvider() {
        if (!isConnected()) {
            return null;
        }
        return mServiceProvider;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Needs some intent to bind to serice
        mConnection = newServiceConnection();
        getApplicationContext().bindService(new Intent(this, ServiceProvider.class), mConnection, Service.BIND_AUTO_CREATE);
    }

    public void onServiceConnected(ServiceProvider serviceProvider) {
        Log.d(this.getClass().getName(), "ServiceConnected to " + this.getClass().getName());
        this.mServiceProvider = serviceProvider;
        serviceProvider.addListener(this);
    }

    public void onServiceDisconnected(ServiceProvider serviceProvider) {
        Log.d(this.getClass().getName(), "ServiceDisconnected from " + this.getClass().getName());
        if (serviceProvider != null) {
            serviceProvider.removeListener(this);
        }
        this.mServiceProvider = null;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getApplicationContext().unbindService(mConnection);
        onServiceDisconnected(mServiceProvider);
        super.onDestroy();
    }

    private ServiceConnection newServiceConnection() {
        return new ServiceConnection() {

            public void onServiceConnected(ComponentName cn, IBinder ib) {
                ServiceProvider.LocalBinder lb = (ServiceProvider.LocalBinder) ib;
                WrapperActivity.this.onServiceConnected(lb.getService());
            }

            public void onServiceDisconnected(ComponentName cn) {
                WrapperActivity.this.onServiceDisconnected(mServiceProvider);
            }
        };
    }
    
    public void mailSent(XOMessage message, Address[] invalidAddress) {
        runOnUiThread(new Runnable() {

            public void run() {
                Toast.makeText(WrapperActivity.this, "Message sent", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void mailSentError(XOMessage message, Exception ex) {
        runOnUiThread(new Runnable() {

            public void run() {
                Toast.makeText(WrapperActivity.this, "Message sending error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void mailReceived(XOMessage message) {
        runOnUiThread(new Runnable() {

            public void run() {
                Toast.makeText(WrapperActivity.this, "1 new message", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void mailReceivedError(Exception ex) {
        runOnUiThread(new Runnable() {

            public void run() {
                Toast.makeText(WrapperActivity.this, "Message recieve error", Toast.LENGTH_LONG).show();
            }
        });
    }
}
