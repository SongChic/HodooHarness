package com.animal.harness.hodoo.hodooharness.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        long now = getArguments().getLong("now");
        nextDate = preDate = new long[3];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat dayOfWeekSdf = new SimpleDateFormat("E");

//        Log.e("TAG", sdf.format( new Date(getArguments().getLong("now"))));

        long tempDate = 0;
        int switchVal = -1;
        for ( int i = 0; i < 2; i++ ) {
            for ( int j = 1; j <= 3; j++) {
                tempDate = now + (24 * 60 * 60 * (j * switchVal) * 1000);
                Log.e("TAG", sdf.format( new Date(tempDate)));

                TextView tv = new TextView(getContext());
                tv.setText( dayOfWeekSdf.format( new Date(tempDate)) );

                wrap.addView(tv);

                if ( switchVal > 0 )
                    preDate[j - 1] = tempDate;
                else
                    nextDate[j - 1] = tempDate;
            }

            if ( switchVal < 0 ) {
                TextView tv = new TextView(getContext());
                tv.setText( dayOfWeekSdf.format( new Date(now)) );

                wrap.addView(tv);
            }
            switchVal = 1;

        }



        return layout;
//        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
