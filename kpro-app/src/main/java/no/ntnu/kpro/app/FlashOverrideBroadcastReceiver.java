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
public class FlashOverrideBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context cntxt, Intent intent) {
        ActivityManager activityManager = (ActivityManager)cntxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> tasks = activityManager.getRunningAppProcesses();
        boolean isRunning = false;
        for(int i = 0; i<tasks.size(); i++){
            Log.i("KPRO-TASKS", tasks.get(i).processName);
            if(tasks.get(i).processName.equals("no.ntnu.kpro.app") &&
                tasks.get(i).importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE){
                isRunning = true;
                Log.i("KPRO-TASKS", "App is running");
            }
        }
        Log.i("KPRO", "Receving broadcast");    
        Intent i = new Intent(cntxt, MainTabActivity.class);
        i.putExtra("message", intent.getParcelableExtra("message"));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //cntxt.startActivity(i);
    }
    
}
