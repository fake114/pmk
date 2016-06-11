package com.example.black.pmk.gui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    SampleDynamicXYDatasource data;
    private Thread myThread;

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

        // getInstance and position datasets:
        data = new SampleDynamicXYDatasource();
        SampleDynamicSeries sine1Series = new SampleDynamicSeries(data, 0, "Temperatur");

        LineAndPointFormatter formatter1 = new LineAndPointFormatter(
                Color.rgb(0, 0, 0), null, null, null);
        formatter1.getLinePaint().setStrokeJoin(Paint.Join.ROUND);
        formatter1.getLinePaint().setStrokeWidth(10);
        dynamicPlot.addSeries(sine1Series,
                formatter1);

        // hook up the plotUpdater to the data model:
        data.addObserver(plotUpdater);

        // thin out domain tick labels so they dont overlap each other:
        dynamicPlot.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
        dynamicPlot.setDomainStepValue(5);

        dynamicPlot.setRangeStepMode(XYStepMode.INCREMENT_BY_VAL);
        dynamicPlot.setRangeStepValue(10);

        dynamicPlot.setRangeValueFormat(new DecimalFormat("###.#"));

        // uncomment this line to freeze the range boundaries:
        dynamicPlot.setRangeBoundaries(30, 45, BoundaryMode.FIXED);

        // create a dash effect for domain and range grid lines:
        DashPathEffect dashFx = new DashPathEffect(
                new float[] {PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);
        dynamicPlot.getGraphWidget().getDomainGridLinePaint().setPathEffect(dashFx);
        dynamicPlot.getGraphWidget().getRangeGridLinePaint().setPathEffect(dashFx);




        //plot.setPlotPadding(20,20,20,20);
        //dynamicPlot.getLegendWidget().setVisible(false);
        //XYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Temperature", 1,2,3,4,5,6,7,8,9,10);
        //XYSeries s2 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Temperature 2", 2,1,2,1,3,4,6,5,8,9);
        //dynamicPlot.addSeries(s1, new LineAndPointFormatter(Color.GREEN, Color.GREEN, null, null));
        //dynamicPlot.addSeries(s2, new LineAndPointFormatter(Color.RED, Color.CYAN, null, null));
    }

    @Override
    public void onResume() {
        // kick off the data generating thread:
        myThread = new Thread(data);
        myThread.start();
        super.onResume();
    }

    @Override
    public void onPause() {
        data.stopThread();
        super.onPause();
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




    //Plotter++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    // redraws a plot whenever an update is received:
    private class MyPlotUpdater implements Observer {
        Plot plot;

        public MyPlotUpdater(Plot plot) {
            this.plot = plot;
        }

        @Override
        public void update(Observable o, Object arg) {
            plot.redraw();
        }
    }

    class SampleDynamicXYDatasource implements Runnable {

        // encapsulates management of the observers watching this datasource for update events:
        class MyObservable extends Observable {
            @Override
            public void notifyObservers() {
                setChanged();
                super.notifyObservers();
            }
        }

        private static final double FREQUENCY = 5; // larger is lower frequency
        private static final int MAX_AMP_SEED = 45;
        private static final int MIN_AMP_SEED = 33;
        private static final int AMP_STEP = 1;
        public static final int SINE1 = 0;
        public static final int SINE2 = 1;
        private static final int SAMPLE_SIZE = 30;
        private int phase = 0;
        private int sinAmp = 1;
        private MyObservable notifier;
        private boolean keepRunning = false;

        {
            notifier = new MyObservable();
        }

        public void stopThread() {
            keepRunning = false;
        }

        //@Override
        public void run() {
            try {
                keepRunning = true;
                boolean isRising = true;
                while (keepRunning) {

                    Thread.sleep(100); // decrease or remove to speed up the refresh rate.
                    phase++;
                    if (sinAmp >= MAX_AMP_SEED) {
                        isRising = false;
                    } else if (sinAmp <= MIN_AMP_SEED) {
                        isRising = true;
                    }

                    if (isRising) {
                        sinAmp += AMP_STEP;
                    } else {
                        sinAmp -= AMP_STEP;
                    }
                    notifier.notifyObservers();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public int getItemCount(int series) {
            return SAMPLE_SIZE;
        }

        public Number getX(int series, int index) {
            if (index >= SAMPLE_SIZE) {
                throw new IllegalArgumentException();
            }
            return index;
        }

        public Number getY(int series, int index) {
            if (index >= SAMPLE_SIZE) {
                throw new IllegalArgumentException();
            }
            double angle = (index + (phase))/FREQUENCY;
            double amp = sinAmp * Math.sin(angle);
            switch (series) {
                case SINE1:
                    return amp;
                case SINE2:
                    return -amp;
                default:
                    throw new IllegalArgumentException();
            }
        }

        public void addObserver(Observer observer) {
            notifier.addObserver(observer);
        }

        public void removeObserver(Observer observer) {
            notifier.deleteObserver(observer);
        }

    }

    class SampleDynamicSeries implements XYSeries {
        private SampleDynamicXYDatasource datasource;
        private int seriesIndex;
        private String title;

        public SampleDynamicSeries(SampleDynamicXYDatasource datasource, int seriesIndex, String title) {
            this.datasource = datasource;
            this.seriesIndex = seriesIndex;
            this.title = title;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public int size() {
            return datasource.getItemCount(seriesIndex);
        }

        @Override
        public Number getX(int index) {
            return datasource.getX(seriesIndex, index);
        }

        @Override
        public Number getY(int index) {
            return datasource.getY(seriesIndex, index);
        }
    }

    //Plotter END +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

}
