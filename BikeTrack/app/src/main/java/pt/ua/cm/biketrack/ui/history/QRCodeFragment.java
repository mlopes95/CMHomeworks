package pt.ua.cm.biketrack.ui.history;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import pt.ua.cm.biketrack.R;


public class QRCodeFragment extends Fragment {

    private ImageView qrCodeImage;

    public QRCodeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static QRCodeFragment newInstance() {
        QRCodeFragment fragment = new QRCodeFragment();
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
        View root =  inflater.inflate(R.layout.fragment_q_r_code, container, false);

        String data = QRCodeFragmentArgs.fromBundle(getArguments()).getQrData();

        qrCodeImage = root.findViewById(R.id.qrCodeView);

        QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, 300);

        Bitmap bitmap = qrgEncoder.getBitmap();
        qrCodeImage.setImageBitmap(bitmap);

        return root;
    }
}