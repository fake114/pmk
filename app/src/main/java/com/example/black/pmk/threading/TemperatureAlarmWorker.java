package com.example.black.pmk.threading;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.example.black.pmk.data.TemperatureStore;
import com.example.black.pmk.gui.MainActivity;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Derek on 14.06.2016.
 */
public class TemperatureAlarmWorker extends AsyncTask<Iterable<Double>, Void, Iterable<Double>> {

    private TemperatureStore store;
    private MainActivity mainActivity;
    private MyValuesObserver myValuesObserver;

    public TemperatureAlarmWorker (TemperatureStore store, MainActivity mainActivity) {
        this.store = store;
        this.mainActivity = mainActivity;
        this.myValuesObserver = new MyValuesObserver(store);
    }

    public MyValuesObserver getMyValuesObserver() {
        return myValuesObserver;
    }


    @Override
    protected Iterable<Double> doInBackground(Iterable<Double>... params) {
        if(params.length != 1 ) throw new  IllegalArgumentException("Invalid amount of parameters.");
        //TODO Implement Alarm Algorithm

        return  params[0];
    }


    private class MyValuesObserver implements Observer {

        private TemperatureStore temperatureStore;

        public MyValuesObserver(TemperatureStore store) {
            this.temperatureStore = store;
        }

        @Override
        public void update(Observable o, Object arg) {
            List values = temperatureStore.getProgressStore();


            if (values.size() >= 6) {
                if (checkHardPositiveTrend(values, values.size() - 5)) {
                    createAlert("ALARM!!! Es existiert ein positiver Trend!");
                    Log.w("TemperatureAlarmWorker ","Positive Trend checked");
                    return;
                } else if (checkHardNegativeTrend(values, values.size() - 5)) {
                        Log.w("TemperatureAlarmWorker ","Checking for negative Trend");
                        createAlert("ALARM!!! Es existiert ein negativer Trend!");
                        Log.w("TemperatureAlarmWorker ","Negative Trend checked");
                        return;
                }

            }

            if (values.size() >= 2) {
                //Check, if last two values are above normal (37 C°)
                if ((((Double) values.get(values.size() - 1) <= 36) && ((Double) values.get(values.size() - 2) <= 36))) {
                    createAlert("ALARM!!! Die aktuellsten Werte haben die untere Grenze überschritten!");
                    return;
                }
                if ((((Double) values.get(values.size() - 1) >= 38) && ((Double) values.get(values.size() - 2) >= 38))) {
                    createAlert("ALARM!!! Die aktuellsten Werte haben die obere Grenze überschritten!");
                    return;
                }
            }
            Log.w("TemperatureAlarmWorker ","Update method called...");
        }

        private boolean checkHardPositiveTrend(List values, int pointer) {
            if(pointer == values.size()-1) {
                if ( (Double) values.get(pointer) > (Double) values.get(pointer-1) ) {
                    return true;
                } else {
                    return false;
                }
            }
            if(pointer < values.size()-1) {
                if ( (Double) values.get(pointer) > (Double) values.get(pointer-1) ) {
                    if (checkHardPositiveTrend(values, pointer+1) == false) {
                        return false;
                    }
                    return true;
                }
            }
            return false;
        }

        private boolean checkHardNegativeTrend(List values, int pointer) {
            if(pointer == values.size()-1) {
                if ( (Double) values.get(pointer) < (Double) values.get(pointer-1) ) {
                    return true;
                } else {
                    return false;
                }
            }
            if(pointer < values.size()-1) {
                if ( (Double) values.get(pointer) < (Double) values.get(pointer-1) ) {
                    if (checkHardNegativeTrend(values, pointer+1) == false) {
                        return false;
                    }
                    return true;
                }
            }
            return false;
        }

        private boolean checkSoftPositiveTrend(){
            return false;
        }

        private void createAlert(String dialog) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mainActivity);
            alertDialogBuilder.setMessage(dialog);
            alertDialogBuilder.show();
        }
    }

}
