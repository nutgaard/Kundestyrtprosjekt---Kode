/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;

/**
 *
 * @author Kristin
 */
public class MessageOperationActivity extends SendMessageActivity {

    private EditText txtMessageReceiver;
    private EditText txtSubject;
    private Spinner sprSecurityLabel;
    private Spinner sprPriority;
    private Spinner sprType;
    private EditText txtMessage;
    private Button btnAddAttachment;
    private Button btnSend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        String mode = i.getStringExtra("mode");
        IXOMessage message = i.getParcelableExtra("message");
        

        String receiver = message.getFrom();
        if (receiver.contains("<")) {
            String[] temp = receiver.split("<");
            if(temp[1].contains(">")){
                String[] temp2 = temp[1].split(">");
                receiver = temp2[0];
            }
        }


        //Find all buttons and spinners
        txtMessageReceiver = (EditText) findViewById(R.id.txtMessageReceiver);
        if(mode.equals("reply")){
            txtMessageReceiver.setText(receiver);
        }
        txtSubject = (EditText) findViewById(R.id.txtSubject);
        if(mode.equals("reply")){
            txtSubject.setText("Re: " + message.getSubject());
        }
        else if(mode.equals("forward")){
            txtSubject.setText("Fwd: " + message.getSubject());
        }
        
        sprSecurityLabel = (Spinner) findViewById(R.id.sprSecurityLabel);
        for (int j = 0; j < sprSecurityLabel.getCount(); j++) {
            if (sprSecurityLabel.getItemAtPosition(j).toString().equals(message.getGrading().toString())) {
                sprSecurityLabel.setSelection(j);
            }
        }
        sprSecurityLabel.setEnabled(false);
        sprPriority = (Spinner) findViewById(R.id.sprPriority);
        for (int j = 0; j < sprPriority.getCount(); j++) {
            if (sprPriority.getItemAtPosition(j).toString().equals(message.getPriority().toString())) {
                sprPriority.setSelection(j);
            }
        }
        sprType = (Spinner) findViewById(R.id.sprType);
        for (int j = 0; j < sprType.getCount(); j++) {
            if (sprType.getItemAtPosition(j).toString().equals(message.getType().toString())) {
                sprType.setSelection(j);
            }
        }
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        txtMessage.setText("\n\n" + message.getFrom() + " wrote: \n\n" + message.getStrippedBody());
    }
    
}
