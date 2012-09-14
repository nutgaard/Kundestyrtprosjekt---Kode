/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.test.suitebuilder.TestSuiteBuilder;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 * @author Nicklas
 */
public class AllTests extends TestSuite {
    public static Test suite() {
        Test t = new TestSuiteBuilder(AllTests.class)
                .includeAllPackagesUnderHere()
                .includePackages("no.ntnu.kpro.core.service.interfaces")
                .includeAllPackagesUnderHere().build();
        
        return t;
    }
    
}
