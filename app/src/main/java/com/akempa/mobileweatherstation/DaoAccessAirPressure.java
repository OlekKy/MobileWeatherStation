package com.akempa.mobileweatherstation;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface DaoAccessAirPressure {

    @Insert
    void insertAirPressure (AirPressures airPressures);

    @Query("SELECT * FROM AirPressures")
    List<AirPressures> getAllAirPressures();

    @Query("SELECT * FROM AirPressures WHERE airPressureTime BETWEEN :from AND :to")
    List<AirPressures> fetchAirPressuresBetweenDate(Date from, Date to);

    @Delete
    void deleteAirPressure(AirPressures airPressures);
}
