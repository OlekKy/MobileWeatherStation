package com.akempa.mobileweatherstation;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
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

public class DayInterval extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String DATABASE_NAME = "movies_db";
    private static final String AIR_PRESSURE_DATABASE = "airpressure_db";
    private static final String HUMIDITY_DATABASE = "humidity_db";
    private static final String DATE_FORMAT = "dd-MM-yyyy";

    private Button btnUpdate;
    private Button previousDay;
    private Button nextDay;
    private TextView actualDate;
    private String measurementType;
    private DateFormat dateFormat;
    private Calendar c;

    int actualDay;
    String actualDateString;
    private BarChart chart;
    private BarData data;
    String[] choose = { "Temperatura", "Ciśnienie", "Wilgotność", "Wszystkie"};

    private List<Temperatures> temperaturesList;

    private TemperaturesDatabase temperaturesDatabase;
    private AirPressuresDatabase airPressuresDatabase;
    private HumiditiesDatabase humiditiesDatabase;

    ArrayList<BarEntry> entries = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.day_fragment,container,false);
        measurementType = "Temperatura";
        temperaturesDatabase = Room.databaseBuilder(getContext(), TemperaturesDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
        airPressuresDatabase = Room.databaseBuilder(getContext(), AirPressuresDatabase.class, AIR_PRESSURE_DATABASE).fallbackToDestructiveMigration().build();
        humiditiesDatabase = Room.databaseBuilder(getContext(), HumiditiesDatabase.class, HUMIDITY_DATABASE).fallbackToDestructiveMigration().build();

        chart = (BarChart) view.findViewById(R.id.bar_chart);

        Spinner spin = (Spinner) view.findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,choose);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);

        actualDate = (TextView) view.findViewById(R.id.actualDate);

        dateFormat = new SimpleDateFormat(DATE_FORMAT);
        c = Calendar.getInstance();
        c.setTime(new Date());
        actualDay = c.getTime().getDate();
        actualDate.setText(dateFormat.format(c.getTime()));

        previousDay = (Button) view.findViewById(R.id.btnPrevious);
        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c.add(Calendar.DATE, -1);
                actualDay = c.getTime().getDate();
                actualDateString = dateFormat.format(c.getTime());
                actualDate.setText(actualDateString);
                setGraphs(actualDay,actualDay, measurementType);
            }
        });

        nextDay = (Button) view.findViewById(R.id.btnNext);
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c.add(Calendar.DATE, 1);
                actualDay = c.getTime().getDate();
                actualDateString = dateFormat.format(c.getTime());
                actualDate.setText(actualDateString);
                setGraphs(actualDay,actualDay, measurementType);
            }
        });

        btnUpdate = (Button) view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGraphs(actualDay,actualDay, measurementType);
            }
        });

        setGraphs(actualDay,actualDay, measurementType);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //temperaturesList = temperaturesDatabase.daoAccess().getAllTemperatures();
//                BarChart chart = (BarChart) view.findViewById(R.id.bar_chart);
//                Date date = new Date();
//
//
//                int day = date.getDate();
//                //int  sdate = cal.getTime().getDate();
//
//                for (int i = 0 ; i < 24 ; i++){
//                    float sumOfAllValues = 0;
//                    float average = 0;
//                    String from = "Wed Aug 15 "+i+":00:00 CEST 2018";
//                    String to =   "Wed Aug 15 "+i+":59:59 CEST 2018";
//                    Date fromm = new Date();
//                    fromm.setDate(15);
//                    fromm.setHours(i);
//                    fromm.setMonth(8-1);
//                    fromm.setMinutes(0);
//                    fromm.setSeconds(0);
//                    fromm.setYear(2018-1900);
//                    Date too = new Date();
//                    too.setDate(15);
//                    too.setHours(i);
//                    too.setMonth(8-1);
//                    too.setMinutes(59);
//                    too.setSeconds(59);
//                    too.setYear(2018-1900);
//                    temperaturesList = temperaturesDatabase.daoAccess()
//                            .fetchTemperaturesBetweenDate(fromm, too);
//
////                    allTemperaturesList = temperaturesDatabase.daoAccess().getAllTemperatures();
////                    for (int x = 0; i<allTemperaturesList.size(); x++) {
////                        String vv  = allTemperaturesList.get(x).getTemperatureValue();
////                        System.out.println(allTemperaturesList.get(x).getTemperatureTime() + " --- " + allTemperaturesList.get(x).getTemperatureId() + " : " + allTemperaturesList.get(x).getTemperatureValue() + " : " + allTemperaturesList.get(x).getTemperatureDate());
////                    }
//
//
//                    int size = temperaturesList.size();
//                    if (size > 0){
//                        for (int j = 0; j < size; j++){
//                            //System.out.println(temperaturesList.get(i).getTemperatureValue());
//                            String valuesString = temperaturesList.get(j).getTemperatureValue();
//                            float tempValue = Float.parseFloat(valuesString);
//                            sumOfAllValues = sumOfAllValues + tempValue;
//                        }
//                        average = sumOfAllValues/size;
//                    }
//
//
//                    entries.add(new BarEntry(i, average));
//
//                }
//                BarDataSet dataSet = new BarDataSet(entries, "Projects");
//                BarData data = new BarData(dataSet);
//                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//                chart.setData(data);
//            }
//        }) .start();


