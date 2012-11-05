/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import java.util.Date;
import java.util.List;
import no.ntnu.kpro.app.activities.LoginActivity;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessageType;
import no.ntnu.kpro.core.service.ServiceProvider;
import no.ntnu.kpro.core.utilities.EnumHelper;

/**
 *
 * @author Nicklas
 */
public class ShareWithBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Received broadcast");
        System.out.println("Context: " + context.getPackageName());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String from = "kprothales@gmail.com";
        String to = prefs.getString("standard_receiver", "kprothales@gmail.com");
        String subject = "Shared through camera";
        String body = "Look at the attachments";
        XOMessageSecurityLabel secLabel = EnumHelper.getEnumValue(XOMessageSecurityLabel.class, prefs.getString("standard_security_label", "UNCLASSIFIED"));
        XOMessagePriority priority = EnumHelper.getEnumValue(XOMessagePriority.class, prefs.getString("standard_priority", "Override"));
        XOMessageType type = EnumHelper.getEnumValue(XOMessageType.class, prefs.getString("standard_type", "Operation"));
        Date sendingDate = new Date();
        Uri uri = (Uri) intent.getParcelableExtra("uri");
        XOMessage m = new XOMessage(from, to, subject, body, secLabel, priority, type, sendingDate, uri);
        if (ServiceProvider.getInstance() == null) {
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("messageSend", m);
            context.startActivity(i);
        } else {
            ServiceProvider.getInstance().getNetworkService().send(m);
        }
        System.out.println("Sending...");
    }
}
