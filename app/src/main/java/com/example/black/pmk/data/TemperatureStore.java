package com.example.black.pmk.data;

import android.util.Log;

import com.example.black.pmk.threading.TemperatureCommitListener;
import com.example.black.pmk.threading.TemperatureCommitWorker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Black on 5/9/2016.
 */
public class TemperatureStore {

    private final List<Double> store = new ArrayList<>();
    private final Object lockObject = new Object();
    private boolean isCommitting = false;

    private final int blockSize;

    public  TemperatureStore(int blockSize){
        this.blockSize = blockSize;
    }

    public void queue(double value){
        synchronized (lockObject) {
            store.add(value);
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
        });
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

}
