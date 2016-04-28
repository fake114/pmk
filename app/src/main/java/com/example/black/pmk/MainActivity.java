package com.example.black.pmk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.black.pmk.threading.HL7Worker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HL7Worker worker = new HL7Worker();
        worker.execute();

    }
}
