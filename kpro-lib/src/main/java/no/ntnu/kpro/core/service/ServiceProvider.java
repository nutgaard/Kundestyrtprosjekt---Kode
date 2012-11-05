/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.sun.mail.smtp.SMTPTransport;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import no.ntnu.kpro.core.model.ModelProxy.IUser;
import no.ntnu.kpro.core.service.factories.HALServiceFactory;
import no.ntnu.kpro.core.service.factories.NetworkServiceFactory;
import no.ntnu.kpro.core.service.factories.SecurityServiceFactory;
import no.ntnu.kpro.core.service.implementation.NetworkService.crypto.CryptoHandler;
import no.ntnu.kpro.core.service.implementation.SecurityService.UserManager;
import no.ntnu.kpro.core.service.interfaces.HALService;
import no.ntnu.kpro.core.service.interfaces.NetworkService;
import no.ntnu.kpro.core.service.interfaces.SecurityService;

/**
 *
 * @author Nicklas
 */
public class ServiceProvider extends Service {

    private static ServiceProvider instance;
    private IBinder mBinder = new LocalBinder();
    private static final String TAG = "KPRO";
    private HALService HALService;
    private NetworkService networkService;
    private SecurityService securityService;

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(START_STICKY, new Notification());
        ServiceProvider.instance = this;
        CryptoHandler.setDefaultMailcap(); //tell java mail how to handle security
        Log.i(TAG, "Service starting");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ServiceProvider.instance = null;
        this.networkService.close();
        Log.i(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (this.mBinder == null) {
            this.mBinder = new LocalBinder();
        }
        Log.d(this.getClass().getName(), "OnBind");
        return this.mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        boolean b = super.onUnbind(intent);
        Log.d(this.getClass().getName(), "OnUnbind");
        return b;
    }

    public class LocalBinder extends Binder {

        public ServiceProvider getService() {
            return ServiceProvider.this;
        }
    }

    public void addListener(Activity activity) {
        for (Class cls : activity.getClass().getInterfaces()) {
            if (cls.equals(NetworkService.Callback.class) && networkService != null) {
                networkService.addListener((NetworkService.Callback) activity);
            }
            if (cls.equals(SecurityService.Callback.class) && securityService != null) {
                securityService.addListener((SecurityService.Callback) activity);
            }
            if (cls.equals(HALService.Callback.class) && HALService != null) {
                HALService.addListener((HALService.Callback) activity);
            }
        }
    }

    public void removeListener(Activity activity) {
        for (Class cls : activity.getClass().getInterfaces()) {
            if (cls.equals(NetworkService.Callback.class) && networkService != null) {
                networkService.removeListener((NetworkService.Callback) activity);
            }
            if (cls.equals(SecurityService.Callback.class) && securityService != null) {
                securityService.removeListener((SecurityService.Callback) activity);
            }
            if (cls.equals(HALService.Callback.class) && HALService != null) {
                HALService.removeListener((HALService.Callback) activity);
            }
        }
    }

    public HALService getHALService() {
        return HALService;
    }

    public NetworkService getNetworkService() {
        return networkService;
    }

    //Can to this because Service is implicit singleton
    public static ServiceProvider getInstance() {
        return instance;
    }

    public IUser login(final IUser user) {
        logout();
        IUser o = null;
        try {
            UserManager um = new UserManager(this);
            Boolean auth = um.authorize(user);
            if (auth == null) {
                //User not found, check through network
                System.out.println("login::checking network");
                Properties p = NetworkServiceFactory.getDefaultProperties();
                Session session = Session.getInstance(p, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user.getName(), user.getPassword());
                    }
                });
                try {
                    SMTPTransport t = (SMTPTransport) session.getTransport("smtps");
                    t.connect(p.getProperty("mail.smtps.host"), user.getName() + "@" + p.getProperty("mail.domain"), user.getPassword());
                } catch (Exception ex) {
                    Logger.getLogger(ServiceProvider.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
                um.createUser(user);
                o = user;
            } else if (auth) {
                //Valid input
                System.out.println("login::valid");
                o = user;
            } else {
                //Invalid input
                System.out.println("Invalid");
            }
        } finally {
            if (o != null) {
                this.HALService = HALServiceFactory.createService();
                this.networkService = NetworkServiceFactory.createService(getApplicationContext(), user);
                this.securityService = SecurityServiceFactory.createService();
                return o;
            }
        }
        return null;
    }

    public void logout() {
        if (networkService != null) {
            this.networkService.close();
        }
        this.HALService = null;
        this.networkService = null;
        this.securityService = null;
    }
}
