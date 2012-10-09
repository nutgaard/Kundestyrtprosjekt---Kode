/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
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
 * @author Kristin
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

        //Add listeners to spinners, this is so that we can for example evaluate
        //fields when a spinner item is selected. 
        addSpinnerListeners();

        btnAddAttachment = (Button) findViewById(R.id.btnAddAttachment);
        btnSend = (Button) findViewById(R.id.btnSend);

        //Add text changed listeners to all fields, so that we can check if fields has been written to.
        //If not, we just dont evaluate fields
        addTextChangedListeners();

        //Add listener to the send button.
        addBtnSendClickListener(btnSend);
        addReceiverOnFocusChangedListener(txtReceiver);
        addSubjectOnFocusChangedListener(txtSubject);

        //Fill all spinners with data values
        populateSpinners();
    }

    public void populateSpinners() {
        EnumHelper.populateSpinnerWithEnumValues(sprSecurityLabel, this, XOMessageSecurityLabel.class);
        EnumHelper.populateSpinnerWithEnumValues(sprPriority, this, XOMessagePriority.class);
        EnumHelper.populateSpinnerWithEnumValues(sprType, this, XOMessageType.class);
    }

    @Override
    public void onServiceConnected(ServiceProvider serviceProvider) {
        super.onServiceConnected(serviceProvider);
//        getServiceProvider().register(this);
    }

    public void mailSent(XOMessage message, Address[] invalidAddress) {
        Toast confirm = Toast.makeText(SendMessageActivity.this, "Message sent", Toast.LENGTH_SHORT);
        confirm.show();
    }

    public void mailSentError(XOMessage message) {
        Toast errorMess = Toast.makeText(SendMessageActivity.this, "Something went wrong", Toast.LENGTH_SHORT);
        errorMess.show();
    }

    public void mailReceived(XOMessage message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mailReceivedError() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void addReceiverOnFocusChangedListener(EditText receiver) {
        final SendMessageActivity t = this;
        receiver.setOnFocusChangeListener(new OnFocusChangeListener() {
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
        receiver.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    doIntermediateValidation();

                }
            }
        });
    }

    private void addBtnSendClickListener(Button btnSend) {
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                while (!isConnected()) {
                    Thread.yield();
                }

                /**
                 * Parse selected values from spinners
                 *
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

                if (doIntermediateValidation()) {
                    if (doSendButtonValidation()) {
                        //getServiceProvider().getNetworkService().sendMail(receiver.getText().toString(), subject.getText().toString(), message.getText().toString(), selectedSecurity, selectedPriority, selectedType);


                        //Is not necessary to have this when callback is implemented, as mailSent() will be called
                        Toast confirm = Toast.makeText(SendMessageActivity.this, "Message sent.", Toast.LENGTH_SHORT);
                        confirm.show();
                        finish();
                    }
                }


            }
        });
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

        if (!isValidMessage && txtMessageBody.hasFocus()) {
            txtMessageBody.setError(getString(R.string.invalidMessageBodyError));
            return false;
        }

        return isValidEmail && isValidSubject && isValidMessage;

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

        boolean isValidSecurityLabel = sprSecurityLabel.getSelectedItemPosition() != 0;

        String inputFieldsError = "";
        if (!isValidEmail || !isValidSubject || !isValidMessage) {
            inputFieldsError = getString(R.string.invalidInputFieldsError);
        }


        if (!isValidSecurityLabel) {
            inputFieldsError += getString(R.string.invalidSecurityLabelError);
        }

        Toast sendMessageError = Toast.makeText(SendMessageActivity.this, inputFieldsError, Toast.LENGTH_LONG);
        sendMessageError.getView().setBackgroundColor(getResources().getColor(R.color.red));
        sendMessageError.show();


        return false;
    }

    /**
     * Returns if email matches the email pattern.
     */
    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void addSpinnerListeners() {
        sprSecurityLabel.post(new Runnable() {
            public void run() {
                sprSecurityLabel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> av, View view, int i, long l) {
                        Toast sendMessageError = Toast.makeText(SendMessageActivity.this, "security", Toast.LENGTH_SHORT);
                        sendMessageError.show();

                    }

                    public void onNothingSelected(AdapterView<?> av) {
                    }
                });
            }
        });

        sprPriority.post(new Runnable() {
            public void run() {
                sprPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> av, View view, int i, long l) {
                        Toast sendMessageError = Toast.makeText(SendMessageActivity.this, "priority", Toast.LENGTH_SHORT);
                        sendMessageError.show();
                    }

                    public void onNothingSelected(AdapterView<?> av) {
                    }
                });
            }
        });

        sprType.post(new Runnable() {
            public void run() {
                sprType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> av, View view, int i, long l) {
                        Toast sendMessageError = Toast.makeText(SendMessageActivity.this, "type", Toast.LENGTH_SHORT);
                        sendMessageError.show();
                    }

                    public void onNothingSelected(AdapterView<?> av) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                });
            }
        });


    }

    private void addTextChangedListeners() {
        txtReceiver.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                SendMessageActivity.this.textEnteredInReceiver = true;
            }

            public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable edtbl) {
            }
        });
        
        
        
        
        txtMessageBody.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) {}
            public void afterTextChanged(Editable edtbl) {
                 doIntermediateValidation();
            }
        });
        
    }
}
