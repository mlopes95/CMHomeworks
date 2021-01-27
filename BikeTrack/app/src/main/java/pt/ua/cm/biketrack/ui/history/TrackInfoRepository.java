package pt.ua.cm.biketrack.ui.history;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import pt.ua.cm.biketrack.models.TrackInfo;

class TrackInfoRepository {
    private TrackInfoDao mTrackInfoDao;
    private LiveData<List<TrackInfo>> mTrackInfo;
    private LiveData<TrackInfo> mTrackInfoID;

    TrackInfoRepository(Application application){
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

    LiveData<TrackInfo> queryID(int id){
        mTrackInfoID = mTrackInfoDao.queryByID(id);
        return mTrackInfoID;
    }

    void deleteTrack(TrackInfo trackInfo){
        TrackInfoDatabase.databaseWriteExecutor.execute(()->{
            mTrackInfoDao.deleteTrack(trackInfo);
        });
    }
}
