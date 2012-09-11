/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import javax.mail.search.FlagTerm;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas
 */
public class NetworkServiceImp implements NetworkService {
    private SMTPS smtps;
    private IMAPS imaps;
    
    public NetworkServiceImp() {
        this.smtps = new SMTPS(this);
        this.imaps = new IMAPS(this);
    }

    public void send(XOMessage message) {
        
    }

    public void startIMAPIdle() {
        
    }

    public void stopIMAPIdle() {
        
    }

    public void getMessages(FlagTerm flagterm, int no) {
        
    }

    public void getAllMessages() {
        
    }
    
}
