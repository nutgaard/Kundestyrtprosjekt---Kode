/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.mail.Address;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.app.managers.ExpandableListManager;
import no.ntnu.kpro.app.managers.PositionManager;
import no.ntnu.kpro.core.model.Attachments;
import no.ntnu.kpro.core.model.ExpandableListChild;
import no.ntnu.kpro.core.utilities.EnumHelper;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessageType;
import no.ntnu.kpro.core.model.attachment.Attachment;
import no.ntnu.kpro.core.model.attachment.ImageAttachment;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.utilities.FileHelper;
import no.ntnu.no.app.validators.SendMessageValidator;

/**
 *
 * @author Administrator
 */
public class InstantMessageActivity extends WrapperActivity implements View.OnClickListener, NetworkService.Callback {

    static final String TAG = "KPRO-GUI-INSTANT";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 67;
    private static final int FETCH_IMAGE_ACTIVITY_REQUEST_CODE = 77;
    private int imageCounter = 1;
    //Attachments//
    private Attachments attachments;
    ArrayList<ExpandableListChild> attachmentsListChildren;
    ExpandableListManager expandableAttachmentsListManager;
    private Button btnAddAttachment;
    private EditText txtMessage;
    //Intents
    private Uri attachmentUri;
    //GPS / WIFI -Location Manager//
    LocationManager locationManager;
    private final int locationUpdateInterval = 5000; //Milliseconds
    private final int locationDistance = 5; //Meters.
    private Location currentLocation = null;
    private PositionManager positionManager;
    //ExpandableList for attachments and manager for holding and setting up attachments
    ExpandableListView expandableListView;
    ExpandableListManager expandableListManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.instant_message);

        updateFields();

        Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);

        btnAddAttachment = (Button) findViewById(R.id.btnAddAttachment);

        //Attachments//
        expandableListView = (ExpandableListView) findViewById(R.id.ExpList);
        expandableListManager = new ExpandableListManager(this, expandableListView);
        attachments = new Attachments();

        //PositionManaging
        positionManager = new PositionManager(this);

        positionManager.startLocationFetching();

        RelativeLayout layInst1 = (RelativeLayout) findViewById(R.id.layInst1);
        RelativeLayout layInst2 = (RelativeLayout) findViewById(R.id.attributes);

        layInst1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        layInst2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        txtMessage = (EditText) findViewById(R.id.txtMessage);
        txtMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean bln) {
                final InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        btnAddAttachment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String[] items = new String[]{"Image From Camera", "Image From Phone", "Location"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(InstantMessageActivity.this, android.R.layout.select_dialog_item, items);
                AlertDialog.Builder builder = new AlertDialog.Builder(InstantMessageActivity.this);

                builder.setTitle("Select Content");

                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            startFetchImageFromCameraActivity();
                        } else if (item == 1) {
                            startFetchImageFromPhoneActivity();
                        } else if (item == 2) {
                            String description = positionManager.getCurrentLocationDescription();
                            if (description.equals("")) {
                                Toast locationNotFound = Toast.makeText(InstantMessageActivity.this, R.string.noLocationFoundError, RESULT_OK);
                                locationNotFound.show();
                            } else {
                                txtMessage.setText(txtMessage.getText() + description);
                            }


                        }
                    }

                    private void startFetchImageFromPhoneActivity() {
                        //Create intent
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent, FETCH_IMAGE_ACTIVITY_REQUEST_CODE);
                    }

                    private void startFetchImageFromCameraActivity() {
                        // create Intent to take a picture and return control to the calling application
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        attachmentUri = FileHelper.getOutputMediaFileUri(FileHelper.MEDIA_TYPE_IMAGE); // create a file to save the image
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, attachmentUri); // set the image file name

                        Log.d("SendMessage", "The file uri of image is: " + attachmentUri.getEncodedPath());

                        // start the image capture Intent
                        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "ReqCode: " + requestCode + ". ResultCode:" + resultCode);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            attachments.addAttachment(new ImageAttachment(attachmentUri));
            expandableListManager.addAttachmentToDropDown(attachmentUri, imageCounter++);
        }

        if (requestCode == FETCH_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri path = data.getData();
            attachments.addAttachment(new ImageAttachment(path));
            expandableListManager.addAttachmentToDropDown(data.getData(), imageCounter++);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        updateFields();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFields();
    }

    private void updateFields() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String from = sharedPrefs.getString("standard_receiver", " ");
        TextView lblFrom = (TextView) findViewById(R.id.lblReceiver);
        lblFrom.setText(from);

        String securityLabel = sharedPrefs.getString("standard_security_label", "UNCLASSIFIED");
        TextView lblSecurityLabel = (TextView) findViewById(R.id.lblSecurityLabel);
        lblSecurityLabel.setText(securityLabel);

        if (securityLabel.equals(XOMessageSecurityLabel.UGRADERT.toString()) || securityLabel.equals(XOMessageSecurityLabel.UNCLASSIFIED.toString()) || securityLabel.equals(XOMessageSecurityLabel.NATO_UNCLASSIFIED.toString())) {
            lblSecurityLabel.setTextColor(getResources().getColor(R.color.SIOLabelBlack));
        } else {
            lblSecurityLabel.setTextColor(getResources().getColor(R.color.SIOLabelRed));
        }

        String priority = sharedPrefs.getString("standard_priority", "Override");
        TextView lblPriority = (TextView) findViewById(R.id.lblPriority);
        lblPriority.setText(priority);

        String type = sharedPrefs.getString("standard_type", "Operation");
        TextView lblType = (TextView) findViewById(R.id.lblType);
        lblType.setText(type);
    }

    public void onClick(View view) {
        String lblReceiver = ((TextView) findViewById(R.id.lblReceiver)).getText().toString();
        if (lblReceiver.length() <= 1) {
            Toast sendMessageError = Toast.makeText(InstantMessageActivity.this, "You need to set a receiver in the settings tab", Toast.LENGTH_LONG);
            sendMessageError.show();
            return;
        }
        String lblLabel = ((TextView) findViewById(R.id.lblSecurityLabel)).getText().toString();
        String lblPriority = ((TextView) findViewById(R.id.lblPriority)).getText().toString();
        String lblType = ((TextView) findViewById(R.id.lblType)).getText().toString();
        Log.i(TAG, lblLabel);
        XOMessageSecurityLabel secLabel = EnumHelper.getEnumValue(XOMessageSecurityLabel.class, lblLabel);
        XOMessagePriority priority = EnumHelper.getEnumValue(XOMessagePriority.class, lblPriority);
        XOMessageType type = EnumHelper.getEnumValue(XOMessageType.class, lblType);
        String text = ((EditText) findViewById(R.id.txtMessage)).getText().toString();
        String subject = "";
        if (text != null && text.length() > 0) {
            subject = text.length() >= 60 ? text.substring(0, 59) : text;
        } else {
            text = "";
            subject = "";
        }
        if (text == null) {
            subject = "";
        }

        XOMessage m = new XOMessage("MyMailAddress@gmail.com", lblReceiver, subject, text, secLabel, priority, type, new Date());

        List<Uri> att = new LinkedList<Uri>();
        for (Attachment a : attachments) {
            System.out.println("Addding URI to attachments: " + a.getUri().toString());
            att.add(a.getUri());
        }
        m.addAttachment(att);
        getServiceProvider().getNetworkService().send(m);
        Intent i = new Intent(getApplicationContext(), MainTabActivity.class);
        startActivity(i);
    }

    @Override
    public void mailSent(IXOMessage message, Address[] invalidAddress) {
        super.mailSent(message, invalidAddress);
        Log.i(TAG, "Trying to reset textfield");
        runOnUiThread(new Runnable() {
            public void run() {
                EditText txtMessage = (EditText) findViewById(R.id.txtMessage);
                txtMessage.setText("");
                Intent i = new Intent(getApplicationContext(), MainTabActivity.class);
                startActivity(i);
            }
        });

    }
}
