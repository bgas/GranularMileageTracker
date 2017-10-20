package com.example.thegassworks.granularmileagetracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.thegassworks.granularmileagetracker.database.TableClient;
import com.example.thegassworks.granularmileagetracker.database.TableTrip;

/**
 * Created by khaln on 5/11/17.
 */

public class DBOpenHelper extends SQLiteOpenHelper {
    //Constants for db name and version
    private static final String DATABASE_NAME = "mileageTracker.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = DBOpenHelper.class.getSimpleName();

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
        db.execSQL(TableClient.createTable());
        db.execSQL(TableTrip.createTable());
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + TableTrip.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableClient.TABLE_NAME);
        onCreate(db);
    }
}
