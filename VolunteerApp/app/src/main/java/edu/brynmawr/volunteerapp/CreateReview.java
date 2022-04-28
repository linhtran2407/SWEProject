package edu.brynmawr.volunteerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CreateReview extends AppCompatActivity {

    Button btnSubmit;
    EditText etReviewName;
    EditText etDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_review);

        btnSubmit = findViewById(R.id.btnSubmit);
        etReviewName = findViewById(R.id.etReviewName);
        etDesc = findViewById(R.id.etDesc);
    }

    public void onSubmitClick(View v) {
        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {
                            //Event query = formatDataAsEvent();
                            int random = 1 + (int)(Math.random() * ((1000 - 1) + 1));
                            Review review = new Review (random, etDesc.getText().toString(), etReviewName.getText().toString());
                            if (TextUtils.isEmpty(etReviewName.getText())) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),
                                                "Event Title is Empty",
                                                Toast.LENGTH_LONG)
                                                .show();
                                        etReviewName.setError("Review Title is required!");
                                    }
                                });
                            } else if (TextUtils.isEmpty(etDesc.getText())) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),
                                                "Review Description is Empty",
                                                Toast.LENGTH_LONG)
                                                .show();
                                        etReviewName.setError("Review Description is required!");
                                    }
                                });
                            } else {
                                URL url = new URL("http://10.0.2.2:3000/createreview/" +
                                        "?body=" + review.body +
                                        "&id=" + review.id +
                                        "&title=" + review.title
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
                                                    "Review Successfully Submitted!",
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
                                                    "Issue with Submitting Review",
                                                    Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    });
                                }
//                                String response = in.nextLine();
//                                JSONObject jo = new JSONObject(response);
                                finishActivity(46);
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
            Intent intent = new Intent(this, ViewEvents.class);
            startActivity(intent);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}