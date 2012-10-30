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
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Address;
import no.ntnu.kpro.app.ContactsActivity;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.app.adapters.ExpandableListAdapter;
import no.ntnu.kpro.core.helpers.FileHelper;
import no.ntnu.kpro.core.model.Attachments;
import no.ntnu.kpro.core.model.ExpandableListChild;
import no.ntnu.kpro.core.model.ExpandableListGroup;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessageType;
import no.ntnu.kpro.core.model.attachment.Attachment;
import no.ntnu.kpro.core.model.attachment.ImageAttachment;
import no.ntnu.kpro.core.service.ServiceProvider;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.utilities.EnumHelper;

/**
 *
 * @author Aleksander and Kristin
 */
public class SendMessageActivity extends MenuActivity implements NetworkService.Callback {

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
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    boolean textEnteredInReceiver = false;
    boolean textEnteredInMessageBody = false;
    XOMessagePriority defaultPriority = XOMessagePriority.ROUTINE;
    XOMessageType defaultType = XOMessageType.OPERATION;
    //Attachments//
    private ExpandableListView expList;
    private Attachments attachments;
    private List<String> attachmentsVisualRepresentation;

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

        //Add listener to the send button.
        addBtnSendClickListener(btnSend);
        addBtnContactsClickListener(btnContacts);
        addReceiverOnFocusChangedListener(txtReceiver);
        addSubjectOnFocusChangedListener(txtSubject);

        addAttachmentClickListener(btnAddAttachment);

        populateSpinners();
        setDefaultSpinnerValues();

        //Attachments//
        expandList = (ExpandableListView) findViewById(R.id.ExpList);
        attachments = new Attachments();

