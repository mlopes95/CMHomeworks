package pt.ua.cm.biketrack.ui.history;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.ua.cm.biketrack.R;
import pt.ua.cm.biketrack.models.TrackInfo;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;

    private FloatingActionButton share_fab;

    private int swipeDirs;

    final ColorDrawable background = new ColorDrawable(Color.RED);


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

        swipeDirs = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT |
                ItemTouchHelper.DOWN | ItemTouchHelper.UP, swipeDirs) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();

                List<TrackInfo> tmpList = new ArrayList<>(adapter.getCurrentList());

                Collections.swap(tmpList, from, to);
                adapter.submitList(tmpList);

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                List<TrackInfo> deleteList = adapter.getCurrentList();
                historyViewModel.deleteTrack(deleteList.get(viewHolder.getAdapterPosition()));
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                background.setBounds(0, viewHolder.itemView.getTop(), (int) (viewHolder.itemView.getLeft() + dX), viewHolder.itemView.getBottom());

                if(dX > 100){
                    background.draw(c);
                }
            }
        });

        helper.attachToRecyclerView(recyclerView);

        share_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.navigation_qrScan);
            }
        });

        return root;
    }
}