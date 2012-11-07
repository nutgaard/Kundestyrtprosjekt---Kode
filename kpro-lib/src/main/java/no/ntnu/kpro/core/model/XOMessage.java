/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

import android.net.Uri;
import android.os.Parcel;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import no.ntnu.kpro.core.utilities.EnumHelper;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;
import no.ntnu.kpro.core.service.implementation.NetworkService.NetworkServiceImp.BoxName;
import org.spongycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.spongycastle.mail.smime.SMIMESignedGenerator;

/**
 *
 * @author Nicklas
 */
public class XOMessage implements ModelProxy.IXOMessage {

    public static String LABEL = "SIO-Label";
    public static String PRIORITY = "MMHS-Primary-Precedence";
    public static String TYPE = "MMHS-Message-Type";
    private String from;
    private String to;
    private String subject;
    private List<String> attachments;
    private String htmlBody;
    private String strippedBody;
    private String grading;
    private String priority;
    private String type;
    private Date date;
    private boolean opened = false;
    private String id = new BigInteger(130, new SecureRandom()).toString(32);
    private String boxAffiliation;

    public XOMessage() {
        this("", "", "", "", XOMessageSecurityLabel.CHOOSE_ONE);
    }

    public XOMessage(String from, String to, String subject, String body, XOMessageSecurityLabel label) {
        this(from, to, subject, body, label, XOMessagePriority.ROUTINE, XOMessageType.OPERATION, new Date());
    }

    public XOMessage(String id, String from, String to, String subject, String body, XOMessageSecurityLabel grading, XOMessagePriority priority, XOMessageType type, Date date, Uri... uris) {
        this(from, to, subject, body, grading, priority, type, date, uris);
        this.id = id;
    }

    public XOMessage(String from, String to, String subject, String body, XOMessageSecurityLabel grading, XOMessagePriority priority, XOMessageType type, Date date, Uri... uris) {
        this(from, to, subject, body, grading, priority, type, date, Arrays.asList(uris));
    }

