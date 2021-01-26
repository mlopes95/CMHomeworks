package pt.ua.cm.biketrack.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pt.ua.cm.biketrack.R;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;

    private FloatingActionButton share_fab;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        historyViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        share_fab = root.findViewById(R.id.fab_hist_share);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerview);
        final TrackInfoAdapter adapter = new TrackInfoAdapter(new TrackInfoAdapter.TrackInfoDiff(), getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));


        historyViewModel.getAllTrackInfo().observe(getViewLifecycleOwner(), trackInfos -> {
            adapter.submitList(trackInfos);
        });

        share_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  HistoryFragmentDirections.ActionNavigationHistoryToHistoryQR action = HistoryFragmentDirections.actionNavigationHistoryToQRScannerFragment();
                action.setQueryId();
                Navigation.findNavController(v).navigate(action);*/

                Navigation.findNavController(v).navigate(R.id.navigation_qrScan);

            }
        });

        return root;
    }
}