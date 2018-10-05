package com.animal.harness.hodoo.hodooharness.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.constant.HodooConstant;
import com.animal.harness.hodoo.hodooharness.domain.GPSData;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private Context mContext;
    private String mName;
    private final String TAG = DBHelper.class.getSimpleName();
    public DBHelper(Context context) {
        this(context, HodooConstant.LOCATION_DB_NAME, null, HodooConstant.DATABASE_VERSION);
    }
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
        mName = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer sb = new StringBuffer();
        sb.append(" CREATE TABLE " + mName + " (");
        sb.append(" ID INTEGER PRIMARY KEY AUTOINCREMENT, "); //인덱스
        sb.append(" CREATED DATETIME, "); //작성일
        sb.append(" TOTAL_TIME INTEGER, "); //총 이동시간
        sb.append(" TOTAL_DISTANCE INTEGER ); "); //총 이동거리
        db.execSQL(sb.toString());
        Log.e(TAG, "데이터베이스 생성 완료");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insertDB (GPSData data) {
        SQLiteDatabase db = getWritableDatabase();
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO " + mName);
        sb.append(" (TOTAL_TIME, CREATED, TOTAL_DISTANCE )");
        sb.append(" VALUES (?, ?, ?)");
        db.execSQL(
                sb.toString(),
                new Object[]{
                      data.getTotal_time(),
                      data.getCreated(),
                      data.getSum()
                }
        );
    }
    public List<GPSData> selectDB () {
        SQLiteDatabase db = getReadableDatabase();
        List<GPSData> datas = new ArrayList<>();
        String sql = "select * from " + mName;
        Cursor cursor = db.rawQuery(sql, null);
        while ( cursor.moveToNext() ) {
            GPSData data = new GPSData();
            data.setId(cursor.getInt(0) );
            data.setCreated( cursor.getLong(1) );
            data.setTotal_time(cursor.getLong(2));
            data.setSum( cursor.getDouble(3) );
            datas.add(data);
        }
        return datas;
    }
    public List<GPSData> selectDBForWhere ( String where ) {
        String sql = mContext.getResources().getString(R.string.base_select) + " " + mContext.getResources().getString(R.string.base_db_name) + " where " + where;
        SQLiteDatabase db = getReadableDatabase();
        List<GPSData> datas = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        while ( cursor.moveToNext() ) {
            GPSData data = new GPSData();
            data.setId(cursor.getInt(0) );
            data.setCreated( cursor.getLong(1) );
            data.setTotal_time(cursor.getLong(2));
            data.setSum( cursor.getDouble(3) );
            datas.add(data);
        }
        return datas;
    }
    public void resetDB() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("drop table  " + mName);
        Log.e(TAG, "데이터베이스 삭제 완료");
    }
}
