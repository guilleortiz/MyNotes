package com.mynotes.android.mynotes.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guille on 10/06/2017.
 */

public class DataUtils {


    public  static void insertFakeData(SQLiteDatabase db){
        if(db==null) {
            return;
        }

        List<ContentValues> list= new ArrayList<ContentValues>();

        ContentValues cv= new ContentValues();
        cv.put(NotesContract.COLUMN_TITLE,"Primer titulo");
        cv.put(NotesContract.COLUMN_NOTE,"Primera nota con cosas");
        list.add(cv);


        db.delete(NotesContract.TABLE_NAME,null,null);

        //insert all guests in one transaction
        try
        {
            db.beginTransaction();
            //clear the table first
            db.delete (NotesContract.TABLE_NAME,null,null);
            //go through the list and add one by one
            for(ContentValues c:list){
                db.insert(NotesContract.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();

        }
        catch (SQLException e) {
            //too bad :(
        }
        finally
        {
            db.endTransaction();
        }



    }








}
