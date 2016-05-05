package com.example.black.pmk;

import android.app.DatePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;


public class PatientActivity extends FragmentActivity implements
        DatePickerDialog.OnDateSetListener {

    private String name, vorname;
    private int year, month, day;
    private boolean isMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //do some stuff for example write on log and update TextField on activity
        //Log.w("DatePicker","Date = " + year);
        ((Button) findViewById(R.id.geburtstagButton)).setText(day + "."+ month + "." + year);
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public void savePatientCredentials(View v) {
        EditText nameField = (EditText) findViewById(R.id.nameTextfeld);
        EditText surnameField = (EditText) findViewById(R.id.vornameTextfeld);
        RadioButton manRadioButton = (RadioButton) findViewById(R.id.geschlechtMaennlichButton);

        name = nameField.getText().toString();
        vorname = surnameField.getText().toString();
        if(manRadioButton.isChecked()) {
            isMan = true;
        } else {
            isMan = false;
        }

        //TODO Delete Logging Events from savePatientCredentials
        Log.w("PatientActivity","name = " + name);
        Log.w("PatientActivity","surname = " + vorname);
        Log.w("PatientActivity","isMan = " + isMan);
        Log.w("PatientActivity","birtday = " + day + "." + month + "." + year);

        //TODO Add Connector for transporting Patient`s credentials to FHIR-Module
    }
}
