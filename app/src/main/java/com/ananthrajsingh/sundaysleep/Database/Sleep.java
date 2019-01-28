package com.ananthrajsingh.sundaysleep.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by ananthrajsingh on 28/01/19
 */
@Entity(tableName = "sleep_table")
public class Sleep {
//    public int sleepId;

    @PrimaryKey
    @ColumnInfo(name = "start_time")
    public long startTime;

    @ColumnInfo(name = "end_time")
    public long endTime;
}
