/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Date;
import javax.mail.Address;
import no.ntnu.kpro.app.ContactsActivity;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.app.managers.ExpandableListManager;
import no.ntnu.kpro.app.managers.PositionManager;
import no.ntnu.kpro.core.helpers.EnumHelper;
import no.ntnu.kpro.core.helpers.FileHelper;
import no.ntnu.kpro.core.model.Attachments;
import no.ntnu.kpro.core.model.ExpandableListChild;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessageType;
import no.ntnu.kpro.core.model.attachment.ImageAttachment;
import no.ntnu.kpro.core.service.ServiceProvider;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.no.app.validators.SendMessageValidator;

/**
 *
 * @author Aleksander and Kristin
 */
public class SendMessageActivity extends MenuActivity implements NetworkService.Callback {

    final static String TAG = "KPRO-GUI-SENDMESSAGE";
    
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 67;
    private static final int FETCH_IMAGE_ACTIVITY_REQUEST_CODE = 77;
    private static final int FETCH_CONTACT_REQUEST_CODE = 1337;
    
    XOMessagePriority defaultPriority = XOMessagePriority.ROUTINE;
    XOMessageType defaultType = XOMessageType.OPERATION;
    
    //Fields
    private EditText txtReceiver;
    private EditText txtSubject;
    private EditText txtMessageBody;
    private Spinner sprSecurityLabel;
    private Spinner sprPriority;
    private Spinner sprType;
    private Button btnAddAttachment;
    private Button btnSend;
    private ImageButton btnContacts;
    private boolean textEnteredInReceiver = false;
    boolean textEnteredInMessageBody = false;
    
    //Attachments//
    private Attachments attachments;
    ArrayList<ExpandableListChild> attachmentsListChildren;
    ExpandableListManager expandableAttachmentsListManager;
    
    //Intents
    private Uri attachmentUri;
    private SendMessageValidator sendMessageValidator;
    
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
        setContentView(R.layout.send_message);

        //Find all textFields
        txtReceiver = (EditText) findViewById(R.id.txtMessageReceiver);
        txtSubject = (EditText) findViewById(R.id.txtSubject);
        txtMessageBody = (EditText) findViewById(R.id.txtMessage);

        //Find all buttons and spinners
        sprSecurityLabel = (Spinner) findViewById(R.id.sprSecurityLabel);
        sprPriority = (Spinner) findViewById(R.id.sprPriority);
        sprType = (Spinner) findViewById(R.id.sprType);

        btnAddAttachment = (Button) findViewById(R.id.btnAddAttachment);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnContacts = (ImageButton) findViewById(R.id.btnContacts);

        //Add text changed listeners to all fields, so that we can check if fields has been written to.
        addTextChangedListeners();
        addClickAndFocusListeners();
        populateSpinners();
        setDefaultSpinnerValues();

        //Attachments//
        expandableListView = (ExpandableListView) findViewById(R.id.ExpList);
        expandableListManager = new ExpandableListManager(this, expandableListView);
        attachments = new Attachments();
        
