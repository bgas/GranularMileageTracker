package com.example.thegassworks.granularmileagetracker.database;

/**
 * Created by khaln on 5/20/17.
 */

public class TableClient {

    //Constants for identifying table and columns
    public static final String TABLE_NAME = "client";
    public static final String ID = "_id";
    public static final String TITLE = "clientName";
    public static final String MILES = "miles";
    public static final String CREATE = "Created";
    public static final String UPDATE = "Updated";

    public static final String[] COLUMNS =
            {ID, TITLE, MILES, CREATE, UPDATE};

    //SQL to create table
    public static final String createTable() {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE + " TEXT, " +
                MILES + " INTEGER, " +
                CREATE + " TEXT default CURRENT_TIMESTAMP," +
                UPDATE + " TEXT default CURRENT_TIMESTAMP" +
                ")";
    }

}
