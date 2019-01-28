package com.ananthrajsingh.sundaysleep.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by ananthrajsingh on 28/01/19
 */
@Dao
public interface SleepDao {

    @Query("SELECT * FROM sleep_table ORDER BY start_time")
    List<Sleep> getAll();

    @Query("SELECT * FROM sleep_table WHERE start_time >= :startTime ORDER BY start_time")
    List<Sleep> getUptoTime(long startTime);

    @Insert
    void insertAll(Sleep... sleeps);
}
