/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.PersistenceService.PersistentWriteThroughStorage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nicklas
 */
public class TraceProxy implements InvocationHandler {

    static Map<Object, Object> map = new HashMap<Object, Object>();
    Object object;
    Object proxy;
    int id = -1;

    private TraceProxy(Object object) {
        System.out.println("Creating new Proxy: " + this);
        this.object = object;
    }

    public static Object trace(Object object) {
        System.out.println("Tracing: " + object);
        if (map.containsKey(object)) {
            return map.get(object);
        } else {
            Object o = Proxy.newProxyInstance(
                    object.getClass().getClassLoader(),
                    object.getClass().getInterfaces(),
                    new TraceProxy(object));
            map.put(object, o);
            return o;
        }
    }

    public static Object untrace(Object object) {
        if (map.containsKey(object)){
            map.remove(object);
            return ((TraceProxy) Proxy.getInvocationHandler(object)).object;
        }
        return object;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        try {
            //All changeable methods should start with set
            result = method.invoke(object, args);
            if (method.getName().startsWith("set")) {
                System.out.println("Detected set method by : " + object);
                PersistentWriteThroughStorage.getInstance().save(map.get(object));
            }
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
        return result;
    }
}
