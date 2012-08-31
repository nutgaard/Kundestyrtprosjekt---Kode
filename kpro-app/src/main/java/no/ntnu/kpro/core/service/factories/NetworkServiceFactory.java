/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.factories;

import no.ntnu.kpro.core.service.implementation.NetworkService.Mail;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas
 */
public class NetworkServiceFactory {
    public static NetworkService createService() {
        return new Mail();
    }
}
