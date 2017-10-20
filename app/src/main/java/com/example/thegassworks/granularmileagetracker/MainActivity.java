package com.example.thegassworks.granularmileagetracker;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
    protected Class childClass;
    protected Class childClassDetails;
    protected ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = getIntent();
        String[] from = {TableClient.TITLE, TableClient.MILES, TableClient.ID};
        int[] to = {R.id.textViewItem, R.id.textViewSubItem};
        cursorAdapter = new ManagerCursorAdapter(this, R.layout.child_list_item, null, from, to, 0);
/*
        MyContentProvider.
        Cursor tempCursor = cursorAdapter ;
        tempCursor.moveToFirst();
        DatabaseUtils.dumpCursor(tempCursor);
*/
        //Set child intent values
        childRepoTitle = TableClient.TITLE;
        childRepoID = TableClient.ID;
        childRepoTableName = TableClient.TABLE_NAME;
        childUri = MyContentProvider.CLIENT_URI;
        childClass = ClientActivity.class;
        childClassDetails = ClientActivity.class;
        childIntent = new Intent(this, childClass);
        list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);
        //Set list-item listener to open item on click
        list.setOnItemClickListener(getChildActionClickListener());
        //Set floating action button to create new element on click
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(childIntent, NEW_REQUEST_CODE);
            }
        });


        getLoaderManager().initLoader(0, null, this);
    }
    /* create and respond to child Activities */
    protected AdapterView.OnItemClickListener getChildActionClickListener(){
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int Position, long id){
                Uri childIDUri = Uri.parse(childUri + "/" + id);
                childIntent.putExtra(childRepoTableName, childIDUri);
                childIntent.putExtra(childRepoID, (String) view.getTag(R.string.item_id_tag));
                childIntent.putExtra(childRepoTitle, (String) view.getTag(R.string.item_title_tag));
                startActivityForResult(childIntent, CHILD_REQUEST_CODE);
            }
        };
        return listener;
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
        int id = item.getItemId();
//        switch (id) {        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(this.getLocalClassName(), "requestCode: " + requestCode + " resultCode: " + resultCode );
        if (requestCode == CHILD_REQUEST_CODE && resultCode == RESULT_OK){
            restartLoader();
        }
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(this.getLocalClassName(), "id: " + id + "bundle: " + args );
        return new CursorLoader(this, MyContentProvider.CLIENT_URI, null, null, null, null);
    }
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        String loaderId = intent.getStringExtra(TermRepo.ID);
//        Uri loaderChildUri = MyContentProvider.COURSE_URI;
//        String loaderChildsParentId = CourseRepo.TERM_ID;
//        return new CursorLoader(this, loaderChildUri, null, loaderChildsParentId + "=" + loaderId, null, null);
//    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

//    public void openEditorForNewItem(View view) {
//        Log.d(this.getLocalClassName(), "openEditor request code: "+NEW_REQUEST_CODE);
//        Intent intent = new Intent(this, childClassDetails);
//        intent.putExtra(childRepoTableName, childUri);
//        startActivityForResult(intent, NEW_REQUEST_CODE);
//    }

}
