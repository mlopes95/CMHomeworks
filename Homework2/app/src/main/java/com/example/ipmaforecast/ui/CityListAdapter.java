package com.example.ipmaforecast.ui;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ipmaforecast.R;
import com.example.ipmaforecast.datamodel.City;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class CityListAdapter extends
        RecyclerView.Adapter<CityListAdapter.CityViewHolder>  {

    private HashMap<String, City> mCityList;
    private Context mContext;
    /* To create a View for a list item, the CityListAdapter needs to inflate the XML for a list
       item. You use a layout inflator for that job. LayoutInflator reads a layout XML description
       and converts it into the corresponding View items. */
    private LayoutInflater mInflater;


    public CityListAdapter(Context context, HashMap<String, City> cityList) {
        mInflater = LayoutInflater.from(context);
        this.mCityList = cityList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public CityListAdapter.CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.custom_row,
                parent, false);
        return new CityViewHolder(mItemView, this);
    }

    /* The onBindViewHolder() method connects your data to the view holder. */
    @Override
    public void onBindViewHolder(@NonNull CityListAdapter.CityViewHolder holder, int position) {
        Set<String> cityNames = mCityList.keySet();
        String mCurrent = "";
        for(String key : cityNames) {
            mCurrent = mCityList.get(key).getLocal();
        }
        holder.cityItemView.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        return mCityList == null ? 0 : mCityList.size();
    }

    class CityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView cityItemView;
        final CityListAdapter mAdapter;

        public CityViewHolder(@NonNull View itemView, CityListAdapter adapter) {
            super(itemView);
            cityItemView = itemView.findViewById(R.id.Title);
            itemView.setOnClickListener(this);
            this.mAdapter = adapter;
        }

        @Override
        public void onClick(View v) {
            City currentCity = mCityList.get(getAdapterPosition());
            Intent intent = new Intent(mContext, DetailCityActivity.class );
            intent.putExtra("title", currentCity.getLocal());
            mContext.startActivity(intent);
        }
    }
}