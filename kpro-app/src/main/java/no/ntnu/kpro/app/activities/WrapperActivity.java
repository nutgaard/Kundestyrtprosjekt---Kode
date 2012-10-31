/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.app.ActivityGroup;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Collections;
import java.util.LinkedList;

import java.util.List;
import javax.mail.Address;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.service.ServiceProvider;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas & Ida
 */
public abstract class WrapperActivity extends ActivityGroup implements NetworkService.Callback {

    private static List<String> dialogId = Collections.synchronizedList(new LinkedList<String>());
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
        getApplicationContext().startService(new Intent(this, ServiceProvider.class));
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


    public void mailSent(IXOMessage message, Address[] invalidAddress) {
        Log.i("KPRO-GUI-WRAPPER", "Her er den sendt");
        runOnUiThread(new Runnable() {

            public void run() {
                Toast.makeText(WrapperActivity.this, "Message sent", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void mailSentError(IXOMessage message, Exception ex) {
        runOnUiThread(new Runnable() {

            public void run() {
                Toast.makeText(WrapperActivity.this, "Message sending error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void mailReceived(IXOMessage message) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(WrapperActivity.this, "1 new message", Toast.LENGTH_LONG).show();
            }
        });

        Log.i("KPRO-GUI-WRAPPER", this.getLocalClassName());
        //Check if visible to limit to one
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
        String topActivity = services.get(0).topActivity.toString();
        String pref = "ComponentInfo{no.ntnu.kpro.app/no.ntnu.kpro.app." + this.getLocalClassName() + "}";
        Log.i("KPRO-GUI-WRAPPER", pref + "=" + topActivity);

        //
        if (topActivity.equals("ComponentInfo{no.ntnu.kpro.app/no.ntnu.kpro.app." + this.getLocalClassName() + "}")) {
            final IXOMessage recMessage = message;
            runOnUiThread(new Runnable() {

                public void run() {

                    XOMessagePriority priority = recMessage.getPriority();
                    Log.i("KPRO-GUI", priority.toString());
                    if (priority.equals(XOMessagePriority.OVERRIDE) || priority.equals(XOMessagePriority.FLASH)) {

                        final Dialog dialog = new Dialog(WrapperActivity.this);
                        dialog.setContentView(R.layout.dialog_flash_override);

                        dialog.setTitle("XOXOmail: Important message!");

                        dialog.setCancelable(true);

                        TextView lblSubject = (TextView) dialog.findViewById(R.id.lblInstSubject);
                        lblSubject.setText("Subject: " + recMessage.getSubject());

                        TextView lblText = (TextView) dialog.findViewById(R.id.lblInstText);
                        String text = recMessage.getStrippedBody().length() > 60 ? recMessage.getStrippedBody().substring(0, 59) : recMessage.getStrippedBody();
                        lblText.setText("Text: " + text);

                        Button btnOpen = (Button) dialog.findViewById(R.id.btnOpen);
                        btnOpen.setOnClickListener(new View.OnClickListener() {

                            public void onClick(View view) {
                                Log.i("KPRO-GUI", "Clicking open");
                                Intent i = new Intent(getApplicationContext(), MessageViewActivity.class);
                                // sending data to new activity

                                i.putExtra("folder", "Inbox");
                                i.putExtra("message", recMessage);
                                Log.i("KPRO-GUI", "Opening message " + recMessage.toString());
                                recMessage.setOpened(true);
                                startActivity(i);
                                dialog.dismiss();
                            }
                        });

                        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                        btnCancel.setOnClickListener(new View.OnClickListener() {

                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                }
            });
        }

    }

    public void mailReceivedError(Exception ex) {
        runOnUiThread(new Runnable() {

            public void run() {
                Toast.makeText(WrapperActivity.this, "Message recieve error", Toast.LENGTH_LONG).show();
            }
        });
    }
}
