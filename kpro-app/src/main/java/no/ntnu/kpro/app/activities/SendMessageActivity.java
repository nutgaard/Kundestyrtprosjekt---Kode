/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Address;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.core.helpers.EnumHelper;
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

        addClickListener(btnAddAttachment);

        populateSpinners();
        setDefaultSpinnerValues();
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
                Toast errorMess = Toast.makeText(SendMessageActivity.this, "Message Received, but I dont care", Toast.LENGTH_SHORT);
                errorMess.show();
            }
        });
    }

    public void mailReceivedError(Exception ex) {
        runOnUiThread(new Runnable() {

            public void run() {
                Toast errorMess = Toast.makeText(SendMessageActivity.this, "Message Received with error, but I dont care", Toast.LENGTH_SHORT);
                errorMess.show();
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
//                                getServiceProvider().getNetworkService().sendMail(txtReceiver.getText().toString(), txtSubject.getText().toString(), txtMessageBody.getText().toString(), selectedSecurity, selectedPriority, selectedType);
                                getServiceProvider().getNetworkService().send(m);

                                //Is not necessary to have this when callback is implemented, as mailSent() will be called
                                Toast confirm = Toast.makeText(SendMessageActivity.this, "Message sent.", Toast.LENGTH_SHORT);
                                confirm.show();
                                resetFields();
//                                try{
//                                    resetFields();
//                                    MainTabActivity act;
//                                    act = (MainTabActivity) getParent();
//                                    act.switchTab(0);
//                                }
//                                catch(Exception e){
//                                    Log.i("KPRO", "Tab change failed");
//                                }
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
    private Uri mImageCaptureUri;
    private Uri soundRecordingUri;
    private ImageView mImageView;
    private static final int PICK_IMAGE_FROM_CAMERA = 0;
    private static final int PICK_IMAGE_FROM_FILE = 1;
    private static final int PICK_VOICE_RECORDING = 2;
    private static final int PICK_GPS_COORDINATES = 3;

    private void addClickListener(Button btnAddAttachment) {
        //A LOT OF DEBUG / TEST CODE HERE. DO NOT RELY ON THIS!!!

        btnAddAttachment.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String[] items = new String[]{"From Camera", "From SD Card", "Voice Recording", "GPS Coordinates"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SendMessageActivity.this, android.R.layout.select_dialog_item, items);
                AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this);

                builder.setTitle("Select Content");
                builder.setAdapter(adapter, null);

                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        if (item == PICK_IMAGE_FROM_CAMERA) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File file = new File(Environment.getExternalStorageDirectory(), "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                            mImageCaptureUri = Uri.fromFile(file);
                            txtMessageBody.setText(mImageCaptureUri.toString());

                            try {
                                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                                intent.putExtra("return-data", true);
                                startActivityForResult(intent, PICK_IMAGE_FROM_CAMERA);
                            } catch (Exception e) {
                                throw new IllegalArgumentException("Could not get image.");
                            }

                            //dialog.cancel();
                        } else if (item == PICK_IMAGE_FROM_FILE) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);

                            startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_IMAGE_FROM_FILE);
                        } else if (item == PICK_VOICE_RECORDING) {
                            Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                            startActivityForResult(intent, PICK_VOICE_RECORDING);

                        } else if (item == PICK_GPS_COORDINATES) {
                            //Pick locations using code from: 
                            //AS soon as we are ready to implement it. A lot of fiddlig work, perhaps, so waiting with it.
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
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        if (requestCode == PICK_VOICE_RECORDING) {
            soundRecordingUri = data.getData();
            Toast.makeText(SendMessageActivity.this, "Saved: " + soundRecordingUri.getPath(), Toast.LENGTH_LONG).show();
        }


//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//            Cursor cursor = getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//
//            // String picturePath contains the path of selected Image                        
//        }
    }
}
