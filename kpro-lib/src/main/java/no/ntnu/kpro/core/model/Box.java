/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author Nicklas
 */
public class Box<T> extends LinkedList<T> {

    public T getNext(T current) {
        ListIterator<T> search = findCurrent(current);
        if (search == null) {
            return null;
        }
        if (search.hasNext()) {
            return search.next();
        }
        return null;
    }

    public T getPrevious(T current) {
        ListIterator<T> search = findCurrent(current);
        if (search == null) {
            return null;
        }
        if (search.hasPrevious()) {
            return search.previous();
        }
        return null;
    }

    private ListIterator<T> findCurrent(T current) {
        ListIterator<T> search = listIterator();
        while (search.hasNext()) {
            if (search.next().equals(current)) {
                return search;
            }
        }
        return null;
    }
}
