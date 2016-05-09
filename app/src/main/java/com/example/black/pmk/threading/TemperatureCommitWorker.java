package com.example.black.pmk.threading;

import android.os.AsyncTask;

import com.example.black.pmk.Test;
import com.example.black.pmk.data.TemperatureStore;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Black on 5/9/2016.
 */
public class TemperatureCommitWorker extends AsyncTask<Iterable<Double>, Void, Iterable<Double>> {

    private final TemperatureCommitListener listener;

    public TemperatureCommitWorker(TemperatureCommitListener listener){
        this.listener = listener;
    }


    @Override
    protected Iterable<Double> doInBackground(Iterable<Double>... params) {
        if(params.length != 1 ) throw new  IllegalArgumentException("Invalid amount of parameters.");
        Test t = new Test(params[0]);
        t.doWork();
        return  params[0];
    }

    @Override
    protected void onPostExecute(Iterable<Double> doubles) {
        super.onPostExecute(doubles);
        listener.onSuccess(doubles);
    }
}
