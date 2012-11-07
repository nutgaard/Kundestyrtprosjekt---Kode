/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService;

import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAPStrategy;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import no.ntnu.kpro.core.model.Box;
import no.ntnu.kpro.core.model.ModelProxy.IXOMessage;
import no.ntnu.kpro.core.model.User;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.ServiceProvider;
import no.ntnu.kpro.core.service.factories.PersistenceServiceFactory;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAP;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAPCache;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAPPull;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAPPush;
import no.ntnu.kpro.core.service.implementation.NetworkService.IMAP.IMAPStrategyFactory;
import no.ntnu.kpro.core.service.implementation.NetworkService.SMTP.SMTP;
import no.ntnu.kpro.core.service.implementation.NetworkService.SMTP.SMTPSender;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.service.interfaces.PersistenceService;
import no.ntnu.kpro.core.utilities.Converter;

/**
 *
 * @author Nicklas
 */
public class NetworkServiceImp extends NetworkService implements NetworkService.Callback, SharedPreferences.OnSharedPreferenceChangeListener {

    static {
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    public enum BoxName {

        INBOX("INBOX", new Box<IXOMessage>()),
        SENT("[Gmail]/Sendt e-post", new Box<IXOMessage>());
        private String boxName;
        private Box<IXOMessage> box;

        private BoxName(String boxName, Box box) {
            this.boxName = boxName;
            this.box = box;
        }

        public String getBoxname() {
            return this.boxName;
        }

        public Box<IXOMessage> getBox() {
            return this.box;
        }
    }
    private SMTP smtp;
    private IMAP imap;
    private IMAPCache cache;
    private PersistenceService persistence;
    private Context context;
    private Properties properties;
    private String username;
    private String password;
    private Date lastSeen;

    public NetworkServiceImp(final String username, final String password, final String mailAdr, Context context) {
        this(username, password, mailAdr, new Properties(), context);
    }

    public NetworkServiceImp(final String username, final String password, final String mailAdr, Properties properties, Context context) {
        for (BoxName bn : BoxName.values()) {
            bn.getBox().clear();
        }
        cache = new IMAPCache(properties, username, password);
        this.persistence = PersistenceServiceFactory.createMessageStorage(new User(username, password), context);
        Converter.setup(PersistenceServiceFactory.createImageStore(context));
        lastSeen = new Date(0);
        this.context = context;
        this.username = username;
        this.password = password;
        this.properties = properties;
        try {
            IXOMessage[] savedMessages = PersistenceService.castTo(this.persistence.findAll(XOMessage.class), IXOMessage[].class);
            for (IXOMessage message : savedMessages) {
                if (message.isDeleted()) {
                    continue;
                }
                System.out.println("Found saved message: " + message);
                message.getBoxAffiliation().getBox().add(message);
                cache.cache(message.getId(), message);
                lastSeen = message.getDate();
            }
        } catch (Exception ex) {
//            Logger.getLogger(NetworkServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Could not load messages from disk");
        }
        this.smtp = new SMTP(username, password, mailAdr, properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        }, listeners);

        System.err.println("JUST ONCE PLEASE");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        this.imap = new IMAP(IMAPStrategyFactory.getStrategy(username, password, properties, listeners, lastSeen, cache, context));
        listeners.add(this);
    }

    public void send(IXOMessage msg) {
        this.smtp.send(msg);
    }

    @Override
    public Box<IXOMessage> getOutbox() {
        return BoxName.SENT.getBox();
    }

    @Override
    public Box<IXOMessage> getInbox() {
        return BoxName.INBOX.getBox();
    }

    public void delete(IXOMessage message) {
        try {
            getInbox().remove(message);
            getOutbox().remove(message);
            persistence.manageAll(message);
        } catch (Exception ex) {
            Logger.getLogger(NetworkServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        smtp.halt();
        imap.halt();
    }

    public void mailSent(IXOMessage message, Address[] invalidAddress) {
        getOutbox().add(message);
        message.setBoxAffiliation(BoxName.SENT);
    }

    public void mailSentError(IXOMessage message, Exception ex) {
    }

    public void mailReceived(IXOMessage message) {
        try {
            if (getInbox().contains(message)){
                return;
            }
            System.out.println("Saving GOD DAMNIT");
            message.setBoxAffiliation(BoxName.INBOX);
            this.persistence.save(message);
            if (message.getPriority().getNumeric() >= 4) {
                Intent i = new Intent("FlashOverride");
                i.putExtra("message", message);
                // Broadcasting intent
                ServiceProvider.getInstance().getApplicationContext().sendBroadcast(i);
            }
            getInbox().add(message);
            System.out.println("Received message with " + message.getAttachments().size() + " attachments");
        } catch (Exception ex) {
            Logger.getLogger(NetworkServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void mailReceivedError(Exception ex) {
    }

    public PersistenceService getMessageStorage() {
        return persistence;
    }

    public void onSharedPreferenceChanged(SharedPreferences sp, String string) {
        imap.changeStrategy(IMAPStrategyFactory.getStrategy(username, password, properties, listeners, lastSeen, cache, context));
    }
}
