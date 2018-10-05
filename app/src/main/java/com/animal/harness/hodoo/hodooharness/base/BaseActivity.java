package com.animal.harness.hodoo.hodooharness.base;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.animal.harness.hodoo.hodooharness.R;

/**
 * Created by Song on 2018-09-10.
 */
public abstract class BaseActivity<D extends Activity> extends AppCompatActivity {
    protected Menu menu;
    protected final String TAG = getClass().getSimpleName();
    protected abstract BaseActivity<D> getActivityClass();

    public interface CustomCallback {
        void onClick(DialogInterface dialog, int which);
    }
    public AlertDialog.Builder showAlertDialog(String title, String content, int cancelStr) {
        AlertDialog.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivityClass())
                    .setTitle(title)
                    .setNegativeButton(cancelStr, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setMessage(content);
        }
        return builder;
    }
    public void setTitleBar( String titleStr ) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "back btn touch");
            }
        });
        TextView title = findViewById(R.id.title_str);
        title.setText(titleStr);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        setSupportActionBar(toolbar);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        for ( int i = 0; i < menu.size(); i++ ) {
            Drawable d = menu.getItem(i).getIcon();
//            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
//            Drawable dr = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 57, true));
//            menu.getItem(i).setIcon(dr);
            if ( d != null ) {
                d.mutate();
                if ( i == 0 & checkGPS() ) {
                    d.setColorFilter(getResources().getColor(R.color.hodoo_menu_active), PorterDuff.Mode.SRC_ATOP);
                } else {
                    d.setColorFilter(getResources().getColor(R.color.hodoo_menu_default), PorterDuff.Mode.SRC_ATOP);
                }

            }
        }
        return true;
    }
    public boolean checkGPS() {
       return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }
}
