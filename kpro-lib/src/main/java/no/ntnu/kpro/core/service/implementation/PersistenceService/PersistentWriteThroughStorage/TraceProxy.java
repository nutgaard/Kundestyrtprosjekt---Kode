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
import java.util.Map.Entry;

/**
 *
 * @author Nicklas
 */
public class TraceProxy implements InvocationHandler {

    static Map<PersistentWriteThroughStorage, Map<Object, Object>> globalMap = new HashMap<PersistentWriteThroughStorage, Map<Object, Object>>();
    Object object;
    Object proxy;
    int id = -1;

    private TraceProxy(Object object) {
        this.object = object;
    }

    public static Object traceAll(Object object) {
        for (Entry<PersistentWriteThroughStorage, Map<Object, Object>> localmap : globalMap.entrySet()) {
            if (localmap.getValue().containsValue(object)) {
                return trace(localmap.getKey(), object);
            }
        }
        if (globalMap.entrySet().size() > 0) {
            PersistentWriteThroughStorage p = globalMap.keySet().iterator().next();
            return trace(p, object);
        }else {
            throw new RuntimeException("Not been persistet yet");
        }
    }

    public static Object trace(PersistentWriteThroughStorage storage, Object object) {
        if (!globalMap.containsKey(storage)) {
            globalMap.put(storage, new HashMap<Object, Object>());
        }
        Map<Object, Object> map = globalMap.get(storage);
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

    public static Object untrace(PersistentWriteThroughStorage storage, Object object) {
        if (!globalMap.containsKey(storage)) {
            return null;
        }
        Map<Object, Object> map = globalMap.get(storage);
        if (map.containsKey(object)) {
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
                for (Entry<PersistentWriteThroughStorage, Map<Object, Object>> e : globalMap.entrySet()) {
                    e.getKey().save(e.getValue().get(object));
//                    PersistentWriteThroughStorage.getInstance().save(map.get(object));
                }
            }
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
        return result;
    }
}
