/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.factories;

import android.content.Context;
import android.net.Uri;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Address;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessageType;
import no.ntnu.kpro.core.service.implementation.NetworkService.NetworkServiceImp;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas
 */
public class NetworkServiceFactory {
    public static NetworkService createService(Context c) {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", "smtp.gmail.com");
        props.put("mail.smtps.socketFactory.port", "465");
        props.put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtps.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtps.port", "465");

        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.socketFactory.port", "993");
        props.put("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.imaps.auth", "true");
        props.put("mail.imaps.port", "993");
        return new NetworkServiceImp("kprothales", "kprothales2012", "kprothales@gmail.com", props, c);
    }
    public static void main(String[] args) {
        System.out.println("Hei");
        NetworkService ns = createService(null);
        System.out.println("ei");
        ns.addListener(new NetworkService.Callback() {

            public void mailSent(IXOMessage message, Address[] invalidAddress) {
                System.out.println("Mailsent: "+message);
            }

            public void mailSentError(IXOMessage message, Exception ex) {
                System.out.println("MailsentError: "+message);
            }

            public void mailReceived(IXOMessage message) {
                System.err.println("Mailreceived: "+message);
            }

            public void mailReceivedError(Exception ex) {
                System.out.println("MailreceivedError");
            }
        });
//        List<URI> a = new LinkedList<URI>();
//        a.add(URI.create("./Koala.jpg"));
        XOMessage m = new XOMessage("kprothales@gmail.com", "kprothales@gmail.com", "Sending with attachment", "Look at the attachments, should contain pom.xml", XOMessageSecurityLabel.UGRADERT, XOMessagePriority.FLASH, XOMessageType.DRILL, new Date(), new LinkedList<Uri>());
//        m.addAttachment(a);
        try {
            ns.send(m);
        } catch (Exception ex) {
            Logger.getLogger(NetworkServiceFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
