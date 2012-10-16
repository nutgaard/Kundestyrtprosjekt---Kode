/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;

import com.sun.mail.imap.IMAPFolder;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.MimeMessage;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAPStrategy;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.utilities.Converter;

/**
 *
 * @author Nicklas
 */
public class IMAPPush extends IMAPStrategy implements MessageCountListener {

    private Properties props;
    private Authenticator auth;
    private Session session;
    private List<NetworkService.Callback> listeners;
    private boolean run = true;

    public IMAPPush(final Properties props, final Authenticator auth, final List<NetworkService.Callback> listeners) {
        this.props = props;
        this.auth = auth;
        this.session = Session.getInstance(props, auth);
        this.listeners = listeners;
    }

    public void run() {
        while (run) {
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
    }

    @Override
    public void halt() {
        run = false;
    }

    public void messagesAdded(MessageCountEvent e) {
        MimeMessage[] messages = (MimeMessage[]) e.getMessages();
        for (MimeMessage m : messages) {
            for (NetworkService.Callback cb : listeners) {
                try {
                    cb.mailReceived(Converter.convertToXO(m));
                } catch (Exception ex) {
                    Logger.getLogger(IMAPPush.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void messagesRemoved(MessageCountEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
