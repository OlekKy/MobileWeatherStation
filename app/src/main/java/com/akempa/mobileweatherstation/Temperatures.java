package com.akempa.mobileweatherstation;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity
public class Temperatures {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int temperatureId;
    @ColumnInfo(name = "temperatureValue")
    private String temperatureValue;
    @ColumnInfo(name = "temperatureDate")
    private String temperatureDate;
    @ColumnInfo(name = "temperatureTime")
    private Date temperatureTime;

    public Temperatures(){
    }
    public Temperatures(int temperatureId, String temperatureValue, String temperatureDate){
        this.temperatureId = temperatureId;
        this.temperatureValue = temperatureValue;
        this.temperatureDate = temperatureDate;
        this.temperatureTime = temperatureTime;
    }

    @NonNull
    public int getTemperatureId() {
        return temperatureId;
    }

    public void setTemperatureId( int temperatureId) {
        this.temperatureId = temperatureId;
    }

    public String getTemperatureValue() {
        return temperatureValue;
    }

    public void setTemperatureValue(String temperatureValue) {
        this.temperatureValue = temperatureValue;
    }

    public String getTemperatureDate() {
        return temperatureDate;
    }

    public void setTemperatureDate(String temperatureDate) {
        this.temperatureDate = temperatureDate;
    }

    public Date getTemperatureTime() {
        return temperatureTime;
    }

    public void setTemperatureTime(Date temperatureTime) {
        this.temperatureTime = temperatureTime;
    }
}
