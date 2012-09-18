/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        
        Button b = (Button) findViewById(R.id.send_button);

        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String receiver = ((EditText) findViewById(R.id.receiver)).getText().toString(); //Only one address atm
                String subject = ((EditText) findViewById(R.id.subject)).getText().toString();
                String content = ((EditText) findViewById(R.id.text)).getText().toString();
                
                Toast confirm = Toast.makeText(SendMessageActivity.this, "Sending message to " + receiver + " with subject \n" + subject, Toast.LENGTH_SHORT);
//                XOMessage mess = new XOMessage(null, receiver, subject, content);
                while (!isConnected()){
                    Thread.yield();
                }
                getServiceProvider().getNetworkService().sendMail(receiver, subject, content);
//                getServiceProvider().getNetworkService().send(mess);
                
                confirm.show();
                
                finish();
            }
        });
    }
    @Override
    public void onServiceConnected(ServiceProvider serviceProvider) {
        super.onServiceConnected(serviceProvider);
        getServiceProvider().register(this);
    }
    public void mailSent(XOMessage message, Address[] invalidAddress) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mailSentError(XOMessage message) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mailReceived(XOMessage message) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mailReceivedError() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }
}
