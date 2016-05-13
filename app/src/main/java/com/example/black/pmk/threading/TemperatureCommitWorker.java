package com.example.black.pmk.threading;

import android.os.AsyncTask;

import com.example.black.pmk.FHIRModule;
import com.example.black.pmk.data.Patient;

/**
 * Created by Black on 5/9/2016.
 */
public class TemperatureCommitWorker extends AsyncTask<Iterable<Double>, Void, Iterable<Double>> {

    private final TemperatureCommitListener listener;
    private Patient patient;

    public TemperatureCommitWorker(TemperatureCommitListener listener, Patient patient){
        this.listener = listener;
        this.patient = patient;
    }


    @Override
    protected Iterable<Double> doInBackground(Iterable<Double>... params) {
        if(params.length != 1 ) throw new  IllegalArgumentException("Invalid amount of parameters.");
        FHIRModule t = new FHIRModule(params[0], patient);
        t.doWork();
        return  params[0];
    }

    @Override
    protected void onPostExecute(Iterable<Double> doubles) {
        super.onPostExecute(doubles);
        listener.onSuccess(doubles);
    }
}
