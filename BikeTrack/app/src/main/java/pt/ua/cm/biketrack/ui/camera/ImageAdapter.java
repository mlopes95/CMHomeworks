package pt.ua.cm.biketrack.ui.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import pt.ua.cm.biketrack.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;
    private List<File> mDowloads;

    public ImageAdapter(Context context, List<File> mDownloads) {
        this.mContext = context;
        this.mDowloads = mDownloads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        File mCurrent = mDowloads.get(position);
        String filePath = mCurrent.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return mDowloads.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public final ImageView imageView;
        final ImageAdapter mAdapter;

        public ImageViewHolder(View itemView, ImageAdapter adapter) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_download);
            this.mAdapter = adapter;
        }
    }
}
