package com.example.black.pmk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.black.pmk.data.TemperatureStore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button temperatureButton;
    int limiter = 1000;
    private final TemperatureStore store = new TemperatureStore(3);
    boolean commit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temperatureButton = (Button) findViewById(R.id.temperatureButton);
    }


    public void updateTemperatureButton(View v) {
        Random random = new Random();
        DecimalFormat df = new DecimalFormat("#.##");
        Double i = ((double) random.nextInt(6)) + random.nextDouble();
        i += 35;
        temperatureButton.setText(df.format(i).toString() + " C°");
        store.queue(i);
    }

    public void startPatientProfileActivity(View v) {
        Toast.makeText(MainActivity.this, "Öffne Patienten-Editor", Toast.LENGTH_LONG).show();
        startActivity(new Intent(MainActivity.this , PatientActivity.class));
    }


    public boolean getCommit() {
        return commit;
    }
}
