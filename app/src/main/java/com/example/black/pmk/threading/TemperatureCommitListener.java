package com.example.black.pmk.threading;

/**
 * Created by Black on 5/9/2016.
 */
public interface TemperatureCommitListener {

    void onFailure(Throwable e);
    void onSuccess(Iterable<Double> values);

}
