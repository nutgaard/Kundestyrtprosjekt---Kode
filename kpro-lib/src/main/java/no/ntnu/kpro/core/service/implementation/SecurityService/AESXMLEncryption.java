/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.SecurityService;

import java.util.logging.Level;
import java.util.logging.Logger;
import no.ntnu.kpro.core.model.ModelProxy.IUser;
import no.ntnu.kpro.core.model.User;
import no.ntnu.kpro.core.service.interfaces.PersistencePostProcessor;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithSalt;

/**
 *
 * @author Nicklas
 */
public class AESXMLEncryption extends PersistencePostProcessor {

    private String key;

    public AESXMLEncryption(IUser user) {
        this.key = new sun.misc.BASE64Encoder().encode(user.getPBK().getBytes());
    }

    @Override
    public byte[] process(byte[] b) {
        byte[] based = new sun.misc.BASE64Encoder().encode(b).getBytes();
        PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
        CipherParameters cp = new KeyParameter(this.key.getBytes());
        aes.init(true, cp);
        return cipherData(aes, based);
    }
    /**
     * Decryps byte stream
     * 
     * @param b
     * @return byte array
     */
    @Override
    public byte[] unprocess(byte[] b) {
        byte[] based = new sun.misc.BASE64Encoder().encode(b).getBytes();
        PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
        CipherParameters cp = new KeyParameter(this.key.getBytes());
        aes.init(false, cp);
        return cipherData(aes, based);
    }

    private static byte[] cipherData(PaddedBufferedBlockCipher cipher, byte[] data) {
        try {
            int minSize = cipher.getOutputSize(data.length);
            byte[] outBuf = new byte[minSize];
            int length1 = cipher.processBytes(data, 0, data.length, outBuf, 0);
            int length2 = cipher.doFinal(outBuf, length1);
            int actualLength = length1 + length2;
            byte[] result = new byte[actualLength];
            System.arraycopy(outBuf, 0, result, 0, result.length);
            return result;
        } catch (Exception ex) {
            Logger.getLogger(AESXMLEncryption.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "".getBytes();
    }
    
    public static void main(String[] args) {
        String userName = "Nicklas";
        String password = "password";
        User user = new User(userName, password);
        AESXMLEncryption enc = new AESXMLEncryption(user);
        String out = enc.process(userName);
        System.out.println("Encrypted: "+out);
        AESXMLEncryption dec = new AESXMLEncryption(user);
        String in = enc.unprocess(out);
        System.out.println("Decrypted: "+in);
        
    }
}
