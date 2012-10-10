/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Collections;
import java.util.List;
import javax.mail.Address;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.app.XOMessageAdapter;
import no.ntnu.kpro.core.helpers.EnumHelper;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.ServiceProvider;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Kristin
 */
public class FoldersActivity extends MenuActivity implements NetworkService.Callback, View.OnClickListener {

    List<XOMessage> messages;
    String folderChoice = "Inbox";
    String[] folderChoices = {"Inbox", "Sent"};
    Spinner sprFolders;
    ListView lstFolder;
    SortCondition sortCon = SortCondition.DATE_DESC;
    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.message_list);
        sprFolders = (Spinner) findViewById(R.id.sprFolders);
        lstFolder = (ListView) findViewById(R.id.lstFolder);
        Button btnSort = (Button) findViewById(R.id.btnSort);
        btnSort.setOnClickListener(this);
    }

    // Populating spinner with folder choices
    private void populateSprFolders() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_textview, folderChoices);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
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


    public void onClick(View view) {
        if (view.equals((Button) findViewById(R.id.btnSort))) {
            showDialogButtonClick();
        }
    }

    private void showDialogButtonClick() {

        Log.i("KPRO", "show Dialog ButtonClick");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort by");
        final CharSequence[] choiceList = {"Date (newest)", "Date (oldest)", "Sender (A to Z)", "Sender (Z to A)", "Subject (A to Z)", "Subject (Z to A)", "Priority (highest)", "Priority (lowest)"};
        int selected = -1; // does not select anything
        builder.setSingleChoiceItems(choiceList, selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(
                    DialogInterface dialog,
                    int which) {
                
                sortCon = EnumHelper.getEnumValue(SortCondition.class, choiceList[which].toString());
                Toast.makeText(
                        mContext,
                        "Selected " + sortCon.toString(),
                        Toast.LENGTH_SHORT)
                        .show();
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void mailSent(XOMessage message, Address[] invalidAddress) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mailSentError(XOMessage message, Exception ex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mailReceived(XOMessage message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mailReceivedError(Exception ex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public enum SortCondition {

        DATE_DESC("Date (newest)"),
        DATE_ASC("Date (oldest)"),
        SUBJECT_DESC("Subject (Z to A)"),
        SUBJECT_ASC("Subject (A to Z)"),
        PRIORITY_DESC("Priority (highest)"),
        PRIORITY_ASC("Priority (lowest)"),
        LABEL_DESC("Label (Z to A)"),
        LABEL_ASC("Label (A to Z)"),
        TYPE_DESC("Type (Z to A)"),
        TYPE_ASC("Type (A to Z)"),
        SENDER_DESC("Sender (Z to A)"),
        SENDER_ASC("Sender (A to Z)");
        private String val;

        private SortCondition(String value) {
            this.val = value;
        }

        @Override
        public String toString() {
            return this.val;
        }
    }
}
