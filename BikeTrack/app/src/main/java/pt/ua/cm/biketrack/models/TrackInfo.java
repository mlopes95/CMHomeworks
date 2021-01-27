package pt.ua.cm.biketrack.models;

import android.widget.Chronometer;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "trackInfo_table")
public class TrackInfo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "speed")
    private int mAvgSpeed;

    @ColumnInfo(name = "distance")
    private int mDistance;

    @ColumnInfo(name = "initLat")
    private double mInitialLatitude;

    @ColumnInfo(name = "initLong")
    private double mInitialLongitude;

    @ColumnInfo(name = "finalLat")
    private double mFinalLatitude;

    @ColumnInfo(name = "finalLong")
    private double mFinalLongitude;

    @ColumnInfo(name = "time")
    private String time;

    public TrackInfo(int mAvgSpeed, int mDistance, double mInitialLatitude, double mInitialLongitude, double mFinalLatitude, double mFinalLongitude, String time) {
        this.mAvgSpeed = mAvgSpeed;
        this.mDistance = mDistance;
        this.mInitialLatitude = mInitialLatitude;
        this.mInitialLongitude = mInitialLongitude;
        this.mFinalLatitude = mFinalLatitude;
        this.mFinalLongitude = mFinalLongitude;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAvgSpeed() {
        return mAvgSpeed;
    }

    public void setAvgSpeed(int mAvgSpeed) {
        this.mAvgSpeed = mAvgSpeed;
    }

    public int getDistance() {
        return mDistance;
    }

    public void setDistance(int mDistance) {
        this.mDistance = mDistance;
    }

    public double getInitialLatitude() {
        return mInitialLatitude;
    }

    public void setInitialLatitude(float mInitialLatitude) {
        this.mInitialLatitude = mInitialLatitude;
    }

    public double getInitialLongitude() {
        return mInitialLongitude;
    }

    public void setInitialLongitude(float mInitialLongitude) {
        this.mInitialLongitude = mInitialLongitude;
    }

    public double getFinalLatitude() {
        return mFinalLatitude;
    }

    public void setFinalLatitude(float mFinalLatitude) {
        this.mFinalLatitude = mFinalLatitude;
    }

    public double getFinalLongitude() {
        return mFinalLongitude;
    }

    public void setFinalLongitude(float mFinalLongitude) {
        this.mFinalLongitude = mFinalLongitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "TrackInfo{" +
                "id=" + id +
                ", mAvgSpeed=" + mAvgSpeed +
                ", mDistance=" + mDistance +
                ", mInitialLatitude=" + mInitialLatitude +
                ", mInitialLongitude=" + mInitialLongitude +
                ", mFinalLatitude=" + mFinalLatitude +
                ", mFinalLongitude=" + mFinalLongitude +
                ", time='" + time + '\'' +
                '}';
    }
}
