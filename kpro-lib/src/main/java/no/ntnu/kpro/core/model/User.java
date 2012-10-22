/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

import no.ntnu.kpro.core.model.ModelProxy.IUser;

/**
 *
 * @author Nicklas
 */
public class User implements IUser {

    public String name;
    private String hash;

    public User() {
    }

    public User(String name, String hash) {
        this.name = name;
        this.hash = hash;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return hash;
    }

    public void setPassword(String password) {
        this.hash = password;
    }

    public boolean authorize(IUser u) {
        return true;
    }
}
