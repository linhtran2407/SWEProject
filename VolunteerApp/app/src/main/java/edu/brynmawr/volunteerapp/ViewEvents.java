package edu.brynmawr.volunteerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ViewEvents extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    public static final String TAG = "MainActivity";
    public List<Event> events;
    public List<Event> Animal_Charity_events;
    public List<Event> Art_Culture_events;
    public List<Event> Community_Dev_events;
    public List<Event> Edu_events;
    public List<Event> Env_events;
    public List<Event> Health_events;
    public List<Event> Human_Services_events;
    public List<Event> Intl_NGOS_events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        RecyclerView rvEvents = findViewById(R.id.rvEvents);
        events = new ArrayList<>();
        Animal_Charity_events = new ArrayList<>();
        Art_Culture_events = new ArrayList<>();
        Community_Dev_events = new ArrayList<>();
        Edu_events = new ArrayList<>();
        Env_events = new ArrayList<>();
        Health_events = new ArrayList<>();
        Human_Services_events = new ArrayList<>();
        Intl_NGOS_events = new ArrayList<>();

        //create an adapter
        EventAdapter eventAdapter = new EventAdapter(this, events);
        // set the adapter on the recycler view
        rvEvents.setAdapter(eventAdapter);
        // set a layout manager on the recycler view
        rvEvents.setLayoutManager(new LinearLayoutManager(this));

        events.addAll(onConnectButtonClick());
        eventAdapter.notifyDataSetChanged();
        Log.i(TAG, "events size: " + events.size());
    }

    public List<Event> onConnectButtonClick() {

//        TextView tv = findViewById(R.id.statusField);
        List<Event> events = new ArrayList<>();

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {
                            // assumes that there is a server running on the AVD's host on port 3000
                            // and that it has a /test endpoint that returns a JSON object with
                            // a field called "message"

                            URL url = new URL("http://10.0.2.2:3000/allapp");

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();
                            int responsecode = conn.getResponseCode();

                            // make sure the response has "200 OK" as the status
                            if (responsecode != 200) {
                                System.out.println("Unexpected status code: " + responsecode);
                            } else {
                                Scanner in = new Scanner(url.openStream());
                                JSONParser parser = new JSONParser();
                                while (in.hasNext()) {

                                    // read the next line of the body of the response
                                    String array = in.nextLine();
                                    System.out.println(array);
                                    JSONArray resp_arr = (JSONArray) parser.parse(array);

                                    // first, create the parser
                                    Iterator iter = resp_arr.iterator();

                                    while (iter.hasNext()) {
                                        JSONObject event = (JSONObject) iter.next();
                                        boolean approved = (boolean) event.get("approved");

                                        if (approved) {
                                            // read the "id" and "status" field from the JSON object
                                            String name = (String) event.get("name");
                                            String desc = (String) event.get("description");
                                            JSONArray categories = (JSONArray) event.get("category");
                                            String category = categories.toString();
                                            Log.i(TAG, "category: " + category);
                                            Event curr = new Event(name, desc, null, "", "", "", "", "");
                                            events.add(curr);
                                            Log.d("trial", curr.name);

                                            if (category.equals("[\"Animal Charity\"]")) {
                                                Log.i(TAG, "add event to: Animal_Charity_events, category is: " + category);
                                                Animal_Charity_events.add(curr);
                                            } else if (category.equals("[\"Arts and Culture Charity\"]")) {
                                                Art_Culture_events.add(curr);
                                            } else if (category.equals("[\"Community Development Charity\"]")) {
                                                Community_Dev_events.add(curr);
                                            } else if (category.equals("[\"Education Charity\"]")) {
                                                Edu_events.add(curr);
                                            } else if (category.equals("[\"Environmental Charity\"]")) {
                                                Env_events.add(curr);
                                            } else if (category.equals("[\"Health Charity\"]")) {
                                                Health_events.add(curr);
                                            } else if (category.equals("[\"Human Services Charity\"]")) {
                                                Human_Services_events.add(curr);
                                            } else if (category.equals("[\"International NGOs\"]")) {
                                                Intl_NGOS_events.add(curr);
                                            }
                                        }
                                    }
                                }

                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
//                            message = e.toString();
                        }
                    }
            );

            // this waits for up to 2 seconds
            // it's a bit of a hack because it's not truly asynchronous
            // but it should be okay for our purposes (and is a lot easier)
            executor.awaitTermination(2, TimeUnit.SECONDS);

            // now we can set the status in the TextView
