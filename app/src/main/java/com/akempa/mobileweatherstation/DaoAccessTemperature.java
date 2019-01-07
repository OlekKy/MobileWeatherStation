package com.akempa.mobileweatherstation;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface DaoAccessTemperature {

    @Insert
    void insertOnlySingleTemperature(Temperatures temperatures);

    @Query("SELECT * FROM Temperatures")
    List<Temperatures> getAllTemperatures();

    @Query("SELECT * FROM Temperatures WHERE temperatureTime BETWEEN :from AND :to")
    List<Temperatures> fetchTemperaturesBetweenDate(Date from, Date to);

    @Update
    void updateTemperature (Temperatures temperatures);
    @Delete
    void deleteTemperature (Temperatures temperatures);
}
