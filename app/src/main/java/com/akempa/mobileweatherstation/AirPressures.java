package com.akempa.mobileweatherstation;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity
public class AirPressures {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int airPressureId;
    @ColumnInfo(name = "airPressureValue")
    private String airPressureValue;
    @ColumnInfo(name = "airPressureTime")
    private Date airPressureTime;

    public AirPressures(){
    }
    public AirPressures(int airPressureId, String airPressureValue, Date airPressureTime){
        this.airPressureId = airPressureId;
        this.airPressureValue = airPressureValue;
        this.airPressureTime = airPressureTime;
    }

    @NonNull
    public int getAirPressureId() {
        return airPressureId;
    }
    public void setAirPressureId(@NonNull int airPressureId) {
        this.airPressureId = airPressureId;
    }
    public String getAirPressureValue() {
        return airPressureValue;
    }
    public void setAirPressureValue(String airPressureValue) {
        this.airPressureValue = airPressureValue;
    }
    public Date getAirPressureTime() {
        return airPressureTime;
    }
    public void setAirPressureTime(Date airPressureTime) {
        this.airPressureTime = airPressureTime;
    }
}
