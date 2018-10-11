package com.animal.harness.hodoo.hodooharness.fragment;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.TransitionDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.text.style.LeadingMarginSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.base.BaseFragment;
import com.animal.harness.hodoo.hodooharness.constant.HodooConstant;
import com.animal.harness.hodoo.hodooharness.domain.GPSData;
import com.animal.harness.hodoo.hodooharness.service.StopWatchService;
import com.animal.harness.hodoo.hodooharness.util.DBHelper;
import com.animal.harness.hodoo.hodooharness.util.HodooUtil;
import com.animal.harness.hodoo.hodooharness.util.TestUtil;
import com.animal.harness.hodoo.hodooharness.view.StopWatch;

import java.util.Date;

import static com.animal.harness.hodoo.hodooharness.constant.HodooConstant.LOCATION_DB_NAME;

public class ActivityFragment extends BaseFragment implements View.OnClickListener {
    private StopWatch stopWatch;
    private Button stopWatchStart;
    private Button stopWatchReset;
    private TextView tipsIcon;
    private TextView tipsContent;
    private int count = 0;
    private boolean restartState = false;
    private DBHelper helper;

    /* GPS */
    private int gpsCheckState = 2000;
    private LocationManager locationManager;
    private Location location;
    private GPSData data;
    private long moveTime = 0;
    private double mOldLat, mOldLon;
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("test", "onLocationChanged, location:" + location);
            calculation(location);
            if ( data != null && stopWatch.getTotalDistance() != data.getSum() )
                stopWatch.setTotalDistance(data.getSum());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, provider);
        }
    };

    @SuppressLint("ObjectAnimatorBinding") ObjectAnimator backgroundColorAnimator;

    private String[] tipsStr = {
            "산책용 목줄을 매면 주저앉는 경우에는 강제로 데려가지 마시고 실내에서 익숙하게 한 후에 서서히 외부로 나오게 하는 것이 좋습니다.",
            "강아지가 흥분하여 제어가 안될 경우에는 목줄을 당기며 \"안돼\"를 강하게 말해 주세요.",
            "일반적으로 분리불안증을 가지고 있는 반려견의 경우에 가장 좋은 처방약은 바로 산책입니다.",
    };

    /* Service */
    private StopWatchService mService;

    private RelativeLayout wrap;

    float one = 0;
    float two = 0;
    float three = 0;

    public ActivityFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //stop_watch
        if ( wrap != null ) {
            return wrap;
        } else {
            wrap = (RelativeLayout) inflater.inflate(R.layout.fragment_stopwatch, container, false);
            RelativeLayout btnWrap = wrap.findViewById(R.id.stop_watch_btn);
            tipsIcon = wrap.findViewById(R.id.tips);
            tipsContent = wrap.findViewById(R.id.tips_content);

            tipsIcon.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tipsContent.getLayoutParams();
                    params.setMargins(0, tipsIcon.getHeight() / 2, 0, 0);
                    tipsContent.setLayoutParams(params);
                    if (count > tipsStr.length - 1)
                        count = 0;

                    SpannableString ss = new SpannableString(tipsStr[count]);
                    ss.setSpan(new Margin(1, tipsIcon.getWidth() + 20), 0, ss.length(), 0);
                    tipsContent.setText(ss);
//                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, tipsContent.getMeasuredHeight());
//                ((RelativeLayout) tipsContent.getParent()).setLayoutParams(params);
                    Log.e(TAG, String.format("tipsContent height : %d", tipsContent.getHeight()));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setTipText();
                            count++;
                        }
                    }, 1000 * 30);
                    tipsIcon.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

            stopWatchStart = wrap.findViewById(R.id.stop_watch_start);
            stopWatchReset = wrap.findViewById(R.id.stop_watch_reset);
            stopWatchStart.setOnClickListener(this);
            stopWatchReset.setOnClickListener(this);
            stopWatch = wrap.findViewById(R.id.stop_watch);
            backgroundColorAnimator = ObjectAnimator.ofObject(stopWatchStart,
                    "textColor",
                    new ArgbEvaluator(),
                    Color.WHITE,
                    Color.BLACK);
            backgroundColorAnimator.setDuration(1000);
        }

        return wrap;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setTipText() {
        tipsContent.animate().alpha(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                if ( count >= tipsStr.length ) count = 0;
                SpannableString ss = new SpannableString(tipsStr[count]);
                ss.setSpan(new Margin(1, tipsIcon.getWidth() + 20), 0, ss.length(), 0);
                tipsContent.setText(ss);
                tipsContent.animate().alpha(1).setDuration(1000);
                count++;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setTipText();
                    }
                }, 1000 * 30);
            }
        }).setDuration(1000);
    }

    @Override
    public void onFragmentSelected() {

    }

    public static ActivityFragment newInstance() {
        ActivityFragment fragment = new ActivityFragment();
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(final View v) {
        final TransitionDrawable background = (TransitionDrawable) stopWatchStart.getBackground();
        switch (v.getId()) {
            case R.id.stop_watch_start :
                /* start */
                if ( !stopWatch.isStart() ) {

                    /* GPS(s) */
                    locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                    if ( locationManager != null ) {
                        /* GPS 체크 */
                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            AlertDialog dialog = new AlertDialog.Builder(getContext())
                                    .setTitle("GPS를 켜주세요")
                                    .setMessage("GPS를 켜야 해당 기능을 사용할 수 있습니다.")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                                            startActivityForResult(intent, gpsCheckState);
                                        }
                                    })
                                    .create();
                            return;
                        }
                    }
                    start();
                    /* GPS(e) */

                    stopWatchReset.setVisibility(View.VISIBLE);
                    stopWatchReset.animate().alpha(1).setDuration(1000).withLayer();
                    if ( !restartState ) {
                        v.animate().translationX( -(v.getWidth() / 2 + 50) ).setDuration(500).withLayer();
                    }

                    backgroundColorAnimator.start();
                    background.startTransition(1000);

                    ((TextView) v).setText("중지");
                    if ( stopWatch != null )
                        stopWatch.start();
                    restartState = true;
                }
                /* stop */
                else {
                    locationManager.removeUpdates(mLocationListener);
                    if ( !restartState ) {
                        stopWatchReset.animate().alpha(0).setDuration(500).withLayer().withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                stopWatchReset.setVisibility(View.GONE);
                            }
                        });
                        v.animate().translationX( 0 ).setDuration(500).withLayer();
                    }
                    stopWatch.stop();
                    background.reverseTransition(1000);
                    backgroundColorAnimator.reverse();
                    ((TextView) v).setText("시작");
                }

                break;
            case R.id.stop_watch_reset :
                /* restart */
                if ( stopWatch.isStart() ) {
                    stopWatch.stop();
                    background.reverseTransition(1000);
                    backgroundColorAnimator.reverse();
                    stopWatchStart.setText("시작");
                }
                stopWatch.reset();
