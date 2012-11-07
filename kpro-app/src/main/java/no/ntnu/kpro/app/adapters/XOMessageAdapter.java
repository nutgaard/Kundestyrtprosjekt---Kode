/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;

/**
 *
 * @author Kristin
 */
public class XOMessageAdapter extends ArrayAdapter {
    private final static String TAG = "XO-MESSAGE-ADAPTER";
    private final Activity activity;
    private final List messages;
    private boolean isInbox = true;
    private Resources resources;

    public XOMessageAdapter(Activity activity, List objects, Resources res) {
        super(activity, R.layout.message_list_item, objects);
        this.activity = activity;
        this.messages = objects;
        //this.isInbox = isInbox;
        this.resources = res;
    }

    public void setIsInbox(boolean value) {
        this.isInbox = value;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        XOMessageView xoView = null;

        IXOMessage message = (IXOMessage) messages.get(position);
        Log.d(TAG, "Got message: "+message);
        if (rowView == null) {
            // Get a new instance of the row layout view
            LayoutInflater inflater = activity.getLayoutInflater();
            if (isInbox) {
                if (message.getOpened()) {
                    rowView = inflater.inflate(R.layout.message_list_item, null);
                } else {
                    rowView = inflater.inflate(R.layout.message_list_item_unread, null);
                }
            }
            else{
                rowView = inflater.inflate(R.layout.message_list_item, null);
            }


            // Hold the view objects in an object,
            // so they don't need to be re-fetched
            xoView = new XOMessageView();
            xoView.address = (TextView) rowView.findViewById(R.id.lblFrom);
            xoView.subject = (TextView) rowView.findViewById(R.id.lblSubject);
            xoView.date = (TextView) rowView.findViewById(R.id.lblDate);
            xoView.label = (TextView) rowView.findViewById(R.id.imvLabel);
            xoView.priority = (TextView) rowView.findViewById(R.id.imvPriority);
            xoView.attachments = (ImageView) rowView.findViewById(R.id.imvAttachment);

            // Cache the view objects in the tag,
            // so they can be re-accessed later
            rowView.setTag(xoView);
        } else {
            xoView = (XOMessageView) rowView.getTag();
        }

        // Transfer the stock data from the data object
        // to the view objects


        if (isInbox) {
            xoView.address.setText(message.getFrom());
        } else {
            xoView.address.setText(message.getTo());
        }
        
        if(message.getAttachments().size() > 0){
            xoView.attachments.setVisibility(View.VISIBLE);
        }
        else{
            xoView.attachments.setVisibility(View.GONE);
        }
        
        xoView.subject.setText(message.getSubject());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        Date date = message.getDate();

        xoView.date.setText(dateFormat.format(date));

        xoView.label.setText(message.getGrading().getShortValue());
        xoView.priority.setText(message.getPriority().getAlpha());

        String shortVal = message.getGrading().getShortValue();
        if (shortVal.equals("nu") || shortVal.equals("ug") || shortVal.equals("uc")) {
            xoView.label.setTextColor(resources.getColor(R.color.SIOLabelBlack));
        } else {
            xoView.label.setTextColor(resources.getColor(R.color.SIOLabelRed));
        }

        return rowView;
    }

    protected static class XOMessageView {

        protected TextView address;
        protected TextView subject;
        protected TextView date;
        protected TextView label;
        protected TextView priority;
        protected ImageView attachments;
        //protected ImageView classification;
        //protected ImageView priority;
    }
}
