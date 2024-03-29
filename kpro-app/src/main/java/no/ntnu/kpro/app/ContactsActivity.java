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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

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
        Contact kpro = new Contact("KproThales", "kprothales@gmail.com", 2);
        Contact magnus = new Contact("Magnus", "magnus@gmail.com", 2);
        Contact aleksander = new Contact("Aleksander", "aleksander@gmail.com", 2);
        Contact ida = new Contact("Ida", "Ida@gmail.com", 2);
        Contact kristin = new Contact("Kristin", "Kristin@gmail.com", 2);
        Contact nicklas = new Contact("Nicklas", "Nicklas@gmail.com", 2);
        Contact christian = new Contact("Christian", "Christian@thales.no", 2);
        Contact stig = new Contact("Stig", "Stig@thales.no", 2);
        Contact kprotest = new Contact("KproTest", "kprotest@gmail.com", 2);

        elements.add(kpro);
        elements.add(kprotest);
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

        myListView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> av, View view, int i, long l) {
                Contact c = (Contact)av.getItemAtPosition(i);
                Intent returnIntent = new Intent();           
                returnIntent.putExtra("result",c.getEmail());
                setResult(RESULT_OK,returnIntent);     
                finish();
            }
        }); 
     
    }
}