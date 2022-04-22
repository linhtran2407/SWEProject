package edu.brynmawr.volunteerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CreateEvent
        extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    // create array of Strings
    // and store name of courses
    String[] courses = { "C", "Data structures",
            "Interview prep", "Algorithms",
            "DSA with java", "OS" };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Take the instance of Spinner and
        // apply OnItemSelectedListener on it which
        // tells which item of spinner is clicked
        Spinner spino = findViewById(R.id.categoryspinner);
        spin.setOnItemSelectedListener(this);

        // Create the instance of ArrayAdapter
        // having the list of courses
        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                courses);

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        spino.setAdapter(ad);
    }

    // Performing action when ItemSelected
    // from spinner, Overriding onItemSelected method
    @Override
    public void onItemSelected(AdapterView<*> arg0,
                               View arg1,
                               int position,
                               long id)
    {

        // make toastof name of course
        // which is selected in spinner
        Toast.makeText(getApplicationContext(),
                courses[position],
                Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onNothingSelected(AdapterView<*> arg0)
    {
        // Auto-generated method stub
    }
}


//    protected String message;
//
//
//    public void onConnectButtonClick(View v) {
//
//        TextView tv = findViewById(R.id.statusField);
//
//        try {
//            ExecutorService executor = Executors.newSingleThreadExecutor();
//            executor.execute( () -> {
//                        try {
//                            // assumes that there is a server running on the AVD's host on port 3000
//                            // and that it has a /test endpoint that returns a JSON object with
//                            // a field called "message"
//
//                            URL url = new URL("http://10.0.2.2:3000/create");
//
//                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                            conn.setRequestMethod("POST");
//                            conn.connect();
//
//                            Scanner in = new Scanner(url.openStream());
//                            String response = in.nextLine();
//
//                            JSONObject jo = new JSONObject(response);
//
//                            // need to set the instance variable in the Activity object
//                            // because we cannot directly access the TextView from here
//                            message = jo.getString("message");
//
//                        }
//                        catch (Exception e) {
//                            e.printStackTrace();
//                            message = e.toString();
//                        }
//                    }
//            );
//
//            // this waits for up to 2 seconds
//            // it's a bit of a hack because it's not truly asynchronous
//            // but it should be okay for our purposes (and is a lot easier)
//            executor.awaitTermination(2, TimeUnit.SECONDS);
//
//            // now we can set the status in the TextView
//            tv.setText(message);
//        }
//        catch (Exception e) {
//            // uh oh
//            e.printStackTrace();
//            tv.setText(e.toString());
//        }
//
//
//    }
}