/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import com.sun.mail.imap.IMAPFolder;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.interfaces.NetworkService.Callback;

/**
 *
 * @author Nicklas
 */
public class IMAPS {

    private IMAPSIdle imapsIdle;
    private NetworkServiceImp imp;
    private Authenticator auth;
    private Session session;

    public IMAPS(NetworkServiceImp imp, final String username, final String password) {
        this.imp = imp;
        this.auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
        this.session = Session.getInstance(imp.getSettings(), auth);
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
                XOMessage message = NetworkServiceImp.convertToXO(messages[i]);
                for (Callback c : imp.getListeners()) {
                    c.mailReceived(message);
                }
            }
        } catch (Exception e) {
            for (Callback c : imp.getListeners()) {
                c.mailReceivedError();
            }
        }
    }

    public class IMAPSIdle implements Runnable, MessageCountListener {

        private boolean run = true;
        private Session session;
        
        public IMAPSIdle(Authenticator auth) {
            this.session = Session.getInstance(imp.getSettings(), auth);
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
                for (Callback c : imp.getListeners()) {
                    c.mailReceived(NetworkServiceImp.convertToXO(msg));
                }
            }
        }

        public void messagesRemoved(MessageCountEvent mce) {
        }
    }
}
