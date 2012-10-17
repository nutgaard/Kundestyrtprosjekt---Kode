/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.crypto;

import java.security.KeyStore;
import java.security.KeyStoreException;
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
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.engines.DESEngine;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
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
    KeyStore ks;
    
    public CryptoHandler(String userName, char[] password){
        try {
        ks = setupKeyStore(userName, password);
        }
        catch (Exception e){
            
        }
    }
    static {
       Security.addProvider(new BouncyCastleProvider());
    }
    
    
    public static KeyStore createKeyStore(String userName, char[] password) throws Exception{
        KeyStore _ks = KeyStore.getInstance(KeyStore.getDefaultType());
        _ks.load(null, password);
        return _ks;
    } 
    public static KeyStore setupKeyStore(String userName, char[] password) throws Exception{
        KeyStore _ks = KeyStore.getInstance(KeyStore.getDefaultType());
        java.io.FileInputStream fis = new java.io.FileInputStream("keyStoreName");
        _ks.load(fis, password);
        fis.close();
        return _ks;
        
    } 
    public static void encrypt(String keyString, String inputString) {
        /*
         * This will use a supplied key, and encrypt the data
         * This is the equivalent of DES/CBC/PKCS5Padding
         */
        BlockCipher engine = new DESEngine();
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(engine);

        byte[] key = keyString.getBytes();
        byte[] input = inputString.getBytes();

        cipher.init(true, new KeyParameter(key));

        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];

        int outputLen = cipher.processBytes(input, 0, input.length, cipherText, 0);
        try {
            cipher.doFinal(cipherText, outputLen);
        } catch (Exception ce) {
            System.err.println(ce);
        }
    }
        public void test(){
           
        }

    public static void setDefaultMailcap() {
        MailcapCommandMap _mailcap =
            (MailcapCommandMap)CommandMap.getDefaultCommandMap();

        _mailcap.addMailcap("application/pkcs7-signature;; x-java-content-handler=org.spongycastle.mail.smime.handlers.pkcs7_signature");
        _mailcap.addMailcap("application/pkcs7-mime;; x-java-content-handler=org.spongycastle.mail.smime.handlers.pkcs7_mime");
        _mailcap.addMailcap("application/x-pkcs7-signature;; x-java-content-handler=org.spongycastle.mail.smime.handlers.x_pkcs7_signature");
        _mailcap.addMailcap("application/x-pkcs7-mime;; x-java-content-handler=org.spongycastle.mail.smime.handlers.x_pkcs7_mime");
        _mailcap.addMailcap("multipart/signed;; x-java-content-handler=org.spongycastle.mail.smime.handlers.multipart_signed");

    	CommandMap.setDefaultCommandMap(_mailcap);
    }
}
