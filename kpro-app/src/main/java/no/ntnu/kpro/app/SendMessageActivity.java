/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import javax.mail.Address;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.ServiceProvider;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Kristin
 */
public class SendMessageActivity extends WrapperActivity implements NetworkService.Callback{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_message);
        
        Button btnSend = (Button) findViewById(R.id.send_button);
        addBtnSendClickListener(btnSend);
        
        Spinner sprGrading = (Spinner) findViewById(R.id.sprGrading);
        
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
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mailReceivedError() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void addBtnSendClickListener(Button btnSend) {
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String receiver = ((EditText) findViewById(R.id.receiver)).getText().toString(); //Only one address atm
                String subject = ((EditText) findViewById(R.id.subject)).getText().toString();
                String content = ((EditText) findViewById(R.id.text)).getText().toString();
                
                while (!isConnected()){
                    Thread.yield();
                }
                getServiceProvider().getNetworkService().sendMail(receiver, subject, content);
                
                //Is not necessary to have this when callback is implemented, as mailSent() will be called
                Toast confirm = Toast.makeText(SendMessageActivity.this, "Message sent.", Toast.LENGTH_SHORT);                
                confirm.show();
                
                finish();
            }
        });
    }
}
