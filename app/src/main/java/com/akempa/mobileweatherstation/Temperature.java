package com.akempa.mobileweatherstation;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class Temperature extends Fragment {

    private static final String DATABASE_NAME = "movies_db";
    private TemperaturesDatabase temperaturesDatabase;
    private List<Temperatures> temperaturesList;
    private Thermometer thermometer;
    private String value = "";
    private float v;
    private Button getTemperatureButton;
    private TextView textTab1;
    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private boolean isLastRead;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] values = sensorEvent.values;
            textTab1.setText(String.format("%.3f C", values[0]));
            v = values[0];
            value = Float.toString(v);
            thermometer.setCurrentTemp(v);
            if (isLastRead){
                //Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
                sensorManager.unregisterListener(sensorEventListener, temperatureSensor);
                // insert to database
                runDbInsertThread();
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.temperature_fragment,container,false);

        temperaturesDatabase = Room.databaseBuilder(getContext(), TemperaturesDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();

        thermometer = (Thermometer) view.findViewById(R.id.thermometer);
        textTab1 = (TextView) view.findViewById(R.id.textTab1);
        getTemperatureButton = (Button) view.findViewById(R.id.btn_get_temp);
        getTemperatureButton.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
                switch (motionEvent.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        sensorManager.registerListener(sensorEventListener, temperatureSensor, SensorManager.SENSOR_DELAY_FASTEST);
                        isLastRead = false;

                        break;

                    case MotionEvent.ACTION_UP:
                        isLastRead = true;

                        break;

                }
                return false;
            }
        });

        return view;
    }

    private void runDbInsertThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Temperatures temperatures = new Temperatures();
                temperatures.setTemperatureValue(value);
                Date date = new Date();
                temperatures.setTemperatureTime(date);
                temperaturesDatabase.daoAccess().insertOnlySingleMovie(temperatures);
                // prepared for august
//                for (int i = 0; i < 15 ; i++){
//                    Date date1 = new Date();
//                    date1.setYear(2018-1900);
//                    date1.setMonth(7);
//                    date1.setDate(15+i);
//                    date1.setMinutes(25);
//                    for (int j = 0; j < 18; j++){
//                        Temperatures preparedTemperature = new Temperatures();
//                        Random r = new Random();
//                        float low = 20f;
//                        float high = 26f;
//                        float fakeValue = low + r.nextFloat() * (high-low);
//                        String sFakeValue = Float.toString(fakeValue);
//                        preparedTemperature.setTemperatureValue(sFakeValue);
//                        date1.setHours(5+j);
//                        preparedTemperature.setTemperatureTime(date1);
//                        temperaturesDatabase.daoAccess().insertOnlySingleMovie(preparedTemperature);
//                    }
//                }
//                // prepared for september
//                for (int i = 0; i < 29 ; i++){
//                    Date date1 = new Date();
//                    date1.setYear(2018-1900);
//                    date1.setMonth(8);
//                    date1.setDate(1+i);
//                    date1.setMinutes(25);
//                    for (int j = 0; j < 20; j++){
//                        Temperatures preparedTemperature = new Temperatures();
//                        Random r = new Random();
//                        float low = 16f;
//                        float high = 23f;
//                        float fakeValue = low + r.nextFloat() * (high-low);
//                        String sFakeValue = Float.toString(fakeValue);
//                        preparedTemperature.setTemperatureValue(sFakeValue);
//                        date1.setHours(5+j);
//                        preparedTemperature.setTemperatureTime(date1);
//                        temperaturesDatabase.daoAccess().insertOnlySingleMovie(preparedTemperature);
//                    }
//                }
//                // prepared for october
//                for (int i = 0; i < 22 ; i++){
//                    Date date1 = new Date();
//                    date1.setYear(2018-1900);
//                    date1.setMonth(9);
//                    date1.setDate(1+i);
//                    date1.setMinutes(25);
//                    for (int j = 0; j < 20; j++){
//                        Temperatures preparedTemperature = new Temperatures();
//                        Random r = new Random();
//                        float low = 9f;
//                        float high = 18f;
//                        float fakeValue = low + r.nextFloat() * (high-low);
//                        String sFakeValue = Float.toString(fakeValue);
//                        preparedTemperature.setTemperatureValue(sFakeValue);
//                        date1.setHours(5+j);
//                        preparedTemperature.setTemperatureTime(date1);
//                        temperaturesDatabase.daoAccess().insertOnlySingleMovie(preparedTemperature);
//                    }
//                }

                //temperaturesList = temperaturesDatabase.daoAccess().getAllTemperatures();
//                for (int i=0; i<temperaturesList.size(); i++){
//                    System.out.println(temperaturesList.get(i).getTemperatureId() +" --- " +temperaturesList.get(i).getTemperatureValue() + " --- " + temperaturesList.get(i).getTemperatureTime());
//                    }
            }
        }).start();
    }
}
