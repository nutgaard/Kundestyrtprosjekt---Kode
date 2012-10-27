/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

/**
 *
 * @author Lars
 */
import no.ntnu.kpro.app.activities.WrapperActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

import no.ntnu.kpro.app.activities.SendMessageActivity;

import no.ntnu.kpro.app.adapters.ContactsAdapter;
import no.ntnu.kpro.core.model.Contact;


public class ContactsActivity extends WrapperActivity {

    ListView myListView;
    ArrayList<Contact> elements;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);

        elements = new ArrayList<Contact>();

        Contact lars = new Contact("Lars", "lars.hoysaeter@gmail.com", 1);
        Contact magnus = new Contact("Magnus", "magnus@gmail.com", 2);
        Contact aleksander = new Contact("Aleksander", "aleksander@gmail.com", 2);
        Contact ida = new Contact("Ida", "Ida@gmail.com", 2);
        Contact kristin = new Contact("Kristin", "Kristin@gmail.com", 2);
        Contact nicklas = new Contact("Nicklas", "Nicklas@gmail.com", 2);
        Contact christian = new Contact("Christian", "Christian@thales.no", 2);
        Contact stig = new Contact("Stig", "Stig@thales.no", 2);
        
        elements.add(lars);
        elements.add(magnus);
        elements.add(aleksander);
        elements.add(ida);
        elements.add(kristin);
        elements.add(nicklas);
        elements.add(christian);
        elements.add(stig);
        
        
       

        //Collections.sort(elements); // Must be sorted!

        // listview
        myListView = (ListView) findViewById(R.id.lstContacts);
        //myListView.setFastScrollEnabled

        ContactsAdapter conAdapter = new ContactsAdapter(ContactsActivity.this, elements);
        myListView.setAdapter(conAdapter);
}
}