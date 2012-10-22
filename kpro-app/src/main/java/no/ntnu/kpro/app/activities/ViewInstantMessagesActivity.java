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
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;

/**
 *
 * @author Administrator
 */
public class ViewInstantMessagesActivity extends WrapperActivity {

    private static final String TAG = "KPRO";
    String to;
    String from;
    String subject;
    String message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String xoMessages = readInstantMessages();


        setContentView(R.layout.main);
        Intent serviceIntent = new Intent("no.ntnu.kpro.core.service.ServiceProvider");
        // Create simple list of the menu choices
        String[] messages = messageSplitter(xoMessages);
        String[] views = new String[10];
        for (int i = 0; i < messages.length - 1; i++) {
            String m = messages[i];
            Log.d("Message", m);
            String[] temp = splitter(m);
            to = sendSplitter(temp[1])[1];
            from = sendSplitter(temp[2])[1];
            subject = sendSplitter(temp[3])[1];
            message = sendSplitter(temp[4])[1];
            Log.d("medlingtokens", to + from + subject + message);
            views[i] = temp[0];
        }

        ListView lstMenuChoices = (ListView) findViewById(R.id.lstMenuChoices);
        lstMenuChoices.setAdapter(new ArrayAdapter<String>(ViewInstantMessagesActivity.this, android.R.layout.simple_list_item_1, views));

        // Add click listener to the menu list
        lstMenuChoices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View view, int i, long l) {
                XOMessage test = new XOMessage(from, to, subject, message, XOMessageSecurityLabel.UGRADERT);
                getServiceProvider().getNetworkService().send(test);
                Toast message = Toast.makeText(ViewInstantMessagesActivity.this, "DU SENDTE MEG MANN.", Toast.LENGTH_SHORT);
                message.show();

            }
        });
    }

    private String readInstantMessages() {
        try {
            FileInputStream fin = openFileInput("mail.txt");
            InputStreamReader isReader = new InputStreamReader(fin);
            char[] buffer = new char[1000];
            // Fill the buffer with data from file
            isReader.read(buffer);
            return new String(buffer);
        } catch (Exception e) {
            Log.i("ReadNWrite, readFile()", "Exception e = " + e);
            return null;
        }
    }

    private String[] splitter(String s) {
        String delims = "[,]";
        String[] tokens = s.split(delims);
        return tokens;
    }

    private String[] messageSplitter(String s) {
        String delims = "[#]";
        String[] tokens = s.split(delims);
        return tokens;
    }

    private String[] sendSplitter(String s) {
        String delims = "[=]";
        String[] tokens = s.split(delims);
        return tokens;
    }
}
