/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.interfaces;

import javax.mail.Address;
import javax.mail.search.SearchTerm;
import no.ntnu.kpro.core.model.Box;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessageType;

/**
 *
 * @author Nicklas
 */
public abstract class NetworkService extends ServiceInterface<NetworkService.Callback> {

    public interface Callback {
        public void mailSent(XOMessage message, Address[] invalidAddress);
        public void mailSentError(XOMessage message);
        public void mailReceived(XOMessage message);
        public void mailReceivedError();
    }

//    public boolean sendMail(final String recipient, final String subject, final String body);
    public abstract boolean sendMail(final String recipient, final String subject, final String body, XOMessageSecurityLabel label, XOMessagePriority priority, XOMessageType type);
    public abstract void send(XOMessage message);
    public abstract void startIMAPIdle();
    public abstract void stopIMAPIdle();
    public abstract void getMessages(SearchTerm searchterm);
    public abstract void getAllMessages();
    public abstract Box<XOMessage> getOutbox();
    public abstract Box<XOMessage> getInbox();
}
