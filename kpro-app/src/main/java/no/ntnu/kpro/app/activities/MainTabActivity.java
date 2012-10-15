/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import no.ntnu.kpro.app.ContactsActivity;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.core.service.ServiceProvider;

/**
 *
 * @author Kristin
 */
public class MainTabActivity extends WrapperTabActivity{
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab);
 
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);

        tabHost.setup(this.getLocalActivityManager());  
 
        // Tab for Photos
        TabSpec photospec = tabHost.newTabSpec("Folders");
        // setting Title and Icon for the Tab
        photospec.setIndicator("Folders", getResources().getDrawable(R.drawable.ic_tab_folder));
        Intent photosIntent = new Intent().setClass(this, FoldersActivity.class);
        photospec.setContent(photosIntent);
 
        // Tab for Songs
        TabSpec songspec = tabHost.newTabSpec("New message");
        songspec.setIndicator("New message", getResources().getDrawable(R.drawable.ic_tab_new));
        Intent songsIntent = new Intent(this, SendMessageActivity.class);
        songspec.setContent(songsIntent);
        
 
        // Tab for Videos
        TabSpec videospec = tabHost.newTabSpec("Contacts");
        videospec.setIndicator("Contacts", getResources().getDrawable(R.drawable.ic_tab_contact));
        Intent videosIntent = new Intent(this, ContactsActivity.class);
        videospec.setContent(videosIntent);
 
        
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(photospec); // Adding photos tab
        tabHost.addTab(songspec); // Adding songs tab
        tabHost.addTab(videospec); // Adding videos tab
        
        tabHost.setCurrentTab(0);
    }
    
    public void switchTab(int tab){
        TabHost th = (TabHost) findViewById(android.R.id.tabhost);
        th.setCurrentTab(tab);
    }
    
    @Override
    public void onServiceConnected(ServiceProvider sp) {
        super.onServiceConnected(sp);
        
    }
}
