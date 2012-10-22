/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.NetworkService.IMAP;

import no.ntnu.kpro.core.service.implementation.NetworkService.IMAPStrategy;

/**
 *
 * @author Nicklas
 */
public class IMAP extends Thread {

    private IMAPStrategy strategy;
    private boolean run;

    public IMAP(IMAPStrategy strategy) {
        this.strategy = strategy;
        this.run = true;
    }

    @Override
    public void run() {
        while (run) {
            if (strategy != null) {
                strategy.run();
            }
        }
    }

    public void changeStrategy(IMAPStrategy strategy) {
        if (this.strategy != strategy) {
            if (strategy != null) {
                this.strategy.halt();
            }
            this.strategy = strategy;
        }
    }

    public void halt() {
        if (strategy != null) {
            this.strategy.halt();
        }
        this.strategy = null;
    }
}
