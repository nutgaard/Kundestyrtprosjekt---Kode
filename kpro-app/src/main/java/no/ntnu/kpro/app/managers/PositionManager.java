/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import no.ntnu.kpro.app.R;

/**
 *
 * @author aleksandersjafjell
 */
public class PositionManager implements SharedPreferences.OnSharedPreferenceChangeListener{
    
    Context context;
    LocationManager locationManager;
    
    private int locationUpdateInterval = 5000; //Milliseconds
    private int locationDistance = 5; //Meters
    
    private Location currentLocation = null;
    
    public PositionManager(Context context){
        this.context = context;
        getValuesFromPreferences();
    }
    
    public void startLocationFetching() {
        String serviceString = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) context.getSystemService(serviceString);
        addLocationListener(locationManager);

    } 
    
    private void getValuesFromPreferences(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
        locationUpdateInterval = Integer.parseInt(sharedPrefs.getString("update_interval_time", "5000"));
        locationDistance = Integer.parseInt(sharedPrefs.getString("update_interval_distance", "5"));
        Log.i("POSITIONMANAGER", "Update interval: " + locationUpdateInterval);
        Log.i("POSITIONMANAGER", "Update distance: " + locationDistance);
    }
    
    private void addLocationListener(LocationManager locationManager) {
        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location lctn) {
               currentLocation = lctn;

            }

            public void onStatusChanged(String string, int i, Bundle bundle) {
            }

            public void onProviderEnabled(String string) {
            }

            public void onProviderDisabled(String string) {
            }
        };
        String bestProvider = locationManager.getBestProvider(criteria, true);

        if (bestProvider != null) {
            locationManager.requestLocationUpdates(bestProvider, locationUpdateInterval, locationDistance, locationListener);
        } else {
            //Toast noProviderFoundMessage = Toast.makeText(SendMessageActivity.this, getString(R.string.noLocationProviderFound), RESULT_OK);
            //noProviderFoundMessage.show();
        }

    }
    
    public Location getCurrentLocation(){
        return this.currentLocation;
    }
    
     public String getCurrentLocationDescription() {
        String locLongString = "";

        if (currentLocation != null) {
            double lat = currentLocation.getLatitude();
            double lng = currentLocation.getLongitude();

            locLongString += "\n" + context.getString(R.string.myLocationNow) + "\n";
            locLongString += context.getString(R.string.locationLatitude) + lat + "\n";
            locLongString += context.getString(R.string.locationLongditude) + lng + "\n";
            locLongString += "Accuracy is " + currentLocation.getAccuracy() + " meters";
           
        } 
        return locLongString;
    }

    public void onSharedPreferenceChanged(SharedPreferences sp, String string) {
        getValuesFromPreferences();
    }
}
