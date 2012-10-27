/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    final static String TAG = "KPRO-GUI-MESSAGEVIEW";
    Box messages;
    XOMessage currentMessage;
    String folder = "Inbox";
    Button btnPrevious;
    Button btnNext;
    Button btnReply;
    Button btnForward;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting onCreate");
        
        Intent i = getIntent();
        // Get what folder you came from and set the correct layout
        folder = i.getStringExtra("folder");
        if (folder.equals("Inbox")) {
            setContentView(R.layout.message_item_in);
        } else if (folder.equals("Sent")) {
            setContentView(R.layout.message_item_out);
        }
        Log.i(TAG, "Setting content view based on folder choice");
        
        // Get the selected message and update the view
        currentMessage = i.getParcelableExtra("message");
        Log.i(TAG, "Fetching current message from parcelable");
        
        Log.i(TAG, "Calling elements from id");
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnReply = (Button) findViewById(R.id.btnReply);
        btnForward = (Button) findViewById(R.id.btnForward);

        Log.i(TAG, "Updating views and enabling buttons");
        updateViews();
        enableButtons();
    }

    // Create "popup" menu (shows by pressing MENU button) based on layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu_message , menu);
        return true;
    }

    // Handler for pressing the popup menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_delete:
                Log.i(TAG, "Delete message pressed");
                Toast.makeText(this, "Fake deleting message", Toast.LENGTH_LONG).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    // Get the previous message
    private void getPreviousMessage() {
        Log.i(TAG, "Getting previous message");
        enableButtons();
        if (messages.getPrevious(currentMessage) != null) {
            XOMessage newM = (XOMessage) messages.getPrevious(currentMessage);
            currentMessage = newM;
            currentMessage.setOpened(true);
            if (messages.getPrevious(currentMessage) == null) {
                btnPrevious.setEnabled(false);
            }
            Log.i(TAG, "Found previous message and set buttons state");
        }
    }

    // Get the next message
    private void getNextMessage() {
        Log.i(TAG, "Getting next message");
        enableButtons();
        if (messages.getNext(currentMessage) != null) {
            XOMessage newM = (XOMessage) messages.getNext(currentMessage);
            currentMessage = newM;
            currentMessage.setOpened(true);
            if (messages.getNext(currentMessage) == null) {
                btnNext.setEnabled(false);
            }
            Log.i(TAG, "Found next message and set buttons state");
        }
    }

    // Enable the prev/next buttons
    private void enableButtons() {
        btnNext.setEnabled(true);
        btnPrevious.setEnabled(true);
    }

    // Update the current message and the fields corresponding to the message
    private void updateViews() {
        Log.i(TAG, "Starting update of views according to current message");
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
        
        Button btnAttachments = (Button) findViewById(R.id.btnAttachments);
        btnAttachments.setVisibility(View.GONE);
        
        // Subject
        String subject = currentMessage.getSubject();
        TextView lblSubject = (TextView) findViewById(R.id.lblSubject);
        lblSubject.setText(subject);

        // Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        Date date = currentMessage.getDate();
        TextView lblDate = (TextView) findViewById(R.id.lblDate);
        lblDate.setText(dateFormat.format(date));
        
        // Text
        String text = currentMessage.getStrippedBody();
        TextView lblText = (TextView) findViewById(R.id.lblText);
        lblText.setText(text);

        // Security label
        TextView lblSecurityLabel = (TextView) findViewById(R.id.lblSecurityLabel);
        XOMessageSecurityLabel label = currentMessage.getGrading();
        lblSecurityLabel.setText(label.toString());

        if (label.equals(XOMessageSecurityLabel.UGRADERT) || label.equals(XOMessageSecurityLabel.UNCLASSIFIED) || label.equals(XOMessageSecurityLabel.NATO_UNCLASSIFIED)) {
            lblSecurityLabel.setTextColor(getResources().getColor(R.color.SIOLabelBlack));
        } else {
            lblSecurityLabel.setTextColor(getResources().getColor(R.color.SIOLabelRed));
        }

        // Priority
        XOMessagePriority priority = currentMessage.getPriority();
        TextView lblPriority = (TextView) findViewById(R.id.lblPriority);
        lblPriority.setText(priority.toString());

        // Type
        XOMessageType type = currentMessage.getType();
        TextView lblType = (TextView) findViewById(R.id.lblType);
        lblType.setText(type.toString());
        Log.i(TAG, "Finished updating views");
    }

    // 
    @Override
    public void onServiceConnected(ServiceProvider sp) {
        super.onServiceConnected(sp);
        Log.i(TAG, "Service is connected");
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
        Log.i(TAG, "Adding button click listeners to previous/next/reply/forward");
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