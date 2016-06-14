package com.example.black.pmk.threading;

import android.os.AsyncTask;

import com.example.black.pmk.data.TemperatureStore;

/**
 * Created by Derek on 14.06.2016.
 */
public class TemperatureAlarmWorker extends AsyncTask<Iterable<Double>, Void, Iterable<Double>> {

    private TemperatureStore store;

    public TemperatureAlarmWorker (TemperatureStore store) {
        this.store = store;
    }


    @Override
    protected Iterable<Double> doInBackground(Iterable<Double>... params) {
        if(params.length != 1 ) throw new  IllegalArgumentException("Invalid amount of parameters.");
        //TODO Implement Alarm Algorithm

        return  params[0];
    }
}
