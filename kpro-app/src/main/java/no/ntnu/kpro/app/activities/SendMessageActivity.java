/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Address;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.core.helpers.EnumHelper;
import no.ntnu.kpro.core.model.AttachmentType;
import no.ntnu.kpro.core.model.Attachments;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageType;
import no.ntnu.kpro.core.service.ServiceProvider;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

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
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    boolean textEnteredInReceiver = false;
    boolean textEnteredInMessageBody = false;
    XOMessagePriority defaultPriority = XOMessagePriority.ROUTINE;
    XOMessageType defaultType = XOMessageType.OPERATION;
    //The lists containing the data fetched from the attachments getter.
    private List<Uri> images;
    private List<Uri> videos;
    private List<Uri> sound;
    //Attachments//
    private ListView lstAttachments;
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

        //Add listeners to spinners, this is so that we can for example evaluate
        //fields when a spinner item is selected. 
        //addSpinnerListeners(); DISABLED NOW: DONT NEED IT RIGHT NOW. DO NOT REMOVE, 
        //AS IT IS A BIT HARD TO GET TO WORK IF WEE NEED IT LATER ON.

        //Add text changed listeners to all fields, so that we can check if fields has been written to.
        addTextChangedListeners();

        //Add listener to the send button.
        addBtnSendClickListener(btnSend);
        addReceiverOnFocusChangedListener(txtReceiver);
        addSubjectOnFocusChangedListener(txtSubject);

        addAttachmentClickListener(btnAddAttachment);

        populateSpinners();
        setDefaultSpinnerValues();

        this.images = new ArrayList<Uri>();
        this.videos = new ArrayList<Uri>();

        //Attachments//
        lstAttachments = (ListView) findViewById(R.id.lstAttachments);
        attachments = new Attachments();
        addAttachmentsListener(lstAttachments);

        startLocationFetching();

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
            Log.d("spr", sprPriority.getItemAtPosition(j).toString());
            if (sprPriority.getItemAtPosition(j).toString().equals(defaultPriority.toString())) {
                Log.d("spr", "I matched");
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
    }

    public void mailSentError(XOMessage message, Exception ex) {

    }

    public void mailReceived(XOMessage message) {
        runOnUiThread(new Runnable() {
            public void run() {
//                Toast errorMess = Toast.makeText(SendMessageActivity.this, "Message Received, but I dont care", Toast.LENGTH_SHORT);
//                errorMess.show();
            }
        });
    }

    public void mailReceivedError(Exception ex) {
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
                            }
                        }


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

        String sendMessageErrorToastMessage = "";

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
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 73;
    private static final int FETCH_IMAGE_ACTIVITY_REQUEST_CODE = 77;
    private Uri attachmentUri;

    private void addAttachmentClickListener(Button btnAddAttachment) {

        btnAddAttachment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String[] items = new String[]{"Image From Camera", "Video From Camera", "Image From Phone", "Location"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SendMessageActivity.this, android.R.layout.select_dialog_item, items);
                AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this);

                builder.setTitle("Select Content");
                builder.setAdapter(adapter, null);

                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            // create Intent to take a picture and return control to the calling application
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                            attachmentUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, attachmentUri); // set the image file name

                            Log.d("SendMessage", "The file uri of imagei is: " + attachmentUri.getEncodedPath());

                            // start the image capture Intent
                            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                        } else if (item == 1) {
                            //create new Intent
                            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                            attachmentUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);  // create a file to save the video
                            Log.d("SendMessage", "File uri is:" + attachmentUri.getPath());
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, attachmentUri);  // set the image file name

                            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high

                            // start the Video Capture Intent
                            startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

                        } else if (item == 2) {
                            //Create intent
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            startActivityForResult(intent, FETCH_IMAGE_ACTIVITY_REQUEST_CODE);
                        } else if (item == 3){
                            addNewLocationToMessage();
                        }
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        logMe("ReqCode: " + requestCode + ". ResultCode:" + resultCode);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                addAttachment(attachmentUri, AttachmentType.IMAGE);
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                logMe("Something failed");
            }
        }

        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                addAttachment(attachmentUri, AttachmentType.VIDEO);
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }

        if (requestCode == FETCH_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri path = data.getData();
                addAttachment(path, AttachmentType.IMAGE);
                //super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void addAttachment(Uri attachmentUri, AttachmentType type) {
        String visualRepresentation = attachments.addAttachment(attachmentUri, type);
        ArrayAdapter<String> adapter = getCurrentAdapter();
        logMe("Size first of Adapter" + adapter.getCount());
        this.getCurrentAdapter().add(visualRepresentation);
        this.getCurrentAdapter().notifyDataSetChanged();
        logMe("Size after of Adapter" + adapter.getCount());

    }

    /**
     *
     * @return the adapter attached to the listview. If it does not exist, a new
     * one is created, connected to the listview and then returned.
     */
    private ArrayAdapter<String> getCurrentAdapter() {
        ArrayAdapter<String> adapter;
        if (lstAttachments.getAdapter() == null) {
            attachmentsVisualRepresentation = new ArrayList<String>();
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, attachmentsVisualRepresentation);
            lstAttachments.setAdapter(adapter);
        } else {
            adapter = (ArrayAdapter<String>) lstAttachments.getAdapter();
        }
        return adapter;
    }

    private void addAttachmentsListener(ListView attachments) {
        logMe("InsideAttachmentsListener");
        attachments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View view, int i, long l) {
                logMe("AttachmentClicked");
                Toast confirm = Toast.makeText(SendMessageActivity.this, "Item " + i, Toast.LENGTH_SHORT);
            }
        });
    }
//    private void updateAttachments() {
//        logMe("Starting to update attachments ...");
//        
//        ArrayAdapter<String> adapter;
//        if (lstAttachments.getAdapter() == null) {
//            logMe("Attachments Adapter was null. Creating new one..");
//            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, attachments.getAttachments());
//            lstAttachments.setAdapter(adapter);
//        }else{            
//            logMe("Used already existing attachments Adapter");
//            adapter = (ArrayAdapter<String>) lstAttachments.getAdapter();
//        }
//        logMe("Number of elements in attachments BEFORE changed:" + adapter);
//        adapter.notifyDataSetChanged();
//        logMe("Number of elements in attachments AFTER changed:" + attachments.getAttachments().size());
//        adapter.getCount();
//        
//    }
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "XOXOmessage");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("SendMessage", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            Log.d("SendMessage", "Picture:" + mediaFile.getAbsolutePath());
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
            Log.d("SendMessage", "Video" + mediaFile.getAbsolutePath());
        } else {
            return null;
        }
        return mediaFile;
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
        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);

        String serviceString = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(serviceString);
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        addLocationListener(locationManager, bestProvider);
        
    }

    private void addNewLocationToMessage() {
        String locLongString = "";

        if (currentLocation != null) {
            double lat = currentLocation.getLatitude();
            double lng = currentLocation.getLongitude();
            locLongString += "\n" + getString(R.string.myLocationNow) + "\n";
            locLongString += getString(R.string.locationLatitude) + lat + "\n";
            locLongString += getString(R.string.locationLongditude)+ lng;
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
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void onProviderEnabled(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void onProviderDisabled(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        locationManager.requestLocationUpdates(bestProvider, locationUpdateInterval, locationDistance, locationListener);
    }
}
