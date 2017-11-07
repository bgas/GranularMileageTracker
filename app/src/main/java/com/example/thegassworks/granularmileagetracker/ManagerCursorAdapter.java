package com.example.thegassworks.granularmileagetracker;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Arrays;

/**
 * Created by khaln on 5/25/17.
 */

public class ManagerCursorAdapter extends CursorAdapter {
    String[] from; //[0]Text column int, [1]id column int, [2] optional item to give "Selected" status
//    int[] where;
    int[] to;
    int layout;


    public ManagerCursorAdapter(Context context, int layout, Cursor c, String[] from, /*int[] where,*/ int[] to, int flags) {
        super(context, c, flags);
        this.to = to;
        this.from = from;
        this.layout = layout;
        Log.d(this.getClass().toString(), "new managercursoradapter context: " + context + " layout: " + layout + " cursor: " + c + " from: " + Arrays.toString(from) + " to: " + Arrays.toString(to) + " flags: " +flags);
    }

    public ManagerCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //get text and id values
        String itemText = cursor.getString(cursor.getColumnIndex(from[0]));
        String itemSubText = cursor.getString(cursor.getColumnIndex(from[1]));
        String itemId = "" + cursor.getInt(cursor.getColumnIndex(from[2]));

        //if itemText contains newline character (10) insert and has length then add ...
        Log.d(this.getClass().toString(), "itemText: "+ itemText + " itemSubText: " + itemSubText + " itemId: " + itemId);
        Log.d(this.getClass().toString(), "from values: " + Arrays.toString(from));

        final String titleText;
        if (itemText != null){
            titleText = ((-1 != itemText.indexOf(10)) && itemText.length() > 0) ? itemText.substring(0,itemText.indexOf(10)) + "..." : itemText;
        } else {
            titleText = "";
        }

        //pin values to view item to be passed as extra
        view.setTag(R.string.item_id_tag, itemId);
        view.setTag(R.string.item_title_tag, titleText);

        /*
        if (from.length > 2){
            view.setBackgroundColor(Color.WHITE);
            if (from[2].equals(itemId)){
                view.setBackgroundColor(Color.CYAN);
            }
        }
        */

        //bind title to view
        TextView listTitle = (TextView) view.findViewById(to[0]);
        TextView listSubTitle = (TextView) view.findViewById(to[1]);
        listTitle.setText("Client: " + titleText);
        listSubTitle.setText("Total miles: " + itemSubText);

    }
}
