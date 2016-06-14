package com.example.black.pmk.data;

import android.util.Log;

import com.example.black.pmk.threading.TemperatureCommitListener;
import com.example.black.pmk.threading.TemperatureCommitWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by Black on 5/9/2016.
 */
public class TemperatureStore extends Observable {

    private final List<Double> store = new ArrayList<>();
    private List<Double> progressStore = new ArrayList<>();
    private final int PROGRESS_STORE_LIMIT = 1000;
    private final Object lockObject = new Object();
    private boolean isCommitting = false;
    private  Patient patient;

    private final int blockSize;

    public  TemperatureStore(int blockSize){
        this.blockSize = blockSize;
    }

    public void queue(double value){
        synchronized (lockObject) {
            storeProgress(value);
            checkState();
        }
    }

    private void startCommit(){
        Log.w("TMPSTORE", String.format("Committing %d entries...", store.size()));
        isCommitting = true;
        List<Double> copyList = new ArrayList<Double>();
        copyList.addAll(store);
        TemperatureCommitWorker tcw = new TemperatureCommitWorker(new TemperatureCommitListener() {
            @Override
            public void onFailure(Throwable e) {
                Log.e("TMPSTORE", e.toString());
                synchronized (lockObject){
                    isCommitting = false;
                }
            }

            @Override
            public void onSuccess(Iterable<Double> values) {
                removeCommitedValues(values);
            }
        }, patient);
        tcw.execute(copyList);
    }

    private void removeCommitedValues(Iterable<Double> commitedValues){
        synchronized (lockObject){
            for(Double d : commitedValues) store.remove(d);
            isCommitting = false;
            checkState();
        }
    }

    private void checkState(){
        if(store.size() >= blockSize && !isCommitting)
            startCommit();
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    private void storeProgress(double value) {
        store.add(value);
        Log.w("TemperatureStore store","Stored Values = " + store.toString());
        Log.w("progressStore.size()","Size = " + progressStore.size());
        if (progressStore.size() <= PROGRESS_STORE_LIMIT) {
            progressStore.add(value);
        } else {
            progressStore.remove(0);
            progressStore.add(value);
        }
        Log.w("TemperatureStore ","Stored Values = " + progressStore.toString());
        Log.w("progressStore.size()","Size = " + progressStore.size());
        this.notifyObservers();
    }

    public List getProgressStore(){
        Log.w("TemperatureStore ","Returning progressStore... ");
        return progressStore;
    }

    @Override
    public void notifyObservers() {
        setChanged();
        super.notifyObservers();
    }

}
