/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SearchTerm;
import no.ntnu.kpro.core.service.implementation.NetworkService.NetworkServiceImp.BoxName;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.service.interfaces.NetworkService.InternalCallback;

/**
 *
 * @author Nicklas
 */
public class IMAPStorage {

    private Properties props;
    private Authenticator auth;
    private NetworkService.InternalCallback listener;

    public IMAPStorage(final Properties props, final Authenticator auth, NetworkService.InternalCallback listener) {
        this.props = props;
        this.auth = auth;
        this.listener = listener;
    }

    public Message[] getAllMessages(final BoxName box, final SearchTerm search) {
        System.out.println("Searching");
        try {
            Session session = Session.getInstance(props, auth);
            Store store = session.getStore("imaps");
            store.connect();
            IMAPFolder folder = (IMAPFolder) store.getFolder(box.getBoxname());
            folder.open(Folder.READ_ONLY);
            Message[] messages = folder.search(search);
            System.out.println("Found "+messages.length+" messages");
            for (Message m : messages) {
                m.getAllHeaders();
                m.getContent();
                System.out.println("Found message: "+m);
                System.out.println("Telling: "+listener);
                listener.mailReceived((IMAPMessage)m);
            }
            store.close();
            return messages;
        } catch (Exception ex) {
            listener.mailReceivedError(ex);
        }
        return null;
    }
    public void setCallback(InternalCallback internalCallback) {
        this.listener = internalCallback;
    }
}
