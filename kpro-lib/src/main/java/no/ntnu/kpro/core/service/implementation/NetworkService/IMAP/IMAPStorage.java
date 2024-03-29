/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SearchTerm;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.implementation.NetworkService.NetworkServiceImp.BoxName;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.service.interfaces.NetworkService.Callback;
import no.ntnu.kpro.core.utilities.Converter;

/**
 *
 * @author Nicklas
 */
public class IMAPStorage {

    private Properties props;
    private Authenticator auth;
    private List<NetworkService.Callback> listeners;
    private IMAPCache cache;

    public IMAPStorage(final Properties props, final Authenticator auth, List<NetworkService.Callback> listeners, IMAPCache cache) {
        this.props = props;
        this.auth = auth;
        this.listeners = listeners;
        this.cache = cache;
    }

    public Message[] getAllMessages(final BoxName box, final SearchTerm search) {
        System.out.println("Searching");
        try {
//            System.out.println("Props: " + props);
//            System.out.println("Auth: " + auth);
            Session session = Session.getInstance(props, auth);
            Store store = session.getStore("imaps");
            store.connect();
            IMAPFolder folder = (IMAPFolder) store.getFolder(box.getBoxname());
            folder.open(Folder.READ_ONLY);
            Message[] messages = folder.search(search);
//            System.out.println("Found " + messages.length + " messages");
            for (Message m : messages) {
                if (cache == null) {
                    break;
                }
                IMAPMessage im = (IMAPMessage) m;
                 if (!cache.contains(im.getMessageID())) {
                    XOMessage xo = Converter.getInstance().convertToXO(m);
                    for (NetworkService.Callback cb : listeners) {
                        cb.mailReceived(xo);
                        cache.cache(im.getMessageID(), xo);
                    }
                } 
            }
            store.close();
            return messages;
        } catch (Exception ex) {
//            ex.printStackTrace();
            for (NetworkService.Callback cb : listeners) {
                cb.mailReceivedError(ex);
            }
        }
        System.out.println("Exeption... Returning null");
        return null;
    }

    public void addCallback(Callback callback) {
//        System.out.println("Adding callback");
        listeners.add(callback);
    }

    public void removeCallback(Callback callback) {
        listeners.remove(callback);
    }
}