//                Log.e(TAG, String.format("총 이동거리 : %2fm", data.getSum()));

                stopWatchStart.animate().translationX( 0 ).setDuration(500).withLayer();
                v.animate().alpha(0).setDuration(500).withLayer().withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.setVisibility(View.GONE);
                    }
                });
                restartState = false;
                moveTime += stopWatch.getTime();
                if ( data == null ) {
                    Toast.makeText(getContext(), "데이터 저장에 실패했습니다.\n잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    break;
                }

                data.setTotal_time(moveTime);
//                Log.e(TAG, String.format("is enabled", locationManager.isLocationEnabled()));
                if ( helper != null ) helper.insertDB(data);
                locationManager.removeUpdates(mLocationListener);
                data = null;
                break;
        }
    }
    private void reset() {

    }

    class Margin implements LeadingMarginSpan.LeadingMarginSpan2
    {
        private int margin;
        private int lines;

        Margin(int lines, int margin)
        {
            this.margin = margin;
            this.lines = lines;
        }

        @Override
        public void drawLeadingMargin(Canvas arg0, Paint arg1, int arg2,
                                      int arg3, int arg4, int arg5, int arg6, CharSequence arg7,
                                      int arg8, int arg9, boolean arg10, Layout arg11) {
        }
        @Override
        public int getLeadingMargin(boolean arg0) {
            if (arg0) {
                /*
                 * This indentation is applied to the number of rows returned
                 * getLeadingMarginLineCount ()
                 */
                return margin;
            }
            else
            {
// Offset for all other Layout layout ) { }
                /*
                 * Returns * the number of rows which should be applied * indent
                 * returned by getLeadingMargin (true) Note:* Indent only
                 * applies to N lines of the first paragraph.
                 */
                return 0;
            }
        }

        @Override
        public int getLeadingMarginLineCount()
        {
            // TODO Auto-generated method stub
            return lines;
        }

    }
    public void calculation( Location location ) {
        if ( data != null ) {
            Location oldLocation = new Location("oldPoint");
            oldLocation.setLatitude(mOldLat);
            oldLocation.setLongitude(mOldLon);
            data.setCreated(new Date().getTime());

            //            data.setSum( data.getSum() + location.distanceTo(oldLocation) );
            data.setSum( data.getSum() + TestUtil.distance(mOldLat, mOldLon, location.getLatitude(), location.getLongitude()) ); // 3안

            /* 2안 (s) */
            double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
            double dLat = Math.toRadians(location.getLatitude()-mOldLat);
            double dLng = Math.toRadians(location.getLongitude()-mOldLon);
            double sindLat = Math.sin(dLat / 2);
            double sindLng = Math.sin(dLng / 2);
            double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                    * Math.cos(Math.toRadians(location.getLatitude())) * Math.cos(Math.toRadians(mOldLat));
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double dist = earthRadius * c;

            one += location.distanceTo(oldLocation);
            two += dist;
            three += TestUtil.distance(mOldLat, mOldLon, location.getLatitude(), location.getLongitude());


            Log.e(TAG, String.format("1안 km : %f", one));
            Log.e(TAG, String.format("2안 km : %f", two));
            Log.e(TAG, String.format("3안 km : %f", three));

            /* 2안 (e) */

            mOldLat = location.getLatitude();
            mOldLon = location.getLongitude();
            Log.e(TAG, String.format("총 이동거리 : %2fm", data.getSum()));
        } else {
            data = GPSData.builder().build();
//            Toast.makeText(getContext(), "잠시 후 다시 시도 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
        }




    }
    public void start() {
        /* Database(s) */
        helper = new DBHelper(
                getContext(),
                LOCATION_DB_NAME,
                null,
                1
        );
        /* Database(e) */
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "권한이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    HodooConstant.MIN_UPDATE_TIME,
                    HodooConstant.MIN_DISTANCE,
                    mLocationListener //리스너 연결
            );
        if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    HodooConstant.MIN_UPDATE_TIME,
                    HodooConstant.MIN_DISTANCE,
                    mLocationListener //리스너 연결
            );
        if ( locationManager != null ) {
            /* GPS 체크 */
            if ( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("GPS를 켜주세요")
                        .setMessage("GPS를 켜야 해당 기능을 사용할 수 있습니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                startActivityForResult(intent, gpsCheckState);
                            }
                        })
                        .create();
            }

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if ( location != null ) {

                double lat = location.getLatitude(), lon = location.getLongitude();
                if ( data == null )
                    data = GPSData.builder()
                            .total_time(0)
                            .sum(0)
                            .created(new Date().getTime())
                            .build();
                mOldLat = lat;
                mOldLon = lon;
            }
        }
    }
    public void stop() {

    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        if ( requestCode == gpsCheckState )
            start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
