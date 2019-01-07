package com.akempa.mobileweatherstation;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {Temperatures.class}, version = 4, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class TemperaturesDatabase extends RoomDatabase {
    public abstract DaoAccessTemperature daoAccess();
}
