package edu.brynmawr.volunteerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Button btnAddEvent;
    Button btnViewEvents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        btnViewEvents = findViewById(R.id.btnViewEvents);
        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createEvent = new Intent(MainActivity.this, CreateEvent.class);
                startActivity(createEvent);
            }
        });
        btnViewEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewEvents = new Intent(MainActivity.this, ViewEvents.class);
                startActivity(viewEvents);
            }
        });
    }
}