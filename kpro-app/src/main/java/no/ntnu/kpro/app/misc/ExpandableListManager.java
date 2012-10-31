/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.misc;

import java.util.ArrayList;
import java.util.List;
import no.ntnu.kpro.core.model.ExpandableListChild;
import no.ntnu.kpro.core.model.ExpandableListGroup;

/**
 *
 * @author aleksandersjafjell
 */
public class ExpandableListManager {
    
    List<ExpandableListGroup> expListItems;
    ArrayList<ExpandableListChild> attachmentsListChildren;
    
    public ExpandableListManager(){
        expListItems = new ArrayList<ExpandableListGroup>();
    }
    
    public ArrayList<ExpandableListGroup> getExpandableListItems(){
        ArrayList<ExpandableListGroup> listGroups = new ArrayList<ExpandableListGroup>();
        ExpandableListGroup attachmentsGroup = new ExpandableListGroup();
        attachmentsGroup.setName("Attachments");
        attachmentsGroup.setItems(attachmentsListChildren);
        listGroups.add(attachmentsGroup);
        return listGroups;
    }
}
