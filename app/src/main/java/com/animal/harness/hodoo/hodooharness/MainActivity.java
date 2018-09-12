package com.animal.harness.hodoo.hodooharness;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.animal.harness.hodoo.hodooharness.activity.GPSActivity;
import com.animal.harness.hodoo.hodooharness.base.BaseActivity;
import com.animal.harness.hodoo.hodooharness.util.DBHelper;

import static com.animal.harness.hodoo.hodooharness.constant.HodooConstant.DEBUG;
import static com.animal.harness.hodoo.hodooharness.constant.HodooConstant.LOCATION_DB_NAME;
import static com.animal.harness.hodoo.hodooharness.constant.HodooConstant.PERMISSIONS;
import static com.animal.harness.hodoo.hodooharness.constant.HodooConstant.PERMISSION_REQUEST_CODE;

public class MainActivity extends BaseActivity<MainActivity> {
    String[] permissionsInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionsInfo = getResources().getStringArray(R.array.permissions_info);
        permissionCheck();
    }

    private void permissionCheck () {
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
                    DBHelper dbHelper = new DBHelper(
                            MainActivity.this,
                            LOCATION_DB_NAME,
                            null,
                            1
                    );
                    dbHelper.getReadableDatabase();     //DB를 읽기전용으로 불러온다.
                    dbHelper.close();
                    startActivity(new Intent(this, GPSActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, permissions[i] + " permission denied.", Toast.LENGTH_LONG).show();
                    super.showAlertDialog("권한을 허용해주세요.", permissionsInfo[i], R.string.cancle)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    for (int i = 0; i < permissions.length; i++) {
                                        if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, PERMISSIONS[i])) {
                                            /* 데이터베이스 생성 */
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                                    .setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                                            startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                                        } else
                                            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_REQUEST_CODE);
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
    protected BaseActivity<MainActivity> getActivityClass() {
        return this;
    }
}
