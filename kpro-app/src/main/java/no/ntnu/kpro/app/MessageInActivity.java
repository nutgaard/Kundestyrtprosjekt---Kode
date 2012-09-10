/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 *
 * @author Kristin
 */
public class MessageInActivity extends WrapperActivity{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_item);
        Intent i = getIntent();
        String from = i.getStringExtra("from");
        String subject = i.getStringExtra("subject");
        TextView v = (TextView)findViewById(R.id.from);
        v.setText(from);
        TextView v2 = (TextView)findViewById(R.id.subject);
        v2.setText(subject);
    }
}
