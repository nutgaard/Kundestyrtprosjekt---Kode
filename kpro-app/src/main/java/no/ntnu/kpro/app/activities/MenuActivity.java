/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import no.ntnu.kpro.app.R;

/**
 *
 * @author Kristin
 */
public class MenuActivity extends WrapperActivity{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }
    
    // Create "popup" menu (shows by pressing MENU button) based on layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu_main , menu);
        return true;
    }

    // Handler for pressing the popup menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_folders:
                Intent i = new Intent(getApplicationContext(), FoldersActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_new:
                Intent i2 = new Intent(getApplicationContext(), SendMessageActivity.class);
                startActivity(i2);
                return true;
            case R.id.menu_new_instant:
                //showHelp();
                return true;
            case R.id.menu_contacts:
                return true;
            case R.id.menu_settings:
                //showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
