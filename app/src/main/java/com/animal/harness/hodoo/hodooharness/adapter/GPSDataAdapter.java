package com.animal.harness.hodoo.hodooharness.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.databinding.GpsDataItemBinding;
import com.animal.harness.hodoo.hodooharness.domain.GPSData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GPSDataAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<GPSData> mItems;
    private GpsDataItemBinding binding;
    private SimpleDateFormat sdf;
    public GPSDataAdapter ( List<GPSData> items ) {
        mItems = items;
        sdf = new SimpleDateFormat("yyyy.MM.dd");
    }
    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if ( convertView == null ) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            binding = DataBindingUtil.inflate(inflater, R.layout.gps_data_item, parent, false);
            convertView = binding.getRoot();
        }
        binding.index.setText(mItems.get(position).getId());
        binding.lat.setText(Double.toString(mItems.get(position).getLat()));
        binding.lon.setText(Double.toString(mItems.get(position).getLon()));
        binding.created.setText(sdf.format(new Date(mItems.get(position).getCreated())));
        binding.totalDistance.setText( Double.toString(mItems.get(position).getSum()) + "m" );
        return convertView;
    }
}
