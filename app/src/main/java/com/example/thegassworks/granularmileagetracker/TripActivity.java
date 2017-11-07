package com.example.thegassworks.granularmileagetracker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thegassworks.granularmileagetracker.database.MyContentProvider;
import com.example.thegassworks.granularmileagetracker.database.TableTrip;

/**
 * Created by benjamin on 8/30/17.
 */

public class TripActivity extends ClientActivity {

    private String action;
    //    private static final int CHILD_REQUEST_CODE = 1001;
//    private static final int NEW_REQUEST_CODE = 1002;
//    private CursorAdapter cursorAdapter;
    protected Uri itemUri;
    protected String repoId;
    protected String repoTitle;
    protected String repoTableName;
    protected String repoTitleLocation;
    protected String whereClause;
    protected String oldTitle;
    protected String oldMiles;
    protected String oldNotes;
    protected EditText tripTitleField;
    protected EditText tripMilesField;
    protected EditText tripNotesField;
    protected EditText startMilesField;
    protected EditText endMilesField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        //get layout elements
        tripTitleField = (EditText) findViewById(R.id.tripTitleField);
        tripMilesField = (EditText) findViewById(R.id.milesField);
        tripNotesField = (EditText) findViewById(R.id.notesField);
        startMilesField = (EditText) findViewById(R.id.mileStartField);
        endMilesField = (EditText) findViewById(R.id.mileEndField);

        //set instance values for this element
        repoId = intent.getStringExtra(TableTrip.ID);
        repoTitleLocation = TableTrip.TITLE;  //get reference to
        repoTitle = intent.getStringExtra(TableTrip.TITLE); //Get title of this item from intent
        repoTableName = TableTrip.TABLE_NAME;
        itemUri = intent.getParcelableExtra(repoTableName);

        Intent intent = getIntent();
        itemUri = intent.getParcelableExtra(TableTrip.TABLE_NAME);
        Log.d(this.getLocalClassName(), "itemUri: " + itemUri);
        if (itemUri == null) {
            action = Intent.ACTION_INSERT;
            //Set old values as default
            oldTitle = (String) getResources().getText(R.string.name_here);
            oldMiles = (String) getResources().getText(R.string.zero);
            oldNotes = (String) getResources().getText(R.string.notes);
            //Set values for various fields
            tripTitleField.setText(oldTitle);
            tripMilesField.setText(oldMiles);
            tripNotesField.setText(oldNotes);
            DatabaseUtils.dumpCursor(getContentResolver().query(MyContentProvider.TRIP_URI, TableTrip.COLUMNS, "", null, null));
//            itemUri = MyContentProvider.CLIENT_URI;
        } else {
            Log.d(this.getLocalClassName(), "itemUri trimmed: " + itemUri.getLastPathSegment());
            action = Intent.ACTION_EDIT;
            whereClause = TableTrip.ID + "=" + itemUri.getLastPathSegment();

            //Get item values


            try {
                Cursor cursor = getContentResolver().query(itemUri, TableTrip.COLUMNS, "", null, null);
                cursor.moveToFirst();
                //Get existing values
                oldTitle = cursor.getString(cursor.getColumnIndex(TableTrip.TITLE));
                oldMiles = cursor.getString(cursor.getColumnIndex(TableTrip.MILES));
                oldNotes = cursor.getString(cursor.getColumnIndex(TableTrip.NOTES));
                cursor.close();
            } catch (NullPointerException e) {
                Toast.makeText(this, R.string.trip_record_not_found, Toast.LENGTH_SHORT).show();
            }

            //Set values for various fields
            tripTitleField.setText(oldTitle);
            tripMilesField.setText(oldMiles);
            tripMilesField.setText(oldNotes);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //    if (action.equals(Intent.ACTION_EDIT)){
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        //    }
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
//        int id=item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteItem();
                break;
        }
        return true;
    }

    private void deleteItem() {
        getContentResolver().delete(itemUri, whereClause, null);
        Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
        Intent deleteIntent = new Intent();
        deleteIntent.putExtra("itemDeleted", true);
        setResult(RESULT_OK, deleteIntent);
        finish();
    }

    private void finishEditing() {
        String newTitle = tripTitleField.getText().toString().trim();
        String newMiles = tripMilesField.getText().toString().trim();
        String newNotes = tripNotesField.getText().toString().trim();
        Log.d(this.getLocalClassName() + " fishishEdit", "oldTitle: " + oldTitle + " oldMiles: " + oldMiles);
        Log.d(this.getLocalClassName() + " fishishEdit", "newTitle: " + newTitle + " newMiles: " + newMiles);
        if (oldMiles.equals(newMiles) && oldTitle.equals(newTitle) && oldNotes.equals(newNotes)) {
            finish();
        } else {
//            addUpdateItem(newTitle, newMiles, newNotes, (itemUri != null));
            try {
                if (itemUri == null) {
                    addItem(getContentValues());
                } else {
                    updateItem(getContentValues());
                }
            } catch (NumberFormatException e){
                Toast.makeText(this, R.string.number_format_problem, Toast.LENGTH_SHORT).show();
                Log.d(this.getLocalClassName(), e.getLocalizedMessage());
                return;
            }
        }
        finish();
    }
