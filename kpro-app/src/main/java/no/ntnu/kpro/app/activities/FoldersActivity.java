/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.mail.Address;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.app.XOMessageAdapter;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.ServiceProvider;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Kristin
 */
public class FoldersActivity extends MenuActivity implements NetworkService.Callback {

    List<XOMessage> messages;
    String folderChoice = "Inbox";
    String[] folderChoices = {"Inbox", "Sent"};
    Spinner sprFolders;
    ListView lstFolder;
    SortCondition sortCon = SortCondition.DATE_DESC;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_list);
        sprFolders = (Spinner) findViewById(R.id.sprFolders);
        lstFolder = (ListView) findViewById(R.id.lstFolder);
    }

    // Populating spinner with folder choices
    private void populateSprFolders() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, folderChoices);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sprFolders.setAdapter(dataAdapter);
    }

    // Adding click listener to the folders spinner and fetching the correct message list
    private void addSprFoldersClickListener(ServiceProvider sp) {
        final ServiceProvider spr;
        spr = sp;
        sprFolders.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> av, View view, int i, long l) {
                String folder = av.getItemAtPosition(i).toString();
                folderChoice = folder;
                if (folder.equals("Inbox")) {
                    messages = spr.getNetworkService().getInbox();
                    lstFolder.setAdapter(new XOMessageAdapter(FoldersActivity.this, messages, true, getResources()));
                } else if (folder.equals("Sent")) {
                    messages = spr.getNetworkService().getOutbox();
                    lstFolder.setAdapter(new XOMessageAdapter(FoldersActivity.this, messages, false, getResources()));
                }
                
            }

            public void onNothingSelected(AdapterView<?> av) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }
    
    // Populate list (inbox/sent/etc) and add click listener for viewing a single message
    private void addLstFolderClickListener() {
        
        lstFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                XOMessage currentMessage = (XOMessage) parent.getItemAtPosition(position);

                // Launching new Activity on selecting single List Item
                Intent i = new Intent(getApplicationContext(), MessageViewActivity.class);
                // sending data to new activity
                i.putExtra("index", position); //TODO: Do not do this
                i.putExtra("folder", folderChoice);
                i.putExtra("message", currentMessage);

                startActivity(i);
            }
        });
    }

    @Override
    public void onServiceConnected(ServiceProvider sp) {
        super.onServiceConnected(sp);
        populateSprFolders();
        messages = sp.getNetworkService().getInbox(); // Default is inbox messages
        Collections.sort(messages, XOMessage.XOMessageSorter.getDateComparator(true));
        
        
        addSprFoldersClickListener(sp);
        addLstFolderClickListener();
    }

    public void mailSent(XOMessage message, Address[] invalidAddress) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mailSentError(XOMessage message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mailReceived(XOMessage message) {
        throw new UnsupportedOperationException("Not supported yet.");
        //TODO: Update list
    }

    public void mailReceivedError() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public enum SortCondition{
        DATE_DESC,
        DATE_ASC,
        SUBJECT_DESC,
        SUBJECT_ASC,
        PRIORITY_DESC,
        PRIORITY_ASC,
        LABEL_DESC,
        LABEL_ASC,
        TYPE_DESC,
        TYPE_ASC,
        SENDER_DESC,
        SENDER_ASC;
    }
}
