/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import no.ntnu.kpro.app.ContactsActivity;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.core.service.ServiceProvider;

/**
 *
 * @author Kristin
 */
public class MainTabActivity extends WrapperTabActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab);



        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);



        tabHost.setup(this.getLocalActivityManager());

        // Tab for Photos
        TabSpec photospec = tabHost.newTabSpec("Folders");
        // setting Title and Icon for the Tab
        photospec.setIndicator(null, getResources().getDrawable(R.drawable.ic_tab_folder));
        Intent photosIntent = new Intent().setClass(this, FoldersActivity.class);
        photospec.setContent(photosIntent);

        // Tab for Songs
        TabSpec songspec = tabHost.newTabSpec("New message");
        songspec.setIndicator(null, getResources().getDrawable(R.drawable.ic_tab_new));
        Intent songsIntent = new Intent(this, SendMessageActivity.class);
        songspec.setContent(songsIntent);


        // Tab for Videos
        TabSpec videospec = tabHost.newTabSpec("Contacts");
        videospec.setIndicator(null, getResources().getDrawable(R.drawable.ic_tab_contact));
        Intent videosIntent = new Intent(this, ContactsActivity.class);
        videospec.setContent(videosIntent);



        // Adding all TabSpec to TabHost
        tabHost.addTab(photospec); // Adding photos tab
        tabHost.addTab(songspec); // Adding songs tab
        tabHost.addTab(videospec); // Adding videos tab

        tabHost.setCurrentTab(0);

        LinearLayout ll = (LinearLayout) tabHost.getChildAt(0);
        TabWidget tw = (TabWidget) ll.getChildAt(0);

        // for changing the text size of first tab
        RelativeLayout rllf = (RelativeLayout) tw.getChildAt(0);
        TextView lf = (TextView) rllf.getChildAt(1);
        lf.setTextSize(1);
    }

    public void switchTab(int tab) {
        TabHost th = (TabHost) findViewById(android.R.id.tabhost);
        th.setCurrentTab(tab);
    }

    @Override
    public void onServiceConnected(ServiceProvider sp) {
        super.onServiceConnected(sp);

    }
}