//    private void addUpdateItem(String title, String miles, String notes, Boolean update) {
//        ContentValues values = new ContentValues();
//        Intent addUpdateIntent = new Intent();
//        values.put(TableTrip.TITLE, title);
//        values.put(TableTrip.MILES, miles);
//        values.put(TableTrip.NOTES, notes);
//        values.put(TableTrip.UPDATE, "");
//        Log.d(this.getLocalClassName(), "values: "+ values.toString());
//        Log.d(this.getLocalClassName(), "add/update itemUri: "+ itemUri + " whereClause: " + whereClause);
//        if (update) {
//            Log.d(this.getLocalClassName(), "updating item");
//            getContentResolver().update(itemUri, values, whereClause, null);
//            Toast.makeText(this, R.string.itemUpdated, Toast.LENGTH_SHORT).show();
//            addUpdateIntent.putExtra("newTitle", title);
//        } else {
//            Log.d(this.getLocalClassName(), "adding item");
//            itemUri = MyContentProvider.TRIP_URI;
//            Uri tempUri = getContentResolver().insert(itemUri, values);
//            addUpdateIntent.putExtra("newUri", tempUri);
//            Log.d(this.getLocalClassName(), "added Uri tempUri: " + tempUri);
//        }
//
//        setResult(RESULT_OK, addUpdateIntent);
//    }

    private int updateItem(ContentValues values) {
        int updateId = getContentResolver().update(itemUri, values, whereClause, null);
        Toast.makeText(this, R.string.itemUpdated, Toast.LENGTH_SHORT).show();
        return updateId;
    }

    private Uri addItem(ContentValues values) {
        values.put(TableTrip.CREATE, "");
        return getContentResolver().insert(MyContentProvider.TRIP_URI, values);
    }

    private ContentValues getContentValues() throws NumberFormatException {
        String title = tripTitleField.getText().toString().trim();
        String miles = tripMilesField.getText().toString().trim();
        String notes = tripNotesField.getText().toString().trim();

        title = (title.equals("")) ? "TripName" : title;
        miles = (miles.equals("")) ? "0" : miles;
        notes = (notes.equals("")) ? " " : notes;

        int milesInt = Integer.parseInt(miles);
        Log.d(this.getLocalClassName(), "title: " + title + " miles: " + miles + " notes: " + notes);

        ContentValues values = new ContentValues();
        values.put(TableTrip.TITLE, title);
        values.put(TableTrip.MILES, milesInt);
        values.put(TableTrip.NOTES, notes);
        values.put(TableTrip.UPDATE, "");
        return values;
    }

    public void calcMiles(View calcButton) {
        String mileStartText;
        String mileEndText;
        mileStartText = startMilesField.getText().toString().trim();
        mileEndText = endMilesField.getText().toString().trim();
        try {
            Long mileDifference = Long.parseLong(mileEndText) - Long.parseLong(mileStartText);
            if (mileDifference < 0) {
                Toast.makeText(this, R.string.error_start_less_end, Toast.LENGTH_SHORT).show();
            } else {
                tripMilesField.setText(mileDifference.toString());
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.number_format_problem, Toast.LENGTH_SHORT).show();
            Log.d(this.getLocalClassName(), e.getLocalizedMessage());
        }

    }
}
