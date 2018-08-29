package com.akempa.mobileweatherstation;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {Humidities.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class HumiditiesDatabase extends RoomDatabase {
    public abstract DaoAccessHumidity daoAccess();
}
