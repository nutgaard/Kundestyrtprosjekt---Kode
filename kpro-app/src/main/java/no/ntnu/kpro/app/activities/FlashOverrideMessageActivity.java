/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import no.ntnu.kpro.app.R;

/**
 *
 * @author Kristin
 */
public class FlashOverrideMessageActivity extends WrapperActivity{
    static final String TAG = "KPRO-GUI-FLASHOVERRIDE";
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Dialog dialog = new Dialog(FlashOverrideMessageActivity.this);
        dialog.setContentView(R.layout.dialog_flash_override);
        dialog.setTitle("Important message received");
        dialog.setCancelable(true);
        
        
        Button btnOpen = (Button) dialog.findViewById(R.id.btnOpen);
        btnOpen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Log.i(TAG, "Clicking open");
            }
        });
        
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }
        });
        
        dialog.show();
    }
}
