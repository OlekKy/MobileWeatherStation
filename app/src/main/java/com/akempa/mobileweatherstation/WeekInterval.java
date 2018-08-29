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
import java.util.Locale;

public class WeekInterval extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String DATE_FORMAT = "dd-MM-yyyy";

    private Button btnUpdate;
    private Button previousDay;
    private Button nextDay;
    private TextView actualDate;
    private String measurementType;

    int actualYear;
    int actualMonth;
    int actualDay;
    String actualDateString;

    private DateFormat dateFormat;
    private Calendar c;
    private Calendar startDate;
    private Calendar endDate;

    private BarChart chart;
    private BarData data;
    String[] choose = { "Temperatura", "Ciśnienie", "Wilgotność", "Wszystkie"};

    private static final String DATABASE_NAME = "movies_db";
    private static final String AIR_PRESSURE_DATABASE = "airpressure_db";
    private static final String HUMIDITY_DATABASE = "humidity_db";
    String startWeek;
    String endWeek;
    private TemperaturesDatabase temperaturesDatabase;
    private AirPressuresDatabase airPressuresDatabase;
    private HumiditiesDatabase humiditiesDatabase;

    ArrayList<BarEntry> entries = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.week_fragment,container,false);
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
        c = Calendar.getInstance(Locale.FRANCE);
        c.setTime(new Date());
        c.setFirstDayOfWeek(Calendar.MONDAY);

        actualDay = c.getTime().getDate();
        actualDate.setText(dateFormat.format(c.getTime()));


        startDate = Calendar.getInstance(Locale.FRANCE);
        endDate = Calendar.getInstance(Locale.FRANCE);
        startDate.setTime(getWeekStartDate());
        endDate.setTime(getWeekEndDate());

        startWeek = dateFormat.format(getWeekStartDate());
        endWeek = dateFormat.format(getWeekEndDate());
        actualDate.setText(startWeek + " " + endWeek);
        setGraphs(actualDay,actualDay, measurementType);
        previousDay = (Button) view.findViewById(R.id.btnPrevious);
        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDate.add(Calendar.DATE, -7);
                endDate.add(Calendar.DATE, -7);
                startWeek = dateFormat.format(startDate.getTime());
                endWeek = dateFormat.format(endDate.getTime());

                //actualDay = c.getTime().getDate();
                actualDay = startDate.getTime().getDate();

                actualDate.setText(startWeek + " " + endWeek);
                setGraphs(actualDay,actualDay, measurementType);
            }
        });

        nextDay = (Button) view.findViewById(R.id.btnNext);
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO change to week
                startDate.add(Calendar.DATE, 7);
                endDate.add(Calendar.DATE, 7);
                startWeek = dateFormat.format(startDate.getTime());
                endWeek = dateFormat.format(endDate.getTime());

                //actualDay = c.getTime().getDate();
                actualDay = startDate.getTime().getDate();

                actualDate.setText(startWeek + " " + endWeek);
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



        return view;
    }

    public static Date getWeekStartDate() {
        Calendar calendar = Calendar.getInstance(Locale.FRANCE);
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, -1);
        }
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0,0);
        //calendar.se
        return calendar.getTime();
    }

    public static Date getWeekEndDate() {
        Calendar calendar = Calendar.getInstance(Locale.FRANCE);
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, 1);
        }
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 23, 59,59);

        return calendar.getTime();
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
                int beginningDay = startDay;

                for (int i = 0 ; i < 7 ; i++){
                    float average = 0;
                   // for (int i = 0 ; i < 24 ; i++){

                        Date fromm = new Date();

                        fromm.setDate(beginningDay);
                        fromm.setHours(0);
                        fromm.setMonth(8-1);
                        fromm.setMinutes(0);
                        fromm.setSeconds(0);
                        fromm.setYear(2018-1900);

                        Date too = new Date();
                        too.setDate(beginningDay);
                        too.setHours(23);
                        too.setMonth(8-1);
                        too.setMinutes(59);
                        too.setSeconds(59);
                        too.setYear(2018-1900);

                        //float average = 0;
                        if (type.equals("Temperatura")) {
                            average = getAverageTemperature(fromm, too);
                        }
                        if (type.equals("Ciśnienie")) {
                            average = getAverageAirPressure(fromm, too);
                        }
                        if (type.equals("Wilgotność")){
                            average = getAverageHumidity(fromm, too);
                        }
                        //entries.add(new BarEntry(i, average));
                 //   }
                    entries.add(new BarEntry(i, average));
                    beginningDay++;
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
