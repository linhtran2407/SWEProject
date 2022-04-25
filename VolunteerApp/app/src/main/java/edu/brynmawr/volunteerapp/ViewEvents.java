package edu.brynmawr.volunteerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ViewEvents extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        List<Event> events;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        RecyclerView rvEvents = findViewById(R.id.rvEvents);
        events = new ArrayList<>();
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

                                        // read the "id" and "status" field from the JSON object
                                        String name = (String) event.get("name");
                                        String desc = (String) event.get("description");

                                        Event curr = new Event(name, desc, null, "", "", "", "", "");
                                        events.add(curr);
                                        Log.d("trial", curr.name);
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
}