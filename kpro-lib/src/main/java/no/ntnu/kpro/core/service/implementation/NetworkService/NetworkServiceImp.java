/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import com.sun.mail.imap.IMAPMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import no.ntnu.kpro.core.model.Box;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAP;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAPPull;
import no.ntnu.kpro.core.service.implementation.NetworkService.SMTP.SMTP;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.utilities.Converter;

/**
 *
 * @author Nicklas
 */
public class NetworkServiceImp extends NetworkService implements NetworkService.InternalCallback {

    class Pair<S, T> {

        S first;
        T second;

        public Pair(S f, T s) {
            this.first = f;
            this.second = s;
        }
    }

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
    private Map<String, Pair<IMAPMessage, XOMessage>> cache;

    public NetworkServiceImp(final String username, final String password, final String mailAdr) {
        this(username, password, mailAdr, new Properties());
    }

    public NetworkServiceImp(final String username, final String password, final String mailAdr, Properties properties) {
        this.smtp = new SMTP(username, password, mailAdr, properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        }, this);
        this.imap = new IMAP(new IMAPPull(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        }, this, 10));
        this.cache = new HashMap<String, Pair<IMAPMessage, XOMessage>>();
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
        for (NetworkService.Callback cb : listeners) {
            cb.mailSent(message, invalidAddress);
        }
    }

    public void mailSentError(XOMessage message, Exception ex) {
        for (NetworkService.Callback cb : listeners) {
            cb.mailSentError(message, ex);
        }
    }

    public void mailReceived(IMAPMessage message) {
        try {
            String id = message.getHeader("Message-ID")[0];
            if (cache.containsKey(id)) {
                return;
            }
            XOMessage xo = Converter.convertToXO(message);
            cache.put(id, new Pair<IMAPMessage, XOMessage>(message, xo));
            getInbox().add(xo);
            for (NetworkService.Callback cb : listeners) {
                cb.mailReceived(xo);
            }
        } catch (Exception ex) {
            Logger.getLogger(NetworkServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void mailReceivedError(Exception ex) {
        for (NetworkService.Callback cb : listeners) {
            cb.mailReceivedError(ex);
        }
    }
}
