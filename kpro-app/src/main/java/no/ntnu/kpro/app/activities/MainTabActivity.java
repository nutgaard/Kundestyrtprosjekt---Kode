/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.service.ServiceProvider;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Kristin
 */
public class MainTabActivity extends WrapperActivity implements TabHost.OnTabChangeListener, NetworkService.Callback {

    final static String TAG = "KPRO-GUI-MAINTAB";
    TabHost tabHost;
    private XOMessage messageToBeSendt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab);
        Log.i(TAG, "Starting onCreate in MainTabActivity");
        Log.i(TAG, "Setting content view");

//        IntentFilter intentFilter = new IntentFilter("FlashOverride");
//        getApplicationContext().registerReceiver(new BroadcastReceiver() {
//
//            @Override
//            public void onReceive(Context cntxt, Intent intent) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//        }, null);
       
        
        
        
        // Finding the tabhost
        tabHost = (TabHost) findViewById(android.R.id.tabhost);

        tabHost.setup(this.getLocalActivityManager());

        // Tab for Folders
        TabSpec folderSpec = tabHost.newTabSpec("Folders");
        // setting Title and Icon for the Tab
        folderSpec.setIndicator("", getResources().getDrawable(R.drawable.ic_tab_folder));
        Intent folderIntent = new Intent().setClass(this, FoldersActivity.class);
        folderSpec.setContent(folderIntent);
        Log.i(TAG, "Creating tabspec for folders");

        // Tab for New Message
        TabSpec newMessageSpec = tabHost.newTabSpec("New message");
        newMessageSpec.setIndicator("", getResources().getDrawable(R.drawable.ic_tab_new));
        Intent newMessageIntent = new Intent(this, SendMessageActivity.class);
        newMessageSpec.setContent(newMessageIntent);
        Log.i(TAG, "Creating tabspec for new message");

        TabSpec instantSpec = tabHost.newTabSpec("Instant");
        instantSpec.setIndicator("", getResources().getDrawable(R.drawable.ic_tab_instant_message));
        Intent instantIntent = new Intent(this, InstantMessageActivity.class);
        instantSpec.setContent(instantIntent);
        Log.i(TAG, "Creating tabspec for instant message");

//        // Tab for Contacts
//        TabSpec contactsSpec = tabHost.newTabSpec("Contacts");
//        contactsSpec.setIndicator("", getResources().getDrawable(R.drawable.ic_tab_contact));
//        Intent contactsIntent = new Intent(this, ContactsActivity.class);
//        contactsSpec.setContent(contactsIntent);
//        Log.i(TAG, "Creating tabspec for contacts");

        // Tab for Settings
        TabSpec settingsSpec = tabHost.newTabSpec("Settings");
        settingsSpec.setIndicator("", getResources().getDrawable(R.drawable.ic_tab_settings));
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        settingsSpec.setContent(settingsIntent);
        Log.i(TAG, "Creating tabspec for settings");

        // Adding all TabSpec to TabHost
        tabHost.addTab(folderSpec); // Adding photos tab
        tabHost.addTab(newMessageSpec); // Adding songs tab
        tabHost.addTab(instantSpec); // Adding videos tab
       // tabHost.addTab(contactsSpec);
        tabHost.addTab(settingsSpec);
        Log.i(TAG, "Adding tabspecs to tabhost");

        try {
            String parent = getIntent().getStringExtra("parent");
            if (parent.equals("widget")) {
                tabHost.setCurrentTab(2);
            } else {
                tabHost.setCurrentTab(0);
            }
        } catch (NullPointerException npe) {
            tabHost.setCurrentTab(0);
        }

        Log.i(TAG, "Setting height on the tabs");
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = 50;
        }

        tabHost.setOnTabChangedListener(this);
        TabWidget tabWidget = tabHost.getTabWidget();

        final TabHost th = tabHost;

        for (int i = 0; i < tabWidget.getTabCount(); i++) {
            tabWidget.getChildAt(i).setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();

                    if (action == MotionEvent.ACTION_UP) {
                        String currentTabTag = (String) th.getCurrentTabTag();
                        String clickedTabTag = (String) v.getTag();

                        boolean isLoggedIn = true;
                        if (isLoggedIn) {
                            return false; // allows tab change
                        }
                        return true; // doesnt allow tab change
                    }
                    return false;
                }
            });
        }
        
        Intent in = getIntent();
        boolean isFlashOverride = in.getBooleanExtra("flashoverride", false);
        if(isFlashOverride){
            XOMessage currentMessage = in.getParcelableExtra("message");
            this.mailReceived(currentMessage);
        }
        XOMessage toBeSent = in.getParcelableExtra("messageSend");
        if (toBeSent != null){
            messageToBeSendt = toBeSent;
        }
    }

    // Method for switching the current tab from outside
    public void switchTab(int tab) {
        TabHost th = (TabHost) findViewById(android.R.id.tabhost);
        th.setCurrentTab(tab);
    }

    // Method for when gets connected to service
    @Override
    public void onServiceConnected(ServiceProvider sp) {
        super.onServiceConnected(sp);
        if (messageToBeSendt != null){
            sp.getNetworkService().send(messageToBeSendt);
            messageToBeSendt = null;
        }
    }

    public void onTabChanged(String string) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tabHost.getApplicationWindowToken(), 0);
        final String s = string;
        final boolean isLoggedIn = getServiceProvider().isLoggedin(); //Check in back-end
        if (isLoggedIn) {
        } else {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        }
    }
    
}
