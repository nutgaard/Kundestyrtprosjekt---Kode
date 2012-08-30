/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service;

import no.ntnu.kpro.core.service.factories.HALServiceFactory;
import no.ntnu.kpro.core.service.interfaces.HALService;

/**
 *
 * @author Nicklas
 */
public class ServiceProvider {
    public static ServiceProvider instance;
    
    private ServiceProvider() {
        
    }
    public static ServiceProvider getInstance() {
        if (instance == null)instance = new ServiceProvider();
        return instance;
    }
    public HALService getHALService() {
        return HALServiceFactory.createService();
    }
    
}
