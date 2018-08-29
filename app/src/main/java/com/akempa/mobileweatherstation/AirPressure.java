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

public class AirPressure  extends Fragment {

    private static final String DATABASE_NAME = "airpressure_db";
    private AirPressuresDatabase airPressuresDatabase;
    private List<AirPressures> airPressuresList;
    // private Thermometer thermometer;
    private String value = "";
    private float v;
    private Button getAirPressureButton;
    private TextView textTab1;
    private SensorManager sensorManager;
    private Sensor airPressureSensor;
    private boolean isLastRead;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] values = sensorEvent.values;
            textTab1.setText(String.format("%.3f hPa", values[0]));
            v = values[0];
            value = Float.toString(v);
            if (isLastRead){
               // Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
                sensorManager.unregisterListener(sensorEventListener, airPressureSensor);
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
        View view = inflater.inflate(R.layout.air_pressure_fragment,container,false);
        airPressuresDatabase = Room.databaseBuilder(getContext(), AirPressuresDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();

        textTab1 = (TextView) view.findViewById(R.id.textTab1);
        getAirPressureButton = (Button) view.findViewById(R.id.btn_get_air_press);
        getAirPressureButton.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                airPressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
                switch (motionEvent.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        sensorManager.registerListener(sensorEventListener, airPressureSensor, SensorManager.SENSOR_DELAY_FASTEST);
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
                AirPressures airPressuresToDel = new AirPressures();
                airPressuresToDel.setAirPressureId(34);
                airPressuresDatabase.daoAccess().deleteAirPressure(airPressuresToDel);

                AirPressures airPressures = new AirPressures();
                airPressures.setAirPressureValue(value);
                Date date = new Date();
                airPressures.setAirPressureTime(date);
                airPressuresDatabase.daoAccess().insertAirPressure(airPressures);
                airPressuresList = airPressuresDatabase.daoAccess().getAllAirPressures();
                for (int i = 0; i < airPressuresList.size(); i++){
                    System.out.println(airPressuresList.get(i).getAirPressureId() + " -- " + airPressuresList.get(i).getAirPressureTime() + " -- " + airPressuresList.get(i).getAirPressureValue());
                    }
            }
        }).start();
    }
}