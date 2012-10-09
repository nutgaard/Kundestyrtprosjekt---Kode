/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.interfaces;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Nicklas
 */
public abstract class ServiceInterface<T> {
    protected List<T> listeners;
    
    public ServiceInterface() {
        this.listeners = new LinkedList<T>();
    }
    public void addListener(T listener){
        if (!this.listeners.contains(listener)){
            this.listeners.add(listener);
        }
    }
    public void removeListener(T listener) {
        this.listeners.remove(listener);
    }
    protected void fireCallback(Enum e) {
        
    }
    public List<T> getListeners() {
        return this.listeners;
    }
    public void clearListeners() {
        this.listeners.clear();
    }
}
