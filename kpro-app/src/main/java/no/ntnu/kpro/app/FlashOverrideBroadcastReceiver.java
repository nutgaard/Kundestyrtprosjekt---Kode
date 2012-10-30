/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.List;
import no.ntnu.kpro.app.activities.MainTabActivity;

/**
 *
 * @author Kristin
 */
public class FlashOverrideBroadcastReceiver extends BroadcastReceiver {
    final static String TAG = "KPRO-GUI-FLASHOVERRIDE-BROADCASTRECEIVER";
    
    @Override
    public void onReceive(Context cntxt, Intent intent) {
        ActivityManager activityManager = (ActivityManager) cntxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
        boolean isRunning = false;
        String topActivity = services.get(0).topActivity.toString();
        if (topActivity.equals("ComponentInfo{no.ntnu.kpro.app/no.ntnu.kpro.app.activities.MainTabActivity}")) {
            Log.i(TAG, "MainTabActivity is running");
            isRunning = true;
        } else if (topActivity.equals("ComponentInfo{no.ntnu.kpro.app/no.ntnu.kpro.app.activities.MessageViewActivity}")) {
            Log.i(TAG, "MessageViewActivity is running");
            isRunning = true;
        } else if (topActivity.equals("ComponentInfo{no.ntnu.kpro.app/no.ntnu.kpro.app.activities.LoginActivity}")) {
            Log.i(TAG, "LoginActivity is running");
            isRunning = true;
        } else if (topActivity.equals("ComponentInfo{no.ntnu.kpro.app/no.ntnu.kpro.app.ContactsActivity}")){
            Log.i(TAG, "ContactsActivity is running");
            isRunning = true;
        }

        Log.i(TAG, "Receiving broadcast");
        if (isRunning) {
        } else {
            Intent i = new Intent(cntxt, MainTabActivity.class);
            i.putExtra("flashoverride", true);
            i.putExtra("message", intent.getParcelableExtra("message"));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cntxt.startActivity(i);
        }

    }
}
