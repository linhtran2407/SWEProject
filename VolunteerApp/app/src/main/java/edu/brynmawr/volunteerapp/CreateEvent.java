package edu.brynmawr.volunteerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CreateEvent
        extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {
    DatePickerDialog picker;
    String date ="";
    // create array of Strings
    // and store name of categories
    String[] categories = { "","Animal Charity", "Arts and Culture Charity",
            "Community Development Charity", "Education Charity",
            "Environmental Charity", "Health Charity", "Human Services Charity", "International NGOs"};
    String category = "";
    Button btnSubmit;
    EditText etEventName;
    EditText etDate;
    EditText etDesc;
    EditText etFirstName;
    EditText etLastName;
    EditText etEmail;
    EditText etPhone;
    EditText etAddress;
    Spinner spin;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Take the instance of Spinner and
        // apply OnItemSelectedListener on it which
        // tells which item of spinner is clicked
        spin = findViewById(R.id.categorySpinner);
        spin.setOnItemSelectedListener(this);

        // Create the instance of ArrayAdapter
        // having the list of courses
        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                categories);

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        spin.setAdapter(ad);

        btnSubmit = findViewById(R.id.btnSubmit);
        etEventName = findViewById(R.id.etReviewName);
        etDate = findViewById(R.id.etDate);
        etDesc = findViewById(R.id.etDesc);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etDate.setInputType(InputType.TYPE_NULL);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(CreateEvent.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                etDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                date =  year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            }
                        }, year, month, day);
                picker.show();
            }
        });
    }

    public Date parseDate(String date) throws ParseException {
        try {
            if (date == null) {
                Log.i("DATE", "date is null");
                return null;
            }
            Log.i("DATE", "Date Parsed Successfully");
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        }
        catch (ParseException e){
            Log.e("DATE", "Issue Parsing Date");
            return null;
        }
    }

    // Performing action when ItemSelected
    // from spinner, Overriding onItemSelected method
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        category = categories[i];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private Event formatDataAsEvent() throws ParseException {
        Event event = new Event(etEventName.getText().toString(),
                etDesc.getText().toString(),
                parseDate(date),
                etFirstName.getText().toString(),
                etLastName.getText().toString(),
                etEmail.getText().toString(),
                category,
                etAddress.getText().toString());
        return event;
    }

    public void onSubmitButtonClick(View v) {
        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {
                            Event query = formatDataAsEvent();
                            if (TextUtils.isEmpty(etEventName.getText())) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),
                                        "Event Title is Empty",
                                        Toast.LENGTH_LONG)
                                        .show();
                                        etEventName.setError("Event Title is required!");
                                    }
                                });
                            } else {
                                URL url = new URL("http://10.0.2.2:3000/createapp/" +
                                        "?name=" + query.name +
                                        "&description=" + query.desc +
                                        "&date=" + query.date +
                                        "&first_name=" + query.first_name +
                                        "&last_name=" + query.last_name +
                                        "&email=" + query.email +
                                        "&category=" + query.category +
                                        "&address=" + query.address
                                        );
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestMethod("GET");
                                conn.connect();

                                Scanner in = new Scanner(url.openStream());

                                String response = in.nextLine();
                                if (response.equals("SUCCESS")){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),
                                                    "Event Successfully Submitted!",
                                                    Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    });
                                    finish();
                                } else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),
                                                    "Issue with Submitting Event",
                                                    Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    });
                                }
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
            // this waits for up to 2 seconds
            // it's a bit of a hack because it's not truly asynchronous
            // but it should be okay for our purposes (and is a lot easier)
            executor.awaitTermination(2, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
