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
import android.widget.Toast;

import java.util.Date;
import java.util.List;
import java.util.Random;

import de.nitri.gauge.Gauge;
import pl.pawelkleczkowski.customgauge.CustomGauge;

public class Humidity extends Fragment {

    private static final String DATABASE_NAME = "humidity_db";
    private HumiditiesDatabase humiditiesDatabase;
    private List<Humidities> humiditiesList;
    private String value = "";
    private float v;
    private Button getHumidityButton;
    private TextView textTab1;
    private SensorManager sensorManager;
    private Sensor humiditySensor;
    private boolean isLastRead;
    private Gauge gauge1;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] values = sensorEvent.values;
            textTab1.setText(String.format("%.3f proc", values[0]));
            v = values[0];
            gauge1.moveToValue(v);
            value = Float.toString(v);
            if (isLastRead){
                sensorManager.unregisterListener(sensorEventListener, humiditySensor);
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
        View view = inflater.inflate(R.layout.humidity_fragment,container,false);
        humiditiesDatabase = Room.databaseBuilder(getContext(), HumiditiesDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();

        gauge1 = view.findViewById(R.id.gauge2);

        textTab1 = view.findViewById(R.id.textTab1);
        getHumidityButton = view.findViewById(R.id.btn_get_humidity);
        getHumidityButton.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
                switch (motionEvent.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        sensorManager.registerListener(sensorEventListener, humiditySensor, SensorManager.SENSOR_DELAY_FASTEST);
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
                Humidities humidities = new Humidities();
                humidities.setHumidityValue(value);
                Date date = new Date();
                humidities.setHumidityTime(date);
                humiditiesDatabase.daoAccess().insertHumidity(humidities);

                // prepared for august
//                for (int i = 0; i < 15 ; i++){
//                    Date date1 = new Date();
//                    date1.setYear(2018-1900);
//                    date1.setMonth(7);
//                    date1.setDate(15+i);
//                    date1.setMinutes(25);
//                    for (int j = 0; j < 18; j++){
//                        Humidities preparedTemperature = new Humidities();
//                        Random r = new Random();
//                        float low = 55f;
//                        float high = 75f;
//                        float fakeValue = low + r.nextFloat() * (high-low);
//                        String sFakeValue = Float.toString(fakeValue);
//                        preparedTemperature.setHumidityValue(sFakeValue);
//                        date1.setHours(5+j);
//                        preparedTemperature.setHumidityTime(date1);
//                        humiditiesDatabase.daoAccess().insertHumidity(preparedTemperature);
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
//                        Humidities preparedTemperature = new Humidities();
//                        Random r = new Random();
//                        float low = 50f;
//                        float high = 80f;
//                        float fakeValue = low + r.nextFloat() * (high-low);
//                        String sFakeValue = Float.toString(fakeValue);
//                        preparedTemperature.setHumidityValue(sFakeValue);
//                        date1.setHours(5+j);
//                        preparedTemperature.setHumidityTime(date1);
//                        humiditiesDatabase.daoAccess().insertHumidity(preparedTemperature);
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
//                        Humidities preparedTemperature = new Humidities();
//                        Random r = new Random();
//                        float low = 50f;
//                        float high = 70f;
//                        float fakeValue = low + r.nextFloat() * (high-low);
//                        String sFakeValue = Float.toString(fakeValue);
//                        preparedTemperature.setHumidityValue(sFakeValue);
//                        date1.setHours(5+j);
//                        preparedTemperature.setHumidityTime(date1);
//                        humiditiesDatabase.daoAccess().insertHumidity(preparedTemperature);
//                    }
//                }

//                humiditiesList = humiditiesDatabase.daoAccess().getAllHumidities();
//                for (int i = 0; i < humiditiesList.size(); i++) {
//                    System.out.println(humiditiesList.get(i).getHumidityId() + " -- " + humiditiesList.get(i).getHumidityTime() + " -- " + humiditiesList.get(i).getHumidityValue());
//                }
            }
        }).start();
    }
}