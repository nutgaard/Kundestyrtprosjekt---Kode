/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import no.ntnu.kpro.core.model.XOMessage;

/**
 *
 * @author Kristin
 */
public class XOMessageAdapter extends ArrayAdapter {
    private final Activity activity;
    private final List messages;
    
     public XOMessageAdapter(Activity activity, List objects) {
        super(activity, R.layout.message_list_item , objects);
        this.activity = activity;
        this.messages = objects;
    }
     
     @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        XOMessageView xoView = null;
 
        if(rowView == null)
        {
            // Get a new instance of the row layout view
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.message_list_item, null);
 
            // Hold the view objects in an object,
            // so they don't need to be re-fetched
            xoView = new XOMessageView();
            xoView.address = (TextView) rowView.findViewById(R.id.from);
            xoView.subject = (TextView) rowView.findViewById(R.id.subject);
            xoView.date = (TextView) rowView.findViewById(R.id.date);
 
            // Cache the view objects in the tag,
            // so they can be re-accessed later
            rowView.setTag(xoView);
        } else {
            xoView = (XOMessageView) rowView.getTag();
        }
 
        // Transfer the stock data from the data object
        // to the view objects
        XOMessage message = (XOMessage)messages.get(position);
        xoView.address.setText(message.getFrom());
        xoView.subject.setText(message.getSubject());
        xoView.date.setText("01.01.2012 00:00");
 
        return rowView;
    }
     
     protected static class XOMessageView {
        protected TextView address;
        protected TextView subject;
        protected TextView date;
    }
}
