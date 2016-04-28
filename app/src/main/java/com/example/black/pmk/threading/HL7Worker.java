package com.example.black.pmk.threading;

import android.os.AsyncTask;

import com.example.black.pmk.Test;

/**
 * Created by Black on 4/28/2016.
 */
public class HL7Worker extends AsyncTask<Void, Void, Test> {



    @Override
    protected Test doInBackground(Void... params) {
        Test t = new Test();
        t.doWork();
        return t;
    }


}
