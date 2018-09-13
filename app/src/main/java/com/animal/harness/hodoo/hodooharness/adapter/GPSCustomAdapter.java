package com.animal.harness.hodoo.hodooharness.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.databinding.GpsDataItemBinding;
import com.animal.harness.hodoo.hodooharness.domain.GPSData;

import java.util.List;
import java.util.Map;

public class GPSCustomAdapter {
    private static final String TAG = GPSCustomAdapter.class.getSimpleName();
    private Map<String, Object> mItems;
    private Context mContext;
    private View convertView;
    private GpsDataItemBinding binding;
    private ViewHolder holder;
    public GPSCustomAdapter (Context context, Map<String, Object> items, ViewGroup layout) {
        mItems = items;
        mContext = context;
        init( layout );
    }
    public void init ( ViewGroup layout ) {
        LinearLayout wrap = new LinearLayout(mContext);
        wrap.setOrientation(LinearLayout.VERTICAL);
        for ( String key : mItems.keySet() ) {
            /* header */
            LinearLayout header = new LinearLayout(mContext);
            header.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 0.5f;

            TextView created = new TextView(mContext);
            created.setLayoutParams(params);
            created.setText(key);
            created.setTypeface(created.getTypeface(), Typeface.BOLD);
            created.setTextSize(20f);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
            View line = new View(mContext);
            line.setLayoutParams(params);
            line.setBackgroundColor(Color.GRAY);

            header.addView(created);


            List<GPSData> data = (List<GPSData>) mItems.get(key);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout body = new LinearLayout(mContext);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            body.setOrientation(LinearLayout.VERTICAL);
            body.setLayoutParams(params);
            int totalDistance = 0;
            for ( int i = 0; i < data.size(); i++ ) {
                binding = DataBindingUtil.inflate(inflater, R.layout.gps_data_item, layout, false);
                binding.setData(data.get(i));
                body.addView(binding.getRoot());
                totalDistance += data.get(i).getSum();
            }
            TextView totalDistanceView = new TextView(mContext);
            totalDistanceView.setText(String.format("총 이동거리 : %d", totalDistance));
            header.addView(totalDistanceView);
            wrap.addView(header);
            wrap.addView(line);
            wrap.addView(body);

//
//            if ( convertView == null ) {
//                binding = DataBindingUtil.inflate(inflater, R.layout.gps_data_item, layout, false);
//                convertView = binding.getRoot();
//                holder = new ViewHolder();
//            }
//
//            Log.e(TAG, key);
        }
        layout.addView(wrap);
    }
    public void reset( ViewGroup layout ) {
        layout.removeAllViews();
        init(layout);
    }
    public class ViewHolder {
        private TextView index;
        private TextView lat;
        private TextView lon;
    }
}
