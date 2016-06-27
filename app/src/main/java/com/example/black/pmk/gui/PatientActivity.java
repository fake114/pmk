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
import android.widget.RadioGroup;

import com.example.black.pmk.R;
import com.example.black.pmk.data.Patient;

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
    private boolean isMan, changedBirthday;
    private com.example.black.pmk.data.Patient patient = new com.example.black.pmk.data.Patient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changedBirthday = false;
        setContentView(R.layout.activity_patient);
        Intent intent = getIntent();
        patient = (com.example.black.pmk.data.Patient) intent.getSerializableExtra("patient");

        if (patient != null) {
            if (patient.getName() != "") {
                ((EditText) findViewById(R.id.nameTextfeld)).setText(patient.getName());
            }
            if (patient.getGivenName() != "") {
                ((EditText) findViewById(R.id.vornameTextfeld)).setText(patient.getGivenName());
            }
            if (patient.getGenderEnum() == AdministrativeGenderEnum.MALE) {
                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);
                radioGroup.check(R.id.geschlechtMaennlichButton);
                //(findViewById(R.id.geschlechtMaennlichButton)).setSelected(true);
            }
            if (patient.getGenderEnum() == AdministrativeGenderEnum.FEMALE) {
                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);
                radioGroup.check(R.id.geschlechtWeiblichButton);
                //(findViewById(R.id.geschlechtWeiblichButton)).setSelected(true);
            }
            if (patient.getBirthday() != null) {
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(patient.getBirthday());
                ((Button) findViewById(R.id.geburtstagButton)).setText(sdf.format(calendar.getTime()));
            }
        }

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //do some stuff for example write on log and update TextField on activity
        ((Button) findViewById(R.id.geburtstagButton)).setText( (day) + "."+ (month +1) + "." + year);
        this.day = day;
        this.month = month;
        this.year = year;
        changedBirthday = true;
    }

    public void savePatientCredentials(View v) {
        EditText nameField = (EditText) findViewById(R.id.nameTextfeld);
        EditText surnameField = (EditText) findViewById(R.id.vornameTextfeld);
        RadioButton manRadioButton = (RadioButton) findViewById(R.id.geschlechtMaennlichButton);

        name = nameField.getText().toString();
        vorname = surnameField.getText().toString();

        if(patient == null) {
            patient = new Patient();
        }
        patient.setName(name);
        patient.setGivenName(vorname);
        if(manRadioButton.isChecked()) {
            isMan = true;
            patient.setGenderEnum(AdministrativeGenderEnum.MALE);
        } else {
            isMan = false;
            patient.setGenderEnum(AdministrativeGenderEnum.FEMALE);
        }

        //ehm... a bit strange...
        if (changedBirthday == true) {
            Calendar cal = new GregorianCalendar();
            cal.set(year, month, day, 0, 0, 0);
            patient.setBirthday(cal.getTime());
            //Calendar calendar = new GregorianCalendar();
            //calendar.setTime(patient.getBirthday());
        }


        Intent intent = new Intent();
        intent.putExtra("patient", patient);
        setResult(RESULT_OK, intent);
        finish();
    }
}
