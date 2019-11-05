package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;

public class TimestampedPosition extends Position {
    @SerializedName("date")
    @Expose
    private Long timestamp;

    public TimestampedPosition(Double latitude, Double longitude, long timestamp) {
        super(latitude, longitude);
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getDateString(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return ""+calendar.get(Calendar.DAY_OF_MONTH)+"."+calendar.get(Calendar.MONTH)+"."+calendar.get(Calendar.YEAR)+"  "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}