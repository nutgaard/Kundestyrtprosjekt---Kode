/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessageType;
import no.ntnu.kpro.core.service.interfaces.PersistenceService;

/**
 *
 * @author Nicklas
 */
public class Converter {
    private static Converter instance;
    private PersistenceService persistence;
    public static String TAG = "CONVERTER";
    public static String LABEL = "SIO-Label";
    public static String PRIORITY = "MMHS-Primary-Precedence";
    public static String TYPE = "MMHS-Message-Type";
    
    private Converter(PersistenceService p){
        this.persistence = p;
    }
    public static void setup(PersistenceService ps){
        instance = new Converter(ps);
    }
    public static Converter getInstance() {
        return instance;
    }
    
    public MimeMessage convertToMime(Session session, IXOMessage message) throws Exception {
        MimeMessage mm = new MimeMessage(session);
        Multipart multipart = new MimeMultipart();

        mm.setFrom(new InternetAddress(message.getFrom()));
        mm.setRecipients(Message.RecipientType.TO, InternetAddress.parse(message.getTo()));
        mm.setSubject(message.getSubject(), "UTF-8");
        mm.setHeader("Content-Type", "text/plain; charset=UTF-8");
        mm.addHeader(PRIORITY, message.getPriority().toString());
        mm.addHeader(LABEL, message.getGrading().getHeaderValue());
        mm.addHeader(TYPE, message.getType().toString());
        mm.setSentDate(message.getDate());

        //Add body
        MimeBodyPart body = new MimeBodyPart();
//        body.setText(message.getStrippedBody(), "UTF-8");
        body.setContent(message.getStrippedBody(), "text/plain");
        multipart.addBodyPart(body);

        //Add attachments

        for (URI uri : message.getAttachments()) {
            File f = new File(uri.getPath());
            MimeBodyPart attachment = new MimeBodyPart();
//            attachment.setHeader("Content-Transfer-Encoding", "base64");
//            attachment.setHeader("Content-Type", "image/jpg");
//            attachment.setHeader("Content-ID", f.getName());
            attachment.setFileName(f.getName());
//            attachment.setDisposition("inline");
            DataSource source = new FileDataSource(uri.getPath());
            attachment.setDataHandler(new DataHandler(source));
            multipart.addBodyPart(attachment);
        }
        mm.setContent(multipart);
        return mm;
    }

    public XOMessage convertToXO(Message message) throws Exception {        
        String id, from, to, subject, body = "";
        XOMessagePriority priority;
        XOMessageSecurityLabel label;
        XOMessageType type;
        List<URI> attachments = new LinkedList<URI>();
        Date date;
        if (message instanceof MimeMessage) {
            MimeMessage m = (MimeMessage) message;
            id = m.getMessageID();
            from = convertAddressArray(m.getFrom());
            to = convertAddressArray(m.getRecipients(Message.RecipientType.TO));
            subject = m.getSubject();
            
            if (m.getContentType().contains("multipart")) {
                System.out.println("Message was multipart");
                Multipart multipart = (Multipart) m.getContent();
                System.out.println("Found " + multipart.getCount() + " different parts");
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart part = multipart.getBodyPart(i);
                    if (part.getContentType().toLowerCase().contains("text")) {
                        System.out.println("Found text part");
                        body = (String) multipart.getBodyPart(i).getContent();
                    }else if (part.getContentType().toLowerCase().contains("image")) {
                        System.out.println("Found image part: "+part.getFileName());
                        String filename = part.getFileName();
                        InputStream is = part.getDataHandler().getInputStream();
                        System.out.println("Fetching outputstream");
                        File f = persistence.createOutputFile(filename);
                        OutputStream os = new FileOutputStream(f);
                        int d;
                        System.out.println("writing to file");
                        while ((d = is.read()) >=0){
                            os.write(d);
                        }
                        is.close();
                        os.close();
                        attachments.add(f.toURI());
                    }else {
                        System.out.println("Unknown type: "+part.getContentType());
                    }
                }
            } else {
                body = m.getContent().toString();
            }
            priority = EnumHelper.getEnumValue(XOMessagePriority.class, m.getHeader(PRIORITY))[0];
            //            label = EnumHelper.getEnumValue(XOMessageSecurityLabel.class, m.getHeader(LABEL))[0];
            label = secLabelParsing(m.getHeader(LABEL));
            type = EnumHelper.getEnumValue(XOMessageType.class, m.getHeader(TYPE))[0];
            date = m.getReceivedDate();

            XOMessage xo = new XOMessage(id, from, to, subject, body, label, priority, type, date);
            xo.addAttachment(attachments);
            return xo;
        }
        return null;
//        return new XOMessage(from, to, subject, body, label, priority, type, date);
    }

    private static XOMessageSecurityLabel secLabelParsing(String[] secLabels) {
        if (secLabels == null || secLabels.length == 0) {
            return XOMessageSecurityLabel.BEGRENSET;
        }
        String s = secLabels[0];
        for (XOMessageSecurityLabel e : XOMessageSecurityLabel.values()) {
            if (s.contains(e.toString())) {
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
}
