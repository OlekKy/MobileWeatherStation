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

public class Temperature extends Fragment {

    private static final String DATABASE_NAME = "temperature_db";
    private TemperaturesDatabase temperaturesDatabase;
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
                sensorManager.unregisterListener(sensorEventListener, temperatureSensor);
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

        thermometer = view.findViewById(R.id.thermometer);
        textTab1 = view.findViewById(R.id.textTab1);
        getTemperatureButton = view.findViewById(R.id.btn_get_temp);
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
                temperaturesDatabase.daoAccess().insertOnlySingleTemperature(temperatures);
            }
        }).start();
    }
}
