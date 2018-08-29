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

public class Humidity extends Fragment {

    private static final String DATABASE_NAME = "humidity_db";
    private HumiditiesDatabase humiditiesDatabase;
    private List<Humidities> humiditiesList;
    // private Thermometer thermometer;
    private String value = "";
    private float v;
    private Button getHumidityButton;
    private TextView textTab1;
    private SensorManager sensorManager;
    private Sensor humiditySensor;
    private boolean isLastRead;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] values = sensorEvent.values;
            textTab1.setText(String.format("%.3f proc", values[0]));
            v = values[0];
            value = Float.toString(v);
            if (isLastRead){
                Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
                sensorManager.unregisterListener(sensorEventListener, humiditySensor);
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
        View view = inflater.inflate(R.layout.humidity_fragment,container,false);
        humiditiesDatabase = Room.databaseBuilder(getContext(), HumiditiesDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();

        textTab1 = (TextView) view.findViewById(R.id.textTab1);
        getHumidityButton = (Button) view.findViewById(R.id.btn_get_humidity);
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
                humiditiesList = humiditiesDatabase.daoAccess().getAllHumidities();
                for (int i = 0; i < humiditiesList.size(); i++) {
                    System.out.println(humiditiesList.get(i).getHumidityId() + " -- " + humiditiesList.get(i).getHumidityTime() + " -- " + humiditiesList.get(i).getHumidityValue());
                }
            }
        }).start();
    }
}