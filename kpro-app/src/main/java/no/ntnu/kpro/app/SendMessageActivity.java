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
import no.ntnu.kpro.core.model.XOMessage;

/**
 *
 * @author Kristin
 */
public class SendMessageActivity extends WrapperActivity{
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
                confirm.show();
                
                finish();
            }
        });
    }
}
