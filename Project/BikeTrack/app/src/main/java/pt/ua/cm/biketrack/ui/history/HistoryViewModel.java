package pt.ua.cm.biketrack.ui.history;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pt.ua.cm.biketrack.models.TrackInfo;

public class HistoryViewModel extends AndroidViewModel {

    private TrackInfoRepository mRepository;

    private final LiveData<List<TrackInfo>> mAllTrackInfo;

    private LiveData<TrackInfo> mTrackInfo;


    public HistoryViewModel(Application application) {
        super(application);
        mRepository = new TrackInfoRepository(application);
        mAllTrackInfo = mRepository.getAllTrackInfo();
    }

    LiveData<List<TrackInfo>> getAllTrackInfo(){
        return mAllTrackInfo;
    }

    LiveData<TrackInfo> getTrack(int id) {
        mTrackInfo = mRepository.queryID(id);
        return mTrackInfo;}

    public void insert(TrackInfo trackInfo) {
        mRepository.insert(trackInfo);
    }

    public void deleteTrack(TrackInfo trackInfo) {mRepository.deleteTrack(trackInfo);}
}