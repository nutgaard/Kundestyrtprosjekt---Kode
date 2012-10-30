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
import no.ntnu.kpro.core.helpers.EnumHelper;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessageType;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Administrator
 */
public class InstantMessageActivity extends WrapperActivity implements View.OnClickListener, NetworkService.Callback {

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
    public void onRestart() {
        super.onRestart();
        updateFields();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFields();
    }

    private void updateFields() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String from = sharedPrefs.getString("standard_receiver", "kprothales@gmail.com");
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

        String type = sharedPrefs.getString("standard_type", "Operation");
        TextView lblType = (TextView) findViewById(R.id.lblType);
        lblType.setText(type);
    }

    public void onClick(View view) {
        String lblReceiver = ((TextView) findViewById(R.id.lblReceiver)).getText().toString();
        String lblLabel = ((TextView) findViewById(R.id.lblSecurityLabel)).getText().toString();
        String lblPriority = ((TextView) findViewById(R.id.lblPriority)).getText().toString();
        String lblType = ((TextView) findViewById(R.id.lblType)).getText().toString();
        Log.i(TAG, lblLabel);
        XOMessageSecurityLabel secLabel = EnumHelper.getEnumValue(XOMessageSecurityLabel.class, lblLabel);
        XOMessagePriority priority = EnumHelper.getEnumValue(XOMessagePriority.class, lblPriority);
        XOMessageType type = EnumHelper.getEnumValue(XOMessageType.class, lblType);
        String text = ((EditText) findViewById(R.id.txtMessage)).getText().toString();
        String subject = "";
        if (text != null && text.length() > 0) {
            subject = text.length() >= 60 ? text.substring(0, 59) : text;
        } else {
            text = "";
            subject = "";
        }
        if (text == null) {
            subject = "";
        }

        XOMessage m = new XOMessage("MyMailAddress@gmail.com", lblReceiver, subject, text, secLabel, priority, type, new Date());
        getServiceProvider().getNetworkService().send(m);
    }

    @Override
    public void mailSent(XOMessage message, Address[] invalidAddress) {
        super.mailSent(message, invalidAddress);
        Log.i(TAG, "Trying to reset textfield");
        runOnUiThread(new Runnable() {
            public void run() {
                EditText txtMessage = (EditText) findViewById(R.id.txtMessage);
                txtMessage.setText("");
            }
        });

    }
}
