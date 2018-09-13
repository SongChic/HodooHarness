package com.animal.harness.hodoo.hodooharness.activity.gps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.activity.chart.ChartActivity;
import com.animal.harness.hodoo.hodooharness.adapter.GPSCustomAdapter;
import com.animal.harness.hodoo.hodooharness.base.BaseActivity;
import com.animal.harness.hodoo.hodooharness.constant.HodooConstant;
import com.animal.harness.hodoo.hodooharness.databinding.ActivityGpsBinding;
import com.animal.harness.hodoo.hodooharness.domain.GPSData;
import com.animal.harness.hodoo.hodooharness.util.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.animal.harness.hodoo.hodooharness.constant.HodooConstant.LOCATION_DB_NAME;

public class GPSActivity extends BaseActivity<GPSActivity> {
    private final String mStatusPrefix = "상태 : ";
    private final boolean resetState = false;

    /* Data */
    private ActivityGpsBinding binding;
    private DBHelper dbHelper;
    private GPSCustomAdapter adapter;

    /* GPS */
    private LocationManager locationManager;
    private Location location;
    private GPSData data;
    private double mOldLat, mOldLon;

    /* View */
    private TextView mTotalDistance;
    private TextView mGpsStatus;
    private ScrollView databaseView;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("test", "onLocationChanged, location:" + location);
            calculation(location);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gps);
        binding.setActivity(this);
//        binding.setGps(GPSData.builder().lat(0).lon(0).build());
        init();
    }

    @Override
    protected BaseActivity<GPSActivity> getActivityClass() {
        return this;
    }

    private void init() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        dbHelper = new DBHelper(
                this,
                LOCATION_DB_NAME,
                null,
                1
        );
        if (resetState) dbHelper.resetDB();

        mTotalDistance = binding.totalDistance;
        mGpsStatus = binding.gpsStatus;
        databaseView = binding.dataView;
        startGPS();
        stopGPS();

    }
    public void startGPS () {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                HodooConstant.MIN_UPDATE_TIME,
                HodooConstant.MIN_DISTANCE,
                mLocationListener
        );
        if ( locationManager != null ) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if ( location != null ) {

                double lat = location.getLatitude(), lon = location.getLongitude();
                data = GPSData.builder()
                        .lat(lat)
                        .lon(lon)
                        .created(new Date().getTime())
                        .build();
                mOldLat = lat;
                mOldLon = lon;

                binding.setGps(data);
            }
        }
        mGpsStatus.setText(mStatusPrefix + "실행");
    }
    public void stopGPS () {
        locationManager.removeUpdates(mLocationListener);
        mGpsStatus.setText(mStatusPrefix + "중지");
    }
    public void reset() {
        mOldLat = location.getLatitude();
        mOldLon = location.getLongitude();
        data.setSum(0);
        mTotalDistance.setText("");
        dbHelper.resetDB();
    }

    public void calculation( Location location ) {
        Location oldLocation = new Location("oldPoint");
        oldLocation.setLatitude(mOldLat);
        oldLocation.setLongitude(mOldLon);
        data.setLat( location.getLatitude() );
        data.setLon( location.getLongitude() );
        data.setCreated(new Date().getTime());
        data.setSum( data.getSum() + location.distanceTo(oldLocation) );
        binding.setGps(data);

        if ( dbHelper != null && mOldLat != location.getLatitude() && mOldLon != location.getLongitude() )
            dbHelper.insertDB(data);

        mOldLat = location.getLatitude();
        mOldLon = location.getLongitude();
        mTotalDistance.setText(String.format("총 이동거리 : %2fm", data.getSum()));

    }
    public void onClick( View view ) {
        switch (view.getId()) {
            case R.id.start :
                startGPS();
                if ( dbHelper != null )
                    dbHelper.insertDB(data);
                break;
            case R.id.stop :
                stopGPS();
                adapter = null;
                break;
            case R.id.reset :
                reset();
                break;
            case R.id.view_data :
                if ( databaseView.getChildCount() > 0 )
                    databaseView.removeAllViews();
                List<GPSData> data = dbHelper.selectDB();
                Map<String, Object> sortMap = new HashMap<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

                for ( int i = 0; i < data.size(); i++ ) {
                    if ( sortMap.containsKey( sdf.format(new Date(data.get(i).getCreated())) ) ) {
                        ((List<GPSData>) sortMap.get( sdf.format(new Date(data.get(i).getCreated())) )).add(data.get(i));
                    } else {
                        List<GPSData> items = new ArrayList<>();
                        items.add(data.get(i));
                        sortMap.put(sdf.format(new Date(data.get(i).getCreated())), items);
                    }
                }

                HashMap<String, Object> reversedHashMap = new HashMap<String, Object>();
                for (String key : sortMap.keySet()){
                    reversedHashMap.put(key, sortMap.get(key));
                }

                if ( adapter == null )
                    adapter = new GPSCustomAdapter(this, reversedHashMap, databaseView);
                else
                    adapter.reset(databaseView);
                break;
            case R.id.move :
                startActivity(new Intent(this, ChartActivity.class));
                break;
        }
    }

}
