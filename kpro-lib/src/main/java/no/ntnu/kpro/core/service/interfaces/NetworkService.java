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
public interface NetworkService extends ServiceInterface {

    public interface callback {
    }

//    public boolean sendMail(final String recipient, final String subject, final String body);
    
    public void send(XOMessage message);
    public void startIMAPIdle();
    public void stopIMAPIdle();
    public void getMessages(FlagTerm flagterm, int no);
    public void getAllMessages();
}
