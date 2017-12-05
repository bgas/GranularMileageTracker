package com.example.thegassworks.granularmileagetracker;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thegassworks.granularmileagetracker.database.MyContentProvider;
import com.example.thegassworks.granularmileagetracker.database.TableClient;
import com.example.thegassworks.granularmileagetracker.database.TableTrip;

/**
 * Created by benjamin on 8/30/17.
 */

//TODO this is updating properly, remove this after next commit

public class ClientActivity extends MainActivity {
    private String action;
    private static final int CHILD_REQUEST_CODE = 1001;
    private static final int NEW_REQUEST_CODE = 1002;
    private CursorAdapter cursorAdapter;
    protected Uri itemUri;
    protected String repoId;
    protected String repoTitle;
    protected String repoTableName;
    protected String repoTitleLocation;
    protected String childRepoTableName;
    protected String whereClause;
    protected String oldTitle;
    protected String oldMiles;
    private int clientId;
    protected EditText clientNameDisplay;
    protected TextView totalMilesDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        Log.d(this.getLocalClassName(), "OnCreate Ran ");

        //get layout elements
        clientNameDisplay = (EditText) findViewById(R.id.textEditClientNameDisplay);
        totalMilesDisplay = (TextView) findViewById(R.id.textViewMilesDisplay);

        //set instance values for this element
        repoTableName = TableClient.TABLE_NAME;
        repoTitleLocation = TableClient.TITLE;

        //get identity variables passes from parent. saves a call to DB
        repoId = intent.getStringExtra(TableClient.ID);
        repoTitle = intent.getStringExtra(TableClient.TITLE);
        itemUri = getIntent().getParcelableExtra(repoTableName);

        //Set child intent values
        childRepoTitle = TableTrip.TITLE;
        childRepoID = TableTrip.ID;
        childRepoTableName = TableTrip.TABLE_NAME;
        childUri = MyContentProvider.TRIP_URI;
        childClass = TripActivity.class;
        childIntent = new Intent(this, childClass);

        //Set Cursor Adapter
        String[] from = {childRepoTitle, TableTrip.MILES, childRepoID};
//        int[] to = {R.id.textViewItem};
        int[] to = {R.id.textViewItem, R.id.textViewSubItem};
        cursorAdapter = new ManagerCursorAdapter(this, R.layout.child_list_item, null, from, to, 0);

        //Set list
        list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        //Set list-item listener to open item on click
        list.setOnItemClickListener(getChildActionClickListener());

        //Set floating action button to create new element on click
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(new Intent(childIntent), NEW_REQUEST_CODE);
            }
        });

        Log.d(this.getLocalClassName(), "itemUri: " + itemUri);
        if (itemUri == null) {
            action = Intent.ACTION_INSERT;
            //Set old values as default
            oldTitle = (String) getResources().getText(R.string.name_here);
            oldMiles = (String) getResources().getText(R.string.zero);
            clientId = 0;
            //Set values for various fields
            clientNameDisplay.setText(oldTitle);
            totalMilesDisplay.setText(oldMiles);
        } else {
            Log.d(this.getLocalClassName(), "itemUri trimmed: " + itemUri.getLastPathSegment());
            action = Intent.ACTION_EDIT;
            whereClause = TableClient.ID + "=" + itemUri.getLastPathSegment();

            //Get item values
            Cursor cursor = getContentResolver().query(itemUri, TableClient.COLUMNS, "", null, null);
            if (cursor.moveToFirst()){
                oldTitle = cursor.getString(cursor.getColumnIndex(TableClient.TITLE));
                oldMiles = cursor.getString(cursor.getColumnIndex(TableClient.MILES));
                clientId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(TableClient.ID)));
            } else {
                Toast.makeText(this, R.string.record_not_found, Toast.LENGTH_SHORT).show();
            }
            cursor.close();

            //Set values for various fields
            clientNameDisplay.setText(oldTitle);
            totalMilesDisplay.setText(oldMiles);
        }
//        LoaderManager loaderManager = getLoaderManager();
//        Loader loader = loaderManager.initLoader(0, null, this);
        getLoaderManager().initLoader(0, null, this);

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
        if (!getHasChildren()) {
            //            content://com.example.thegassworks.granularmileagetracker.database.MyContentProvider/client
            getContentResolver().delete(itemUri, whereClause, null);
            Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
            Intent deleteIntent = new Intent();
            deleteIntent.putExtra("itemDeleted", true);
            setResult(RESULT_OK, deleteIntent);
            finish();
        } else {
            Toast.makeText(this, R.string.itemWithSubsNotDeletable, Toast.LENGTH_SHORT).show();
        }
    }

    private void finishEditing() {
        if (clientNameDisplay.getText().toString().trim() == "") {
            clientNameDisplay.setText("ClientName");
        }
        if (oldTitle.equals(clientNameDisplay.getText().toString().trim()) && !getHasChildren()) {
            //has no children, has no new data finish without action
            finish();
        } else {
            if (itemUri == null) {
                addItem(getContentValues());
            } else {
                updateItem(getContentValues());
            }
        }
        setResult(RESULT_OK);
        finish();
    }

    private int updateItem(ContentValues values) {
        int updateId = getContentResolver().update(itemUri, values, whereClause, null);
        Toast.makeText(this, R.string.itemUpdated, Toast.LENGTH_SHORT).show();
        return updateId;
    }

    private Uri addItem(ContentValues values) {
        values.put(TableClient.CREATE, "");
        Uri addUri = getContentResolver().insert(MyContentProvider.CLIENT_URI, values);
        return addUri;
    }

    private ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        String clientText = clientNameDisplay.getText().toString().trim();
        String clientName = (clientText == "") ? "ClientName" : clientText;

        values.put(TableClient.TITLE, clientName);
