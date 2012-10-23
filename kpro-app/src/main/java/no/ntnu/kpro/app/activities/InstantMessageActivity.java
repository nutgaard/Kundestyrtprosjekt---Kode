/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.mail.Address;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.app.activities.WrapperActivity;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessageType;

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

        SharedPreferences sharedPrefs = getPreferences(MODE_PRIVATE);

        String from = sharedPrefs.getString("standard_receiver", "");
        TextView lblFrom = (TextView) findViewById(R.id.lblFrom);
        lblFrom.setText(from);
       
        String securityLabel = sharedPrefs.getString("standard_security_label", "UNCLASSIFIED");
        TextView lblSecurityLabel = (TextView) findViewById(R.id.lblSecurityLabel);
        lblSecurityLabel.setText(securityLabel);

        String priority = sharedPrefs.getString("standard_priority", "Override");
        TextView lblPriority = (TextView) findViewById(R.id.lblPriority);
        lblPriority.setText(priority);

        String type = sharedPrefs.getString("standard_type", "Operation");
        TextView lblType = (TextView) findViewById(R.id.lblType);
        lblType.setText(type);
    }
//        Button createNewIM = (Button) findViewById(R.id.createInstantMessage);
//        Button viewIM = (Button) findViewById(R.id.viewInstantMessages);
//
//        createNewIM.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                
//                Toast.makeText(getApplicationContext(), "LAG INSTAMESSAGE!", Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(getApplicationContext(), CreateInstantMessageActivity.class);
//                startActivity(i);
//
//            }
//        });
//        viewIM.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "SE INSTAMESSAGES!", Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(getApplicationContext(), ViewInstantMessagesActivity.class);
//                startActivity(i);
//            }
//        });
}
