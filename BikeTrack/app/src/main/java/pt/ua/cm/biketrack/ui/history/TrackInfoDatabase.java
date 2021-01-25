package pt.ua.cm.biketrack.ui.history;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pt.ua.cm.biketrack.models.TrackInfo;

@Database(entities = {TrackInfo.class}, version = 1, exportSchema = false)
public abstract class TrackInfoDatabase extends RoomDatabase {
    public abstract TrackInfoDao trackInfoDao();

    private static volatile TrackInfoDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;

    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static TrackInfoDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (TrackInfoDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TrackInfoDatabase.class, "trackInfo_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
