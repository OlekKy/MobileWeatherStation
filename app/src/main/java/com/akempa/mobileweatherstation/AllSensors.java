package com.akempa.mobileweatherstation;

import android.annotation.SuppressLint;
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

public class AllSensors extends Fragment {

    private Button getAllSensorsButton;
    private TextView temperatureValue;
    private TextView pressureValue;
    private TextView humidityValue;
    private SensorManager sensorManagerTemperature;
    private SensorManager sensorManagerAirPressure;
    private SensorManager sensorManagerHumidity;
    private Sensor temperatureSensor;
    private Sensor airPressureSensor;
    private Sensor humiditySensor;

    private boolean isLastRead;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor sensor = sensorEvent.sensor;
            if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                float[] values = sensorEvent.values;
                temperatureValue.setText(String.format("%.3f C", values[0]));
            }else if (sensor.getType() == Sensor.TYPE_PRESSURE) {
                float[] values = sensorEvent.values;
                pressureValue.setText(String.format("%.3f hPa", values[0]));
            } else {
                float[] values = sensorEvent.values;
                humidityValue.setText(String.format("%.3f proc", values[0]));
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
        View view = inflater.inflate(R.layout.all_sensors_fragment,container,false);

        temperatureValue = view.findViewById(R.id.tempTextTab);
        pressureValue= view.findViewById(R.id.airPressureTextTab);
        humidityValue = view.findViewById(R.id.humidityTextTab);
        getAllSensorsButton = view.findViewById(R.id.btn_get_all_sensors);

        getAllSensorsButton.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                sensorManagerTemperature = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                sensorManagerAirPressure = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                sensorManagerHumidity = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

                temperatureSensor = sensorManagerTemperature.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
                airPressureSensor = sensorManagerTemperature.getDefaultSensor(Sensor.TYPE_PRESSURE);
                humiditySensor = sensorManagerTemperature.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

                switch (motionEvent.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        sensorManagerTemperature.registerListener(sensorEventListener, temperatureSensor, SensorManager.SENSOR_DELAY_FASTEST);
                        sensorManagerAirPressure.registerListener(sensorEventListener, airPressureSensor, SensorManager.SENSOR_DELAY_FASTEST);
                        sensorManagerHumidity.registerListener(sensorEventListener, humiditySensor, SensorManager.SENSOR_DELAY_FASTEST);

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
}
