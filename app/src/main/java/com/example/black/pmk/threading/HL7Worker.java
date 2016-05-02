package com.example.black.pmk.threading;

import android.os.AsyncTask;

import com.example.black.pmk.MainActivity;
import com.example.black.pmk.Test;

/**
 * Created by Black on 4/28/2016.
 */
public class HL7Worker extends AsyncTask<Void, Void, Test> {
    Test t;
    MainActivity main;
public HL7Worker(MainActivity main){
    this.main = main;
    t = new Test(main);
}

    @Override
    protected Test doInBackground(Void... params) {
        t.doWork();
        return t;
    }


}
