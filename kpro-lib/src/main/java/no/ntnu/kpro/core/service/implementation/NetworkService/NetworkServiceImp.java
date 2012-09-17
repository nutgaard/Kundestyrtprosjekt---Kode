/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;
import no.ntnu.kpro.core.model.Settings;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas
 */
public class NetworkServiceImp extends NetworkService {

    private SMTPS smtps;
    private IMAPS imaps;
    private Settings settings;
    private Authenticator authenticator;
    private String username, password;
    private Session session;

    public NetworkServiceImp(final String username, final String password) {
        try {
            this.smtps = new SMTPS(this);
            this.imaps = new IMAPS(this);
            this.settings = new DummySettings();
//            this.settings.readFromFile(new FileInputStream("settings.xml"));
            this.username = username;
            this.password = password;
            this.authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            };
            this.session = Session.getInstance(this.settings.getProperties(), this.authenticator);
        } catch (Exception ex) {
            Logger.getLogger(NetworkServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
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

    public static MimeMessage convertToMime(XOMessage message) {
        return null;
    }

    public static XOMessage convertToXO(MimeMessage message) {
        return null;
    }

    //Don't make public to everybody
    Session getSession() {
        return this.session;
    }
    Settings getSettings() {
        return this.settings;
    }
    String getUsername() {
        return this.username;
    }
    String getPassword() {
        return this.password;
    }
}
