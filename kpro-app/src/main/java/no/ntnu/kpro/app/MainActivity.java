/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

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

        String[] views = {"Inbox", "Sent", "SendMessage"};
        ListView views_list = (ListView) findViewById(R.id.views_list);
        views_list.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, views));
        
        views_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> av, View view, int i, long l) {
                try{
                    Class newClass = Class.forName("no.ntnu.kpro.app." + av.getItemAtPosition(i) + "Activity");
                    Intent intent = new Intent(MainActivity.this, newClass);
                    startActivity(intent);
                }
                catch(ClassNotFoundException e){
                    
                }
            }
        });
        
       

        /*
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
         * /
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
