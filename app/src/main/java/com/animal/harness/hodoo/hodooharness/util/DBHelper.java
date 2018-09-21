package com.animal.harness.hodoo.hodooharness.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.animal.harness.hodoo.hodooharness.domain.GPSData;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private Context mContext;
    private String mName;
    private final String TAG = DBHelper.class.getSimpleName();
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
        mName = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer sb = new StringBuffer();
        sb.append(" CREATE TABLE " + mName + " (");
        sb.append(" ID INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(" LAT INTEGER, ");
        sb.append(" LON INTEGER, ");
        sb.append(" CREATED DATETIME, ");
        sb.append(" TOTAL_DISTANCE INTEGER ); ");
        db.execSQL(sb.toString());
        Log.e(TAG, "데이터베이스 생성 완료");
        Toast.makeText(mContext, "데이터베이스 생성 완료", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insertDB (GPSData data) {
        SQLiteDatabase db = getWritableDatabase();
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO " + mName);
        sb.append(" (LAT, LON, CREATED, TOTAL_DISTANCE )");
        sb.append(" VALUES (?, ?, ?, ?)");
        db.execSQL(
                sb.toString(),
                new Object[]{
                      data.getLat(),
                      data.getLon(),
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
            data.setLat( cursor.getDouble(1) );
            data.setLon( cursor.getDouble(2) );
            data.setCreated( cursor.getLong(3) );
            data.setSum( cursor.getDouble(4) );
            datas.add(data);
        }
        return datas;
    }
    public void resetDB() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("drop table  " + mName);
    }
}
