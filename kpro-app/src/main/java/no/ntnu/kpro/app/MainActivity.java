/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import no.ntnu.kpro.core.service.factories.NetworkServiceFactory;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas
 */
public class MainActivity extends Activity {
    private static final String TAG = "KPRO";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Activity started");
        setContentView(R.layout.main);
        
        Log.i(TAG, "Calling new intent to start service");
        Intent serviceIntent = new Intent("no.ntnu.kpro.core.service.ServiceProvider");
        Log.i(TAG, "ServiceAction: "+serviceIntent.getAction());
        Log.i(TAG, "Service: "+serviceIntent.toString());
        Log.i(TAG, "StartService: "+startService(serviceIntent));
        Log.i(TAG, "No errors, service should be running");
        
        Button b = (Button)findViewById(R.id.login_button);
        
        b.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
               Intent myIntent = new Intent(view.getContext(), SendMessageActivity.class);
               startActivityForResult(myIntent, 0);
            }
        });
        
        /*
        Button b = (Button)findViewById(R.id.morse);
        b.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Log.i(TAG, "Button clicked, sending mail");
                NetworkService ns = NetworkServiceFactory.createService();
                ns.sendMail("Testmail", "This is the testBody", "nutgaard@gmail.com", "nutgaard@gmail.com");
            }
        });*/
    }
}
