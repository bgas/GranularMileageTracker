package com.example.thegassworks.granularmileagetracker.database;

/**
 * Created by Ben on 5/17/17.
 */

public class TableTrip {
    //Constants for identifying table and columns
    public static final String TABLE_NAME = "trip";
    public static final String ID = "_id";
    public static final String TITLE = "tripName";
    public static final String MILES = "miles";
    public static final String NOTES = "notes";
    public static final String CREATE = "Created";
    public static final String UPDATE = "Updated";
    public static final String CLIENT_ID = "client_id";

    public static final String[] COLUMNS =
            {ID, TITLE, MILES, NOTES, CREATE, UPDATE, CLIENT_ID};

    //SQL to create table
    public static final String createTable() {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE + " TEXT, " +
                MILES + " INTEGER, " +
                NOTES + " TEXT, " +
                CREATE + " TEXT default CURRENT_TIMESTAMP," +
                UPDATE + " TEXT default CURRENT_TIMESTAMP," +
                CLIENT_ID + " INTEGER " +
                ")";
    }
}
