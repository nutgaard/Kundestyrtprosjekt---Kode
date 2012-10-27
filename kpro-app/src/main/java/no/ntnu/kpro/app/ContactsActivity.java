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
<<<<<<< HEAD
import no.ntnu.kpro.app.activities.SendMessageActivity;
=======
import no.ntnu.kpro.app.adapters.ContactsAdapter;
>>>>>>> 9a6ab9612d93f5c28700d3ec66f295aff7691caf

public class ContactsActivity extends WrapperActivity {

    ListView myListView;
    ArrayList<String> elements;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);

        // elements
        String s = "QWERTZUIOPASDFGHJKLYXCVBNM";
        Random r = new Random();
        elements = new ArrayList<String>();
        //     for (int i = 0; i < 300; i++) {

        //                      elements.add(s.substring(r.nextInt(s.length())));

//                }
        elements.add("lars@gmail.com");
        elements.add("magnus@gmail.com");
        elements.add("aleksander@gmail.com");
        elements.add("ida@gmail.com");
        elements.add("kristin@gmail.com");
        elements.add("nicklas@gmail.com");
        elements.add("christian@thales.no");
        elements.add("stig@thales.no");

        Collections.sort(elements); // Must be sorted!

        // listview
        myListView = (ListView) findViewById(R.id.lstContacts);
        //myListView.setFastScrollEnabled(true);
        MyIndexerAdapter<String> adapter = new MyIndexerAdapter<String>(
                getApplicationContext(), android.R.layout.simple_list_item_1,
                elements);
<<<<<<< HEAD
=======
        ContactsAdapter conAdapter = new ContactsAdapter(ContactsActivity.this, elements);
        myListView.setAdapter(conAdapter);
>>>>>>> 9a6ab9612d93f5c28700d3ec66f295aff7691caf

    }

    class MyIndexerAdapter<T> extends ArrayAdapter<T> implements SectionIndexer {

        ArrayList<String> myElements;
        HashMap<String, Integer> alphaIndexer;
        String[] sections;

        public MyIndexerAdapter(Context context, int textViewResourceId,
                List<T> objects) {
            super(context, textViewResourceId, objects);
            myElements = (ArrayList<String>) objects;
            // here is the tricky stuff
            alphaIndexer = new HashMap<String, Integer>();
            // in this hashmap we will store here the positions for
            // the sections

            int size = elements.size();
            for (int i = size - 1; i >= 0; i--) {
                String element = elements.get(i);
                alphaIndexer.put(element.substring(0, 1), i);
                //We store the first letter of the word, and its index.
                //The Hashmap will replace the value for identical keys are putted in
            }

            // now we have an hashmap containing for each first-letter
            // sections(key), the index(value) in where this sections begins

            // we have now to build the sections(letters to be displayed)
            // array .it must contains the keys, and must (I do so...) be
            // ordered alphabetically

            Set<String> keys = alphaIndexer.keySet(); // set of letters ...sets
            // cannot be sorted...

            Iterator<String> it = keys.iterator();
            ArrayList<String> keyList = new ArrayList<String>(); // list can be
            // sorted

            while (it.hasNext()) {
                String key = it.next();
                keyList.add(key);
            }

            Collections.sort(keyList);

            sections = new String[keyList.size()]; // simple conversion to an
            // array of object
            keyList.toArray(sections);

            // ooOO00K !

        }

        @Override
        public int getPositionForSection(int section) {
            // Log.v("getPositionForSection", ""+section);
            String letter = sections[section];

            return alphaIndexer.get(letter);
        }

        @Override
        public int getSectionForPosition(int position) {

            // you will notice it will be never called (right?)
            Log.v("getSectionForPosition", "called");
            return 0;
        }

        @Override
        public Object[] getSections() {

            return sections; // to string will be called each object, to display
            // the letter
        }
    }
}
