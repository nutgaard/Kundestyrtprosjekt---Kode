/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

    static final String TAG = "KPRO-GUI-FOLDERS";
    List<XOMessage> messages;
    Spinner sprFolders;
    ListView lstFolder;
    SortCondition sortCon = SortCondition.DATE_DESC;
    BoxOption selectedBox = BoxOption.INBOX;
    ServiceProvider serviceProvider;
    XOMessageAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_list);
        Log.i(TAG, "Starting onCreate");
        Log.i(TAG, "Setting content view");

        // Find elements by ID
        Log.i(TAG, "Finding elements by id and setting on click listeners to buttons");
        sprFolders = (Spinner) findViewById(R.id.sprFolders);
        lstFolder = (ListView) findViewById(R.id.lstFolder);
        Button btnSort = (Button) findViewById(R.id.btnSort);
        btnSort.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        fetchMessages();
    }

    private void fetchMessages() {
        adapter = new XOMessageAdapter(this, messages, getResources());
        if (selectedBox.equals(BoxOption.INBOX)) {
            messages = serviceProvider.getNetworkService().getInbox();
            sortOnCondition();
            adapter.setIsInbox(true);
            lstFolder.setAdapter(adapter);
        } else if (selectedBox.equals(BoxOption.OUTBOX)) {
            messages = null;
            lstFolder.setAdapter(adapter);
        } else if (selectedBox.equals(BoxOption.SENT)) {
            messages = serviceProvider.getNetworkService().getOutbox();
            sortOnCondition();
            adapter.setIsInbox(false);
            lstFolder.setAdapter(adapter);
        }
    }

    // Populating spinner with folder choices
    private void populateSprFolders() {
        Log.i(TAG, "Starting populate of folders spinner");
        EnumHelper.populateSpinnerWithEnumValues(sprFolders, this, BoxOption.class);
        Log.i(TAG, "Finished populate of folders spinner");
    }

    // Adding click listener to the folders spinner and fetching the correct message list
    private void addSprFoldersClickListener(ServiceProvider sp) {
        Log.i(TAG, "Starting fetching messages");
        serviceProvider = sp;
        final ServiceProvider spr = serviceProvider;

        sprFolders.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> av, View view, int i, long l) {
                String folder = av.getItemAtPosition(i).toString();
                selectedBox = EnumHelper.getEnumValue(BoxOption.class, folder);
                Log.i(TAG, "Chosen folder is " + selectedBox.toString());
                if (folder.equals(selectedBox.INBOX.toString())) {
                    messages = spr.getNetworkService().getInbox();
                    sortOnCondition();

                    adapter = new XOMessageAdapter(FoldersActivity.this, messages, getResources());
                    adapter.setIsInbox(true);
                    adapter.setNotifyOnChange(true);
                    lstFolder.setAdapter(adapter);

                    Log.i(TAG, "Finished fetching inbox messages");
                } else if (folder.equals(BoxOption.SENT.toString())) {
                    messages = spr.getNetworkService().getOutbox();
                    sortOnCondition();

                    adapter = new XOMessageAdapter(FoldersActivity.this, messages, getResources());
                    adapter.setIsInbox(false);
                    adapter.setNotifyOnChange(true);
                    lstFolder.setAdapter(adapter);
                    Log.i(TAG, "Finsihed fetching sent messages");
                }

            }

            public void onNothingSelected(AdapterView<?> av) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }

    // Populate list (inbox/sent/etc) and add click listener for viewing a single message
    private void addLstFolderClickListener() {
        Log.i(TAG, "Adding click listener to show a single message");
        lstFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                XOMessage currentMessage = (XOMessage) parent.getItemAtPosition(position);

                // Launching new Activity on selecting single List Item
                Intent i = new Intent(getApplicationContext(), MessageViewActivity.class);
                // sending data to new activity

                i.putExtra("folder", selectedBox.toString());
                i.putExtra("message", currentMessage);
                Log.i(TAG, "Opening message " + currentMessage.toString());
                currentMessage.setOpened(true); // Problem hvis man blar...


                startActivity(i);
            }
        });
    }

    public void onClick(View view) {
        if (view.equals((Button) findViewById(R.id.btnSort))) {
            showDialogButtonClick();
        }
    }

    private void sortOnCondition() {
        switch (sortCon) {
            case DATE_DESC:
                Log.i(TAG, "Sorting on date descending");
                Collections.sort(messages, XOMessage.XOMessageSorter.getDateComparator(true));
                Log.i(TAG, "Finished sorting");
                break;
            case DATE_ASC:
                Log.i(TAG, "Sorting on date ascending");
                Collections.sort(messages, XOMessage.XOMessageSorter.getDateComparator(false));
                Log.i(TAG, "Finished sorting");
                break;
            case SENDER_DESC:
                Log.i(TAG, "Sorting on sender descending");
                Collections.sort(messages, XOMessage.XOMessageSorter.getSenderComparator(true));
                Log.i(TAG, "Finished sorting");
                break;
            case SENDER_ASC:
                Log.i(TAG, "Sorting on sender ascending");
                Collections.sort(messages, XOMessage.XOMessageSorter.getSenderComparator(false));
                Log.i(TAG, "Finished sorting");
                break;
        }
    }

    private void showDialogButtonClick() {

        Log.i(TAG, "Show sort dialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort by");
        final CharSequence[] choiceList = {"Date (newest)", "Date (oldest)", "Sender (A to Z)", "Sender (Z to A)", "Subject (A to Z)", "Subject (Z to A)", "Priority (highest)", "Priority (lowest)"};
        int selected = -1;
        for (int i = 0; i < choiceList.length; i++) {
            if (choiceList[i].equals(sortCon.toString())) {
                selected = i;
            }
        }
        builder.setSingleChoiceItems(choiceList, selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sortCon = EnumHelper.getEnumValue(SortCondition.class, choiceList[which].toString());
                fetchMessages();
                Toast.makeText(FoldersActivity.this, "Selected " + sortCon.toString(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onServiceConnected(ServiceProvider sp) {
        super.onServiceConnected(sp);
        Log.i(TAG, "Service connected to folders");

        populateSprFolders();
        Log.i(TAG, "Populating folders spinner");

        messages = sp.getNetworkService().getInbox(); // Default is inbox messages
        Collections.sort(messages, XOMessage.XOMessageSorter.getDateComparator(true));
        Log.i(TAG, "Fetching inbox messages and sorting on date desc");

        serviceProvider = sp;
        addSprFoldersClickListener(sp);
        Log.i(TAG, "Adding click listener to folders spinner");

        addLstFolderClickListener();
        Log.i(TAG, "Adding click listener to message list");
    }

    @Override
    public void mailSent(XOMessage message, Address[] invalidAddress) {
        super.mailSent(message, invalidAddress);
        Log.i(TAG, "Mail sent");
    }

    @Override
    public void mailSentError(XOMessage message, Exception ex) {
        super.mailSentError(message, ex);
        Log.i(TAG, "Mail sent error");
    }

    @Override
    public void mailReceived(XOMessage message) {
        super.mailReceived(message);
        runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, "Mail received in UI");
                if (selectedBox.equals(BoxOption.INBOX)) {
                    messages = serviceProvider.getNetworkService().getInbox();
                    sortOnCondition();
                    lstFolder.setAdapter(new XOMessageAdapter(FoldersActivity.this, messages, getResources()));
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void mailReceivedError(Exception ex) {
        super.mailReceivedError(ex);
        final Exception e = ex;
        runOnUiThread(new Runnable() {
            public void run() {
                Log.i(TAG, "Mail received error in UI");
                //Log.i(TAG, e.getMessage());
            }
        });
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

    public enum BoxOption {

        INBOX("Inbox"),
        OUTBOX("Outbox"),
        SENT("Sent");
        private String val;

        private BoxOption(String value) {
            this.val = value;
        }

        @Override
        public String toString() {
            return this.val;
        }
    }
}
