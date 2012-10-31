/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.no.app.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.app.activities.SendMessageActivity;

/**
 *
 * @author aleksandersjafjell
 */
public class SendMessageValidator {

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    
    SendMessageActivity activity;
    
    public SendMessageValidator(SendMessageActivity activity) {
        this.activity = activity;
    }
    
    public boolean isValidIntermediateValidation(boolean isInReceiverField){
     
        //Find all relevant text strings to validate
        String txtReceiverString = activity.getTxtReceiver().getText().toString();

        //Validate email field
        boolean isValidEmail = isValidEmail(txtReceiverString.trim());

        //If text never has been entered in receiver field, we should not show validations.
        if (!activity.isTextEnteredInReceiver()) {
            return false;
        }

        if (!isValidEmail && !isInReceiverField) {    //Set error on receiver field only if mail is invalid and we are not in it.
            activity.getTxtReceiver().setError(activity.getString(R.string.invalidMessageReceiverError));
            return false;
        } else if (isValidEmail) { //Only clear the error message if it is a valid mail.
            activity.getTxtReceiver().setError(null);
        }
        return isValidEmail;   
    }
    
    public boolean isValidFinalValidation() {

        //Find all relevant text strings to validate
        String txtReceiverString = activity.getTxtReceiver().getText().toString();

        //Validate all fields
        boolean isValidEmail = isValidEmail(txtReceiverString.trim());
        boolean isValidSecurityLabel = activity.getSprSecurityLabel().getSelectedItemPosition() != 0;

        boolean isValidDataFields = isValidEmail && isValidSecurityLabel;
        if (isValidDataFields) {
            return true;
        }
        
        return false; //If we end up here, it means validation has failed
    }

    /**
     * Returns if email matches the email pattern.
     */
    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
