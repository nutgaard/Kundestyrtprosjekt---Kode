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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.search.MessageIDTerm;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;
import no.ntnu.kpro.core.service.implementation.NetworkService.NetworkServiceImp;
import no.ntnu.kpro.core.service.implementation.PersistenceService.PersistentWriteThroughStorage.PersistentWriteThroughStorage;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.service.interfaces.PersistenceService;
import no.ntnu.kpro.core.utilities.Converter;
import no.ntnu.kpro.core.utilities.Pair;

/**
 *
 * @author Nicklas
 */
public class IMAPCache {

    private String username, password;
    private Properties props;
    private Map<String, Pair<IMAPMessage, IXOMessage>> cache;

    public IMAPCache(Properties props, String username, String password) {
        this.props = props;
        this.cache = new HashMap<String, Pair<IMAPMessage, IXOMessage>>();
        this.username = username;
        this.password = password;
    }

    public void cache(String s, IXOMessage xo) {
        System.out.println("Cache::Medium");
        this.cache.put(s, new Pair<IMAPMessage, IXOMessage>(null, xo));
    }

    public void cache(String s, IMAPMessage m, IXOMessage xo) {
        System.out.println("Cache::Full");
        this.cache.put(s, new Pair<IMAPMessage, IXOMessage>(m, xo));
    }

    public IXOMessage getXOMessage(String id) {
        return this.cache.get(id).second;
    }
    public boolean contains(String id){
        return cache.containsKey(id);
    }
    public boolean containsXOMessage(String id){
        return cache.get(id).second!=null;
    }
    public boolean containsIMAPMessage(String id){
        return cache.get(id) != null && cache.get(id).first!=null;
    }
    public void update(String id, IMAPMessage im){
        System.out.println("Cache::Update");
        cache.get(id).first = im;
    }
    public IMAPMessage getIMAPMessage(String id) {
        System.out.println("Cache::RequestIMAP");
        Pair<IMAPMessage, IXOMessage> entry = cache.get(id);
        if (entry != null || entry.first == null) {
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
                try {
                    cache.put(messages[0].getMessageID(), new Pair<IMAPMessage,IXOMessage>(messages[0], Converter.convertToXO(messages[0])));
                } catch (Exception ex) {
                    Logger.getLogger(IMAPCache.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return cache.get(id).first;
    }
}
