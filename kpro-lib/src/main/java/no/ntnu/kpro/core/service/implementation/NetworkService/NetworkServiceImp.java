/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import com.sun.mail.smtp.SMTPSSLTransport;
import com.sun.mail.smtp.SMTPTransport;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
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
            this.settings = new Settings();
            this.settings.readFromFile(new FileInputStream("settings.xml"));
            this.username = username;
            this.password = password;
            this.authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            };
            this.session = Session.getInstance(this.settings.getProperties(), this.authenticator);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NetworkServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    public void send(XOMessage msg) {
        try {
            MimeMessage message = convertToMime(msg);
            SMTPTransport transport = (SMTPSSLTransport) session.getTransport("smtps");

            transport.connect(this.settings.getAttribute("mail.smtps.host"), username, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            //Callback
            fireCallback(NetworkService.callback.event.MAIL_OK);
        } catch (Exception ex) {
            fireCallback(NetworkService.callback.event.MAIL_ERROR);
        }
    }

    public void startIMAPIdle() {
    }

    public void stopIMAPIdle() {
    }

    public void getMessages(FlagTerm flagterm, int no) {
    }

    public void getAllMessages() {
    }

    private MimeMessage convertToMime(XOMessage message) {
        return null;
    }

    private XOMessage convertToXO(MimeMessage message) {
        return null;
    }
}
