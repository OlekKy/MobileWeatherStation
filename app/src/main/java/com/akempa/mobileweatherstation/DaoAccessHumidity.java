package com.akempa.mobileweatherstation;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface DaoAccessHumidity {

    @Insert
    void insertHumidity(Humidities humidities);

    @Query("SELECT * FROM Humidities")
    List<Humidities> getAllHumidities();

    @Query("SELECT * FROM Humidities WHERE humidityTime BETWEEN :from AND :to")
    List<Humidities> fetchHumiditiesBetweenDate(Date from, Date to);

    @Delete
    void deleteHumidity (Humidities humidities);
}
