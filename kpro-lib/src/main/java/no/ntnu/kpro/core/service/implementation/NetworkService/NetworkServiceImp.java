/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import java.util.Properties;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.search.SearchTerm;
import no.ntnu.kpro.core.model.Box;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAP;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAPPull;
import no.ntnu.kpro.core.service.implementation.NetworkService.SMTP.SMTP;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas
 */
public class NetworkServiceImp extends NetworkService implements NetworkService.Callback {

    public enum BoxName {

        INBOX("INBOX", new Box<XOMessage>()),
        SENT("[Gmail]/Sendt e-post", new Box<XOMessage>());
        private String boxName;
        private Box<XOMessage> box;

        private BoxName(String boxName, Box box) {
            this.boxName = boxName;
            this.box = box;
        }

        public String getBoxname() {
            return this.boxName;
        }

        public Box<XOMessage> getBox() {
            return this.box;
        }
    }
    private SMTP smtp;
    private IMAP imap;

    public NetworkServiceImp(final String username, final String password, final String mailAdr) {
        this(username, password, mailAdr, new Properties());
    }

    public NetworkServiceImp(final String username, final String password, final String mailAdr, Properties properties) {
        this.smtp = new SMTP(username, password, mailAdr, properties, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        }, listeners);
        this.imap = new IMAP(new IMAPPull(properties, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        }, listeners, 10));
        addListener(this);
    }

    public void send(XOMessage msg) {
        this.smtp.send(msg);
    }

    @Override
    public Box<XOMessage> getOutbox() {
        return BoxName.SENT.getBox();
    }

    @Override
    public Box<XOMessage> getInbox() {
        return BoxName.INBOX.getBox();
    }
    public void close() {
        smtp.halt();
        imap.halt();
    }
    public void mailSent(XOMessage message, Address[] invalidAddress) {
        getOutbox().add(message);
    }

    public void mailSentError(XOMessage message, Exception ex) {
    }

    public void mailReceived(XOMessage message) {
        getInbox().add(message);
    }

    public void mailReceivedError(Exception ex) {
    }
}
