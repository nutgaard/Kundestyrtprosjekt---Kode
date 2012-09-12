/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Kristin
 */
public class InboxActivity extends WrapperActivity {

    ArrayList<HashMap<String, String>> list;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_list);

        list = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("from", "Kristin");
        map.put("subject", "Meeting");
        map.put("date", "30.08.2012");
        list.add(map);
        HashMap<String, String> map2 = new HashMap<String, String>();
        map2.put("from", "Ida");
        map2.put("subject", "Dinner");
        map2.put("date", "29.08.2012");
        list.add(map2);

        SimpleAdapter adapter = new SimpleAdapter((this), list, R.layout.message_list_item, new String[]{"from", "subject", "date"}, new int[]{R.id.from, R.id.subject, R.id.date});

        ListView v = (ListView) findViewById(R.id.list);

        v.setAdapter(adapter);
        
        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                // selected item
                //String from = ((TextView) view).getText().toString();

                HashMap hmap = list.get(position);
                String from = hmap.get("from").toString();
                String subj = hmap.get("subject").toString();

//                TextView v = (TextView) findViewById(R.id.from);
//                String s = v.getText().toString();
//                TextView v2 = (TextView) findViewById(R.id.subject);
//                String s2 = v2.getText().toString();

                // Launching new Activity on selecting single List Item
                Intent i = new Intent(getApplicationContext(), MessageInActivity.class);
                // sending data to new activity
                i.putExtra("from", from);
                i.putExtra("subject", subj);
                startActivity(i);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu_inbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_new:
                Intent i = new Intent(getApplicationContext(), SendMessageActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_sort:
                //showHelp();
                return true;
            case R.id.menu_settings:
                //showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
