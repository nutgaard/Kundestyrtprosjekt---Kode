/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Bundle extras = intent.getExtras();
        String action = intent.getAction();

        if (Intent.ACTION_SEND.equals(action)) {
            if (extras.containsKey(Intent.EXTRA_STREAM)) {
                Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);

                String to = sharedPrefs.getString("standard_receiver", "kprothales@gmail.com");
                XOMessageSecurityLabel secLabel = EnumHelper.getEnumValue(XOMessageSecurityLabel.class, sharedPrefs.getString("standard_security_label", "UNCLASSIFIED"));
                XOMessagePriority priority = EnumHelper.getEnumValue(XOMessagePriority.class, sharedPrefs.getString("standard_priority", "Override"));
                XOMessageType type = EnumHelper.getEnumValue(XOMessageType.class, sharedPrefs.getString("standard_type", "Operation"));
                List<Uri> uris = new LinkedList<Uri>();
                uris.add(uri);
                XOMessage m = new XOMessage("MyMailAddress@gmail.com", to, "Shared via camera", "See attachment", secLabel, priority, type, new Date(), uris);
                ServiceProvider.getInstance().getNetworkService().send(m);
            }
        }
    }
}
