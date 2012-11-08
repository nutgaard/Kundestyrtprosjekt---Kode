/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public User() {
    }

    public User(String name, String password) {
        try {
            this.name = name;
            this.password = password;
            MessageDigest m = MessageDigest.getInstance("SHA-256");
            byte[] passB = password.getBytes();
            hash = new String(m.digest(passB));
        } catch (NoSuchAlgorithmException ex) {
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
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean authorize(IUser u) {
        return this.name.equals(u.getName())&&this.password.equals(u.getPassword());
    }
}
