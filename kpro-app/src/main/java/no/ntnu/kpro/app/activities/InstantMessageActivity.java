/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Date;
import javax.mail.Address;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.core.utilities.EnumHelper;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessageType;

/**
 *
 * @author Administrator
 */
public class InstantMessageActivity extends WrapperActivity implements View.OnClickListener{
    static final String TAG = "KPRO-GUI-INSTANT";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.instant_message);

        updateFields();
        
        Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
    }
    
    @Override
    public void onRestart(){
       super.onRestart();
       updateFields();
    }
    
    @Override
    public void onResume(){
        super.onResume();
        updateFields();
    }
    
    private void updateFields(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String from = sharedPrefs.getString("standard_receiver", "");
        TextView lblFrom = (TextView) findViewById(R.id.lblReceiver);
        lblFrom.setText(from);
       
        String securityLabel = sharedPrefs.getString("standard_security_label", "UNCLASSIFIED");
        TextView lblSecurityLabel = (TextView) findViewById(R.id.lblSecurityLabel);
        lblSecurityLabel.setText(securityLabel);

        if (securityLabel.equals(XOMessageSecurityLabel.UGRADERT.toString()) || securityLabel.equals(XOMessageSecurityLabel.UNCLASSIFIED.toString()) || securityLabel.equals(XOMessageSecurityLabel.NATO_UNCLASSIFIED.toString())) {
            lblSecurityLabel.setTextColor(getResources().getColor(R.color.SIOLabelBlack));
        } else {
            lblSecurityLabel.setTextColor(getResources().getColor(R.color.SIOLabelRed));
        }
        
        String priority = sharedPrefs.getString("standard_priority", "Override");
        TextView lblPriority = (TextView) findViewById(R.id.lblPriority);
        lblPriority.setText(priority);

        String type = sharedPrefs.getString("standard_type", "Operations");
        TextView lblType = (TextView) findViewById(R.id.lblType);
        lblType.setText(type);
    }
    
    public void onClick(View view) {
        String lblReceiver = ((TextView)findViewById(R.id.lblReceiver)).getText().toString();
        String lblLabel = ((TextView)findViewById(R.id.lblSecurityLabel)).getText().toString();
        String lblPriority = ((TextView)findViewById(R.id.lblPriority)).getText().toString();
        String lblType = ((TextView)findViewById(R.id.lblType)).getText().toString();
        Log.i(TAG, lblLabel);
        XOMessageSecurityLabel secLabel = EnumHelper.getEnumValue(XOMessageSecurityLabel.class, lblLabel);
        XOMessagePriority priority = EnumHelper.getEnumValue(XOMessagePriority.class, lblPriority);
        XOMessageType type = EnumHelper.getEnumValue(XOMessageType.class, lblType);
        String text = ((EditText)findViewById(R.id.txtMessage)).getText().toString();
        String subject = text.length() >= 60 ? text.substring(0, 59) : text;
        
        IXOMessage m = new XOMessage("MyMailAddress@gmail.com", lblReceiver, subject, text, secLabel, priority, type, new Date());
        getServiceProvider().getNetworkService().send(m);
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

    @Override
    public void mailSent(IXOMessage message, Address[] invalidAddress) {
        super.mailSent(message, invalidAddress);
        EditText txtMessage = (EditText)findViewById(R.id.txtMessage);
        txtMessage.setText("");
    }
}
