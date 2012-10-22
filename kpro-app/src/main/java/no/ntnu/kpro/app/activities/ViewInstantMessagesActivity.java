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
import android.widget.Toast;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.core.model.XOMessage;
/**
 *
 * @author Administrator
 */
public class ViewInstantMessagesActivity extends WrapperActivity{
    
    private static final String TAG = "KPRO";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String xoMessage = readInstantMessages();
        setContentView(R.layout.main);
        Intent serviceIntent = new Intent("no.ntnu.kpro.core.service.ServiceProvider");
        // Create simple list of the menu choices
        String[] views = {xoMessage};
        ListView lstMenuChoices = (ListView) findViewById(R.id.lstMenuChoices);
        lstMenuChoices.setAdapter(new ArrayAdapter<String>(ViewInstantMessagesActivity.this, android.R.layout.simple_list_item_1, views));

        // Add click listener to the menu list
        lstMenuChoices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View view, int i, long l) {
                 Toast message = Toast.makeText(ViewInstantMessagesActivity.this, "DU SENDTE MEG OMG.", Toast.LENGTH_SHORT);
            }
        });
    }
        private String readInstantMessages() {
        try {
            FileInputStream fin = openFileInput("mail.txt");
            InputStreamReader isReader = new InputStreamReader(fin);
            char[] buffer = new char[300];
            // Fill the buffer with data from file
            isReader.read(buffer);
            return new String(buffer);
        } catch (Exception e) {
            Log.i("ReadNWrite, readFile()", "Exception e = " + e);
            return null;
        }
    }
}