    public XOMessage(String from, String to, String subject, String body, XOMessageSecurityLabel grading, XOMessagePriority priority, XOMessageType type, Date date, List<Uri> uris) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.attachments = new LinkedList<String>();
        addAttachment(uris);
        this.htmlBody = body;
        this.strippedBody = body.replaceAll("\\<.*?>", "");
        this.grading = grading.name();
        this.priority = priority.name();
        this.type = type.name();
        this.date = date;
    }

    public void setBoxAffiliation(BoxName box) {
        this.boxAffiliation = box.toString();
    }

    public BoxName getBoxAffiliation() {
        return BoxName.valueOf(boxAffiliation);
    }

    public XOMessage(Parcel in) throws ParseException {
        this(in.readString(), in.readString(), in.readString(), in.readString(), in.readString(),
                EnumHelper.getEnumValue(XOMessageSecurityLabel.class, in.readString()),
                EnumHelper.getEnumValue(XOMessagePriority.class, in.readString()),
                EnumHelper.getEnumValue(XOMessageType.class, in.readString()),
                new Date(in.readLong()));
        in.readList(attachments, Uri.class.getClassLoader());
        this.opened = false;
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public String getTo() {
        return to;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    public void addAttachment(List<Uri> uri) {
        this.attachments.clear();
        for (Uri u : uri) {
            this.attachments.add(u.toString());
        }
    }

    @Override
    public List<Uri> getAttachments() {
        List<Uri> l = new LinkedList<Uri>();
        for (String uriPath : attachments) {
            l.add(Uri.parse(uriPath));
        }
        return l;
    }

    @Override
    public String getHtmlBody() {
        return htmlBody;
    }

    @Override
    public String getStrippedBody() {
        return strippedBody;
    }

    @Override
    public XOMessageSecurityLabel getGrading() {
        return XOMessageSecurityLabel.valueOf(grading);
    }

    @Override
    public XOMessagePriority getPriority() {
        return XOMessagePriority.valueOf(priority);
    }

    @Override
    public XOMessageType getType() {
        return XOMessageType.valueOf(type);
    }

    @Override
    public boolean getOpened() {
        return opened;
    }

    @Override
    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    @Override
    public int compareTo(XOMessage o) {
        if (this == o) {
            System.out.println("Was equals");
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "XOMessage{" + "from=" + from + ", to=" + to + ", subject=" + subject + ", attachments=" + attachments + ", htmlBody=" + htmlBody + ", strippedBody=" + strippedBody + ", grading=" + grading + ", priority=" + priority + ", type=" + type + ", date=" + date + ", opened=" + opened + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final XOMessage other = (XOMessage) obj;
        if (this.id != null && other.id != null && this.id.equals(other.id)) {
            return true;
        }
        if ((this.from == null) ? (other.from != null) : !this.from.equals(other.from)) {
            return false;
        }
        if ((this.to == null) ? (other.to != null) : !this.to.equals(other.to)) {
            return false;
        }
        if ((this.subject == null) ? (other.subject != null) : !this.subject.equals(other.subject)) {
            return false;
        }
        if (this.attachments != other.attachments && (this.attachments == null || !this.attachments.equals(other.attachments))) {
            return false;
        }
        if ((this.htmlBody == null) ? (other.htmlBody != null) : !this.htmlBody.equals(other.htmlBody)) {
            return false;
        }
        if ((this.strippedBody == null) ? (other.strippedBody != null) : !this.strippedBody.equals(other.strippedBody)) {
            return false;
        }
        if ((this.grading == null) ? (other.grading != null) : !this.grading.equals(other.grading)) {
            return false;
        }
        if ((this.priority == null) ? (other.priority != null) : !this.priority.equals(other.priority)) {
            return false;
        }
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if (this.date != other.date && (this.date == null || !this.date.equals(other.date))) {
            return false;
        }
        if (this.opened != other.opened) {
            return false;
        }
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.boxAffiliation == null) ? (other.boxAffiliation != null) : !this.boxAffiliation.equals(other.boxAffiliation)) {
            return false;
        }
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(from);
        parcel.writeString(to);
        parcel.writeString(subject);
        parcel.writeString(htmlBody);
        //parcel.writeString(strippedBody);
        parcel.writeString(grading.toString());
        parcel.writeString(priority.toString());
        parcel.writeString(type.toString());
        parcel.writeLong(date.getTime());
        parcel.writeList(attachments);
    }

    public static MimeMessage convertToMime(Session session, XOMessage message) throws Exception {
        SMIMESignedGenerator gen = new SMIMESignedGenerator();
        X509Certificate signCert = null;
        KeyPair signKP = null;
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
        mm.addHeader(LABEL, XOMessageSecurityLabel.valueOf(message.grading).getHeaderValue());
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
            System.out.println("E: " + e);
            System.out.println("S: " + s);
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

        public static Comparator<IXOMessage> getSendingPriority() {
            return new Comparator<IXOMessage>() {
                public int compare(IXOMessage o1, IXOMessage o2) {
                    int p = o1.getPriority().compareTo(o2.getPriority());
                    return p == 0 ? o2.getDate().compareTo(o1.getDate()) : p;
                }
            };
        }

        public static Comparator<IXOMessage> getDateComparator(final boolean descending) {
            return new Comparator<IXOMessage>() {
                public int compare(IXOMessage o1, IXOMessage o2) {
                    return descending ? o2.getDate().compareTo(o1.getDate()) : o1.getDate().compareTo(o2.getDate());
                }
            };
        }

        public static Comparator<IXOMessage> getSenderComparator(final boolean descending) {
            return new Comparator<IXOMessage>() {
                public int compare(IXOMessage o1, IXOMessage o2) {
                    int p = descending ? o2.getFrom().compareToIgnoreCase(o1.getFrom()) : o1.getFrom().compareTo(o2.getFrom());
                    return (p == 0) ? getDateComparator(descending).compare(o1, o2) : p;
                }
            };
        }

        public static Comparator<IXOMessage> getPriorityComparator(final boolean descending) {
            return new Comparator<IXOMessage>() {
                public int compare(IXOMessage o1, IXOMessage o2) {
                    int p = o1.getPriority().getNumeric() - o2.getPriority().getNumeric() * (descending ? -1 : 1);
                    return (p == 0) ? getDateComparator(descending).compare(o1, o2) : p;
                }
            };
        }

        public static Comparator<IXOMessage> getLabelComparator(final boolean descending) {
            return new Comparator<IXOMessage>() {
                public int compare(IXOMessage o1, IXOMessage o2) {
                    int p = o1.getGrading().getSortVal() - o2.getGrading().getSortVal() * (descending ? -1 : 1);
                    return (p == 0) ? getDateComparator(descending).compare(o1, o2) : p;
                }
            };
        }

        public static Comparator<IXOMessage> getTypeComparator(final boolean descending) {
            return new Comparator<IXOMessage>() {
                public int compare(IXOMessage o1, IXOMessage o2) {
                    int p = o1.getType().getNumval() - o2.getType().getNumval() * (descending ? -1 : 1);
                    return (p == 0) ? getDateComparator(descending).compare(o1, o2) : p;
                }
            };
        }

        public static Comparator<IXOMessage> getSubjectComparator(final boolean descending) {
            return new Comparator<IXOMessage>() {
                public int compare(IXOMessage o1, IXOMessage o2) {
                    int p = descending ? o2.getSubject().compareToIgnoreCase(o1.getSubject()) : o1.getSubject().compareTo(o2.getSubject());
                    return (p == 0) ? getDateComparator(descending).compare(o1, o2) : p;
                }
            };
        }
    }
}
