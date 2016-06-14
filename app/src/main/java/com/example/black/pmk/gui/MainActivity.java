package com.example.black.pmk.gui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidplot.Plot;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.example.black.pmk.data.Patient;
import com.example.black.pmk.data.TemperatureStore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import  com.example.black.pmk.R;

import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;

public class MainActivity extends AppCompatActivity {

    private Button temperatureButton, namensButton;
    private final TemperatureStore store = new TemperatureStore(3);
    private boolean commit = false;
    private Patient patient;

    private XYPlot dynamicPlot;
    private MyPlotUpdater plotUpdater;
    private List plotData = new ArrayList();
    private XYSeries temperatureSeries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temperatureButton = (Button) findViewById(R.id.temperatureButton);

        // TODO persistenten/gespeicherten Patienten aus bestehender File zum Programmstart auslesen
        patient = new Patient();


        //Plotter++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        dynamicPlot = (XYPlot) findViewById(R.id.plot);
        plotUpdater = new MyPlotUpdater(dynamicPlot);

        // only display whole numbers in domain labels
        dynamicPlot.getGraphWidget().setDomainValueFormat(new DecimalFormat("0"));

        //TODO Initialisiere plotData zum Programmstart aus bestehender File

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

        // create a dash effect for domain and range grid lines:
        DashPathEffect dashFx = new DashPathEffect(
                new float[] {PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);
        dynamicPlot.getGraphWidget().getDomainGridLinePaint().setPathEffect(dashFx);
        dynamicPlot.getGraphWidget().getRangeGridLinePaint().setPathEffect(dashFx);
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
                plotData.addAll(progressStore.subList(progressStore.size()-61, progressStore.size()-0));
            } else {
                plotData.addAll(progressStore);
            }
            //TODO Remove Logs in MainActivity MyPlotUpdater
            Log.w("MainActivity ","plotData = " + plotData);
            dynamicPlot.clear();
            createAndAddNewSeries();
            plot.redraw();
            Log.w("MainActivity ","Update method called...");
        }
    }

}
