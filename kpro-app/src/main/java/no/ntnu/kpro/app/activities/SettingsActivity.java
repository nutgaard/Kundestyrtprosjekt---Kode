/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import no.ntnu.kpro.app.R;

/**
 *
 * @author Kristin
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        String strategy = prefs.getString("message_retrieval", "Push");
        String s = prefs.getString("security_labels_available", "NATO_CONFIDENTIAL");

        
        
        Log.i("Settings", strategy);
        Log.i("Settings", s);
        CharSequence[] labels = s.split(getResources().getString(R.string.separator));
        for (int i = 0; i < labels.length; i++) {
            Log.i("Security label: ", labels[i].toString());
        }
        
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        //TODO: Set security labels spinner according to the choices
        //TODO: Set pull/push according to the choice
    }

    public void onSharedPreferenceChanged(SharedPreferences sp, String string) {
        updatePrefSummary(findPreference(string));
        Log.w("Settings", "NOE SKJEDDE");
    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            p.setSummary(editTextPref.getText());
        }

    }
}
