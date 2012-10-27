/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import no.ntnu.kpro.app.R;
/**
 *
 * @author Kristin
 */
public class ContactsAdapter extends ArrayAdapter{
    List<Object> contacts;
    Activity activity;
    final static String TAG = "KPRO-APP_CONTACTSADAPTER";
    public ContactsAdapter(Activity activity, List objects){
        super(activity, R.layout.contact_item, objects);
        this.contacts = objects;
        this.activity = activity;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ContactsAdapter.ContactsView conView = null;
        
        Object contact = contacts.get(position);
        Log.d(TAG, "Adding contact");

        if (rowView == null) {
            // Get a new instance of the row layout view
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.contact_item, null);

            conView = new ContactsAdapter.ContactsView();
            conView.name = (TextView) rowView.findViewById(R.id.lblName);
            conView.address = (TextView) rowView.findViewById(R.id.lblMail);
            
            rowView.setTag(conView);
        } else {
            conView = (ContactsAdapter.ContactsView) rowView.getTag();
        }
        conView.name.setText("Kristin Tønnesen");
        conView.address.setText("kristintonnesen@gmail.com");
        // Transfer the stock data from the data object
        // to the view objects

        return rowView;
    }

    protected static class ContactsView {

        protected TextView name;
        protected TextView address;
    }
}
