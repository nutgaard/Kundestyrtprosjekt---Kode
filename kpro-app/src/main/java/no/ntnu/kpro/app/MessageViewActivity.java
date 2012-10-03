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
import no.ntnu.kpro.core.model.Box;
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

    Box messages;
    XOMessage currentMessage;
    String folder = "Inbox";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        // Get what folder you came from and set the correct layout
        folder = i.getStringExtra("folder");
        if (folder.equals("Inbox")) {
            setContentView(R.layout.message_item_in);
        } else if (folder.equals("Outbox")) {
            setContentView(null);//TODO:
        } else if (folder.equals("Sent")) {
            setContentView(R.layout.message_item_out);
        }
        // Get the selected message and update the view
        currentMessage = i.getParcelableExtra("message");
        updateViews();
        enableButtons();
    }

    // Get the previous message
    private void getPreviousMessage() {
        enableButtons();
        if (messages.getPrevious(currentMessage) != null) {
            XOMessage newM = (XOMessage) messages.getPrevious(currentMessage);
            currentMessage = newM;
            Button btnPrev = (Button) findViewById(R.id.btnPrevious);
            if (messages.getPrevious(currentMessage) == null) {
                btnPrev.setEnabled(false);
            }
        }
    }

    // Get the next message
    private void getNextMessage() {
        enableButtons();
        if (messages.getNext(currentMessage) != null) {
            XOMessage newM = (XOMessage) messages.getNext(currentMessage);
            currentMessage = newM;
            Button btnNext = (Button) findViewById(R.id.btnNext);
            if (messages.getNext(currentMessage) == null) {
                btnNext.setEnabled(false);
            }
        }
    }

    // Enable the prev/next buttons
    private void enableButtons() {
        Button btnNext = (Button) findViewById(R.id.btnNext);
        Button btnPrev = (Button) findViewById(R.id.btnPrevious);
        btnNext.setEnabled(true);
        btnPrev.setEnabled(true);
    }

    // Update the current message and the fields corresponding to the message
    private void updateViews() {
        // From
        String from = currentMessage.getFrom();
        TextView lblFrom = (TextView) findViewById(R.id.lblFrom);
        lblFrom.setText(from);

        // Subject
        String subject = currentMessage.getSubject();
        TextView lblSubject = (TextView) findViewById(R.id.lblSubject);
        lblSubject.setText(subject);

        // Text
        String text = currentMessage.getStrippedBody();
        TextView lblText = (TextView) findViewById(R.id.lblText);
        lblText.setText(text);

        // Security label
        TextView lblSecurityLabel = (TextView) findViewById(R.id.lblSecurityLabel);
        XOMessageSecurityLabel label = currentMessage.getGrading();
        lblSecurityLabel.setText(label.toString());

        if (label.equals(XOMessageSecurityLabel.UGRADERT) || label.equals(XOMessageSecurityLabel.UNCLASSIFIED) || label.equals(XOMessageSecurityLabel.NATO_UNCLASSIFIED)) {
            lblSecurityLabel.setTextColor(getResources().getColor(R.color.black));
        } else {
            lblSecurityLabel.setTextColor(getResources().getColor(R.color.red));
        }

        // Priority
        XOMessagePriority priority = currentMessage.getPriority();
        TextView lblPriority = (TextView) findViewById(R.id.lblPriority);
        lblPriority.setText(priority.toString());

        // Type
        XOMessageType type = currentMessage.getType();
        TextView lblType = (TextView) findViewById(R.id.lblType);
        lblType.setText(type.toString());
    }

    // 
    @Override
    public void onServiceConnected(ServiceProvider sp) {
        super.onServiceConnected(sp);
        // Fetch messages acc to what folder you came from (storage later?)
        if (folder.equals("Inbox")) {
            messages = sp.getNetworkService().getInbox();
        } else if (folder.equals("Sent")) {
            messages = sp.getNetworkService().getOutbox();
        }

        // Disable buttons if no next/previous
        if (messages.getPrevious(currentMessage) == null) {
            Button btnPrev = (Button) findViewById(R.id.btnPrevious);
            btnPrev.setEnabled(false);
        }
        if (messages.getNext(currentMessage) == null) {
            Button btnNext = (Button) findViewById(R.id.btnNext);
            btnNext.setEnabled(false);
        }
        
        addButtonClickListeners();

    }

    // Add button click listeners
    private void addButtonClickListeners() {
        // Add click listener to Previous button
        Button prev = (Button) findViewById(R.id.btnPrevious);
        prev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                getPreviousMessage();
                updateViews();
            }
        });

        // Add click listener to Next button
        Button next = (Button) findViewById(R.id.btnNext);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                getNextMessage();
                updateViews();
            }
        });

        // Add click listener to Reply button
        Button reply = (Button) findViewById(R.id.btnReply);
    }
}