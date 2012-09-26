/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

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
import java.util.List;
import javax.mail.Address;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.ServiceProvider;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Kristin
 */
public class FoldersActivity extends WrapperActivity implements NetworkService.Callback {

    List<XOMessage> messages;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // GetExtra for in/out/sent, set corresponding content view
        setContentView(R.layout.message_list);
    }

    private void populateSprFolders(){
        Spinner folders = (Spinner) findViewById(R.id.folders);
        String[] folderChoices = {"Inbox", "Outbox", "Sent"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
		android.R.layout.simple_spinner_item, folderChoices);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        folders.setAdapter(dataAdapter);
    }
    
    private void addSprFoldersClickListener(ServiceProvider sp, Spinner sprFolders){
        final ServiceProvider spr;
        spr = sp;
        sprFolders.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            
            public void onItemSelected(AdapterView<?> av, View view, int i, long l) {
                String folder = av.getItemAtPosition(i).toString();
                if(folder.equals("Inbox")){
                    messages = spr.getNetworkService().getInbox();
                }
                else if (folder.equals("Outbox")) {
                    
                }
                else if (folder.equals("Sent")) {
                    messages = spr.getNetworkService().getOutbox();
                }
                ListView v = (ListView) findViewById(R.id.list);
                v.setAdapter(new XOMessageAdapter(FoldersActivity.this, messages));
            }

            public void onNothingSelected(AdapterView<?> av) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }
    
    // Create "popup" menu (shows by pressing MENU button) based on layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu_inbox, menu);
        return true;
    }

    // Handler for pressing the popup menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_new:
                Intent i = new Intent(getApplicationContext(), SendMessageActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_sort:
                //showHelp();
                return true;
            case R.id.menu_settings:
                //showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onServiceConnected(ServiceProvider sp) {
        
        populateSprFolders();
        messages = sp.getNetworkService().getInbox();
        addSprFoldersClickListener(sp, (Spinner) findViewById(R.id.folders));
        
        ListView v = (ListView) findViewById(R.id.list);

        v.setAdapter(new XOMessageAdapter(this, messages));

        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                // selected item
                //String from = ((TextView) view).getText().toString();

                XOMessage mess = messages.get(position);
                String from = mess.getFrom();
                String subj = mess.getSubject();
                String text = mess.getStrippedBody();

//                
//                HashMap hmap = list.get(position);
//                String from = hmap.get("from").toString();
//                String subj = hmap.get("subject").toString();

//                TextView v = (TextView) findViewById(R.id.from);
//                String s = v.getText().toString();
//                TextView v2 = (TextView) findViewById(R.id.subject);
//                String s2 = v2.getText().toString();

                // Launching new Activity on selecting single List Item
                Intent i = new Intent(getApplicationContext(), MessageInActivity.class);
                // sending data to new activity
                i.putExtra("from", from);
                i.putExtra("subject", subj);
                i.putExtra("text", text);
                startActivity(i);

            }
        });
    }

    public void mailSent(XOMessage message, Address[] invalidAddress) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mailSentError(XOMessage message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mailReceived(XOMessage message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mailReceivedError() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
