/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;

import com.sun.mail.imap.IMAPMessage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.search.MessageIDTerm;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;
import no.ntnu.kpro.core.service.implementation.NetworkService.NetworkServiceImp;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.service.interfaces.PersistenceService;

/**
 *
 * @author Nicklas
 */
public class IMAPCache {

    private String username, password;
    private Properties props;
    private Map<String, IXOMessage> cache;

    public IMAPCache(Properties props, String username, String password) {
        this.props = props;
        this.cache = new HashMap<String, IXOMessage>();
        this.username = username;
        this.password = password;
    }

    public void cache(String s, IXOMessage xo) {
        System.out.println("Cache::Medium");
        this.cache.put(s, xo);
    }

    public IXOMessage getXOMessage(String id) {
        return this.cache.get(id);
    }

    public boolean contains(String id) {
        return cache.containsKey(id);
    }

    public boolean containsXOMessage(String id) {
        return cache.get(id) != null;
    }

    public IMAPMessage getIMAPMessage(String id) {
        IMAPStorage store = new IMAPStorage(props, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        }, new LinkedList<NetworkService.Callback>(), null);
        Message[] mTest = store.getAllMessages(NetworkServiceImp.BoxName.INBOX, new MessageIDTerm(id));
        IMAPMessage[] messages = PersistenceService.castTo(mTest, IMAPMessage[].class);
        if (messages.length == 0 || messages.length > 1) {
            return null;
        } else {
            return messages[0];
        }
    }
}
