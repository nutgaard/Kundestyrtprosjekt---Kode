/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import com.sun.mail.imap.IMAPFolder;
import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.service.interfaces.NetworkService.Callback;
import no.ntnu.kpro.core.utilities.Converter;

/**
 *
 * @author Nicklas
 */
public class IMAPS {

    private IMAPSIdle imapsIdle;
    private Properties props;
    private Authenticator auth;
    private Session session;
    private List<NetworkService.Callback> listener;

    public IMAPS(final String password, final Properties props, final Authenticator auth, final List<NetworkService.Callback> listeners) {
        this.props = props;
        this.auth = auth;
        this.session = Session.getInstance(props, auth);
        this.listener = listeners;
    }

    void startIMAPIdle() {
        if (this.imapsIdle != null && this.imapsIdle.isRunning()) {
            this.stopIMAPIdle();
        }
        this.imapsIdle = new IMAPSIdle(auth);
        new Thread(this.imapsIdle).start();
    }

    void stopIMAPIdle() {
        if (this.imapsIdle != null) {
            this.imapsIdle.stop();
            this.imapsIdle = null;
        }
    }

    public void getMessages(SearchTerm searchterm) {
        try {
            Store store = session.getStore("imaps");
            store.connect();
            IMAPFolder inbox = (IMAPFolder) store.getFolder("Inbox");
            inbox.open(Folder.READ_ONLY);
            MimeMessage[] messages = (MimeMessage[]) inbox.search(searchterm);
            for (int i = 0; i < messages.length; i++) {
                XOMessage message = Converter.convertToXO(messages[i]);
                for (Callback c : listener) {
                    c.mailReceived(message);
                }
            }
        } catch (Exception e) {
            for (Callback c : listener) {
                c.mailReceivedError(e);
            }
        }
    }

    public class IMAPSIdle implements Runnable, MessageCountListener {

        private boolean run = true;
        private Session session;
        
        public IMAPSIdle(Authenticator auth) {
            this.session = Session.getInstance(props, auth);
        }

        public void run() {
            try {
                Store store = session.getStore("imaps");
                store.connect();
                IMAPFolder inbox = (IMAPFolder) store.getFolder("Inbox");
                inbox.open(Folder.READ_ONLY);
                inbox.addMessageCountListener(this);

                while (run) {
                    inbox.idle();
                }

            } catch (Exception e) {
            }
        }

        public void stop() {
            this.run = false;
        }

        public boolean isRunning() {
            return this.run;
        }

        public void messagesAdded(MessageCountEvent mce) {
            MimeMessage[] messages = (MimeMessage[]) mce.getMessages();
            for (MimeMessage msg : messages) {
                for (Callback c : listener) {
//                    c.mailReceived(XOMessage.convertToXO(msg));
                }
            }
        }

        public void messagesRemoved(MessageCountEvent mce) {
        }
    }
}
