/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import no.ntnu.kpro.app.activities.MessageOperationActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import no.ntnu.kpro.app.R;
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
    Button btnPrevious;
    Button btnNext;
    Button btnReply;
    Button btnForward;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        // Get what folder you came from and set the correct layout
        folder = i.getStringExtra("folder");
        if (folder.equals("Inbox")) {
            setContentView(R.layout.message_item_in);
        } else if (folder.equals("Sent")) {
            setContentView(R.layout.message_item_out);
        }
        // Get the selected message and update the view
        currentMessage = i.getParcelableExtra("message");

        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnReply = (Button) findViewById(R.id.btnReply);
        btnForward = (Button) findViewById(R.id.btnForward);

        updateViews();
        enableButtons();
    }

    // Get the previous message
    private void getPreviousMessage() {
        enableButtons();
        if (messages.getPrevious(currentMessage) != null) {
            XOMessage newM = (XOMessage) messages.getPrevious(currentMessage);
            currentMessage = newM;
            if (messages.getPrevious(currentMessage) == null) {
                btnPrevious.setEnabled(false);
            }
        }
    }

    // Get the next message
    private void getNextMessage() {
        enableButtons();
        if (messages.getNext(currentMessage) != null) {
            XOMessage newM = (XOMessage) messages.getNext(currentMessage);
            currentMessage = newM;
            if (messages.getNext(currentMessage) == null) {
                btnNext.setEnabled(false);
            }
        }
    }

    // Enable the prev/next buttons
    private void enableButtons() {
        btnNext.setEnabled(true);
        btnPrevious.setEnabled(true);
    }

    // Update the current message and the fields corresponding to the message
    private void updateViews() {
        // From
        if (folder.equals("Inbox")) {
            String from = currentMessage.getFrom();
            TextView lblFrom = (TextView) findViewById(R.id.lblFrom);
            lblFrom.setText(from);
        } else if (folder.equals("Sent")) {
            String to = currentMessage.getTo();
            TextView lblFrom = (TextView) findViewById(R.id.lblFrom);
            lblFrom.setText(to);
        }

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
            btnPrevious.setEnabled(false);
        }
        if (messages.getNext(currentMessage) == null) {
            btnNext.setEnabled(false);
        }

        addButtonClickListeners();

    }

    // Add button click listeners
    private void addButtonClickListeners() {
        // Add click listener to Previous button
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                getPreviousMessage();
                updateViews();
            }
        });

        // Add click listener to Next button
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                getNextMessage();
                updateViews();
            }
        });

        // Add click listener to Reply button
        btnReply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MessageOperationActivity.class);
                i.putExtra("message", currentMessage);
                i.putExtra("mode", "reply");
                startActivity(i);
            }
        });

        // Add click listener to Forward button
        btnForward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MessageOperationActivity.class);
                i.putExtra("message", currentMessage);
                i.putExtra("mode", "forward");
                startActivity(i);
            }
        });
    }
}