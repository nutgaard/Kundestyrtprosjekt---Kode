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

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
