/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.app.activities.WrapperActivity;

/**
 *
 * @author Administrator
 */
public class InstantMessageActivity extends WrapperActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.instant_message);

        Button createNewIM = (Button) findViewById(R.id.createInstantMessage);
        Button viewIM = (Button) findViewById(R.id.viewInstantMessages);

        createNewIM.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                
                Toast.makeText(getApplicationContext(), "LAG INSTAMESSAGE!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), CreateInstantMessageActivity.class);
                startActivity(i);

            }
        });
        viewIM.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "SE INSTAMESSAGES!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), ViewInstantMessagesActivity.class);
                startActivity(i);
            }
        });

    }
}
