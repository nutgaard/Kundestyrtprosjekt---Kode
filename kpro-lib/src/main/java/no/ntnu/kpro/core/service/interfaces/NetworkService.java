/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.interfaces;

import com.sun.mail.imap.IMAPMessage;
import javax.mail.Address;
import no.ntnu.kpro.core.model.Box;
import no.ntnu.kpro.core.model.XOMessage;

/**
 *
 * @author Nicklas
 */
public abstract class NetworkService extends ServiceInterface<NetworkService.Callback> {

    public interface Callback {
        public void mailSent(XOMessage message, Address[] invalidAddress);
        public void mailSentError(XOMessage message, Exception ex);
        public void mailReceived(XOMessage message);
        public void mailReceivedError(Exception ex);
    }
    public interface InternalCallback {
        public void mailSent(XOMessage message, Address[] invalidAddress);
        public void mailSentError(XOMessage message, Exception ex);
        public void mailReceived(IMAPMessage message);
        public void mailReceivedError(Exception ex);
    }

//    public boolean sendMail(final String recipient, final String subject, final String body);
    public abstract void send(XOMessage message);
    public abstract Box<XOMessage> getOutbox();
    public abstract Box<XOMessage> getInbox();
    public abstract void close();
}
