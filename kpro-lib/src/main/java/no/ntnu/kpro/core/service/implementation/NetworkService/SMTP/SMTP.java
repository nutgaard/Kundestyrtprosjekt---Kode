/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.SMTP;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import no.ntnu.kpro.core.model.XOMessage;

/**
 *
 * @author Nicklas
 */
public class SMTP {

    private final List<XOMessage> queue;
    private final PushToSend pusher;
    private final SMTPSender sender;

    public SMTP(final String username, final String password, final String mailAdr, final Properties props, final Authenticator auth) {
//        this.queue = Collections.synchronizedList(new LinkedList<XOMessage>());
        this.queue = new LinkedList<XOMessage>();
        this.pusher = new PushToSend();
        this.sender = new SMTPSender(username, password, mailAdr, props, auth);
        this.pusher.start();
    }

    public SMTPSender getSender() {
        return this.sender;
    }

    public void send(XOMessage msg) {
        System.out.println("Adding message");
        if (!pusher.run) {
            this.pusher.start();
        }
//        int index = Collections.binarySearch(queue, msg, XOMessage.XOMessageSorter.getSendingPriority());
//        System.out.println("Insert at "+index);
//        if (index < 0) {
        synchronized (queue) {
            if (!queue.contains(msg)) {
                queue.add(msg);
                Collections.sort(queue, XOMessage.XOMessageSorter.getSendingPriority());
            }
        }
        System.out.println("Message added to queue, queuesize: " + queue.size());
//        }
    }

    class PushToSend extends Thread {

        boolean run = false;

        @Override
        public void start() {
            this.run = true;
            super.start();
        }

        @Override
        public void run() {
            super.run();
//            System.out.println("Running: "+run);
            while (run) {
                while (queue.isEmpty()) {
                    PushToSend.yield();
                }
//                System.out.println("Pusher waked");
                XOMessage msg;
                synchronized (queue) {
                    msg = queue.remove(0);
                }
                System.out.println("Message removed from queue, queuesize: " + queue.size());
                sender.sendMail(msg);
                System.out.println("Pusher ready, queuesize: " + queue.size());
            }
        }

        public void halt() {
            this.run = false;
        }
    }
}
