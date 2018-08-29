package com.akempa.mobileweatherstation;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {AirPressures.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AirPressuresDatabase extends RoomDatabase {
    public abstract DaoAccessAirPressure daoAccess();
}
