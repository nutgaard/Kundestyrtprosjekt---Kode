/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import no.ntnu.kpro.app.R;

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

        // Create simple list of the menu choices
        String[] views = {"Folders", "SendMessage", "MainTab","Login"};
        ListView lstMenuChoices = (ListView) findViewById(R.id.lstMenuChoices);
        lstMenuChoices.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, views));

        // Add click listener to the menu list
        lstMenuChoices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View view, int i, long l) {
                try{
                    Class newClass = Class.forName("no.ntnu.kpro.app.activities." + av.getItemAtPosition(i) + "Activity");
                    Intent intent = new Intent(MainActivity.this, newClass);
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                }
            }
        });
    }

    //TODO: Check username/password later
    public boolean checkLogin(String email, String password) {
        if (email.equalsIgnoreCase("123") && password.equalsIgnoreCase("123")) {
            return true;
        } else {
            return false;
        }
    }
}
