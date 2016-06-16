package com.example.black.pmk.data;

import java.util.Random;

/**
 * Created by singer on 16.06.16.
 */
public class TemperatureGenerator {

    private final Random random = new Random();
    private static final double MAX_VALUE = 43;
    private static final double MIN_VALUE = 35;


    public double generateValue(){
        double rnd = random.nextDouble();
        return  MIN_VALUE + (rnd * (MAX_VALUE - MIN_VALUE));
    }


}
