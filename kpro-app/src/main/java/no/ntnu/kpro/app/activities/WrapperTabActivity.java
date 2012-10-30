/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.app.ActivityGroup;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import javax.mail.Address;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.service.ServiceProvider;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas & Ida
 */
public class WrapperTabActivity extends ActivityGroup implements NetworkService.Callback {

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
        bindService(new Intent(this, ServiceProvider.class), mConnection, Service.BIND_AUTO_CREATE);
    }

    public void onServiceConnected(ServiceProvider serviceProvider) {
        Log.d(this.getClass().getName(), "ServiceConnected to " + this.getClass().getName());
        this.mServiceProvider = serviceProvider;
    }

    public void onServiceDisconnected(ServiceProvider serviceProvider) {
        Log.d(this.getClass().getName(), "ServiceDisconnected from " + this.getClass().getName());
        this.mServiceProvider = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        onServiceDisconnected(mServiceProvider);
        super.onDestroy();
    }

    private ServiceConnection newServiceConnection() {
        return new ServiceConnection() {
            public void onServiceConnected(ComponentName cn, IBinder ib) {
                ServiceProvider.LocalBinder lb = (ServiceProvider.LocalBinder) ib;
                WrapperTabActivity.this.onServiceConnected(lb.getService());
            }

            public void onServiceDisconnected(ComponentName cn) {
                WrapperTabActivity.this.onServiceDisconnected(mServiceProvider);
            }
        };
    }

    public void mailSent(XOMessage message, Address[] invalidAddress) {
    }

    public void mailSentError(XOMessage message, Exception ex) {
    }

    public void mailReceived(XOMessage message) {
        /*Log.i("KPRO-GUI-WRAPPER", this.getLocalClassName());
        final XOMessage recMessage = message;
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(WrapperTabActivity.this, "1 new message", Toast.LENGTH_LONG).show();
                XOMessagePriority priority = recMessage.getPriority();
                Log.i("KPRO-GUI", priority.toString());
                //Log.i("KPRO-GUI", recMessage.getSubject());
                //Log.i("KPRO-GUI", recMessage.getStrippedBody());
                if (priority.equals(XOMessagePriority.OVERRIDE) || priority.equals(XOMessagePriority.FLASH)) {

                    final Dialog dialog = new Dialog(WrapperTabActivity.this);
                    dialog.setContentView(R.layout.dialog_flash_override);
                    dialog.setTitle("XOXOmail: Important message received!");
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
        });*/
    }

    public void mailReceivedError(Exception ex) {
    }
}
