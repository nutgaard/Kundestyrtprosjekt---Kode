/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

/**
 *
 * @author Nicklas
 */
public class ModelProxy {
    public static interface IUser {
        public void setName(String name);
        public String getName();
        public boolean authorize(IUser u);
    }
}
