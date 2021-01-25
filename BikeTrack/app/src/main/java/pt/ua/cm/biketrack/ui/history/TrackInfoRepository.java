package pt.ua.cm.biketrack;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import pt.ua.cm.biketrack.models.TrackInfo;

public class TrackInfoRepository {
    private TrackInfoDao mTrackInfoDao;
    private LiveData<List<TrackInfo>> mTrackInfo;

    public TrackInfoRepository(Application application){
        TrackInfoDatabase db = TrackInfoDatabase.getDatabase(application);
        mTrackInfoDao = db.trackInfoDao();
        mTrackInfo = mTrackInfoDao.queryAll();
    }

    LiveData<List<TrackInfo>> getAllTrackInfo(){
        return mTrackInfo;
    }

    void insert (TrackInfo trackInfo){
        TrackInfoDatabase.databaseWriteExecutor.execute(()->{
            mTrackInfoDao.insert(trackInfo);
        });
    }
}
