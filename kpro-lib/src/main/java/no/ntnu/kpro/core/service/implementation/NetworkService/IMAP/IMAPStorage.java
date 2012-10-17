/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;

import com.sun.mail.imap.IMAPFolder;
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
    private List<NetworkService.Callback> listener;

    public IMAPStorage(final Properties props, final Authenticator auth, List<NetworkService.Callback> listeners) {
        this.props = props;
        this.auth = auth;
        this.listener = listeners;
    }

    public Message[] getAllMessages(final BoxName box, final SearchTerm search) {
        try {
            Session session = Session.getInstance(props, auth);
            Store store = session.getStore("imaps");
            store.connect();
            IMAPFolder folder = (IMAPFolder) store.getFolder(box.getBoxname());
            folder.open(Folder.READ_ONLY);
            Message[] messages = folder.search(search);
            for (Message m : messages) {
                XOMessage xo = Converter.convertToXO(m);
                System.out.println(xo);
                box.getBox().add(xo);
                for (Callback cb : listener) {
                    cb.mailReceived(xo);
                }
            }
            store.close();
            return messages;
        } catch (Exception ex) {
            ex.printStackTrace();
            for (Callback cb : listener) {
                cb.mailReceivedError(ex);
            }
        }
        return null;
    }

    public void addCallback(Callback listener) {
        this.listener.add(listener);
    }

    public void removeCallback(Callback listener) {
        this.listener.remove(listener);
    }
}
