/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import no.ntnu.kpro.core.model.Settings;

/**
 *
 * @author Nicklas
 */
public class DummySettings extends Settings {
    public DummySettings(){
        super();
        setup();
    }
    private void setup() {
        this.properties.put("mail.transport.protocol", "smtps");
        this.properties.put("mail.smtps.host", "smtp.gmail.com");
        this.properties.put("mail.smtps.socketFactory.port", "465");
        this.properties.put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        this.properties.put("mail.smtps.auth", "true");
        this.properties.put("mail.smtps.port", "465");

        this.properties.put("mail.store.protocol", "imaps");
        this.properties.put("mail.imaps.host", "imap.gmail.com");
        this.properties.put("mail.imaps.socketFactory.port", "993");
        this.properties.put("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        this.properties.put("mail.imaps.auth", "true");
        this.properties.put("mail.imaps.port", "993");
        
    }
}
