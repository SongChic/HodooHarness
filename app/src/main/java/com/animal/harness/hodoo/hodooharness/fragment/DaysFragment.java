package com.animal.harness.hodoo.hodooharness.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.animal.harness.hodoo.hodooharness.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DaysFragment extends Fragment {
    private long[] preDate, nextDate;
    public DaysFragment () {}

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        days
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_days, container, false);
        LinearLayout wrap = layout.findViewById(R.id.days);
        TextView today = layout.findViewById(R.id.today);

        int position = getArguments().getInt("position");
        long now = getArguments().getLong("now");

        switch (position) {
            case 0 :
                now = now - ( (24 * 7) * 60 * 60 * 1000 );
                break;
            case 2 :
                now = now + ( (24 * 7) * 60 * 60 * 1000 );
                break;
        }


        preDate = new long[3];
        nextDate = new long[3];
        SimpleDateFormat sdf = new SimpleDateFormat("M/d");
        SimpleDateFormat dayOfWeekSdf = new SimpleDateFormat("E");
        SimpleDateFormat testSdf = new SimpleDateFormat("yyyy.M.d");

//        Log.e("TAG", sdf.format( new Date(getArguments().getLong("now"))));

        int switchVal = -1;
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.weight = 1;
        for ( int i = 0; i < 2; i++ ) {
            for ( int j = 1; j <= 3; j++) {
                //3 pre and next day of week setting
                long tempDate = now + ((24 * 60 * 60 *  1000) * ( j * switchVal ));
                if ( switchVal < 0 )
                    preDate[j - 1] = tempDate;
                else
                    nextDate[j - 1] = tempDate;
            }
            switchVal = 1;
        }
        TextView tv;
        for ( int i = preDate.length; i > 0; i-- ) {
            tv = new TextView(getContext());
            tv.setText( dayOfWeekSdf.format( new Date(preDate[i - 1])) );
            Log.e("test", testSdf.format(new Date(preDate[i - 1])));
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setLayoutParams(param);
            wrap.addView(tv);
        }
        tv = new TextView(getContext());
        tv.setText( dayOfWeekSdf.format( new Date(now)) );
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        today.setText( sdf.format( new Date(now) ) );
        tv.setLayoutParams(param);
        wrap.addView(tv);
        for ( int i = 0; i < nextDate.length; i++ ) {
            tv = new TextView(getContext());
            tv.setText( dayOfWeekSdf.format( new Date(nextDate[i])) );
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setLayoutParams(param);
            wrap.addView(tv);
        }
        return layout;
    }
}
