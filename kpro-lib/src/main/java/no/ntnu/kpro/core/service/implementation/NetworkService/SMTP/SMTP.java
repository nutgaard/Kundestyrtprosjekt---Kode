/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.SMTP;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import no.ntnu.kpro.core.model.XOMessage;
import no.ntnu.kpro.core.service.interfaces.NetworkService;

/**
 *
 * @author Nicklas
 */
public class SMTP extends Thread {

    boolean run = false;
    private final List<XOMessage> queue;
    private final SMTPSender sender;

    public SMTP(final String username, final String password, final String mailAdr, final Properties props, final Authenticator auth, List<NetworkService.Callback> listener) {
        this(new SMTPSender(username, password, mailAdr, props, auth, listener));
    }

    public SMTP(SMTPSender sender) {
        this.queue = Collections.synchronizedList(new LinkedList<XOMessage>());
        this.sender = sender;
        start();
    }

    public SMTPSender getSender() {
        return this.sender;
    }

    public void send(XOMessage msg) {
//        System.out.println("Adding message");
        if (!run) {
            start();
        }
//        int index = Collections.binarySearch(queue, msg, XOMessage.XOMessageSorter.getSendingPriority());
//        System.out.println("Insert at "+index);
//        if (index < 0) {
        synchronized (this) {
            if (!queue.contains(msg)) {
                synchronized (queue) {
                    queue.add(msg);
                    Collections.sort(queue, XOMessage.XOMessageSorter.getSendingPriority());
                    System.out.println("Notify");
                }
                notifyAll();
            }
        }
//        System.out.println("Message added to queue, queuesize: " + queue.size());
//        }
    }

    @Override
    public void start() {
        if (run) {
            return;
        }
        this.run = true;
        super.start();
    }

    @Override
    public void run() {
        super.run();
//            System.out.println("Running: "+run);
        while (run) {
            while (queue.isEmpty()) {
//                SMTP.yield();
                synchronized (this) {
                    try {
                        System.out.println("Going to sleep");
                        wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SMTP.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
//                System.out.println("Pusher waked");
            XOMessage msg;
            synchronized (queue) {
                msg = queue.remove(0);
            }
//            System.out.println("Message removed from queue, queuesize: " + queue.size());
            sender.sendMail(msg);
//            System.out.println("Pusher ready, queuesize: " + queue.size());
        }
    }

    public void halt() {
        this.run = false;
    }
}
