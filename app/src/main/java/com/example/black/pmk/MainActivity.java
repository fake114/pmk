package com.example.black.pmk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.black.pmk.threading.HL7Worker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button temperatureButton;
    List<Double> temperatureList = new ArrayList<>();
    List<Double> temperatureCommitList = new ArrayList<>();
    int limiter = 1000;
    HL7Worker worker;
    boolean commit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        worker = new HL7Worker(this);
        worker.execute();
        temperatureButton = (Button) findViewById(R.id.temperatureButton);
    }


    public void updateTemperatureButton(View v) {
        Random random = new Random();
        DecimalFormat df = new DecimalFormat("#.##");
        Double i = ((double) random.nextInt(6)) + random.nextDouble();
        i += 35;
        temperatureButton.setText(df.format(i).toString() + " C°");
        if (temperatureList.size() == limiter)
            temperatureList.remove(0);
        if (temperatureCommitList.size() >= 2) {
            worker = new HL7Worker(this);
            worker.execute();
        }

        if (temperatureCommitList.size() == limiter)
            temperatureCommitList.remove(0);

        temperatureList.add(i);
        temperatureCommitList.add(i);
    }

    public void startPatientProfileActivity(View v) {
        Toast.makeText(MainActivity.this, "Öffne Patienten-Editor", Toast.LENGTH_LONG).show();
        startActivity(new Intent(MainActivity.this , PatientActivity.class));
    }

    protected List<Double> getTemperatureCommitList() {
        return temperatureCommitList;
    }

    protected void clearTemperatureCommitList() {
        temperatureCommitList.clear();
    }

    public boolean getCommit() {
        return commit;
    }
}
