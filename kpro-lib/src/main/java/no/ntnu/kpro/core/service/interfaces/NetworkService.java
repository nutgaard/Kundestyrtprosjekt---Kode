/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.interfaces;

import javax.mail.Address;
import no.ntnu.kpro.core.model.Box;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;

/**
 *
 * @author Nicklas
 */
public abstract class NetworkService extends ServiceInterface<NetworkService.Callback> {

    public interface Callback {
        public void mailSent(IXOMessage message, Address[] invalidAddress);
        public void mailSentError(IXOMessage message, Exception ex);
        public void mailReceived(IXOMessage message);
        public void mailReceivedError(Exception ex);
    }
//    public boolean sendMail(final String recipient, final String subject, final String body);
    public abstract void send(IXOMessage message);
    public abstract Box<IXOMessage> getOutbox();
    public abstract Box<IXOMessage> getInbox();
    public abstract void close();
}