//        entries.add(new BarEntry(4f, 0));
//        entries.add(new BarEntry(4f, 0));
//        entries.add(new BarEntry(4f, 0));
//        entries.add(new BarEntry(8f, 1));
//        entries.add(new BarEntry(6f, 2));
//        entries.add(new BarEntry(12f, 3));
//        entries.add(new BarEntry(18f, 4));
//        entries.add(new BarEntry(9f, 5));

       // BarDataSet dataSet = new BarDataSet(entries, "Projects");

//        labels.add("January");
//        labels.add("February");
//        labels.add("March");
//        labels.add("April");
//        labels.add("May");
//        labels.add("June");

//        BarData data = new BarData(dataSet);
//        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//        chart.setData(data);
       // chart.setDescription("No of Projects");

        return view;
    }

    public void setGraphs(final int startDay, final int endDay, final String type){
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

                for (int i = 0 ; i < 24 ; i++){

                    Date fromm = new Date();

                    fromm.setDate(startDay);
                    fromm.setHours(i);
                    fromm.setMonth(8-1);
                    fromm.setMinutes(0);
                    fromm.setSeconds(0);
                    fromm.setYear(2018-1900);
                    Date too = new Date();

                    too.setDate(endDay);
                    too.setHours(i);
                    too.setMonth(8-1);
                    too.setMinutes(59);
                    too.setSeconds(59);
                    too.setYear(2018-1900);

                    float average = 0;
                    if (type.equals("Temperatura")) {
                        average = getAverageTemperature(fromm, too);
                    }
                    if (type.equals("Ciśnienie")) {
                        average = getAverageAirPressure(fromm, too);
                    }
                    if (type.equals("Wilgotność")){
                        average = getAverageHumidity(fromm, too);
                    }

//                    temperaturesList = temperaturesDatabase.daoAccess()
//                            .fetchTemperaturesBetweenDate(fromm, too);
//
//                    int size = temperaturesList.size();
//                    if (size > 0){
//                        for (int j = 0; j < size; j++){
//
//                            String valuesString = temperaturesList.get(j).getTemperatureValue();
//                            float tempValue = Float.parseFloat(valuesString);
//                            sumOfAllValues = sumOfAllValues + tempValue;
//                        }
//                        average = sumOfAllValues/size;
//                    }

                    //entries.add(new BarEntry(i, average));
                    entries.add(new BarEntry(i, average));
                }
                dataSet = new BarDataSet(entries, "Projects");
                data = new BarData(dataSet);
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                chart.setData(data);
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
            setGraphs(actualDay,actualDay, measurementType);
           // transactFragment(this,true);
        }
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
            System.out.println("Exception Exception Exception Database");
        } finally {
            temperaturesDatabase.endTransaction();
            //temperaturesDatabase.close();
        }
        //tpList = temperaturesDatabase.daoAccess()
        //        .fetchTemperaturesBetweenDate(fromm, too);
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

    private float getAverageAirPressure(Date fromm, Date too){
        float averageAirPressure = 0;
        float sumOfAllValues = 0;
        List<AirPressures> airPressuresList = null;
        airPressuresDatabase.beginTransaction();
        try{
            airPressuresList = airPressuresDatabase.daoAccess().fetchAirPressuresBetweenDate(fromm, too);
            airPressuresDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            System.out.println("Exception Exception Exception Database AIR PRESSURE");
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

    private float getAverageHumidity(Date fromm, Date too){
        float averageHumidity = 0;
        float sumOfAllValues = 0;
        List<Humidities> humiditiesList = null;
        humiditiesDatabase.beginTransaction();
        try{
            humiditiesList = humiditiesDatabase.daoAccess().fetchHumiditiesBetweenDate(fromm,too);
            humiditiesDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            System.out.println("Exception Exception Exception Database HUMIDITY");
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
}
