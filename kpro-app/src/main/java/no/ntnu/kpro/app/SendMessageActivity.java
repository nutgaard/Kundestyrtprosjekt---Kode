/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
                finish();
            }
        });
    }
}
