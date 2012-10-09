/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
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

    
public class SendMessageActivity extends MenuActivity implements NetworkService.Callback{
    private EditText receiver;
    private EditText subject;
    private EditText message;    
    private Spinner sprSecurityLabel;
    private Spinner sprPriority;
    private Spinner sprType;
    private Button btnAddAttachment;
    private Button btnSend;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_message);

        //Find all textFields
        receiver = (EditText) findViewById(R.id.txtMessageReceiver);
        subject = (EditText) findViewById(R.id.txtSubject);
        message = (EditText) findViewById(R.id.txtMessage);

        //Find all buttons and spinners
        sprSecurityLabel = (Spinner) findViewById(R.id.sprSecurityLabel);
        sprPriority = (Spinner) findViewById(R.id.sprPriority);
        sprType = (Spinner) findViewById(R.id.sprType);

        addSpinnerStuff();
        
        //Make spinners focusable, so that usability is improved
        sprSecurityLabel.setFocusable(true);
        sprPriority.setFocusable(true);
        sprType.setFocusable(true);



        btnAddAttachment = (Button) findViewById(R.id.btnAddAttachment);
        btnSend = (Button) findViewById(R.id.btnSend);

        //Add listener to the send button.
        addBtnSendClickListener(btnSend);
        addReceiverOnFocusChangedListener(receiver);
        addSubjectOnFocusChangedListener(subject);



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
                    isValidFields();
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
                    isValidFields();

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

                if (isValidFields()) {
                    //getServiceProvider().getNetworkService().sendMail(txtReceiver, txtSubject, txtMessage, selectedSecurity, selectedPriority, selectedType);

                    //Is not necessary to have this when callback is implemented, as mailSent() will be called
                    Toast confirm = Toast.makeText(SendMessageActivity.this, "Message sent.", Toast.LENGTH_SHORT);
                    confirm.show();
                    finish();
                }
                
                //TODO: Set default values (routine/operation)
                //TODO: Check if security label picked
                //TODO: Check other fields
                
                getServiceProvider().getNetworkService().sendMail(receiver.getText().toString(), subject.getText().toString(), message.getText().toString(), selectedSecurity, selectedPriority, selectedType);
                
                
                //Is not necessary to have this when callback is implemented, as mailSent() will be called
                Toast confirm = Toast.makeText(SendMessageActivity.this, "Message sent.", Toast.LENGTH_SHORT);                
                confirm.show();
                
                finish();
			}
        });
    }

    private boolean isValidInputField(String input) {
        return !input.isEmpty();
    }

    private boolean isValidFields() {

        //Find all relevant text strings to validate
        String txtReceiver = receiver.getText().toString();
        String txtSubject = subject.getText().toString();
        String txtMessage = message.getText().toString();

        //Validate all fields
        boolean isValidEmail = isValidEmail(txtReceiver.trim());
        boolean isValidSubject = isValidInputField(txtSubject);
        boolean isValidMessage = isValidInputField(txtMessage);
        boolean isValidSecurityLabel = sprSecurityLabel.getSelectedItemPosition() != 0;

        //Validate email
        if (!isValidEmail) {
            receiver.setError(getString(R.string.invalidMessageReceiverError));
        }

        if (isValidEmail && !isValidSubject && !subject.hasFocus()) {
            subject.setError(getString(R.string.invalidMessageSubjectError));
        }

        if (isValidEmail && isValidSubject && !isValidMessage) {
            message.setError(getString(R.string.invalidMessageBodyError));
        }

        String inputFieldsError = "";
        if (!isValidEmail || !isValidSubject || !isValidMessage) {
            inputFieldsError = getString(R.string.invalidInputFieldsError);
            Toast invalidFieldErrorToast = Toast.makeText(SendMessageActivity.this, inputFieldsError, Toast.LENGTH_LONG);
        }

        if (isValidEmail && isValidSubject && isValidMessage && !isValidSecurityLabel) {
            String securityLabelError = "";
            if (!isValidSecurityLabel) {
                securityLabelError = getString(R.string.invalidSecurityLabelError);
            }

            Toast sendMessageError = Toast.makeText(SendMessageActivity.this, securityLabelError, Toast.LENGTH_LONG);
            sendMessageError.show();
        }

        return isValidEmail && isValidSubject && isValidMessage && isValidSecurityLabel;

    }

    /**
     * Returns if email matches the email pattern.
     */
    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void addValidatorCheckToSpinners() {
    }

    private void addSpinnerStuff() {
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
}
