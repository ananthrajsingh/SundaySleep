package com.ananthrajsingh.sundaysleep;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ananthrajsingh.sundaysleep.Database.Sleep;

import java.util.Calendar;
import java.util.List;


/**
 * Created by ananthrajsingh on 28/01/19
 */
public class SleepHistoryAdapter extends RecyclerView.Adapter<SleepHistoryAdapter.SleepViewHolder> {


    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<Sleep> mList;

    public SleepHistoryAdapter(Context mContext) {
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public SleepViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.sleep_list_item, viewGroup, false);
        return new SleepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SleepViewHolder holder, int i) {
        Sleep currentSleep = mList.get(i);
        Calendar calendar = Calendar.getInstance();

        long startTimeLong = currentSleep.startTime;
        calendar.setTimeInMillis(startTimeLong);
        String startDate = "" + calendar.get(Calendar.DAY_OF_MONTH) + ":" + calendar.get(Calendar.MONTH) + ":" + calendar.get(Calendar.YEAR);
        String startTime = "" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        long endTimeLong = currentSleep.endTime;
        calendar.setTimeInMillis(endTimeLong);
        String endDate = "" + calendar.get(Calendar.DAY_OF_MONTH) + ":" + calendar.get(Calendar.MONTH) + ":" + calendar.get(Calendar.YEAR);
        String endTime = "" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

        holder.startDate.setText(startDate);
        holder.startTime.setText(startTime);
        holder.endDate.setText(endDate);
        holder.endTime.setText(endTime);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void swapList(List<Sleep> list){
        mList = list;
    }

    public class SleepViewHolder extends RecyclerView.ViewHolder{

        TextView startTime;
        TextView endTime;
        TextView startDate;
        TextView endDate;

        public SleepViewHolder(@NonNull View itemView) {
            super(itemView);
            startTime = itemView.findViewById(R.id.sleepTimeStartTv);
            endTime = itemView.findViewById(R.id.sleepTimeEndTv);
            startDate = itemView.findViewById(R.id.sleepDateStartTv);
            endDate = itemView.findViewById(R.id.sleepDateEndTv);
        }
    }
}
