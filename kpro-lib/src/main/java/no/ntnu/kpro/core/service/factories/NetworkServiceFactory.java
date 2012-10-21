/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.factories;

import java.util.Properties;
import javax.mail.Address;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.implementation.NetworkService.NetworkServiceImp;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas
 */
public class NetworkServiceFactory {
    public static NetworkService createService() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", "smtp.gmail.com");
        props.put("mail.smtps.socketFactory.port", "465");
        props.put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.port", "465");

        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.socketFactory.port", "993");
        props.put("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.imaps.auth", "true");
        props.put("mail.imaps.port", "993");
        return new NetworkServiceImp("kprothales", "kprothales2012", "kprothales@gmail.com", props);
    }
    public static void main(String[] args) {
        NetworkService ns = createService();
        ns.addListener(new NetworkService.Callback() {

            public void mailSent(XOMessage message, Address[] invalidAddress) {
                System.out.println("Mailsent: "+message);
            }

            public void mailSentError(XOMessage message, Exception ex) {
                System.out.println("MailsentError: "+message);
            }

            public void mailReceived(XOMessage message) {
                System.out.println("Mailreceived: "+message);
            }

            public void mailReceivedError(Exception ex) {
                System.out.println("MailreceivedError");
            }
        });
    }
}
