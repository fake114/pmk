package com.example.black.pmk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.black.pmk.threading.HL7Worker;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button temperatureButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HL7Worker worker = new HL7Worker();
        worker.execute();
        temperatureButton = (Button) findViewById(R.id.temperatureButton);
    }


    public void updateTemperatureButton(View v){
        Random random = new Random();
        Integer i = random.nextInt(5);
        i += 35;
        temperatureButton.setText(i.toString() + " C°");


    }

}
