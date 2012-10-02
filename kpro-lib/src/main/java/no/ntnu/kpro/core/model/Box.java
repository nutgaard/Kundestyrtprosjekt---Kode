/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 *
 * @author Nicklas
 */
public class Box<T> extends ArrayList<T> {

    public T getNext(T current) {
        int search = indexOf(current);
        if (search+1 < size()){
            return get(search+1);
        }
        return null;
    }

    public T getPrevious(T current) {
        int search = indexOf(current);
        if (search-1 >= 0){
            return get(search-1);
        }
        return null;
    }  
}
