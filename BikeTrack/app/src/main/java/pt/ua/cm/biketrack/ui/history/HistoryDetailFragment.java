package pt.ua.cm.biketrack.ui.history;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pt.ua.cm.biketrack.R;

public class HistoryDetailFragment extends Fragment {

    private static final String QUERY_ID = "query_id";

    private int queryID;

    public HistoryDetailFragment() {
        // Required empty public constructor
    }

    public static HistoryDetailFragment newInstance(int id) {
        HistoryDetailFragment fragment = new HistoryDetailFragment();
        Bundle args = new Bundle();
        args.putInt(QUERY_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            queryID = getArguments().getInt(QUERY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_detail, container, false);
    }
}