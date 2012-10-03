/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.PersistenceService.PersistentWriteThroughStorage;

import no.ntnu.kpro.core.service.interfaces.PersistencePostProcessor;

/**
 *
 * @author Nicklas
 */
public class FileCryptoFactory {

    public enum Crypto {

        NONE, BITSHIFT;
    }

    public static PersistencePostProcessor getProcessor(Crypto c) {
        switch (c) {
            case NONE:
                return new PersistencePostProcessor() {
                    @Override
                    public byte[] process(byte[] b) {
                        return b;
                    }

                    @Override
                    public byte[] unprocess(byte[] b) {
                        return b;
                    }
                };
            case BITSHIFT:
                return new PersistencePostProcessor() {
                    @Override
                    public byte[] process(byte[] b) {
                        for (int i = 0; i < b.length; i++) {
                            b[i]--;
                        }
                        return b;
                    }

                    @Override
                    public byte[] unprocess(byte[] b) {
                        for (int i = 0; i < b.length; i++) {
                            b[i]++;
                        }
                        return b;
                    }
                };
            default:
                return getProcessor(Crypto.NONE);
        }
    }
}
