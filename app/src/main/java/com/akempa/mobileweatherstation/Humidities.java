package com.akempa.mobileweatherstation;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity
public class Humidities {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int humidityId;
    @ColumnInfo(name = "humidityValue")
    private String humidityValue;
    @ColumnInfo(name = "humidityTime")
    private Date humidityTime;

    public Humidities(){

    }

    public Humidities(@NonNull int humidityId, String humidityValue, Date humidityTime) {
        this.humidityId = humidityId;
        this.humidityValue = humidityValue;
        this.humidityTime = humidityTime;
    }

    @NonNull
    public int getHumidityId() {
        return humidityId;
    }
    public void setHumidityId(@NonNull int humidityId) {
        this.humidityId = humidityId;
    }
    public String getHumidityValue() {
        return humidityValue;
    }
    public void setHumidityValue(String humidityValue) {
        this.humidityValue = humidityValue;
    }
    public Date getHumidityTime() {
        return humidityTime;
    }
    public void setHumidityTime(Date humidityTime) {
        this.humidityTime = humidityTime;
    }
}
