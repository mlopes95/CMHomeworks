package pt.ua.cm.biketrack.ui.camera;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CameraViewModel extends AndroidViewModel {

    private MutableLiveData<List<File>> mFile;
    private List<File> mFiles;


    public CameraViewModel(Application application) {
        super(application);
        mFile = new MutableLiveData<>();
        mFiles = new ArrayList<>();
        for(File file : Objects.requireNonNull(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES).listFiles())){
            if(file.getPath().contains("JPEG_")){
                Log.i("Extension", file.getPath());
                mFiles.add(file);
            }
        }
        mFile.setValue(mFiles);
    }

    public LiveData<List<File>> getAllFiles() {
        return mFile;
    }
}