package pt.ua.cm.biketrack.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pt.ua.cm.biketrack.R;
import pt.ua.cm.biketrack.models.TrackInfo;

class TrackInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final TextView trackItemView;
    public final TextView distanceItemView;
    public final TextView speedItemView;
    public final TextView timeItemView;
    private final List<TrackInfo> trackInfos = new ArrayList<>();
    private Context context;
    private List<TrackInfo> mList;


    private TrackInfoViewHolder(View itemView, Context context, List<TrackInfo> mList) {
        super(itemView);
        trackItemView = itemView.findViewById(R.id.txtTrack);
        distanceItemView = itemView.findViewById(R.id.txtDistance);
        speedItemView = itemView.findViewById(R.id.txtSpeed);
        timeItemView = itemView.findViewById(R.id.txtTime);
        this.context = context;
        this.mList = mList;
        itemView.setOnClickListener(this);
    }

    public void bind(int track, int distance, int avgSpeed, String time, TrackInfo mCurrent) {
        trackItemView.setText("Track " + track);
        distanceItemView.setText("Distance: " + String.valueOf(distance) + " m");
        speedItemView.setText("Average Speed: " + String.valueOf(avgSpeed) + " Km/h");
        timeItemView.setText("Time: " + time);;
    }

    static TrackInfoViewHolder create(ViewGroup parent,Context context, List<TrackInfo> mList) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new TrackInfoViewHolder(view, context, mList);
    }

    @Override
    public void onClick(View v) {
        // Get the position of the item that was clicked.
        int mPosition = getLayoutPosition();

        TrackInfo element = mList.get(mPosition);

        int search_id = element.getId();

        Navigation.findNavController(v).navigate(R.id.action_navigation_history_to_historyDetailFragment);
    }
}
