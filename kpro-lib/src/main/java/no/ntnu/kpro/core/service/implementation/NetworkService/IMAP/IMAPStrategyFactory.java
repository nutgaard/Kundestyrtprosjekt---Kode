/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Kristin
 */
public class IMAPStrategyFactory {
    public static IMAPStrategy getStrategy(final String username,
            final String password,
            Properties properties, 
            List<NetworkService.Callback> listeners,
            Date lastSeen,
            IMAPCache cache,
            Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isPush = sharedPref.getString("message_retrieval", "Push").equals("Push");
        IMAPStrategy s;
        if (isPush) {
            Log.d("IMAPStrategyFactory", "Push");
            s = new IMAPPush(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            }, lastSeen, listeners, cache);
        } else {
            Log.d("IMAPStrategyFactory", "Pull");
            int pollInterval = Integer.parseInt(sharedPref.getString("poll_interval", "10"));
            Log.d("IMAPStrategyFactory", "Poll interval: " + pollInterval);
            s = new IMAPPull(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            }, lastSeen, listeners, pollInterval, cache);
        }
        return s;
    }
}
