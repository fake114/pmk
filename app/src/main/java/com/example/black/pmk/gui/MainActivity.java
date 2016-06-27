package com.example.black.pmk.gui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidplot.Plot;
import com.androidplot.ui.Size;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.example.black.pmk.data.Patient;
import com.example.black.pmk.data.ProgressStore;
import com.example.black.pmk.data.TemperatureGenerator;
import com.example.black.pmk.data.TemperatureStore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.example.black.pmk.R;
import com.example.black.pmk.threading.TemperatureAlarmWorker;
import com.google.gson.Gson;

import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;

public class MainActivity extends AppCompatActivity {

    private Button temperatureButton, namensButton;
    private TemperatureStore store;

    private boolean commit = false;
    private Patient patient;
    private TemperatureAlarmWorker alarm;

    private XYPlot dynamicPlot;
    private MyPlotUpdater plotUpdater;
    private List plotData = new ArrayList();
    private XYSeries temperatureSeries;

    private SharedPreferences mPrefs;

    private Handler handler;
    private boolean isGeneratorWorking = false;
    private final TemperatureGenerator generator = new TemperatureGenerator();

    private boolean canceled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        store = new TemperatureStore(3, this);
        mPrefs = getApplicationContext().getSharedPreferences("TMP_STORE", MODE_PRIVATE);

        setContentView(R.layout.activity_main);
        temperatureButton = (Button) findViewById(R.id.temperatureButton);


        //Zuweisung des TemperatureStore in den TemperatureAlarmWorker zur Überwachung
        alarm = new TemperatureAlarmWorker(store, this);
        store.addObserver(alarm.getMyValuesObserver());


        //Plotter++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        dynamicPlot = (XYPlot) findViewById(R.id.plot);
        plotUpdater = new MyPlotUpdater(dynamicPlot);

        // only display whole numbers in domain labels
        dynamicPlot.getGraphWidget().setDomainValueFormat(new DecimalFormat("0"));

        createAndAddNewSeries();

        // hook up the plotUpdater to the data model:
        store.addObserver(plotUpdater);

        // thin out domain tick labels so they dont overlap each other:
        dynamicPlot.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
        dynamicPlot.setDomainStepValue(5);

        dynamicPlot.setRangeStepMode(XYStepMode.INCREMENT_BY_VAL);
        dynamicPlot.setRangeStepValue(2);

        dynamicPlot.setRangeValueFormat(new DecimalFormat("###.##"));

        // uncomment this line to freeze the range boundaries:
        dynamicPlot.setRangeBoundaries(35, 43, BoundaryMode.FIXED);
        dynamicPlot.getLegendWidget().setVisible(false);
        dynamicPlot.getLegendWidget().setSize(new Size(0, SizeLayoutType.ABSOLUTE, 0, SizeLayoutType.ABSOLUTE));

        dynamicPlot.getGraphWidget().setMargins(0, 0, 0, 0);
        dynamicPlot.getGraphWidget().setPadding(0, 0, 0, 0);

        // create a dash effect for domain and range grid lines:
        DashPathEffect dashFx = new DashPathEffect(
                new float[]{PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);
        dynamicPlot.getGraphWidget().getDomainGridLinePaint().setPathEffect(dashFx);
        dynamicPlot.getGraphWidget().getRangeGridLinePaint().setPathEffect(dashFx);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#d6d7d7"));
        dynamicPlot.getGraphWidget().setBackgroundPaint(paint);
        dynamicPlot.setBackgroundPaint(paint);
        dynamicPlot.getGraphWidget().setGridBackgroundPaint(paint);

        //initRandomValues();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        canceled = true;
    }

    private void initRandomValues() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTemperature(generator.generateValue());
                if (!canceled) handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void stopRandomValues() {
        handler.removeCallbacksAndMessages(null);
    }

    public void updateTemperatureButton(View v) {

        if (isGeneratorWorking == false) {
            initRandomValues();
            isGeneratorWorking = true;
        } else if (isGeneratorWorking == true) {
            stopRandomValues();
            isGeneratorWorking = false;
        }
    }

    private void updateTemperature(double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        if (temperatureButton != null)
            temperatureButton.setText(df.format(value).toString() + " C°");
        store.setPatient(patient);
        store.queue(value);
    }

    public void startPatientProfileActivity(View v) {
        Toast.makeText(MainActivity.this, "Öffne Patienten-Editor", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MainActivity.this, PatientActivity.class);
        intent.putExtra("patient", patient);
        startActivityForResult(intent, 1);
    }

    public boolean getCommit() {
        return commit;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                patient = (Patient) data.getSerializableExtra("patient");
                updateNameButton();
            }
        }
    }

    public void updateNameButton() {
        String gender;
        /*
            if (patient.getGenderEnum() == AdministrativeGenderEnum.MALE) {
                gender = "Herr";
            } else if (patient.getGenderEnum() == AdministrativeGenderEnum.FEMALE) {
                gender = "Frau";
            } else {
                gender = "";
            }
            */
        namensButton = (Button) findViewById(R.id.namensButton);
        //namensButton.setText(gender + " " + patient.getName());
        namensButton.setText(patient.getGivenName() + " " + patient.getName());
        storeProgressAndPatient();
    }


    public void createAndAddNewSeries() {
        temperatureSeries = new SimpleXYSeries((List<Double>) plotData, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Temperatur");
        LineAndPointFormatter formatter1 = new LineAndPointFormatter(
                Color.rgb(0, 0, 0), null, null, null);
        formatter1.getLinePaint().setStrokeJoin(Paint.Join.ROUND);
        formatter1.getLinePaint().setStrokeWidth(10);
        dynamicPlot.addSeries(temperatureSeries, formatter1);
    }

    //Plotter++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // redraws a plot whenever an update is received:
    private class MyPlotUpdater implements Observer {
        Plot plot;

        public MyPlotUpdater(Plot plot) {
            this.plot = plot;
        }

        @Override
        public void update(Observable o, Object arg) {
            plotData.clear();
            List progressStore = store.getProgressStore();
            if (progressStore.size() >= 61) {
                plotData.addAll(progressStore.subList(progressStore.size() - 61, progressStore.size() - 0));
            } else {
                plotData.addAll(progressStore);
            }
            dynamicPlot.clear();
            createAndAddNewSeries();
            plot.redraw();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void storeProgressAndPatient() {
        ProgressStore progressStore = new ProgressStore(store.getProgressStore());
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(progressStore);
        prefsEditor.putString("progressStore", json);
        if (patient != null) {
            prefsEditor.putString("patient", gson.toJson(patient));
        }
        prefsEditor.commit();
    }


    @Override
    protected void onPause() {
        super.onPause();
        storeProgressAndPatient();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Gson gson = new Gson();
        String json = mPrefs.getString("progressStore", null);
        if (json == null) {
            Log.e("RESTORE", "The progress values could not be restored.");
        } else {
            ProgressStore progressStore = gson.fromJson(json, ProgressStore.class);
            store.setProgressStore(progressStore.getStore());
            plotUpdater.update(null, null);
        }
        json = null;
        json = mPrefs.getString("patient", null);
        if (json == null) {
            Log.e("RESTORE", "The patient could not be restored.");
        } else {
            patient = gson.fromJson(json, Patient.class);
            store.setPatient(patient);
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                updateNameButton();
            }
        }
    }

}
