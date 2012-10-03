/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.PersistenceService.PersistentWriteThroughStorage;

import com.thoughtworks.xstream.XStream;
import java.io.*;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import no.ntnu.kpro.core.model.User;
import no.ntnu.kpro.core.service.interfaces.PersistencePostProcessor;

/**
 *
 * @author Nicklas
 */
public class PersistentWriteThroughStorage {

    private static PersistentWriteThroughStorage instance;
    private User user;
    private PersistencePostProcessor postProcessor;
    private XStream xstream;
    private Map<String, Integer> index;
    private File baseDir;

    public static PersistentWriteThroughStorage getInstance() {
        return instance;
    }

    public static PersistentWriteThroughStorage create(User user, PersistencePostProcessor postProcessor, File basedir) throws Exception {
        if (instance != null && instance.user != user) {
            throw new RuntimeException("Initalizing storage with changed user is not allowed.");
        }
        if (instance == null) {
            instance = new PersistentWriteThroughStorage(user, postProcessor, basedir);
        }
        return instance;
    }

    private PersistentWriteThroughStorage(User user, PersistencePostProcessor postProcessor, File basedir) throws Exception {
        this.user = user;
        this.postProcessor = postProcessor;
        this.xstream = new XStream();
        this.baseDir = basedir;
        if (!basedir.exists()){
            basedir.mkdirs();
        }
        getIndex();
    }

    public synchronized Object manage(Object o) throws Exception {
        Object p;
        if (!Proxy.isProxyClass(o.getClass())) {
            p = TraceProxy.trace(o);
        } else {
            p = o;
        }
        save(p);
        return p;
    }

    public synchronized Object unmanage(Object o) {
        return TraceProxy.untrace(o);
    }

    public synchronized void save(Object object) throws Exception {
        if (!Proxy.isProxyClass(object.getClass())) {
            manage(object);
            return;
        } else {
        }

        TraceProxy proxy = ((TraceProxy) Proxy.getInvocationHandler(object));
        String className = proxy.object.getClass().getSimpleName();
        File base = getBaseDir();
        File[] dirList = base.listFiles(new DirectoryFilter(className));
        if (dirList == null) {
            return;
        } else {
            //Directory doesnt exist
            File dir = new File(base, className);
            if (!dir.exists()) {
                dir.mkdir();
                dirList = new File[]{dir};
            }
        }
        File classDir = dirList[0];
        Integer currentIndex = -1;
        File file;
        if (proxy.id < 0) {
            currentIndex = this.index.get(className);
            if (currentIndex == null) {
                currentIndex = 0;
            }
            proxy.id = currentIndex;
            this.index.put(className, currentIndex + 1);
        } else {
            currentIndex = proxy.id;
        }
        file = new File(classDir, String.valueOf(currentIndex));
        if (!file.exists()) {
            file.createNewFile();
        }
        //Convert to xml and process
        byte[] data = xstream.toXML(proxy.object).getBytes();
        data = postProcessor.process(data);
        OutputStream os = new FileOutputStream(file);
        os.write(data);

        saveIndex();
    }

    public synchronized Object[] findAll(Class cls) throws Exception {
        File base = getBaseDir();
        //Will return null, an empty array, or one that maximum contains one element since duplicate directories are not allowed. 
        File[] dirList = base.listFiles(new DirectoryFilter(cls.getName()));
        if (dirList == null || dirList.length == 0) {
            return null;
        } else {
            File classDir = dirList[0];
            File[] objectList = classDir.listFiles();
            Object[] savedObjects = new Object[objectList.length];
            int parsedCounter = 0;
            for (File file : objectList) {
                //First read the file
                //Need bufferedInputStream to support mark and reset
                InputStream is = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                //Read from file
                is.read(data);
                //Unprocess raw data
                data = postProcessor.unprocess(data);
                //

                Object o = xstream.fromXML(new ByteArrayInputStream(data));
                Object t = TraceProxy.trace(o);
                ((TraceProxy) Proxy.getInvocationHandler(t)).id = Integer.parseInt(file.getName());

                savedObjects[parsedCounter] = t;
                parsedCounter++;
            }
            return savedObjects;
        }
    }

    public synchronized Object find(Class cls, int id) throws Exception {
        //Create new user and proxy
        if (id < 0){
            return manage(cls.getConstructor().newInstance());
        }
        File base = getBaseDir();
        //Will return null, an empty array, or one that maximum contains one element since duplicate directories are not allowed. 
        File[] dirList = base.listFiles(new DirectoryFilter(cls.getSimpleName()));
        if (dirList == null || dirList.length == 0) {
            return null;
        }
        File classDir = dirList[0];
        File[] objectList = classDir.listFiles(new NameFilter(String.valueOf(id)));
        if (objectList == null || objectList.length == 0) {
            return null;
        } else {
            File file = objectList[0];
            InputStream is = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            is.read(data);
            data = postProcessor.unprocess(data);
            Object o = xstream.fromXML(new ByteArrayInputStream(data));
            Object t = TraceProxy.trace(o);
            ((TraceProxy) Proxy.getInvocationHandler(t)).id = id;
            return t;
        }
    }

    private String getObjectClassName(Object o) {
        if (Proxy.isProxyClass(o.getClass())) {
            return TraceProxy.untrace(o).getClass().getSimpleName();
        } else {
            return o.getClass().getSimpleName();
        }
    }

    private File getBaseDir() {
        File base = new File(baseDir, "/" + postProcessor.process(user.getName()));
        if (!base.exists()) {
            base.mkdir();
        }
        return base;
    }

    private void getIndex() throws Exception {
        File base = getBaseDir();
        File[] indL = base.listFiles(new NameFilter("index"));
        File ind;
        if (indL == null || indL.length == 0) {
            //No index file found, create one
            ind = new File(base, "index");

            ind.createNewFile();
        } else {
            ind = indL[0];
        }
        InputStream is = new FileInputStream(ind);
        byte[] data = new byte[(int) ind.length()];
        is.read(data);
        data = postProcessor.unprocess(data);
        xstream.addImplicitCollection(ConcurrentHashMap.class, "classes");
        if (data.length == 0) {
            this.index = new ConcurrentHashMap<String, Integer>();
        } else {
            this.index = (ConcurrentHashMap<String, Integer>) xstream.fromXML(new ByteArrayInputStream(data));
        }
    }

    private void saveIndex() throws Exception {
        File base = getBaseDir();
        File[] indL = base.listFiles(new NameFilter("index"));
        File ind;
        if (indL == null || indL.length == 0) {
            //No index file found, create one
            ind = new File(base, "index");
        } else {
            ind = indL[0];
        }
        OutputStream os = new FileOutputStream(ind);
        xstream.addImplicitCollection(ConcurrentHashMap.class, "classes");
        byte[] data = xstream.toXML(this.index).getBytes();
        data = postProcessor.process(data);
        os.write(data);
    }

    class DirectoryFilter implements FileFilter {

        private String directoryName;

        public DirectoryFilter(String directoryName) {
            super();
            this.directoryName = directoryName;
        }

        @Override
        public boolean accept(File f) {
            return (f.isDirectory() && f.getName().equals(this.directoryName));
        }
    }

    class NameFilter implements FilenameFilter {

        private String filename;

        public NameFilter(String filename) {
            this.filename = filename;
        }

        public boolean accept(File dir, String name) {
            return this.filename.equals(name);
        }
    }
}
