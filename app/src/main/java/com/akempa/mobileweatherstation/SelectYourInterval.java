package com.akempa.mobileweatherstation;

import android.app.DatePickerDialog;
import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SelectYourInterval extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Button btnDatePicker, btnTimePicker;
    private EditText txtDate, txtTime;
    private int mYear, mMonth, mDay;
    private int endYear, endMonth, endDay;
    private static final String DATABASE_NAME = "temperature_db";
    private static final String AIR_PRESSURE_DATABASE = "airpressure_db";
    private static final String HUMIDITY_DATABASE = "humidity_db";
    private static final String DATE_FORMAT = "MM-yyyy";

    private Button btnUpdate;
    private String measurementType;
    private DateFormat dateFormat;
    private Calendar c;
    private Date dateStarting;
    private Date dateEnding;

    String actualDateString;
    private BarChart chart;
    private BarData data;
    String[] choose = { "Temperatura", "Ciśnienie", "Wilgotność", "Wszystkie"};

    private TemperaturesDatabase temperaturesDatabase;
    private AirPressuresDatabase airPressuresDatabase;
    private HumiditiesDatabase humiditiesDatabase;

    ArrayList<BarEntry> entries = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_your_internal_fragment,container,false);

        btnDatePicker = view.findViewById(R.id.btn_date);
        btnTimePicker= view.findViewById(R.id.btn_time);
        txtDate = view.findViewById(R.id.in_date);
        txtTime = view.findViewById(R.id.in_time);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);

        measurementType = "Temperatura";
        temperaturesDatabase = Room.databaseBuilder(getContext(), TemperaturesDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
        airPressuresDatabase = Room.databaseBuilder(getContext(), AirPressuresDatabase.class, AIR_PRESSURE_DATABASE).fallbackToDestructiveMigration().build();
        humiditiesDatabase = Room.databaseBuilder(getContext(), HumiditiesDatabase.class, HUMIDITY_DATABASE).fallbackToDestructiveMigration().build();

        chart = view.findViewById(R.id.bar_chart);

        Spinner spin = view.findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,choose);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);

        dateFormat = new SimpleDateFormat(DATE_FORMAT);
        c = Calendar.getInstance();
        c.setTime(new Date());

        dateStarting = c.getTime();
        dateStarting.setMinutes(0);
        dateStarting.setSeconds(0);
        dateEnding = c.getTime();
        dateEnding.setMinutes(59);
        dateEnding.setSeconds(59);

        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGraphs(measurementType);
            }
        });
        setGraphs(measurementType);

        return view;
    }

    public void setGraphs(final String type){
        new Thread(new Runnable() {
            @Override
            public void run() {

                BarDataSet dataSet;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        entries.clear();
                        chart.notifyDataSetChanged();
                        chart.invalidate();
                        chart.clear();
                    }
                });

                Date dateStarting = c.getTime();
                dateStarting.setYear(mYear);
                dateStarting.setMonth(mMonth);
                dateStarting.setDate(mDay);
                Date dateEnding = c.getTime();
                dateEnding.setYear(endYear);
                dateEnding.setMonth(endMonth);
                dateEnding.setDate(endDay);
                float period = (((dateEnding.getTime() - dateStarting.getTime())/ (8640 * 10^7)) +1 ) / 1000;
                System.out.println(""+period);

                for (int i = 1 ; i < period+1 ; i++){

                    float average = 0;
                    if (type.equals("Temperatura")) {
                        average = getAverageTemperature(i);
                    }
                    if (type.equals("Ciśnienie")) {
                        average = getAverageAirPressure(i);
                    }
                    if (type.equals("Wilgotność")){
                        average = getAverageHumidity(i);
                    }

                    entries.add(new BarEntry(i, average));
                }

                dataSet = new BarDataSet(entries, "Pomiary");
                data = new BarData(dataSet);
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                chart.setData(data);
                Description description = new Description();
                description.setText(" ");
                chart.setDescription(description);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chart.notifyDataSetChanged();
                        chart.invalidate();
                    }
                });

            }
        }) .start();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            System.out.println("REFRESHED !  !");
            setGraphs(measurementType);
            // transactFragment(this,true);
        }
    }

    private float getAverageTemperature(int i){
        float averageTemperature = 0;
        float sumOfAllValues = 0;
        List<Temperatures> tpList = null;
        temperaturesDatabase.beginTransaction();
        try {
            Date dataStart = c.getTime();
            dataStart.setDate(mDay+i);
            dataStart.setMonth(mMonth);
            dataStart.setHours(1);
            dataStart.setMinutes(0);
            dataStart.setSeconds(0);
            Date dataEnd = c.getTime();
            dataEnd.setMonth(mMonth);
            dataEnd.setDate(mDay+i);
            dataEnd.setHours(23);
            dataEnd.setMinutes(59);
            dataEnd.setSeconds(59);

            tpList = temperaturesDatabase.daoAccess()
                    .fetchTemperaturesBetweenDate(dataStart, dataEnd);
            temperaturesDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            System.out.println("Exception Database TEMPERATURE");
        } finally {
            temperaturesDatabase.endTransaction();
        }

        int size = tpList.size();
        if (size > 0){
            for (int j = 0; j < size; j++){
                String valuesString = tpList.get(j).getTemperatureValue();
                float tempValue = Float.parseFloat(valuesString);
                sumOfAllValues = sumOfAllValues + tempValue;
            }
            averageTemperature = sumOfAllValues/size;
        }
        return averageTemperature;
    }

    private float getAverageTemperature(Date fromm, Date too){
        float averageTemperature = 0;
        float sumOfAllValues = 0;
        List<Temperatures> tpList = null;
        temperaturesDatabase.beginTransaction();
        try {
            tpList = temperaturesDatabase.daoAccess()
                    .fetchTemperaturesBetweenDate(fromm, too);
            temperaturesDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            System.out.println("Exception Database TEMPERATURE");
        } finally {
            temperaturesDatabase.endTransaction();
        }
        int size = tpList.size();
        if (size > 0){
            for (int j = 0; j < size; j++){
                String valuesString = tpList.get(j).getTemperatureValue();
                float tempValue = Float.parseFloat(valuesString);
                sumOfAllValues = sumOfAllValues + tempValue;
            }
            averageTemperature = sumOfAllValues/size;
        }
        return averageTemperature;
    }

    private float getAverageAirPressure(int i){
        float averageAirPressure = 0;
        float sumOfAllValues = 0;
        List<AirPressures> airPressuresList = null;
        airPressuresDatabase.beginTransaction();
        try{
            Date dataStart = c.getTime();
            dataStart.setDate(mDay+i);
            dataStart.setMonth(mMonth);
            dataStart.setHours(1);
            dataStart.setMinutes(0);
            dataStart.setSeconds(0);
            Date dataEnd = c.getTime();
            dataEnd.setMonth(mMonth);
            dataEnd.setDate(mDay+i);
            dataEnd.setHours(23);
            dataEnd.setMinutes(59);
            dataEnd.setSeconds(59);
            airPressuresList = airPressuresDatabase.daoAccess().fetchAirPressuresBetweenDate(dataStart, dataEnd);
            airPressuresDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            System.out.println("Exception Database AIR PRESSURE");
        } finally {
            airPressuresDatabase.endTransaction();
        }
        int size = airPressuresList.size();
        if (size > 0){
            for (int j = 0; j < size; j++){
                String valuesString = airPressuresList.get(j).getAirPressureValue();
                float airPressureValue = Float.parseFloat(valuesString);
                sumOfAllValues = sumOfAllValues + airPressureValue;
            }
            averageAirPressure = sumOfAllValues/size;
        }
        return averageAirPressure;
    }

    private float getAverageHumidity(int i){
        float averageHumidity = 0;
        float sumOfAllValues = 0;
        List<Humidities> humiditiesList = null;
        humiditiesDatabase.beginTransaction();
        try{
            Date dataStart = c.getTime();
            dataStart.setDate(mDay+i);
            dataStart.setMonth(mMonth);
            dataStart.setHours(1);
            dataStart.setMinutes(0);
            dataStart.setSeconds(0);
            Date dataEnd = c.getTime();
            dataEnd.setMonth(mMonth);
            dataEnd.setDate(mDay+i);
            dataEnd.setHours(23);
            dataEnd.setMinutes(59);
            dataEnd.setSeconds(59);
            humiditiesList = humiditiesDatabase.daoAccess().fetchHumiditiesBetweenDate(dataStart,dataEnd);
            humiditiesDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            System.out.println("Exception Database HUMIDITY");
        } finally {
            humiditiesDatabase.endTransaction();
        }
        int size = humiditiesList.size();
        if (size > 0){
            for (int j = 0; j < size; j++){
                String valuesString = humiditiesList.get(j).getHumidityValue();
                float humidityValue = Float.parseFloat(valuesString);
                sumOfAllValues = sumOfAllValues + humidityValue;
            }
            averageHumidity = sumOfAllValues/size;
        }
        return averageHumidity;
    }

    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Toast.makeText(getContext(),choose[position] , Toast.LENGTH_LONG).show();
        measurementType = choose[position].toString();
        // setGraphs(actualDay, actualDay, measurementType);
        System.out.println(measurementType);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onClick(View v) {
        if (v == btnDatePicker) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog;
            datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            mYear = view.getYear();
                            mMonth = view.getMonth()+1;
                            mDay = view.getDayOfMonth();
                            System.out.println("Y: " + mYear + " M: " + mMonth + " D: " + mDay);
                        }
                    }, mYear, mMonth, mDay);

            datePickerDialog.show();

        }
        if (v == btnTimePicker) {

            final Calendar c = Calendar.getInstance();
            endYear = c.get(Calendar.YEAR);
            endMonth = c.get(Calendar.MONTH);
            endDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog;
            datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            txtTime.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            endDay = view.getDayOfMonth();
                            endMonth = view.getMonth()+1;
                            endYear = view.getYear();
                            System.out.println("Y: " + endYear + " M: " + endMonth + " D: " + endDay);
                        }
                    }, endYear, endMonth, endDay);

            datePickerDialog.show();
        }
    }
}
