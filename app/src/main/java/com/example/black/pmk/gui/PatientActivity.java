package com.example.black.pmk.gui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import com.example.black.pmk.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;


public class PatientActivity extends FragmentActivity implements
        DatePickerDialog.OnDateSetListener {

    private String name, vorname;
    private int year, month, day;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    private boolean isMan;
    private com.example.black.pmk.data.Patient patient = new com.example.black.pmk.data.Patient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        Intent intent = getIntent();
        patient = (com.example.black.pmk.data.Patient) intent.getSerializableExtra("patient");

        if(patient.getName() != "") {
            ((EditText) findViewById(R.id.nameTextfeld)).setText(patient.getName());
        }
        if(patient.getGivenName() != "") {
            ((EditText) findViewById(R.id.vornameTextfeld)).setText(patient.getGivenName());
        }
        if(patient.getGenderEnum() == AdministrativeGenderEnum.MALE) {
            (findViewById(R.id.geschlechtMaennlichButton)).setActivated(true);
        }
        if(patient.getGenderEnum() == AdministrativeGenderEnum.FEMALE) {
            (findViewById(R.id.geschlechtWeiblichButton)).setActivated(true);
        }
        if(patient.getBirthday() != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(patient.getBirthday());
            Log.w("PatientActivityOnCreate","Date from Calendar parsed = " + sdf.format(calendar.getTime()));
            ((Button) findViewById(R.id.geburtstagButton)).setText(sdf.format(calendar.getTime()));
        }

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //do some stuff for example write on log and update TextField on activity
        //Log.w("DatePicker","Date = " + year);
        ((Button) findViewById(R.id.geburtstagButton)).setText( (day) + "."+ (month +1) + "." + year);
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
        patient.setName(name);
        patient.setGivenName(vorname);
        if(manRadioButton.isChecked()) {
            isMan = true;
            patient.setGenderEnum(AdministrativeGenderEnum.MALE);
        } else {
            isMan = false;
            patient.setGenderEnum(AdministrativeGenderEnum.FEMALE);
        }

        Calendar cal = new GregorianCalendar();
        cal.set(year , month , day, 0, 0, 0);
        patient.setBirthday(cal.getTime());


        //TODO Delete Logging Events from savePatientCredentials
        Log.w("PatientActivity","name = " + name);
        Log.w("PatientActivity","surname = " + vorname);
        Log.w("PatientActivity","isMan = " + isMan);
        Log.w("PatientActivity","birthday = " + day + "." + month + "." + year);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(patient.getBirthday());
        Log.w("PatientActivity","Date from Calendar parsed = " + sdf.format(calendar.getTime()));


        Intent intent = new Intent();
        intent.putExtra("patient", patient);
        setResult(RESULT_OK, intent);
        finish();

    }
}
