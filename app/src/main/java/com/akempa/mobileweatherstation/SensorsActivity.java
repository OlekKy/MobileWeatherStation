package com.akempa.mobileweatherstation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SensorsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        final TextView textView1 = (TextView) findViewById(R.id.textView);
        final TextView textView2 = (TextView) findViewById(R.id.textView2);
        final TextView textView3 = (TextView) findViewById(R.id.textView3);

        Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  goToSensorsActivity();
            }
        });

        Button button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            Sensor temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            SensorEventListener sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    float[] values = sensorEvent.values;
                    textView2.setText(String.format("%.3f C", values[0]));
                }
                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {
                }
            };
            sensorManager.registerListener(sensorEventListener, temperatureSensor, SensorManager.SENSOR_DELAY_UI);
            }
        });

        Button button7 = (Button) findViewById(R.id.button7);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                Sensor pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
                SensorEventListener sensorEventListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent sensorEvent) {
                        float[] values = sensorEvent.values;
                        textView1.setText(String.format("%.3f mbar", values[0]));
                    }
                    @Override
                    public void onAccuracyChanged(Sensor sensor, int i) {
                    }
                };
                sensorManager.registerListener(sensorEventListener, pressureSensor, SensorManager.SENSOR_DELAY_UI);
            }
        });

        Button button8 = (Button) findViewById(R.id.button8);
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                Sensor humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
                SensorEventListener sensorEventListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent sensorEvent) {
                        float[] values = sensorEvent.values;
                        textView3.setText(String.format("%.3f proc", values[0]));
                    }
                    @Override
                    public void onAccuracyChanged(Sensor sensor, int i) {
                    }
                };
                sensorManager.registerListener(sensorEventListener, humiditySensor, SensorManager.SENSOR_DELAY_UI);

            }
        });

    }
}
