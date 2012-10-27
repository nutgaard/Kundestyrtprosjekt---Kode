/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.crypto;

import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
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
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.RSAPublicKeyStructure;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.engines.DESEngine;
import org.spongycastle.crypto.generators.RSAKeyPairGenerator;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.cert.X509v1CertificateBuilder;
import org.spongycastle.crypto.params.RSAKeyParameters;


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
    Context c;
    
    public CryptoHandler(String userName, char[] password, Context context){
        try {
        c = context;
        ks = setupLocalKeyStore(userName, password);
        }
        catch (Exception e){
            
        }
    }
    static {
       Security.addProvider(new BouncyCastleProvider());
    }
    
    
    public KeyStore createKeyStore(String userName, char[] password) throws Exception{
        KeyStore _ks = KeyStore.getInstance(KeyStore.getDefaultType());
        File file = new File("", userName);
        FileOutputStream fos = new FileOutputStream(file);
        _ks.load(null, password);
        _ks.store(fos, password);
        //return _ks;
        return cheatMethodForGettingAKeySetAndCert(_ks,userName, password);
    } 
    private KeyStore setupLocalKeyStore(String userName, char[] password) throws Exception{ 
         try {
        KeyStore _ks = KeyStore.getInstance(KeyStore.getDefaultType());
        File dir = c.getDir("", c.MODE_PRIVATE);
        File file = new File(dir, userName);
        java.io.FileInputStream fis = new java.io.FileInputStream(file);
        _ks.load(fis, password);
        fis.close();
        return _ks;
         } catch (FileNotFoundException e) {
             return createKeyStore(userName, password);
         }
    } 
    private KeyStore cheatMethodForGettingAKeySetAndCert(KeyStore ks,String userName,char[] password){
        KeyGenerationParameters param = new KeyGenerationParameters(null, 256);
        RSAKeyPairGenerator gen = new RSAKeyPairGenerator();
        gen.init(param);
        AsymmetricCipherKeyPair ourKey = gen.generateKeyPair();
         RSAKeyParameters publicKey = (RSAKeyParameters) ourKey.getPublic();
       RSAPublicKeyStructure rsaPublicKey = new RSAPublicKeyStructure(publicKey.getModulus(), publicKey.getExponent()); 
        AlgorithmIdentifier rsaEncryption = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, null); 
        SubjectPublicKeyInfo publicKeyInfo = new SubjectPublicKeyInfo(rsaEncryption, rsaPublicKey);

        
        X500Name us = new X500Name(userName);
        Date now = new Date();
        Date later = new Date();
        later.setTime(later.getTime() + 9000000000l);
        X509v1CertificateBuilder Cgen = new X509v1CertificateBuilder(us, BigInteger.ZERO, now, later, us, publicKeyInfo);
        Certificate[] chain = null;
//        ks.setCertificateEntry(null, null);
//        ks.setKeyEntry(userName + "-Private",  ourKey.getPrivate(), password, chain);
//        ks.setKeyEntry(userName + "-Public", ourKey.getPublic(), password, chain);
        return ks;
    }
    
    public Key getKey(String Alias, char[] password) throws Exception {
        return ks.getKey(Alias, password);
    }
    public  Certificate getCert(String Alias) throws Exception {
        
        return ks.getCertificate(Alias);
    }
    
    private static String encryptString(Key keyString, String inputString) {
        /*
         * This will use a supplied key, and encrypt the data
         * This is the equivalent of DES/CBC/PKCS5Padding
         */
        BlockCipher engine = new DESEngine();
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(engine);

        byte[] key = keyString.getEncoded();
        byte[] input = inputString.getBytes();

        cipher.init(true, new KeyParameter(key));

        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];

        int outputLen = cipher.processBytes(input, 0, input.length, cipherText, 0);
        try {
            cipher.doFinal(cipherText, outputLen);
        } catch (Exception ce) {
            System.err.println(ce);
        }
        return bytArrayToHex(cipherText);
    }
    private static String  bytArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for (byte b : a) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
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
