/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import java.net.URI;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.ntnu.kpro.core.service.implementation.NetworkService.NetworkServiceImp.BoxName;

/**
 *
 * @author Nicklas
 */
public class ModelProxy {

    public static interface IUser {

        public void setName(String name);

        public String getName();

        public boolean authorize(IUser u);
    }

    public static interface IXOMessage extends Comparable<XOMessage>, Parcelable {

        Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public XOMessage createFromParcel(Parcel parcel) {
                try {
                    return new XOMessage(parcel);
                } catch (ParseException ex) {
                    Logger.getLogger(XOMessage.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            public XOMessage[] newArray(int i) {
                return new XOMessage[i];
            }
        };

        void addAttachment(List<Uri> is);

        int compareTo(XOMessage o);

        int describeContents();

        boolean equals(Object obj);

        List<Uri> getAttachments();

        Date getDate();

        String getFrom();

        XOMessageSecurityLabel getGrading();

        String getHtmlBody();

        String getId();

        boolean getOpened();

        XOMessagePriority getPriority();

        String getStrippedBody();

        String getSubject();

        String getTo();

        XOMessageType getType();

        void setOpened(boolean opened);

        String toString();

        public BoxName getBoxAffiliation();
        public void setBoxAffiliation(BoxName box);
        void writeToParcel(Parcel parcel, int i);
    }
}
