package pt.ua.cm.biketrack;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import pt.ua.cm.biketrack.models.TrackInfo;

@Dao
public interface TrackInfoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TrackInfo trackInfo);

    @Delete
    void deleteTrack(TrackInfo trackInfo);

    @Query("SELECT * FROM trackInfo_table")
    LiveData<List<TrackInfo>> queryAll();

}
