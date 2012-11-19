/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.factories;

import android.content.Context;
import java.util.Properties;
import javax.mail.Address;
import no.ntnu.kpro.core.model.ModelProxy.IUser;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;
import no.ntnu.kpro.core.model.User;
import no.ntnu.kpro.core.service.implementation.NetworkService.NetworkServiceImp;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas
 */
public class NetworkServiceFactory {
    public static Properties getDefaultProperties() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", "smtp.gmail.com");
        props.put("mail.smtps.socketFactory.port", "465");
        props.put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtps.auth", "true");
        props.put("mail.debug", "false");
        props.put("mail.smtps.port", "465");

        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.socketFactory.port", "993");
        props.put("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.imaps.auth", "true");
        props.put("mail.imaps.port", "993");
        
        props.put("mail.domain", "gmail.com");
        return props;
    }
    public static NetworkService createService(Context c, IUser user) {
        Properties p = getDefaultProperties();
        return new NetworkServiceImp(user.getName(), user.getPassword(), user.getName()+"@"+p.getProperty("mail.domain"), getDefaultProperties(), c);
    }
    public static void main(String[] args) {
        System.out.println("Hei");
        NetworkService ns = createService(null, new User("kprothales", "kprothales2012"));
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
    }
}
