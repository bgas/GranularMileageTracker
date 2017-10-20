package com.example.thegassworks.granularmileagetracker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class ClientActivity extends MainActivity{
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
    protected EditText clientNameDisplay;
    protected EditText totalMilesDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        //get layout elements
        clientNameDisplay = (EditText) findViewById(R.id.textEditClientNameDisplay);
        totalMilesDisplay = (EditText) findViewById(R.id.textViewMilesDisplay);
        //set instance values for this element
        repoId = intent.getStringExtra(TableClient.ID);
        repoTitleLocation = TableClient.TITLE;  //get reference to
        repoTitle = intent.getStringExtra(TableClient.TITLE); //Get title of this item from intent
        repoTableName = TableClient.TABLE_NAME;
        itemUri = intent.getParcelableExtra(repoTableName);
        //Set child intent values
        childRepoTitle = TableTrip.TITLE;
        childRepoID = TableTrip.ID;
        childRepoTableName = TableTrip.TABLE_NAME;
        childUri = MyContentProvider.TRIP_URI;
        childClass = TripActivity.class;
        childClassDetails = TripActivity.class;
        childIntent = new Intent(this, childClass);

        //set instance values for child elements
        String[] from = {childRepoTitle, childRepoID};
        int[] to = {R.id.textViewItem};
        cursorAdapter = new ManagerCursorAdapter(this, R.layout.child_list_item, null, from, to, 0);


        list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        //Set list-item listener to open item on click
        list.setOnItemClickListener(getChildActionClickListener());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(childIntent, NEW_REQUEST_CODE);
            }
        });

        getLoaderManager().initLoader(0, null, this);

        Intent intent = getIntent();
        itemUri = intent.getParcelableExtra(TableClient.TABLE_NAME );
        Log.d(this.getLocalClassName(), "itemUri: "+ itemUri);
        if (itemUri == null){
            action = Intent.ACTION_INSERT;
            //Set old values as default
            oldTitle = String.valueOf((R.string.name_here));
            oldMiles = String.valueOf(R.string.zero);
            //Set values for various fields
            clientNameDisplay.setText(oldTitle);
            totalMilesDisplay.setText(oldMiles);
        } else {
            Log.d(this.getLocalClassName(), "itemUri trimmed: " + itemUri.getLastPathSegment() );
            action = Intent.ACTION_EDIT;
            whereClause = TableClient.ID + "=" + itemUri.getLastPathSegment();

            //Get item values
            Cursor cursor = getContentResolver().query(itemUri, TableClient.COLUMNS, "", null, null);
            cursor.moveToFirst();
            //Get existing values
            oldTitle = cursor.getString(cursor.getColumnIndex(TableClient.TITLE));
            oldMiles = cursor.getString(cursor.getColumnIndex(TableClient.MILES));

            //Set values for various fields
            clientNameDisplay.setText(oldTitle);
            totalMilesDisplay.setText(oldMiles);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    //    if (action.equals(Intent.ACTION_EDIT)){
        getMenuInflater().inflate(R.menu.menu_editor, menu);
    //    }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
//        int id=item.getItemId();
        switch (item.getItemId()){
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
        if(!getHasChildren()) {
            Log.d(this.getLocalClassName(), "delete itemUri: " + itemUri + " whereClause: " + whereClause);
//            content://com.example.thegassworks.granularmileagetracker.database.MyContentProvider/client
            getContentResolver().delete(itemUri, whereClause, null);
            Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
            Intent deleteIntent = new Intent();
            deleteIntent.putExtra("itemDeleted", true);
            setResult(RESULT_OK, deleteIntent);
            finish();
        }else{
            Toast.makeText(this, R.string.itemWithSubsNotDeletable, Toast.LENGTH_SHORT).show();
        }
    }

    private void finishEditing() {
        String newTitle = clientNameDisplay.getText().toString().trim();
        String newMiles = totalMilesDisplay.getText().toString().trim();
        Log.d(this.getLocalClassName()+" fishishEdit", "oldTitle: " + oldTitle + " oldMiles: " + oldMiles);
        Log.d(this.getLocalClassName()+" fishishEdit", "newTitle: " + newTitle + " newMiles: " + newMiles);
        if (oldMiles.equals(newMiles) && oldTitle.equals(newTitle)){
            finish();
        } else {
            addUpdateItem(newTitle, newMiles, (itemUri != null));
        }
        finish();
    }
    private void addUpdateItem(String title, String miles, Boolean update) {
        ContentValues values = new ContentValues();
        values.put(TableClient.TITLE, title);
        values.put(TableClient.MILES, miles);
        values.put(TableClient.UPDATE, "");
        Log.d(this.getLocalClassName(), "values: "+ values.toString());
        Log.d(this.getLocalClassName(), "add/update itemUri: "+ itemUri + " whereClause: " + whereClause);
        if (update) {
            Log.d(this.getLocalClassName(), "updating item");
            getContentResolver().update(itemUri, values, whereClause, null);
            Toast.makeText(this, R.string.itemUpdated, Toast.LENGTH_SHORT).show();
            Intent updateIntent = new Intent();
            updateIntent.putExtra("newTitle", title);
            setResult(RESULT_OK, updateIntent);
        } else {
            Log.d(this.getLocalClassName(), "adding item");
            itemUri = MyContentProvider.CLIENT_URI;
            Uri tempUri = getContentResolver().insert(itemUri, values);
            Log.d(this.getLocalClassName(), "added Uri: " + tempUri);
            setResult(RESULT_OK);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(this.getLocalClassName(), "requestCode: " + requestCode + " resultCode: " + resultCode + " RESULT_OK "+ RESULT_OK);
        if (requestCode == CHILD_REQUEST_CODE && resultCode == RESULT_OK){
            Log.d(this.getLocalClassName(), "child and result satisfied, restart loader ran");
            restartLoader();
        } else if (requestCode == NEW_REQUEST_CODE && resultCode == RESULT_OK){
//            Cursor cursor = getContentResolver().query(itemUri, TableClient.COLUMNS, "", null, null);
//            Cursor cursor = getContentResolver().update(itemUri, TableClient., "", null, null);
//
//            cursor.moveToFirst();
            Log.d(this.getLocalClassName(), "new and result satisfied, restart loader ran");
            Log.d(this.getLocalClassName(), "newURI value: " + data.getParcelableExtra("newUri"));
            restartLoader();
        }
    }



    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    public Boolean getHasChildren() {
        return (list.getChildCount() > 0);
    }


//    public void openEditorForNewItem(View view) {
//        Intent intent = new Intent(this, childClassDetails);
//        intent.putExtra(childRepoTableName, childUri);
//        Log.d(this.getLocalClassName(), "editor for new item with request code: " + NEW_REQUEST_CODE);
//        startActivityForResult(intent, NEW_REQUEST_CODE);
//    }
}
