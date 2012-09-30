/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

/**
 *
 * @author Nicklas
 */
public class User {
    public String name;

    public User(String name) {
        this.name = name;
    }
        
    public String getName() {
        return this.name;
    }
}