//        values.put(TableClient.MILES, totalMilesDisplay.getText().toString().trim());
        values.put(TableClient.UPDATE, "");
        return values;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Content values object to push to DB
        Log.d(this.getLocalClassName(), "requestCode: " + requestCode + " resultCode: " + resultCode + "resultCode == RESULT_OK" + (resultCode == RESULT_OK));
        if (resultCode == RESULT_OK) {
            if (requestCode == NEW_REQUEST_CODE ) {
                //child added, add this item to db, get id and update child with said id
                Uri tripUri = data.getParcelableExtra("newUri");
                String tripId = tripUri.getLastPathSegment();
                Uri tripIdUri = Uri.parse(MyContentProvider.TRIP_URI + "/" + tripId);
                String tripWhereClause = TableTrip.ID + "=" + tripId;

                //add client to DB and get its ID
                Uri clientUri = addItem(getContentValues());
                clientId = Integer.parseInt(clientUri.getLastPathSegment());

                //add client ID to trip DB record
                ContentValues tripClientIDValues = new ContentValues();
                tripClientIDValues.put(TableTrip.CLIENT_ID, clientId);
                Log.d(this.getLocalClassName(), "tripIdUri: " + tripIdUri + " tripClientIDValues: " + tripClientIDValues + " tripWhereClause: " + tripWhereClause);
                int updateTripID = getContentResolver().update(tripIdUri, tripClientIDValues, tripWhereClause, null);
                Log.d(this.getLocalClassName(), "updateTripID: " + updateTripID);
            }
            //Update miles
            Log.d(this.getLocalClassName(), "updateMiles: ");
            totalMilesDisplay.setText(updateMiles());
            restartLoader();
        }
    }
    private String updateMiles(){
//        Cursor cursor = getContentResolver().query(itemUri, TableClient.COLUMNS, "", null, null);
        //Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder
        Long mileSum = 0L;
        String whereMiles = TableTrip.CLIENT_ID+"="+clientId;
        String[] columns = new String[] { "sum(" + TableTrip.MILES + ")" };
        Cursor mileQuery = getContentResolver().query(MyContentProvider.TRIP_URI, columns, whereMiles, null, null);
        DatabaseUtils.dumpCursor(mileQuery);
        if (mileQuery.moveToFirst()){
            mileSum = mileQuery.getLong(0);
        } else {
            Toast.makeText(this, R.string.trip_miles_not_found, Toast.LENGTH_SHORT).show();
            Log.d(this.getLocalClassName(), "milequery movetofirst failed");
        }
        mileQuery.close();
        return Long.toString(mileSum);
    }

    public Boolean getHasChildren() {
        return (list.getChildCount() > 0);
    }

    /*
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(this.getLocalClassName(), "onCreateLoader id: " + id + "bundle: " + args );
        return new CursorLoader(this, MyContentProvider.TRIP_URI, null, null, null, null);
    }
    */

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        String loaderId = intent.getStringExtra(CourseRepo.ID);
//        Uri loaderChildUri = MyContentProvider.ASSESSMENT_URI;
//        String loaderChildsParentId = AssessmentRepo.COURSE_ID;
//        return new CursorLoader(this, loaderChildUri, null, loaderChildsParentId + "=" + loaderId, null, null);
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
Log.d(this.getLocalClassName(), "create loader projection: "+ MyContentProvider.TRIP_URI + " selection: " + TableClient.ID + "=" + clientId);
        //TODO get ID from instance variable
//        String loaderId = intent.getStringExtra(TableClient.ID);
//        Uri loaderChildUri = MyContentProvider.CLIENT_URI;
//        String loaderChildsParentId = TableClient.ID;
        return new CursorLoader(this, MyContentProvider.TRIP_URI, null, TableClient.ID + "=" + clientId, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(this.getLocalClassName(), "onLoadFinished");
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    private void restartLoader() {
        Log.d(this.getLocalClassName(), "loader restart");
        getLoaderManager().restartLoader(0, null, this);
    }

    //Set list-item listener to open item on click
    protected AdapterView.OnItemClickListener getChildActionClickListener(){
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int Position, long id){
                Intent childDetailsIntent = new Intent(childIntent);
                Uri childIDUri = Uri.parse(childUri + "/" + id);
                childDetailsIntent.putExtra(childRepoTableName, childIDUri);
                childDetailsIntent.putExtra(childRepoID, (String) view.getTag(R.string.item_id_tag));
                childDetailsIntent.putExtra(childRepoTitle, (String) view.getTag(R.string.item_title_tag));
                startActivityForResult(childDetailsIntent, CHILD_REQUEST_CODE);
            }
        };
        return listener;
    }



}
