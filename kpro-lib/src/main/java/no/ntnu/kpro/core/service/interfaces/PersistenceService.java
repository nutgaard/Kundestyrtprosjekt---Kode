/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.interfaces;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aleksander Sj�fjell
 */
public abstract class PersistenceService extends ServiceInterface<PersistenceService.Callback> {

    protected File baseDir;

    public interface Callback {
    }

    public PersistenceService(File baseDir) {
        this.baseDir = baseDir;
    }

    public abstract void close();

    public abstract Object manage(Object o) throws Exception;
    public abstract Object manageAll(Object o) throws Exception;

    public abstract Object unmanage(Object o);

    public abstract void save(Object o) throws Exception;

    public abstract void delete(Object o);

    public abstract Object[] findAll(Class cls) throws Exception;

    public abstract Object find(Class cls, int id) throws Exception;

    public static <T> T[] castTo(Object[] l, Class<? extends T[]> cls) {
        System.out.println("ListLength: " + l.length);
        if (l == null || l.length == 0) {
            return null;
        }
        return Arrays.copyOf(l, l.length, cls);
    }

    public File createOutputFile(String filename) {
        if (baseDir == null) {
            throw new RuntimeException("Persistence not yet started");
        } else {
            try {
                File o = new File(baseDir, filename);
                int i = 2;
                while (o.exists()) {
                    String[] s = filename.split("\\.");
                    o = new File(baseDir, s[0]+i+"."+s[1]);
                    i++;
                }
                o.createNewFile();
                System.out.println("Created outputstream to "+o.getAbsolutePath());
                return o;
            } catch (IOException ex) {
                throw new RuntimeException("IO Error: "+ex.getMessage());
            }

        }
    }
}
