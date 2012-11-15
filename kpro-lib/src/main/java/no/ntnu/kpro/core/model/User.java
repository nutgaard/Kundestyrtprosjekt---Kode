/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.security.Key;

import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import no.ntnu.kpro.core.model.ModelProxy.IUser;

/**
 *
 * @author Nicklas
 */
public class User implements IUser {

    public String name;
    @XStreamOmitField
    private String password;
    private String hash;
    @XStreamOmitField
    private String pbk;

    public User() {
    }

    public User(String name, String password) {
        try {
            this.name = name;
            this.password = password;
            if (password == null || password.equals("")) {
                throw new RuntimeException("Password cannot be null or an empty string");
            }
            MessageDigest m = MessageDigest.getInstance("SHA-256");
            byte[] passB = password.getBytes();
            hash = new String(m.digest(passB));
            
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keyspec = new PBEKeySpec(password.toCharArray(), name.getBytes(), 1000, 128);
            Key key = factory.generateSecret(keyspec);
            this.pbk = new String(key.getEncoded());
        } catch (Exception ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getPassword() {
        return this.password;
    }

    public String getHash() {
        return hash;
    }
    public String getPBK() {
        return pbk;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean authorize(IUser u) {
        return this.name.equals(u.getName())&&this.hash.equals(u.getHash());
    }
}
