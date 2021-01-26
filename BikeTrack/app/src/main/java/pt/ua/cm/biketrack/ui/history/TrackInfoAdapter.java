package pt.ua.cm.biketrack.ui.history;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import pt.ua.cm.biketrack.models.TrackInfo;

public class TrackInfoAdapter extends ListAdapter<TrackInfo, TrackInfoViewHolder> {

    private Context context;

    public TrackInfoAdapter(@NonNull DiffUtil.ItemCallback<TrackInfo> diffCallback, Context context) {
        super(diffCallback);
        this.context = context;
    }

    @NonNull
    @Override
    public TrackInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return TrackInfoViewHolder.create(parent, context, getCurrentList());
    }

    @Override
    public void onBindViewHolder(@NonNull TrackInfoViewHolder holder, int position) {
        TrackInfo mCurrent = getItem(position);
        holder.bind(position + 1, mCurrent.getDistance(), mCurrent.getAvgSpeed(), mCurrent.getTime(), mCurrent);
    }

    static class TrackInfoDiff extends DiffUtil.ItemCallback<TrackInfo> {

        @Override
        public boolean areItemsTheSame(@NonNull TrackInfo oldItem, @NonNull TrackInfo newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull TrackInfo oldItem, @NonNull TrackInfo newItem) {
            return false;
        }
    }
}
