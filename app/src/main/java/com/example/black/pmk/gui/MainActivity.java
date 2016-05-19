package com.example.black.pmk.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.black.pmk.data.Patient;
import com.example.black.pmk.data.TemperatureStore;

import java.text.DecimalFormat;
import java.util.Random;
import  com.example.black.pmk.R;

import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;

public class MainActivity extends AppCompatActivity {

    private Button temperatureButton, namensButton;
    private final TemperatureStore store = new TemperatureStore(3);
    private boolean commit = false;
    private Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temperatureButton = (Button) findViewById(R.id.temperatureButton);

        // TODO persistenten/gespeicherten Patienten aus bestehender File zum Programmstart auslesen
        patient = new Patient();
    }

    public void updateTemperatureButton(View v) {
        Random random = new Random();
        DecimalFormat df = new DecimalFormat("#.##");
        Double i = ((double) random.nextInt(6)) + random.nextDouble();
        i += 35;
        temperatureButton.setText(df.format(i).toString() + " C°");
        store.setPatient(patient);
        store.queue(i);
    }

    public void startPatientProfileActivity(View v) {
        Toast.makeText(MainActivity.this, "Öffne Patienten-Editor", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MainActivity.this , PatientActivity.class);
        intent.putExtra("patient", patient);
        startActivityForResult(intent, 1);
    }

    public boolean getCommit() {
        return commit;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                patient = (Patient) data.getSerializableExtra("patient");
                String gender;
                if (patient.getGenderEnum() == AdministrativeGenderEnum.MALE){
                    gender = "Herr";
                } else if (patient.getGenderEnum() == AdministrativeGenderEnum.FEMALE){
                    gender = "Frau";
                } else {
                    gender = "";
                }
                namensButton = (Button) findViewById(R.id.namensButton);
                namensButton.setText(gender + " " + patient.getName());
            }
        }
    }
}
