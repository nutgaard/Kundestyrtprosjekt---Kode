/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;
import no.ntnu.kpro.core.model.Box;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.model.XOMessagePriority;
import no.ntnu.kpro.core.model.XOMessageSecurityLabel;
import no.ntnu.kpro.core.model.XOMessageType;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas
 */
public class NetworkServiceImp extends NetworkService {

    private SMTPS smtps;
    private IMAPS imaps;
    private Properties props;
//    private Authenticator authenticator;
    private String username, mailAdr;
//    private Session session;

    public NetworkServiceImp(final String username, final String password, final String mailAdr) {
        this(username, password, mailAdr, new Properties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }
    public NetworkServiceImp(final String username, final String password, final String mailAdr, Properties properties, Authenticator authenticator) {
        try {
            this.props = properties;
            this.smtps = new SMTPS(mailAdr, password, properties, authenticator, listeners);
            this.imaps = new IMAPS(password, props, authenticator, listeners);
//            this.settings.readFromFile(new FileInputStream("settings.xml"));
            this.username = username;
//            this.password = password;
            this.mailAdr = mailAdr;
//            this.authenticator = authenticator;
//            this.session = Session.getInstance(this.props, this.authenticator);
        } catch (Exception ex) {
            Logger.getLogger(NetworkServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } finally {
            System.out.println("Network constructed");
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
//    Session getSession() {
//        return this.session;
//    }
    Properties getSettings() {
        return this.props;
    }
    String getUsername() {
        return this.username;
    }
//    String getPassword() {
//        return this.password;
//    }

    String getUserMail() {
        return this.mailAdr;
    }

    @Override
    public boolean sendMail(String recipient, String subject, String body, XOMessageSecurityLabel label, XOMessagePriority priority, XOMessageType type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Box<XOMessage> getOutbox() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Box<XOMessage> getInbox() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
