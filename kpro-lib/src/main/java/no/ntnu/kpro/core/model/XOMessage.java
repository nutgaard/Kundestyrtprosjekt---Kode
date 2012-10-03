/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import javax.mail.internet.MimeMessage;
import no.ntnu.kpro.core.helpers.EnumHelper;

/**
 *
 * @author Nicklas
 */
public class XOMessage implements Comparable<XOMessage>, Parcelable {
    private final String from;
    private final String to;
    private final String subject;
    private final List<InputStream> attachments;
    private final String htmlBody;
    private final String strippedBody;
    private final XOMessageSecurityLabel grading;
    private final XOMessagePriority priority;
    private final XOMessageType type;

    public XOMessage(String from, String to, String subject, String body, XOMessageSecurityLabel label) {
        this(from, to, subject, body, label, XOMessagePriority.ROUTINE, XOMessageType.OPERATION);
    }

    public XOMessage(String from, String to, String subject, String body, XOMessageSecurityLabel grading, XOMessagePriority priority, XOMessageType type) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.attachments = new LinkedList<InputStream>();
        this.htmlBody = body;
        this.strippedBody = body.replaceAll("\\<.*?>","");
        this.grading = grading;
        this.priority = priority;
        this.type = type;
    }

    public XOMessage(Parcel in){
        this(in.readString(), in.readString(), in.readString(), in.readString(), 
                EnumHelper.getEnumValue(XOMessageSecurityLabel.class, in.readString()), 
                EnumHelper.getEnumValue(XOMessagePriority.class, in.readString()),
                EnumHelper.getEnumValue(XOMessageType.class, in.readString()));
    }
    
    public String getFrom() {
        return from;
    }

    
    public String getTo() {
        return to;
    }

    
    public String getSubject() {
        return subject;
    }
    
    public void addAttachment(InputStream is) {
        this.attachments.add(is);
    }
    
    public List<InputStream> getAttachments() {
        return attachments;
    }

    
    public String getHtmlBody() {
        return htmlBody;
    }

    
    public String getStrippedBody() {
        return strippedBody;
    }

    
    public XOMessageSecurityLabel getGrading() {
        return grading;
    }

    
    public XOMessagePriority getPriority() {
        return priority;
    }

    
    public XOMessageType getType() {
        return type;
    }

    public int compareTo(XOMessage o) {
        return 0;
    }

    
    public String toString() {
        return "XOMessage{" + "from=" + from + ", to=" + to + ", subject=" + subject + ", strippedBody=" + strippedBody + '}';
    }
    
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final XOMessage other = (XOMessage) obj;
        if ((this.from == null) ? (other.from != null) : !this.from.equals(other.from)) {
            return false;
        }
        if ((this.to == null) ? (other.to != null) : !this.to.equals(other.to)) {
            return false;
        }
        if ((this.subject == null) ? (other.subject != null) : !this.subject.equals(other.subject)) {
            return false;
        }
        if ((this.htmlBody == null) ? (other.htmlBody != null) : !this.htmlBody.equals(other.htmlBody)) {
            return false;
        }
        if (this.grading != other.grading) {
            return false;
        }
        if (this.priority != other.priority) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }
    
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(from);
        parcel.writeString(to);
        parcel.writeString(subject);
        //parcel.writeList(attachments); 
        parcel.writeString(htmlBody);
        //parcel.writeString(strippedBody);
        parcel.writeString(grading.toString());
        parcel.writeString(priority.toString());
        parcel.writeString(type.toString());
    }
    
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public XOMessage createFromParcel(Parcel parcel) {
            return new XOMessage(parcel);
        }

        public XOMessage[] newArray(int i) {
            return new XOMessage[i];
        }
    };
    
}
