/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

import java.util.ArrayList;

/**
 *
 * @author aleksandersjafjell
 */
public class ExpandableListGroup {

    private String Name;
    private ArrayList<ExpandableListChild> Items;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public ArrayList<ExpandableListChild> getItems() {
        return Items;
    }

    public void setItems(ArrayList<ExpandableListChild> Items) {
        this.Items = Items;
    }



}
