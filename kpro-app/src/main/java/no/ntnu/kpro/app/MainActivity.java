/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 *
 * @author Nicklas
 */
public class MainActivity extends WrapperActivity {

    private static final String TAG = "KPRO";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Activity started");
        setContentView(R.layout.main);

        Log.i(TAG, "Calling new intent to start service");
        Intent serviceIntent = new Intent("no.ntnu.kpro.core.service.ServiceProvider");
        Log.i(TAG, "ServiceAction: " + serviceIntent.getAction());
        Log.i(TAG, "Service: " + serviceIntent.toString());
        Log.i(TAG, "StartService: " + startService(serviceIntent));
        Log.i(TAG, "No errors, service should be running");

        Button b = (Button) findViewById(R.id.login_button);

        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText usernameText = (EditText)findViewById(R.id.username);
                String username = usernameText.getText().toString();
                EditText passwordText = (EditText)findViewById(R.id.password);
                String password = passwordText.getText().toString();
                if (checkLogin(username, password)) {
                    Intent myIntent = new Intent(view.getContext(), InboxActivity.class);
                    startActivityForResult(myIntent, 0);
                }
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

    public boolean checkLogin(String email, String password) {
        if (email.equalsIgnoreCase("123") && password.equalsIgnoreCase("123")) {
            return true;
        } else {
            return false;
        }
    }
}
