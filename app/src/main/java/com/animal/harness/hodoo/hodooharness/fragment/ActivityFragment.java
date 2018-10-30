package com.animal.harness.hodoo.hodooharness.fragment;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.TransitionDrawable;
import android.location.GpsSatellite;
import android.location.GpsStatus;
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
import android.text.Layout;
import android.text.SpannableString;
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
import com.animal.harness.hodoo.hodooharness.view.StopWatch;

import java.util.Date;
import java.util.Iterator;

import static com.animal.harness.hodoo.hodooharness.constant.HodooConstant.LOCATION_DB_NAME;

public class ActivityFragment extends BaseFragment implements View.OnClickListener, Runnable {
    private StopWatch stopWatch;
    private Button stopWatchStart, stopWatchReset;
    private TextView tipsIcon, tipsContent, infoText;
    private int count = 0, j = 0;
    private boolean restartState = false;
    private DBHelper helper;

    /* GPS */
    private int gpsCheckState = 2000;
    private LocationManager locationManager;
    private Location location, oldLocation, bestLocation;
    private GPSData data;
    private long moveTime = 0;
    private Thread checkThread;
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("test", "onLocationChanged, location:" + location);
            calculation(location);
            if (data != null && stopWatch.getTotalDistance() != data.getSum())
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

    @SuppressLint("ObjectAnimatorBinding")
    ObjectAnimator backgroundColorAnimator;

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

    public ActivityFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //stop_watch
        init();
        if (wrap != null) {
            return wrap;
        } else {
            wrap = (RelativeLayout) inflater.inflate(R.layout.fragment_stopwatch, container, false);
            RelativeLayout btnWrap = wrap.findViewById(R.id.stop_watch_btn);
            tipsIcon = wrap.findViewById(R.id.tips);
            tipsContent = wrap.findViewById(R.id.tips_content);
            infoText = wrap.findViewById(R.id.info_text);

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

    private void init() {
        if (locationManager == null)
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setTipText() {
        tipsContent.animate().alpha(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                if (count >= tipsStr.length) count = 0;
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

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(final View v) {
        final TransitionDrawable background = (TransitionDrawable) stopWatchStart.getBackground();
        switch (v.getId()) {
            case R.id.stop_watch_start:
                /* start */
                if (!stopWatch.isStart()) {
                    if (!HodooUtil.getGPSState(getContext())) {
                        HodooUtil.GPSCheck(getContext(), new HodooUtil.AlertCallback() {
                            @Override
                            public void setNegativeButton(DialogInterface dialog, int which) {

                            }

                            @Override
                            public void setPositiveButton(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, HodooConstant.GPS_SETTING_REQUEST_CODE);
                                dialog.cancel();
                            }
                        });
                    } else {
                        start();
                        if (data == null) {
                            Toast.makeText(getContext(), "데이터 초기화에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        stopWatchReset.setVisibility(View.VISIBLE);
                        stopWatchReset.animate().alpha(1).setDuration(1000).withLayer();
                        if (!restartState) {
                            v.animate().translationX(-(v.getWidth() / 2 + 50)).setDuration(500).withLayer();
                        }

                        backgroundColorAnimator.start();
                        background.startTransition(1000);

                        ((TextView) v).setText("중지");
                        if (stopWatch != null)
                            stopWatch.start();
                        restartState = true;
                    }
                }
                /* stop */
                else {
                    locationManager.removeUpdates(mLocationListener);
                    if (!restartState) {
                        stopWatchReset.animate().alpha(0).setDuration(500).withLayer().withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                stopWatchReset.setVisibility(View.GONE);
                            }
                        });
                        v.animate().translationX(0).setDuration(500).withLayer();
                    }
                    stopWatch.stop();
                    background.reverseTransition(1000);
                    backgroundColorAnimator.reverse();
                    ((TextView) v).setText("시작");
                    oldLocation = null;
                }

                break;
            case R.id.stop_watch_reset:
                /* restart */
                if (stopWatch.isStart()) {
                    stopWatch.stop();
                    background.reverseTransition(1000);
                    backgroundColorAnimator.reverse();
                    stopWatchStart.setText("시작");
                }
                stopWatch.reset();
//                Log.e(TAG, String.format("총 이동거리 : %2fm", data.getSum()));

                stopWatchStart.animate().translationX(0).setDuration(500).withLayer();
                v.animate().alpha(0).setDuration(500).withLayer().withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.setVisibility(View.GONE);
                    }
                });
                restartState = false;
                moveTime += stopWatch.getTime();
                if (data == null) {
                    Toast.makeText(getContext(), "데이터 저장에 실패했습니다.\n잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    break;
                }

                data.setTotal_time(moveTime);
//                Log.e(TAG, String.format("is enabled", locationManager.isLocationEnabled()));
                if (helper != null) helper.insertDB(data);
                locationManager.removeUpdates(mLocationListener);
                data = null;
                break;
        }
    }

    private void reset() {

    }

    @Override
    public void run() {
        while (location == null) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            HodooConstant.MIN_UPDATE_TIME,
                            HodooConstant.MIN_DISTANCE,
                            mLocationListener //리스너 연결
                    );
                infoText.setText("ON");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Margin implements LeadingMarginSpan.LeadingMarginSpan2 {
        private int margin;
        private int lines;

        Margin(int lines, int margin) {
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
            } else {
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
        public int getLeadingMarginLineCount() {
            // TODO Auto-generated method stub
            return lines;
        }

    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    @SuppressLint("MissingPermission")
    public void calculation(Location location) {
        if (data != null) {
            if (isBetterLocation(location, oldLocation)) {
                Log.e(TAG, String.format( "gps count : %d", getGpsSatelliteCount() ));
                if (oldLocation != null) {
                    data.setCreated(new Date().getTime());
                    data.setSum(data.getSum() + location.distanceTo(oldLocation));
                }
                oldLocation = location;
            }


        } else {
            data = GPSData.builder().build();
//            Toast.makeText(getContext(), "잠시 후 다시 시도 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        helper = new DBHelper(
                getContext(),
                LOCATION_DB_NAME,
                null,
                1
        );
    }

    public void start() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(
                    getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
                return;
            }
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                Log.e(TAG, String.format("신뢰도 : %f", location.getAccuracy()));
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
                if (data == null)
                    data = GPSData.builder()
                            .total_time(0)
                            .sum(0)
                            .created(new Date().getTime())
                            .build();
                infoText.setText("ON");
            } else {
                if (data == null)
                    data = GPSData.builder()
                            .total_time(0)
                            .sum(0)
                            .created(new Date().getTime())
                            .build();
                checkThread = new Thread(this);
                checkThread.start();
                infoText.setText("OFF");
                return;
            }
        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // 기존의 위치 정보가 없다면 새로운게 좋은것이다.
            return true;
        }
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;
        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        } // 정확성을 가져와서 비교
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private int getGpsSatelliteCount() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return 0;
        }
        final GpsStatus gs = locationManager.getGpsStatus(null);
        int i = 0;
        final Iterator<GpsSatellite> it = gs.getSatellites().iterator();
        while(it.hasNext()) {
            GpsSatellite satellite = it.next();
            // [수정 : 2013/10/25]
            // 단순 위성 갯수가 아니라 사용할 수 있게 잡히는 위성의 갯수가 중요하다.
            if (satellite.usedInFix()) {
                j++; // i 값 보다는 이 값이 GPS 위성 사용 여부를 확인하는데 더 중요하다.
            }
            i++;
        }
        return j;
    }

}
