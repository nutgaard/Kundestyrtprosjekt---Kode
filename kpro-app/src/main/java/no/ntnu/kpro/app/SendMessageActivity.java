/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import javax.mail.Address;
import no.ntnu.kpro.core.helpers.EnumHelper;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageType;
import no.ntnu.kpro.core.service.ServiceProvider;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
/**
 *
 * @author Kristin
 */
public class SendMessageActivity extends WrapperActivity implements NetworkService.Callback{
    
    private Spinner sprSecurityLabel;
    private Spinner sprPriority;
    private Spinner sprType;
    
    private Button btnAddAttachment;
    private Button btnSend;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_message);
        
        //Find all buttons and spinners
        sprSecurityLabel =(Spinner) findViewById(R.id.sprSecurityLabel);
        sprPriority = (Spinner) findViewById(R.id.sprPriority);
        sprType = (Spinner) findViewById(R.id.sprType);
        
        btnAddAttachment = (Button) findViewById(R.id.btnAddAttachment);
        btnSend = (Button) findViewById(R.id.btnSend);
        
        //Add listener to the send button.
        addBtnSendClickListener(btnSend);        
        
        //Fill all spinners with data values
        populateSpinners();
    }
    
    public void populateSpinners(){
        EnumHelper.populateSpinnerWithEnumValues(sprSecurityLabel, this, XOMessageSecurityLabel.class);
        EnumHelper.populateSpinnerWithEnumValues(sprPriority, this, XOMessagePriority.class);
        EnumHelper.populateSpinnerWithEnumValues(sprType, this, XOMessageType.class);
    }
       
    @Override
    public void onServiceConnected(ServiceProvider serviceProvider) {
        super.onServiceConnected(serviceProvider);
//        getServiceProvider().register(this);
    }
    
    public void mailSent(XOMessage message, Address[] invalidAddress) {
         Toast confirm = Toast.makeText(SendMessageActivity.this, "Message sent", Toast.LENGTH_SHORT);
         confirm.show();
    }

    public void mailSentError(XOMessage message) {
         Toast errorMess = Toast.makeText(SendMessageActivity.this, "Something went wrong", Toast.LENGTH_SHORT);
         errorMess.show();
    }

    public void mailReceived(XOMessage message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mailReceivedError() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void addBtnSendClickListener(Button btnSend) {
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String txtReceiver = ((EditText) findViewById(R.id.txtMessageReceiver)).getText().toString();
                String txtSubject = ((EditText) findViewById(R.id.txtSubject)).getText().toString();
                String txtMessage = ((EditText) findViewById(R.id.txtMessage)).getText().toString();
                
                while (!isConnected()){
                    Thread.yield();
                }
                
                /**
                 * Parse selected values from spinners
                 **/
                
                //Parse security string
                String selectedSecurityString = (String) sprSecurityLabel.getSelectedItem();
                XOMessageSecurityLabel selectedSecurity = EnumHelper.getEnumValue(XOMessageSecurityLabel.class, selectedSecurityString);
               
                //Parse priority string
                String selectedPriorityString = (String) sprPriority.getSelectedItem();
                XOMessagePriority selectedPriority = EnumHelper.getEnumValue(XOMessagePriority.class, selectedPriorityString);
                        
                //Parse message type string
                String selectedTypeString = (String) sprType.getSelectedItem();
                XOMessageType selectedType = EnumHelper.getEnumValue(XOMessageType.class, selectedTypeString);                
                
                getServiceProvider().getNetworkService().sendMail(txtReceiver, txtSubject, txtMessage, selectedSecurity, selectedPriority, selectedType);
                
                
                //Is not necessary to have this when callback is implemented, as mailSent() will be called
                Toast confirm = Toast.makeText(SendMessageActivity.this, "Message sent.", Toast.LENGTH_SHORT);                
                confirm.show();
                
                finish();
            }
        });
        
        
    }
    
    

}
