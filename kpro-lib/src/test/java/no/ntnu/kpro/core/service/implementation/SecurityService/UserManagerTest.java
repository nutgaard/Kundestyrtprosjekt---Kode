/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.SecurityService;

import android.content.Context;
import java.io.File;
import no.ntnu.kpro.core.model.ModelProxy.IUser;
import no.ntnu.kpro.core.model.User;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 *
 * @author Nicklas
 */
public class UserManagerTest {

    private UserManager m;
    private Context c;
    private User u;

    public UserManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        c = mock(Context.class);
        when(c.getDir(anyString(), anyInt())).thenReturn(new File("/"));
        m = new UserManager(c);
        u = new User("Hei", "oasidj");

    }

    @After
    public void tearDown() {
        c = null;
        m = null;
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    @Test
    public void createUser() {
        m.createUser(u);
        assertTrue(true);
    }
    
    @Test
    public void auth() {
        m.createUser(u);
        IUser u2 = m.authorize(u);
        assertNotNull(u2);  
    }
}