//            tv.setText(message);
        }
        catch (Exception e) {
            // uh oh
            e.printStackTrace();
//            tv.setText(e.toString());
        }

        return events;
    }

    /** Called when the user taps the Send button */
    public void createReview(View view) {
        Intent intent = new Intent(this, CreateReview.class);
//        EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }


    /************************************************************/
    public void filterEventsByCategory(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.category_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        List<Event> filtered_events = new ArrayList<>();;
        setContentView(R.layout.activity_view_events);
        RecyclerView rvEvents = findViewById(R.id.rvEvents);
        //create an adapter
        EventAdapter eventAdapter = new EventAdapter(this, filtered_events);
        // set the adapter on the recycler view
        rvEvents.setAdapter(eventAdapter);
        // set a layout manager on the recycler view
        rvEvents.setLayoutManager(new LinearLayoutManager(this));

        switch (menuItem.getItemId()) {
            case R.id.animalEvents:
                Toast.makeText(this,"Filter by: Animal Charity Events", Toast.LENGTH_SHORT).show();
                filtered_events.clear();
                filtered_events.addAll(Animal_Charity_events);
                eventAdapter.notifyDataSetChanged();
                Log.i(TAG, "animal events size: " + filtered_events.size());
                return true;
            case R.id.artsCultureEvents:
                Toast.makeText(this,"Filter by: Arts and Culture Charity Events", Toast.LENGTH_SHORT).show();
                filtered_events.clear();
                filtered_events.addAll(Art_Culture_events);
                eventAdapter.notifyDataSetChanged();
                Log.i(TAG, "arts and culture events size: " + filtered_events.size());
                return true;
            case R.id.commDevEvents:
                Toast.makeText(this,"Filter by: Community Development Charity Events", Toast.LENGTH_SHORT).show();
                filtered_events.clear();
                filtered_events.addAll(Community_Dev_events);
                eventAdapter.notifyDataSetChanged();
                Log.i(TAG, "comm and dev events size: " + filtered_events.size());
                return true;
            case R.id.eduEvents:
                Toast.makeText(this,"Filter by: Education Charity Events", Toast.LENGTH_SHORT).show();
                filtered_events.clear();
                filtered_events.addAll(Edu_events);
                eventAdapter.notifyDataSetChanged();
                Log.i(TAG, "edu events size: " + filtered_events.size());
                return true;
            case R.id.evnEvents:
                Toast.makeText(this,"Filter by: Environmental Charity Events", Toast.LENGTH_SHORT).show();
                filtered_events.clear();
                filtered_events.addAll(Env_events);
                eventAdapter.notifyDataSetChanged();
                Log.i(TAG, "environmental events size: " + filtered_events.size());
                return true;
            case R.id.heathEvents:
                Toast.makeText(this,"Filter by: Health Charity Events", Toast.LENGTH_SHORT).show();
                filtered_events.clear();
                filtered_events.addAll(Health_events);
                eventAdapter.notifyDataSetChanged();
                Log.i(TAG, "health events size: " + filtered_events.size());
                return true;
            case R.id.humanServicesEvents:
                Toast.makeText(this,"Filter by: Human Services Charity Events", Toast.LENGTH_SHORT).show();
                filtered_events.clear();
                filtered_events.addAll(Human_Services_events);
                eventAdapter.notifyDataSetChanged();
                Log.i(TAG, "human services events size: " + filtered_events.size());
                return true;
            case R.id.intlNGOEvents:
                Toast.makeText(this,"Filter by: International NGOs Events", Toast.LENGTH_SHORT).show();
                filtered_events.clear();
                filtered_events.addAll(Intl_NGOS_events);
                eventAdapter.notifyDataSetChanged();
                Log.i(TAG, "intl events size: " + filtered_events.size());
                return true;
            case R.id.all:
                Toast.makeText(this,"Show all events", Toast.LENGTH_SHORT).show();
                filtered_events.clear();
                filtered_events.addAll(events);
                eventAdapter.notifyDataSetChanged();
                Log.i(TAG, "events size: " + filtered_events.size());
                return true;
            default:
                return false;
        }
    }
}