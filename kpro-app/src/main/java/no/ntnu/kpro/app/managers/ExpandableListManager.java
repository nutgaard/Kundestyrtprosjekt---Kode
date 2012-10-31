/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app.managers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ExpandableListView;
import java.util.ArrayList;
import java.util.List;
import no.ntnu.kpro.app.activities.SendMessageActivity;
import no.ntnu.kpro.app.adapters.ExpandableListAdapter;
import no.ntnu.kpro.core.model.ExpandableListChild;
import no.ntnu.kpro.core.model.ExpandableListGroup;

/**
 *
 * @author aleksandersjafjell
 */
public class ExpandableListManager {
    Context context;
    ExpandableListView expandableList;
    ArrayList<ExpandableListGroup> expListItems;
    ArrayList<ExpandableListChild> attachmentsListChildren;
    
    public ExpandableListManager(Context context, ExpandableListView view){
        this.context = context;
        this.expandableList = view;
        attachmentsListChildren = new ArrayList<ExpandableListChild>();
        expListItems = new ArrayList<ExpandableListGroup>();
        updateExpandableList();
    }
    
    
   public void addAttachmentToDropDown(Uri uri, int attachmentCounter) {
        String name = "Image " + attachmentCounter; 
        ExpandableListChild child = new ExpandableListChild(name, uri);
        attachmentsListChildren.add(child);
        updateExpandableList();
    }
   
   
   private void updateExpandableList(){
       expListItems = getExpandableListItems();

        if (attachmentsListChildren.isEmpty()) {
            expandableList.setVisibility(View.GONE);
        } else {
            expandableList.setVisibility(View.VISIBLE);
        }

        ExpandableListAdapter adapter;
        adapter = new ExpandableListAdapter(context, expListItems);
        expandableList.setAdapter(adapter);

        ExpandableListView.OnChildClickListener childClickListener = new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView elv, View view, int i, int i1, long l) {
                ExpandableListChild currentChild = attachmentsListChildren.get(i1);
                Intent showImageIntent = new Intent();
                showImageIntent.setAction(Intent.ACTION_VIEW);
                showImageIntent.setDataAndType(currentChild.getUri(), "image/jpg");
                context.startActivity(showImageIntent);
                return true;
            }
        };
        expandableList.setOnChildClickListener(childClickListener);
   }
   
   private ArrayList<ExpandableListGroup> getExpandableListItems(){
        ArrayList<ExpandableListGroup> listGroups = new ArrayList<ExpandableListGroup>();
        ExpandableListGroup attachmentsGroup = new ExpandableListGroup();
        attachmentsGroup.setName("Attachments");
        attachmentsGroup.setItems(attachmentsListChildren);
        listGroups.add(attachmentsGroup);
        return listGroups;
    }
    
   public void clearAttachmentsChildren(){
       this.attachmentsListChildren.clear();
       this.updateExpandableList();
   }
   
}
