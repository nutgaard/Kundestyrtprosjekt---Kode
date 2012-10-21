/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import no.ntnu.kpro.core.helpers.EnumHelper;
import org.spongycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.spongycastle.mail.smime.SMIMESignedGenerator;

/**
 *
 * @author Nicklas
 */
public class XOMessage implements Comparable<XOMessage>, Parcelable {

    public static String LABEL = "SIO-Label";
    public static String PRIORITY = "MMHS-Primary-Precedence";
    public static String TYPE = "MMHS-Message-Type";
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
    private boolean opened = false;

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
        this.opened = true;
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

    public boolean getOpened(){
        return opened;
    }
    
    public void setOpened(boolean opened){
        this.opened = opened;
    }
    
    public int compareTo(XOMessage o) {
        if (this == o) {
            System.out.println("Was equals");
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "XOMessage{" + "from=" + from + ", to=" + to + ", subject=" + subject + ", attachments=" + attachments + ", htmlBody=" + htmlBody + ", strippedBody=" + strippedBody + ", grading=" + grading + ", priority=" + priority + ", type=" + type + ", date=" + date + ", opened=" + opened + '}';
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
        SMIMESignedGenerator gen = new SMIMESignedGenerator();
        X509Certificate signCert = null;
        KeyPair         signKP   = null; 
        gen.addSignerInfoGenerator(new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC").build("SHA1withRSA", signKP.getPrivate(), signCert));
        
        MimeBodyPart msg = new MimeBodyPart();
        msg.setText(message.getStrippedBody());
        MimeMultipart crypoedText = gen.generate(msg);
        
        MimeMessage mm = new MimeMessage(session);
        mm.setFrom(new InternetAddress(message.getFrom()));
        mm.setRecipients(Message.RecipientType.TO, InternetAddress.parse(message.getTo()));

        mm.setSubject(message.getSubject(), "UTF-8");
        mm.setContent(crypoedText, crypoedText.getContentType());
        mm.setContent(message.getStrippedBody(), "UTF-8");
        mm.setHeader("Content-Type", "text/plain; charset=UTF-8");
        mm.addHeader(PRIORITY, message.priority.toString());
        mm.addHeader(LABEL, message.grading.getHeaderValue());
        mm.addHeader(TYPE, message.type.toString());
        mm.setSentDate(message.date);
        return mm;
    }

    public static XOMessage convertToXO(Message message) throws Exception {
        String from, to, subject, body;
        XOMessagePriority priority;
        XOMessageSecurityLabel label;
        XOMessageType type;
        Date date;
        if (message instanceof MimeMessage) {
            MimeMessage m = (MimeMessage) message;
            from = convertAddressArray(m.getFrom());
            to = convertAddressArray(m.getRecipients(Message.RecipientType.TO));
            subject = m.getSubject();
            body = m.getContent().toString();
            priority = EnumHelper.getEnumValue(XOMessagePriority.class, m.getHeader(PRIORITY))[0];
//            label = EnumHelper.getEnumValue(XOMessageSecurityLabel.class, m.getHeader(LABEL))[0];
            label = secLabelParsing(m.getHeader(LABEL));
            type = EnumHelper.getEnumValue(XOMessageType.class, m.getHeader(TYPE))[0];
            date = m.getReceivedDate();

            return new XOMessage(from, to, subject, body, label, priority, type, date);
        }
        return null;
//        return new XOMessage(from, to, subject, body, label, priority, type, date);
    }

    private static XOMessageSecurityLabel secLabelParsing(String[] secLabels) {
        if (secLabels == null || secLabels.length == 0) {
            return null;
        }
        String s = secLabels[0];
        for (XOMessageSecurityLabel e : XOMessageSecurityLabel.values()) {
            System.out.println("E: "+e);
            System.out.println("S: "+s);
            if (s.equalsIgnoreCase(e.getHeaderValue())) {
                return e;
            }
        }
        return null;
    }

    private static String convertAddressArray(Address[] al) {
        StringBuilder sb = new StringBuilder();
        for (Address a : al) {
            sb.append(a.toString()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static class XOMessageSorter {

        public static Comparator<XOMessage> getSendingPriority() {
            return new Comparator<XOMessage>() {
                public int compare(XOMessage o1, XOMessage o2) {
                    int p = o1.priority.compareTo(o2.priority);
                    return p == 0 ? o2.date.compareTo(o1.date) : p;
                }
            };
        }

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
