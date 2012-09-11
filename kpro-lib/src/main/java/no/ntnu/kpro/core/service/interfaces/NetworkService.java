/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.interfaces;

import javax.mail.search.FlagTerm;
import no.ntnu.kpro.core.model.XOMessage;

/**
 *
 * @author Nicklas
 */
public abstract class NetworkService extends ServiceInterface {

    public interface callback {
        public enum event{
            MAIL_OK, MAIL_ERROR;
        }
    }

//    public boolean sendMail(final String recipient, final String subject, final String body);
    
    public abstract void send(XOMessage message);
    public abstract void startIMAPIdle();
    public abstract void stopIMAPIdle();
    public abstract void getMessages(FlagTerm flagterm, int no);
    public abstract void getAllMessages();
}
