package com.example.thegassworks.granularmileagetracker;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.example.thegassworks.granularmileagetracker.database.MyContentProvider;
import com.example.thegassworks.granularmileagetracker.database.TableClient;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int CHILD_REQUEST_CODE = 1001;
    private static final int NEW_REQUEST_CODE = 1002;
    private CursorAdapter cursorAdapter;
    protected Intent intent;
    protected String childRepoTitle;
    protected String childRepoID;
    protected String childRepoTableName;
    protected Uri childUri;
    protected Intent childIntent;
    protected Intent addItemIntent;
    protected Class childClass;
    protected Class thisClass;
    protected ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = getIntent();

        //Set child intent values
        childRepoTitle = TableClient.TITLE;
        childRepoID = TableClient.ID;
        childRepoTableName = TableClient.TABLE_NAME;
        childUri = MyContentProvider.CLIENT_URI;
        childClass = ClientActivity.class;
        childIntent = new Intent(this, childClass);

        //No values to set

        //Set Cursor Adapter
        String[] from = {childRepoTitle, TableClient.MILES, childRepoID};
//        String[] from = {TableClient.TITLE, TableClient.MILES, TableClient.ID};
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

        getLoaderManager().initLoader(0, null, this);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(this.getLocalClassName(), "requestCode: " + requestCode + " resultCode: " + resultCode + "resultCode == RESULT_OK: "+ (resultCode == RESULT_OK));
        if (resultCode == RESULT_OK){
            restartLoader();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {        }
        return super.onOptionsItemSelected(item);
    }

    private void restartLoader() {
        Log.d(this.getLocalClassName(), "loader restart");
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MyContentProvider.CLIENT_URI, null, null, null, null);
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
}
