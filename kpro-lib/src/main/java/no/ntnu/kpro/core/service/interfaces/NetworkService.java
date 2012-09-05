/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.interfaces;

/**
 *
 * @author Nicklas
 */
public interface NetworkService extends ServiceInterface {

    public interface callback {
    }

    public boolean sendMail(final String recipient, final String subject, final String body);
}
