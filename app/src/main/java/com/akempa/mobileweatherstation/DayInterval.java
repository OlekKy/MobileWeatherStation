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
import java.util.GregorianCalendar;
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
    private Date dateStarting;
    private Date dateEnding;

    String actualDateString;
    private BarChart chart;
    private BarData data;
    String[] choose = { "Temperatura", "Ciśnienie", "Wilgotność", "Wszystkie"};

    private List<Temperatures> temperaturesList;

    private TemperaturesDatabase temperaturesDatabase;
    private AirPressuresDatabase airPressuresDatabase;
    private HumiditiesDatabase humiditiesDatabase;

    ArrayList<BarEntry> entries = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.day_fragment,container,false);
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

        actualDate = view.findViewById(R.id.actualDate);

        dateFormat = new SimpleDateFormat(DATE_FORMAT);
        c = Calendar.getInstance();
        c.setTime(new Date());

        dateStarting = c.getTime();
        dateStarting.setMinutes(0);
        dateStarting.setSeconds(0);
        dateEnding = c.getTime();
        dateEnding.setMinutes(59);
        dateEnding.setSeconds(59);

        actualDate.setText(dateFormat.format(c.getTime()));

        previousDay = view.findViewById(R.id.btnPrevious);
        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c.add(Calendar.DATE, -1);
               // subtractDays(c.getTime(), 1);
                System.out.println(c.getTime().getDate());
//
//                System.out.println(dateStarting);
//                System.out.println(dateEnding);
                dateStarting.setDate(c.getTime().getDate());
                dateEnding.setDate(c.getTime().getDate());
//                System.out.println(dateStarting);
//                System.out.println(dateEnding);
                //Date ssDate  = c.getTime();
                actualDateString = dateFormat.format(c.getTime());
                actualDate.setText(actualDateString);
                setGraphs(measurementType);
            }
        });

        nextDay = view.findViewById(R.id.btnNext);
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c.add(Calendar.DATE, 1);
                dateStarting.setDate(c.getTime().getDate());
                dateEnding.setDate(c.getTime().getDate());
                actualDateString = dateFormat.format(c.getTime());
                actualDate.setText(actualDateString);
                setGraphs(measurementType);
            }
        });

        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGraphs(measurementType);
            }
        });

        setGraphs(measurementType);
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

                for (int i = 0 ; i < 24 ; i++){

                    dateStarting.setHours(i);

                    dateEnding.setHours(i);
//                    Date fromm = new Date();
//
//                    fromm.setDate(startDay);
//                    fromm.setHours(i);
//                    fromm.setMonth(8-1);
//                    fromm.setMinutes(0);
//                    fromm.setSeconds(0);
//                    fromm.setYear(2018-1900);
//                    Date too = new Date();
//
//                    too.setDate(endDay);
//                    too.setHours(i);
//                    too.setMonth(8-1);
//                    too.setMinutes(59);
//                    too.setSeconds(59);
//                    too.setYear(2018-1900);

                    float average = 0;
                    if (type.equals("Temperatura")) {
                        average = getAverageTemperature(dateStarting, dateEnding);
                        average = getAverageTemperature(i);
                    }
                    if (type.equals("Ciśnienie")) {
                        average = getAverageAirPressure(dateStarting, dateEnding);
                    }
                    if (type.equals("Wilgotność")){
                        average = getAverageHumidity(dateStarting, dateEnding);
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
            dataStart.setHours(i);
            dataStart.setMinutes(0);
            dataStart.setSeconds(0);
            Date dataEnd = c.getTime();
            dataEnd.setHours(i);
            dataEnd.setMinutes(59);
            dataEnd.setSeconds(59);
//            System.out.println("SK: "+dataStart);
//            System.out.println("SK: "+dataEnd);
            tpList = temperaturesDatabase.daoAccess()
                    .fetchTemperaturesBetweenDate(dataStart, dataEnd);
            temperaturesDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            System.out.println("Exception Exception Exception Database");
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

//        System.out.println("AK: "+fromm);
//        System.out.println("AK: "+too);

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

    private Date addDays(Date date, int days){
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    private Date subtractDays(Date date, int days){
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);
        return cal.getTime();
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
