/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.search.SearchTerm;
import no.ntnu.kpro.core.model.Box;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.implementation.NetworkService.SMTP.SMTP;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas
 */
public class NetworkServiceImp extends NetworkService {

    private SMTP smtps;
    private IMAPS imaps;

    public NetworkServiceImp(final String username, final String password, final String mailAdr) {
        this(username, password, mailAdr, new Properties());
    }
    public NetworkServiceImp(final String username, final String password, final String mailAdr, Properties properties) {
        this.smtps = new SMTP(username, password, mailAdr, properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }
    public void send(XOMessage msg) {
        this.smtps.send(msg);
    }

    public void startIMAPIdle() {
        this.imaps.startIMAPIdle();
    }

    public void stopIMAPIdle() {
        this.imaps.stopIMAPIdle();
    }

    public void getMessages(SearchTerm searchterm) {
        this.imaps.getMessages(searchterm);
    }

    public void getAllMessages() {
        this.imaps.getMessages(null);
    }

    @Override
    public Box<XOMessage> getOutbox() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Box<XOMessage> getInbox() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void addListener(NetworkService.Callback listener){
        smtps.getSender().addCallback(listener);
    }
    @Override
    public void removeListener(NetworkService.Callback listener) {
        smtps.getSender().removeCallback(listener);
    }
}
