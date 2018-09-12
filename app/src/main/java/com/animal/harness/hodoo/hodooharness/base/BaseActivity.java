package com.animal.harness.hodoo.hodooharness.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.animal.harness.hodoo.hodooharness.R;

/**
 * Created by Song on 2018-09-10.
 */
public abstract class BaseActivity<D extends Activity> extends AppCompatActivity {
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
}
