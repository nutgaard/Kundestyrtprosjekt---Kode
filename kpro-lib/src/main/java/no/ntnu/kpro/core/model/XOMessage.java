/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
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
    private final Date date;

    public XOMessage(String from, String to, String subject, String body, XOMessageSecurityLabel label) {
        this(from, to, subject, body, label, XOMessagePriority.ROUTINE, XOMessageType.OPERATION, new Date());
    }

    public XOMessage(String from, String to, String subject, String body, XOMessageSecurityLabel grading, XOMessagePriority priority, XOMessageType type, Date date) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.attachments = new LinkedList<InputStream>();
        this.htmlBody = body;
        this.strippedBody = body.replaceAll("\\<.*?>", "");
        this.grading = grading;
        this.priority = priority;
        this.type = type;
        this.date = date;
    }

    public XOMessage(Parcel in) throws ParseException {
        this(in.readString(), in.readString(), in.readString(), in.readString(),
                EnumHelper.getEnumValue(XOMessageSecurityLabel.class, in.readString()),
                EnumHelper.getEnumValue(XOMessagePriority.class, in.readString()),
                EnumHelper.getEnumValue(XOMessageType.class, in.readString()),
                new Date(in.readLong()));
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
        if (this == o) {
            System.out.println("Was equals");
            return 0;
        } else {
            return -1;
        }
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

    public Date getDate() {
        return date;
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
        parcel.writeLong(date.getTime());
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
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

    public static MimeMessage convertToMime(Session session, XOMessage message) throws Exception {
        MimeMessage mm = new MimeMessage(session);
        mm.setFrom(new InternetAddress(message.getFrom()));
        mm.setRecipients(Message.RecipientType.TO, InternetAddress.parse(message.getTo()));

        mm.setSubject(message.getSubject(), "UTF-8");
        mm.setText(message.getStrippedBody(), "UTF-8");
        mm.setHeader("Content-Type", "text/plain; charset=UTF-8");
        return mm;
    }

    public static XOMessage convertToXO(MimeMessage message) {
        return null;
    }

    public static class XOMessageSorter {

        public static Comparator<XOMessage> getDateComparator(final boolean descending) {
            return new Comparator<XOMessage>() {
                public int compare(XOMessage o1, XOMessage o2) {
                    return descending ? o2.date.compareTo(o1.date) : o1.date.compareTo(o2.date);
                }
            };
        }

        public static Comparator<XOMessage> getSenderComparator(final boolean descending) {
            return new Comparator<XOMessage>() {
                public int compare(XOMessage o1, XOMessage o2) {
                    return descending ? o2.from.compareToIgnoreCase(o1.from) : o1.from.compareTo(o2.from);
                }
            };
        }

        public static Comparator<XOMessage> getPriorityComparator(final boolean descending) {
            return new Comparator<XOMessage>() {
                public int compare(XOMessage o1, XOMessage o2) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
        }

        public static Comparator<XOMessage> getLabelComparator(boolean descending) {
            return new Comparator<XOMessage>() {
                public int compare(XOMessage o1, XOMessage o2) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
        }

        public static Comparator<XOMessage> getTypeComparator(boolean descending) {
            return new Comparator<XOMessage>() {
                public int compare(XOMessage o1, XOMessage o2) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
        }

        public static Comparator<XOMessage> getSubjectComparator(final boolean descending) {
            return new Comparator<XOMessage>() {
                public int compare(XOMessage o1, XOMessage o2) {
                    return descending ? o2.subject.compareToIgnoreCase(o1.subject) : o1.subject.compareTo(o2.subject);
                }
            };
        }
    }
}
