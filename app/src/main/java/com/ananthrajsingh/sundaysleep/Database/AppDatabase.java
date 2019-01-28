package com.ananthrajsingh.sundaysleep.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by ananthrajsingh on 28/01/19
 */
@Database(entities = {Sleep.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract SleepDao sleepDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context){
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "sleep_database")
                            .allowMainThreadQueries() // This is not scalable, only for testing
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
