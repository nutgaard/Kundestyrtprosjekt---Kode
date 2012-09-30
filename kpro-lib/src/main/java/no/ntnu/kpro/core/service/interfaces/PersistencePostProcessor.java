/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.interfaces;

/**
 *
 * @author Nicklas
 */
public abstract class PersistencePostProcessor {
    public String process(String s){
        return new String(process(s.getBytes()));
    }
    public String unprocess(String s){
        return new String(unprocess(s.getBytes()));
    }
    public abstract byte[] process(byte[] b);
    public abstract byte[] unprocess(byte[] b);
}
