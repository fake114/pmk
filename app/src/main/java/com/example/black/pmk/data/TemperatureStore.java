package com.example.black.pmk.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.black.pmk.gui.MainActivity;
import com.example.black.pmk.threading.TemperatureCommitListener;
import com.example.black.pmk.threading.TemperatureCommitWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class TemperatureStore extends Observable {

    private final List<Double> store = new ArrayList<>();
    private MainActivity mainActivity;
    private List<Double> progressStore = new ArrayList<>();
    private final int PROGRESS_STORE_LIMIT = 1000;
    private final Object lockObject = new Object();
    private boolean isCommitting = false;
    private  Patient patient;

    private final int blockSize;

    public  TemperatureStore(int blockSize, MainActivity mainActivity){
        this.blockSize = blockSize;
        this.mainActivity = mainActivity;
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
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        if(store.size() >= blockSize && !isCommitting && connected == true)
            startCommit();
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Patient getPatient() {
        return patient;
    }

    private void storeProgress(double value) {
        store.add(value);
        if (progressStore.size() <= PROGRESS_STORE_LIMIT) {
            progressStore.add(value);
        } else {
            progressStore.remove(0);
            progressStore.add(value);
        }
        this.notifyObservers();
    }
    public void setProgressStore(List<Double> progressStore){
        this.progressStore = progressStore;
    }

    public List getProgressStore(){
        return progressStore;
    }

    @Override
    public void notifyObservers() {
        setChanged();
        super.notifyObservers();
    }

}
