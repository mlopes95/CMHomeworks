package pt.ua.cm.biketrack.ui.history;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.Result;

import pt.ua.cm.biketrack.R;
import pt.ua.cm.biketrack.models.TrackInfo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QRScannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QRScannerFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private CodeScanner codeScanner;
    private CodeScannerView codeScannerView;
    private String res;
    private FloatingActionButton saveQR_fab;
    private TextView scanText;
    private HistoryViewModel mHistoryViewModel;
    private String[] results;

    public QRScannerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static QRScannerFragment newInstance() {
        QRScannerFragment fragment = new QRScannerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_q_r_scanner, container, false);

        final Activity activity = getActivity();

        codeScannerView = root.findViewById(R.id.scannerView);

        codeScanner = new CodeScanner(this.getContext(), codeScannerView);

        mHistoryViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        saveQR_fab = root.findViewById(R.id.fab_save_qr);

        scanText = root.findViewById(R.id.textScan);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
               activity.runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                        res = result.getText();
                        scanText.setText("Click the button to save!");
                   }
               });
            }
        });

        saveQR_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(res != null){
                    res = res.replaceAll("\\s+","");
                    results = res.split(",");
                    TrackInfo info = new TrackInfo(Integer.parseInt(results[0]), Integer.parseInt(results[1]),
                            Double.parseDouble(results[2]), Double.parseDouble(results[3]),
                            Double.parseDouble(results[4]), Double.parseDouble(results[5]), results[6]);
                    mHistoryViewModel.insert(info);
                    Navigation.findNavController(v).navigateUp();
                    Navigation.findNavController(v).popBackStack();
                } else {
                    return;
                }
            }
        });


        return root;
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this.getActivity(),
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!checkPermission()){
            requestPermission();
        }
        else{
            codeScanner.startPreview();
        }
    }

    @Override
    public void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }
}