package com.example.black.pmk.data;

import java.io.Serializable;
import java.util.List;

public class ProgressStore implements Serializable {

    private List<Double> store;

    public ProgressStore(List store) {
        setStore(store);
    }

    public List<Double> getStore() {
        return store;
    }

    public void setStore(List<Double> store) {
        this.store = store;
    }
}
