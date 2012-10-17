/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.crypto;

import java.security.Security;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import no.ntnu.kpro.core.model.XOMessage;
import org.spongycastle.jce.provider.BouncyCastleProvider;


/**
 * This class should ideally be able to take in a XOMessage and spit out a 
 * MimeMessage with everything fixed; the message signed and encrypted.
 * Conversely, it should be able to take in an encrypted MimeMessage, decrypt 
 * and verify, and spit it out as an XOMessage.
 * 
 * @author magnus
 */
public class CryptoHandler {
    	
    static {
       Security.addProvider(new BouncyCastleProvider());
    }
       
    
    public MimeMessage prepareToSend(XOMessage mail){
        try {
        //SMIMESignedGenerator gen = new SMIMESignedGenerator();
        //gen.addCertificates(certs);

        //
        // create the base for our message
        //
        MimeBodyPart msg = new MimeBodyPart();

        msg.setText(mail.getStrippedBody()); //TODO: consider adding support for HTML

        //
        // extract the multipart object from the SMIMESigned object.
        //
        //MimeMultipart mm = gen.generate(msg);

        //
        // Get a Session object and create the mail message
        //
        Properties props = System.getProperties();
        Session session = Session.getDefaultInstance(props, null);

        Address fromUser = new InternetAddress(mail.getFrom());
        Address toUser = new InternetAddress(mail.getTo());

        MimeMessage body = new MimeMessage(session);
        body.setFrom(fromUser);
        body.setRecipient(Message.RecipientType.TO, toUser);
        body.setSubject(mail.getSubject());
        //body.setContent(mm, mm.getContentType());
        body.saveChanges();
        return null;
        }
        catch(Exception e){
            return null;
        }
    }
    public XOMessage decrypt(MimeMessage mail){
        return null;
    }
    
    public static void setDefaultMailcap()
    {
        MailcapCommandMap _mailcap =
            (MailcapCommandMap)CommandMap.getDefaultCommandMap();

        _mailcap.addMailcap("application/pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_signature");
        _mailcap.addMailcap("application/pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_mime");
        _mailcap.addMailcap("application/x-pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_signature");
        _mailcap.addMailcap("application/x-pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_mime");
        _mailcap.addMailcap("multipart/signed;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.multipart_signed");

    	CommandMap.setDefaultCommandMap(_mailcap);
    }
}