        //PositionManaging
        positionManager = new PositionManager(this);
        positionManager.startLocationFetching();
        sendMessageValidator = new SendMessageValidator(this);
    }

    /**
     * Fills the spinners with data values based on all values of input enums.
     */
    public void populateSpinners() {
        EnumHelper.populateSpinnerWithEnumValues(getSprSecurityLabel(), this, XOMessageSecurityLabel.class);
        EnumHelper.populateSpinnerWithEnumValues(sprPriority, this, XOMessagePriority.class);
        EnumHelper.populateSpinnerWithEnumValues(sprType, this, XOMessageType.class);
    }

    /**
     * Sets the default spinner values based on what is set for
     * "defaultPriority" and "defaultType"
     */
    private void setDefaultSpinnerValues() {
        //Set default value on message priority spinner
        for (int j = 0; j < sprPriority.getCount(); j++) {
            if (sprPriority.getItemAtPosition(j).toString().equals(defaultPriority.toString())) {
                sprPriority.setSelection(j);
            }
        }

        //Set default value on message type spinner.
        for (int j = 0; j < sprType.getCount(); j++) {
            if (sprType.getItemAtPosition(j).toString().equals(defaultType.toString())) {
                sprType.setSelection(j);
            }
        }

    }

    private void addClickAndFocusListeners() {
        btnSend.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        while (!isConnected()) {
                            Thread.yield();
                        }

                        /**
                         * Parse selected values from spinners
                         */
                        //Parse security string
                        String selectedSecurityString = (String) getSprSecurityLabel().getSelectedItem();
                        XOMessageSecurityLabel selectedSecurity = EnumHelper.getEnumValue(XOMessageSecurityLabel.class, selectedSecurityString);

                        //Parse priority string
                        String selectedPriorityString = (String) sprPriority.getSelectedItem();
                        XOMessagePriority selectedPriority = EnumHelper.getEnumValue(XOMessagePriority.class, selectedPriorityString);

                        //Parse message type string
                        String selectedTypeString = (String) sprType.getSelectedItem();
                        XOMessageType selectedType = EnumHelper.getEnumValue(XOMessageType.class, selectedTypeString);

                        //Do all validation, both intermediate validation and send validation.
                        if (sendMessageValidator.isValidIntermediateValidation(false)) {
                            if (sendMessageValidator.isValidFinalValidation()) {
                                XOMessage m = new XOMessage("MyMailAddress@gmail.com", getTxtReceiver().getText().toString(), txtSubject.getText().toString(), getTxtMessageBody().getText().toString(), selectedSecurity, selectedPriority, selectedType, new Date());
                                getServiceProvider().getNetworkService().send(m);
                            } else {
                                //Create a big,bad and flashy toast that gets attention.
                                Toast sendMessageError = Toast.makeText(SendMessageActivity.this, R.string.invalidSecurityLabelError, Toast.LENGTH_LONG);
                                sendMessageError.getView().setBackgroundColor(getResources().getColor(R.color.red));
                                sendMessageError.show();

                            }

                        }
                    }
                });

        btnContacts.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent i = new Intent(SendMessageActivity.this, ContactsActivity.class);
                        startActivityForResult(i, 1337);
                    }
                });

        getTxtReceiver().setOnFocusChangeListener(
                new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            sendMessageValidator.isValidIntermediateValidation(false);
                        }
                    }
                });

        btnAddAttachment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String[] items = new String[]{"Image From Camera", "Image From Phone", "Location"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SendMessageActivity.this, android.R.layout.select_dialog_item, items);
                AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this);

                builder.setTitle("Select Content");

                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            startFetchImageFromCameraActivity();
                        } else if (item == 1) {
                            startFetchImageFromPhoneActivity();
                        } else if (item == 2) {
                            String description = positionManager.getCurrentLocationDescription();
                            if(description.equals("")){
                                Toast locationNotFound = Toast.makeText(SendMessageActivity.this, R.string.noLocationFoundError, RESULT_OK);
                                locationNotFound.show();
                            }else{
                                txtMessageBody.setText(txtMessageBody.getText() + description);
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

        btnContacts.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent i = new Intent(SendMessageActivity.this, ContactsActivity.class);
                        startActivityForResult(i, 1337);
                    }
                });
    }

    private void resetFields() {
        getTxtReceiver().setText("");
        txtSubject.setText("");
        getTxtMessageBody().setText("");
        getSprSecurityLabel().setSelection(0);
        this.attachmentUri = null;
        this.attachments.clear();
        expandableListManager.clearAttachmentsChildren();
        setDefaultSpinnerValues();
        this.textEnteredInMessageBody = false;
        this.textEnteredInReceiver = false;
    }

    private void addTextChangedListeners() {
        txtReceiver.addTextChangedListener(
                new TextWatcher() {
                    public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                        textEnteredInReceiver = true;
                    }

                    public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {
                    }

                    public void afterTextChanged(Editable edtbl) {
                        if (getTxtReceiver().getText().toString().length() > 0) {
                            sendMessageValidator.isValidIntermediateValidation(true);
                        }
                    }
                });
    }
    private int imageCounter = 1;

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
        if (requestCode == FETCH_CONTACT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String email = data.getStringExtra("result");
                getTxtReceiver().setText(email);
                sendMessageValidator.isValidIntermediateValidation(false); //Set to false, because we want to force an evaluation.
            }
        }
    }

    @Override
    public void onServiceConnected(ServiceProvider serviceProvider) {
        super.onServiceConnected(serviceProvider);
    }

    @Override
    public void mailSent(XOMessage message, Address[] invalidAddress) {
        super.mailSent(message, invalidAddress);
        runOnUiThread(new Runnable() {
            public void run() {
                Toast confirm = Toast.makeText(SendMessageActivity.this, "Message sent", Toast.LENGTH_SHORT);
                confirm.show();
                SendMessageActivity.this.resetFields();
            }
        });
    }

    @Override
    public void mailSentError(XOMessage message, Exception ex) {
        super.mailSentError(message, ex);
        runOnUiThread(new Runnable() {
            public void run() {
//                Toast errorMess = Toast.makeText(SendMessageActivity.this, "Something went wrong", Toast.LENGTH_SHORT);
//                errorMess.show();
            }
        });
    }

    @Override
    public void mailReceived(XOMessage message) {
        super.mailReceived(message);
        runOnUiThread(new Runnable() {
            public void run() {
//                Toast errorMess = Toast.makeText(SendMessageActivity.this, "Message Received, but I dont care", Toast.LENGTH_SHORT);
//                errorMess.show();
            }
        });
    }

    @Override
    public void mailReceivedError(Exception ex) {
        super.mailReceivedError(ex);
        runOnUiThread(new Runnable() {
            public void run() {
//                Toast errorMess = Toast.makeText(SendMessageActivity.this, "Message Received with error, but I dont care", Toast.LENGTH_SHORT);
//                errorMess.show();
            }
        });
    }

    /**
     * @return the txtReceiver
     */
    public EditText getTxtReceiver() {
        return txtReceiver;
    }

    /**
     * @return the txtMessageBody
     */
    public EditText getTxtMessageBody() {
        return txtMessageBody;
    }

    /**
     * @return the sprSecurityLabel
     */
    public Spinner getSprSecurityLabel() {
        return sprSecurityLabel;
    }

    /**
     * @return the textEnteredInReceiver
     */
    public boolean isTextEnteredInReceiver() {
        return textEnteredInReceiver;
    }
}
