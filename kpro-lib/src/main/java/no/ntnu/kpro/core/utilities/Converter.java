/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.utilities;

import java.util.Date;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import no.ntnu.kpro.core.helpers.EnumHelper;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessageType;

/**
 *
 * @author Nicklas
 */
public class Converter {

    public static String TAG = "CONVERTER";
    public static String LABEL = "SIO-Label";
    public static String PRIORITY = "MMHS-Primary-Precedence";
    public static String TYPE = "MMHS-Message-Type";

    public static MimeMessage convertToMime(Session session, XOMessage message) throws Exception {
        MimeMessage mm = new MimeMessage(session);
        mm.setFrom(new InternetAddress(message.getFrom()));
        mm.setRecipients(Message.RecipientType.TO, InternetAddress.parse(message.getTo()));

        mm.setSubject(message.getSubject(), "UTF-8");
        mm.setText(message.getStrippedBody(), "UTF-8");
        mm.setHeader("Content-Type", "text/plain; charset=UTF-8");
        mm.addHeader(PRIORITY, message.getPriority().toString());
        mm.addHeader(LABEL, message.getGrading().getHeaderValue());
        mm.addHeader(TYPE, message.getType().toString());
        mm.setSentDate(message.getDate());
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
//            System.out.println("E: " + e.getHeaderValue());
//            System.out.println("S: " + s);
//            if (s.equalsIgnoreCase(e.getHeaderValue())) {
            if (s.contains(e.toString())) {
//                System.out.println("Returning: "+e.getShortValue());
                return e;
            }
        }
//        System.out.println("Returning null, no grading found");
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
}
