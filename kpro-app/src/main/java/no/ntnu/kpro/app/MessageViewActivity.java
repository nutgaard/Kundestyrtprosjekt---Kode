/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessageType;
import no.ntnu.kpro.core.service.ServiceProvider;

/**
 *
 * @author Kristin
 */
public class MessageViewActivity extends WrapperActivity {
    List<XOMessage> messages;
    XOMessage currentMessage;
    int index;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_item_in); //TODO: Change this if sent message

        Intent i = getIntent();
        index = i.getIntExtra("index", 0);

    }

    // Increases the index (limit at list size)
    private int increaseIndex(){
        return index<messages.size()-1 ? index++ : messages.size()-1;
    }
    
    //TODO: Disable buttons if index too small or big
    
    // Decrease the index (limit at 0)
    private int decreaseIndex(){
        return index>0 ? index-- : 0;
    }
    
    // Update the current message and the fields corresponding to the message
    private void updateViews(){
        currentMessage = messages.get(index);
        String from = currentMessage.getFrom();
        String subject = currentMessage.getSubject();
        String text = currentMessage.getStrippedBody();
        TextView v = (TextView) findViewById(R.id.lblFrom);
        v.setText(from);
        TextView v2 = (TextView) findViewById(R.id.lblSubject);
        v2.setText(subject);
        TextView v3 = (TextView) findViewById(R.id.lblText);
        v3.setText(text);
        TextView lblSecurityLabel = (TextView) findViewById(R.id.lblSecurityLabel);
        XOMessageSecurityLabel label = currentMessage.getGrading();
        lblSecurityLabel.setText(label.toString());
        if(label.equals(XOMessageSecurityLabel.UGRADERT) || label.equals(XOMessageSecurityLabel.UNCLASSIFIED) || label.equals(XOMessageSecurityLabel.NATO_UNCLASSIFIED)){
            lblSecurityLabel.setTextColor(getResources().getColor(R.color.black));
        }
        else{
            lblSecurityLabel.setTextColor(getResources().getColor(R.color.red));
        }
        XOMessagePriority priority = currentMessage.getPriority();
        TextView lblPriority = (TextView) findViewById(R.id.lblPriority);
        lblPriority.setText(priority.toString());
        XOMessageType type = currentMessage.getType();
        TextView lblType = (TextView) findViewById(R.id.lblType);
        lblType.setText(type.toString());
    }
    
    @Override
    public void onServiceConnected(ServiceProvider sp) {
        super.onServiceConnected(sp);
        
        // Fetch inbox messages, will be replaced by fetching messages from storage?
        // TODO: Find if the user is in inbox or sent.. 
        messages = sp.getNetworkService().getInbox();
        updateViews();
        
        // Add click listener to Previous button
        Button prev = (Button) findViewById(R.id.btnPrevious);
        prev.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                decreaseIndex();
                updateViews(); 
            }
        });
        
        // Add click listener to Next button
        Button next = (Button) findViewById(R.id.btnNext);
        next.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                increaseIndex();
                updateViews();
            }
        });
    }
}
