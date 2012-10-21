/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.factories;

import no.ntnu.kpro.core.service.implementation.NetworkService.NetworkServiceImp;
import no.ntnu.kpro.core.service.implementation.NetworkService.SimpleMail;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas
 */
public class NetworkServiceFactory {
    public static NetworkService createService() {
//        return new SimpleMail("kprothales", "kprothales2012", "kprothales@gmail.com");
        return new NetworkServiceImp("kprothales", "kprothales2012", "kprothales@gmail.com", props);
    }
}
