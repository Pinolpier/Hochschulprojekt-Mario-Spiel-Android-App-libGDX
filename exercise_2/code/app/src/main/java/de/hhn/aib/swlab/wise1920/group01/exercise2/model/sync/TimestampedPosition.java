package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync;

public class TimestampedPosition extends Position {
    private Long timestamp;

    public TimestampedPosition(Double latitude, Double longitude, long timestamp) {
        super(latitude, longitude);
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