        startLocationFetching();
        this.fillExpandableList();

    }

    /**
     * Fills the spinners with data values based on all values of input enums.
     */
    public void populateSpinners() {
        EnumHelper.populateSpinnerWithEnumValues(sprSecurityLabel, this, XOMessageSecurityLabel.class);
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

    @Override
    public void onServiceConnected(ServiceProvider serviceProvider) {
        super.onServiceConnected(serviceProvider);
//        getServiceProvider().register(this);
    }

    public void mailSent(XOMessage message, Address[] invalidAddress) {
        super.mailSent(message, invalidAddress);
        runOnUiThread(new Runnable() {
            public void run() {
                Toast confirm = Toast.makeText(SendMessageActivity.this, "Message sent", Toast.LENGTH_SHORT);
                confirm.show();
            }
        });
    }

    public void mailSentError(XOMessage message, Exception ex) {
        super.mailSentError(message, ex);
        runOnUiThread(new Runnable() {
            public void run() {
//                Toast errorMess = Toast.makeText(SendMessageActivity.this, "Something went wrong", Toast.LENGTH_SHORT);
//                errorMess.show();
            }
        });
    }

    public void mailReceived(XOMessage message) {
        super.mailReceived(message);
        runOnUiThread(new Runnable() {
            public void run() {
//                Toast errorMess = Toast.makeText(SendMessageActivity.this, "Message Received, but I dont care", Toast.LENGTH_SHORT);
//                errorMess.show();
            }
        });
    }

    public void mailReceivedError(Exception ex) {
        super.mailReceivedError(ex);
        runOnUiThread(new Runnable() {
            public void run() {
//                Toast errorMess = Toast.makeText(SendMessageActivity.this, "Message Received with error, but I dont care", Toast.LENGTH_SHORT);
//                errorMess.show();
            }
        });

    }

    private void addBtnSendClickListener(Button btnSend) {
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
                        String selectedSecurityString = (String) sprSecurityLabel.getSelectedItem();
                        XOMessageSecurityLabel selectedSecurity = EnumHelper.getEnumValue(XOMessageSecurityLabel.class, selectedSecurityString);

                        //Parse priority string
                        String selectedPriorityString = (String) sprPriority.getSelectedItem();
                        XOMessagePriority selectedPriority = EnumHelper.getEnumValue(XOMessagePriority.class, selectedPriorityString);

                        //Parse message type string
                        String selectedTypeString = (String) sprType.getSelectedItem();
                        XOMessageType selectedType = EnumHelper.getEnumValue(XOMessageType.class, selectedTypeString);

                        //Do all validation, both intermediate validation and send validation.
                        if (doIntermediateValidation()) {
                            if (doSendButtonValidation()) {
                                XOMessage m = new XOMessage("MyMailAddress@gmail.com", txtReceiver.getText().toString(), txtSubject.getText().toString(), txtMessageBody.getText().toString(), selectedSecurity, selectedPriority, selectedType, new Date());
                                getServiceProvider().getNetworkService().send(m);
                            }
                        }


                    }
                });
    }

    private void addBtnContactsClickListener(ImageButton btnContacts) {
        btnContacts.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent i = new Intent(SendMessageActivity.this, ContactsActivity.class);
                        startActivityForResult(i, 1337);
                    }
                });
    }

    private void resetFields() {
        txtReceiver.setText("");
        txtSubject.setText("");
        txtMessageBody.setText("");
        sprSecurityLabel.setSelection(0);
        setDefaultSpinnerValues();
    }

    private boolean isValidInputField(String input) {
        return !input.equals("");
    }

    /**
     * Shows all relevant error messages, if necessary.
     *
     * @return true if all intermediate validation is okay.
     */
    private boolean doIntermediateValidation() {

        //Find all relevant text strings to validate
        String txtReceiverString = txtReceiver.getText().toString();
        String txtSubjectString = txtSubject.getText().toString();
        String txtMessageString = txtMessageBody.getText().toString();

        //Validate all fields
        boolean isValidEmail = isValidEmail(txtReceiverString.trim());
        boolean isValidSubject = isValidInputField(txtSubjectString);
        boolean isValidMessage = isValidInputField(txtMessageString);

        //If text never has been entered in receiver field, we should not show validations.
        if (!textEnteredInReceiver) {
            return false;
        }

        //Validate email
        if (!isValidEmail) {
            txtReceiver.setError(getString(R.string.invalidMessageReceiverError));
            return false;
        }

        if (!isValidSubject && !txtSubject.hasFocus()) {
            txtSubject.setError(getString(R.string.invalidMessageSubjectError));
            return false;
        }

        if (!isValidMessage && textEnteredInMessageBody && !txtMessageBody.hasFocus()) {
            txtMessageBody.requestFocus();
            txtMessageBody.setError(getString(R.string.invalidMessageBodyError));
            return false;
        }

        return isValidEmail && isValidSubject;

    }

    private boolean doSendButtonValidation() {

        //Find all relevant text strings to validate
        String txtReceiverString = txtReceiver.getText().toString();
        String txtSubjectString = txtSubject.getText().toString();
        String txtMessageString = txtMessageBody.getText().toString();

        //Validate all fields
        boolean isValidEmail = isValidEmail(txtReceiverString.trim());
        boolean isValidSubject = isValidInputField(txtSubjectString);
        boolean isValidMessage = isValidInputField(txtMessageString);

        if (!isValidMessage) {
            txtMessageBody.setError(getString(R.string.invalidMessageBodyError));
            txtMessageBody.requestFocus();
            return false;
        }

        boolean isValidSecurityLabel = sprSecurityLabel.getSelectedItemPosition() != 0;

        boolean isValidDataFields = isValidEmail && isValidSubject && isValidMessage && isValidSecurityLabel;
        if (isValidDataFields) {
            return true;
        }

        //Create a big,bad and flashy toast that gets attention.
        Toast sendMessageError = Toast.makeText(SendMessageActivity.this, R.string.invalidSecurityLabelError, Toast.LENGTH_LONG);
        sendMessageError.getView().setBackgroundColor(getResources().getColor(R.color.red));
        sendMessageError.show();

        return false; //If we end up here, it means validation has failed, so we return false.
    }

    /**
     * Returns if email matches the email pattern.
     */
    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void addTextChangedListeners() {
        txtReceiver.addTextChangedListener(
                new TextWatcher() {
                    public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                        SendMessageActivity.this.textEnteredInReceiver = true;
                    }

                    public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {
                    }

                    public void afterTextChanged(Editable edtbl) {
                    }
                });




        txtMessageBody.addTextChangedListener(
                new TextWatcher() {
                    public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {
                    }

                    public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                    }

                    public void afterTextChanged(Editable edtbl) {
                        if (txtMessageBody.getText().toString().length() > 0) {
                            textEnteredInMessageBody = true;
                        }
                        doIntermediateValidation();
                    }
                });

    }

    private void addReceiverOnFocusChangedListener(EditText receiver) {
        final SendMessageActivity t = this;
        receiver.setOnFocusChangeListener(
                new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            doIntermediateValidation();
                        }
                    }
                });
    }

    private void addSubjectOnFocusChangedListener(EditText receiver) {
        final SendMessageActivity t = this;
        receiver.setOnFocusChangeListener(
                new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            doIntermediateValidation();

                        }
                    }
                });
    }
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 67;
    private static final int FETCH_IMAGE_ACTIVITY_REQUEST_CODE = 77;
    private static final int FETCH_CONTACT_REQUEST_CODE = 1337;
    private Uri attachmentUri;

    private void addAttachmentClickListener(Button btnAddAttachment) {

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
                            addNewLocationToMessage();
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

                        Log.d("SendMessage", "The file uri of imagei is: " + attachmentUri.getEncodedPath());

                        // start the image capture Intent
                        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
    private int imageCounter = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        logMe("ReqCode: " + requestCode + ". ResultCode:" + resultCode);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            addAttachment(new ImageAttachment(attachmentUri));
            addAttachmentToDropDown(attachmentUri);
            fillExpandableList();
        }

        if (requestCode == FETCH_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri path = data.getData();
            addAttachment(new ImageAttachment(path));
            addAttachmentToDropDown(data.getData());
            fillExpandableList();
            //super.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == FETCH_CONTACT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String email = "LOL";
                email = data.getStringExtra("result");
                txtReceiver.setText(email);
            }

        }
    }

    private void addAttachment(Attachment attachment) {
        attachments.addAttachment(attachment);
    }

    private void logMe(String message) {
        Log.d("SendMessage", message);
    }
    //GPS / WIFI -Location Manager//
    LocationManager locationManager;
    private final int locationUpdateInterval = 5000; //Milliseconds
    private final int locationDistance = 5; //Meters.
    private Location currentLocation = null;

    private void startLocationFetching() {
        String serviceString = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(serviceString);
        addLocationListener(locationManager);

    }

    private void addLocationListener(LocationManager locationManager) {
        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location lctn) {
                SendMessageActivity.this.currentLocation = lctn;

            }

            public void onStatusChanged(String string, int i, Bundle bundle) {
            }

            public void onProviderEnabled(String string) {
            }

            public void onProviderDisabled(String string) {
            }
        };
        String bestProvider = locationManager.getBestProvider(criteria, true);

        if (bestProvider != null) {
            locationManager.requestLocationUpdates(bestProvider, locationUpdateInterval, locationDistance, locationListener);
        } else {
            Toast noProviderFoundMessage = Toast.makeText(SendMessageActivity.this, getString(R.string.noLocationProviderFound), RESULT_OK);
            noProviderFoundMessage.show();
        }

    }

    private void addNewLocationToMessage() {
        String locLongString = "";

        if (currentLocation != null) {
            double lat = currentLocation.getLatitude();
            double lng = currentLocation.getLongitude();

            locLongString += "\n" + getString(R.string.myLocationNow) + "\n";
            locLongString += getString(R.string.locationLatitude) + lat + "\n";
            locLongString += getString(R.string.locationLongditude) + lng + "\n";
            locLongString += "Accuracy is " + currentLocation.getAccuracy() + " meters";
            this.txtMessageBody.setText(txtMessageBody.getText() + locLongString);

        } else {
            Toast locationNotFound = Toast.makeText(SendMessageActivity.this, R.string.noLocationFoundError, RESULT_OK);
            locationNotFound.show();
        }
    }

    private void addLocationListener(LocationManager locationManager, String bestProvider) {
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location lctn) {
                SendMessageActivity.this.currentLocation = lctn;

            }

            public void onStatusChanged(String string, int i, Bundle bundle) {
                
            }

            public void onProviderEnabled(String string) {
                
            }

            public void onProviderDisabled(String string) {
                
            }
        };

        locationManager.requestLocationUpdates(bestProvider, locationUpdateInterval, locationDistance, locationListener);
    }
    private ExpandableListAdapter expAdapter;
    private ArrayList<ExpandableListGroup> expListItems;
    private ExpandableListView expandList;

    //Expandable list example:
    private void fillExpandableList() {

        expListItems = getExpandableListItems();
        expAdapter = new ExpandableListAdapter(SendMessageActivity.this, expListItems);
        expandList.setAdapter(expAdapter);

        OnChildClickListener childClickListener = new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView elv, View view, int i, int i1, long l) {
                ExpandableListChild currentChild = children.get(i1);
                logMe("Starting intent...");
                logMe("Uri is: " + currentChild.getUri().toString());
                Intent showImageIntent = new Intent();
                showImageIntent.setAction(Intent.ACTION_VIEW);
                showImageIntent.setDataAndType(currentChild.getUri(), "image/jpg");
                startActivity(showImageIntent);
                return true;
            }
        };
        expandList.setOnChildClickListener(childClickListener);
    }
    ArrayList<ExpandableListChild> children = new ArrayList<ExpandableListChild>();

    private ArrayList<ExpandableListGroup> getExpandableListItems() {
        ArrayList<ExpandableListGroup> listGroups = new ArrayList<ExpandableListGroup>();
        ExpandableListGroup attachmentsGroup = new ExpandableListGroup();
        attachmentsGroup.setName("Attachments");
        attachmentsGroup.setItems(children);
        listGroups.add(attachmentsGroup);
        return listGroups;
    }

    private void addAttachmentToDropDown(Uri uri) {
        String name = "Image " + imageCounter++ + " (" + FileHelper.getImageFileLastPathSegmentFromImage(uri) + ")";
        ExpandableListChild child = new ExpandableListChild(name, uri);
        children.add(child);
    }
}
