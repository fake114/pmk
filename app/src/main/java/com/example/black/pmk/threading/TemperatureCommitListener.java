package com.example.black.pmk.threading;

public interface TemperatureCommitListener {

    void onFailure(Throwable e);
    void onSuccess(Iterable<Double> values);

}
