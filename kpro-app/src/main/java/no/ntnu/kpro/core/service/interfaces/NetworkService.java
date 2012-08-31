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
    public void sendMail(String subject, String body, String sender, String recipients);
}
