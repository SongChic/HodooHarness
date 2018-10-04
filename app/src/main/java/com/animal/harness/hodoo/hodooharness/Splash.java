package com.animal.harness.hodoo.hodooharness;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.animal.harness.hodoo.hodooharness.activity.gps.GPSActivity;
import com.animal.harness.hodoo.hodooharness.base.BaseActivity;
import com.animal.harness.hodoo.hodooharness.util.DBHelper;

import static com.animal.harness.hodoo.hodooharness.constant.HodooConstant.LOCATION_DB_NAME;
import static com.animal.harness.hodoo.hodooharness.constant.HodooConstant.PERMISSIONS;
import static com.animal.harness.hodoo.hodooharness.constant.HodooConstant.PERMISSION_REQUEST_CODE;

public class Splash extends BaseActivity<Splash> {

    String[] permissionsInfo;

    String gpsEnabled;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        permissionsInfo = getResources().getStringArray(R.array.permissions_info);
        permissionCheck();
    }

    private void permissionCheck () {
//        chkGpsService();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            for ( int i = 0; i < PERMISSIONS.length; i++ )
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSIONS[i]))
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
                else
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(Splash.this, MainActivity.class));
                            finish();
                        }
                    }, 5000);
                } else {
                    Toast.makeText(this, permissions[i] + " permission denied.", Toast.LENGTH_LONG).show();
                    super.showAlertDialog("권한을 허용해주세요.", permissionsInfo[i], R.string.cancle)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    for (int i = 0; i < permissions.length; i++) {
                                        if (!ActivityCompat.shouldShowRequestPermissionRationale(Splash.this, PERMISSIONS[i])) {
                                            /* 데이터베이스 생성 */
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                                    .setData(Uri.parse("package:" + Splash.this.getPackageName()));
                                            startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                                        } else
                                            ActivityCompat.requestPermissions(Splash.this, PERMISSIONS, PERMISSION_REQUEST_CODE);
                                    }
                                }
                            })
                            .show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == PERMISSION_REQUEST_CODE ) permissionCheck();
    }

    @Override
    protected BaseActivity<Splash> getActivityClass() {
        return this;
    }

    private boolean chkGpsService() {

        //GPS가 켜져 있는지 확인함.
        gpsEnabled = android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!(gpsEnabled.matches(".*gps.*") && gpsEnabled.matches(".*network.*"))) {
            //gps가 사용가능한 상태가 아니면
            new AlertDialog.Builder(this).setTitle("GPS 설정").setMessage("GPS가 꺼져 있습니다. \nGPS를 활성화 하시겠습니까?").setPositiveButton("GPS 켜기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    //GPS 설정 화면을 띄움
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }).setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).create().show();

        }else if((gpsEnabled.matches(".*gps.*") && gpsEnabled.matches(".*network.*"))) {
            Toast.makeText(getApplicationContext(), "정보를 읽어오는 중입니다.", Toast.LENGTH_LONG).show();
//            intent = new Intent(this, CurrentLocatinActivity.class); //현재 위치 화면 띄우기 위해 인텐트 실행.
//            startActivity(intent);
        }
        return false;
    }
}
