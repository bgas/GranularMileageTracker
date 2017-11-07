package com.example.thegassworks.granularmileagetracker.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.thegassworks.granularmileagetracker.database.TableTrip;
import com.example.thegassworks.granularmileagetracker.database.TableClient;

/**
 * Created by khaln on 5/11/17.
 */

public class MyContentProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.thegassworks.granularmileagetracker.database.MyContentProvider";
    public static final Uri CLIENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TableClient.TABLE_NAME);
    public static final Uri TRIP_URI = Uri.parse("content://" + AUTHORITY + "/" + TableTrip.TABLE_NAME);



    public static Uri getTableUri(String repoName) {
        Uri result = Uri.parse("content://" + AUTHORITY + "/" + repoName);
        return result;
    }

//    public static final String CONTENT_ITEM_TYPE = "term";

    // Constant to identify the requested operation
    private static final int CLIENT = 1;
    private static final int CLIENT_ID = 2;
    private static final int TRIP = 3;
    private static final int TRIP_ID = 4;


    private SQLiteDatabase database;

    // Creates a UriMatcher object.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // addURI() calls for multi row and single-row
        sUriMatcher.addURI(AUTHORITY, TableClient.TABLE_NAME, CLIENT);
        sUriMatcher.addURI(AUTHORITY, TableClient.TABLE_NAME + "/#", CLIENT_ID);
        sUriMatcher.addURI(AUTHORITY, TableTrip.TABLE_NAME, TRIP);
        sUriMatcher.addURI(AUTHORITY, TableTrip.TABLE_NAME + "/#", TRIP_ID);
    }

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //select
        String table;
        String[] columns;
        switch (sUriMatcher.match(uri)) {
            case CLIENT:
                table = TableClient.TABLE_NAME;
                columns = TableClient.COLUMNS;
                break;
            case CLIENT_ID:
                table = TableClient.TABLE_NAME;
                columns = TableClient.COLUMNS;
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;
            case TRIP:
                table = TableTrip.TABLE_NAME;
                columns = TableTrip.COLUMNS;
                break;
            case TRIP_ID:
                table = TableTrip.TABLE_NAME;
                columns = TableTrip.COLUMNS;
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;
            default: throw new SQLException("Failed to query " + uri);
        }
        //Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder
//        String columnStretch = "";
//        for (String column : columns){ columnStretch += column + ", "; }
        return database.query(table, columns, selection, null, null, null, null);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d("DBOpenHelper", "insert uri in: " + uri);
        String table;
        switch (sUriMatcher.match(uri)) {
            case CLIENT:
                table = TableClient.TABLE_NAME;
                break;
            case TRIP:
                table = TableTrip.TABLE_NAME;
                break;
            default: throw new SQLException("Failed to insert " + uri);
        }

        long id = database.insert(table, null, values);
        return Uri.parse(table + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String table;
        Log.d(this.getClass().toString(), "delete uri: "+uri+" selection: "+selection+" selectionArgs: "+ selectionArgs );
        Log.d(this.getClass().toString(), "urimatcher result: " + sUriMatcher.match(uri));
        switch (sUriMatcher.match(uri)) {
            case CLIENT_ID:
                table = TableClient.TABLE_NAME;
                break;
            case TRIP_ID:
                table = TableTrip.TABLE_NAME;
                break;
            default: throw new SQLException("Failed to delete " + uri);
        }
        return database.delete(table, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String table;
        Log.d(this.getClass().toString(), "uri: " + uri + " values " + values + " selection " + selection + " selectionArgs: " + selectionArgs);
        switch (sUriMatcher.match(uri)) {
            case CLIENT_ID:
                table = TableClient.TABLE_NAME;
                break;
            case TRIP_ID:
                table = TableTrip.TABLE_NAME;
                break;
            default: throw new SQLException("Failed to update: " + uri +" case: "+sUriMatcher.match(uri));
        }
        return database.update(table, values, selection, selectionArgs);
    }
}
