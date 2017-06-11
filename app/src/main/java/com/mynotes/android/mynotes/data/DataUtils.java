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
        cv.put(NotesContract.COLUMN_NOTE,"Primera nota");
        cv.put(NotesContract.COLUMN_DATE,"10/06/2017");
        list.add(cv);

         cv= new ContentValues();
        cv.put(NotesContract.COLUMN_TITLE,"segundo titulo");
        cv.put(NotesContract.COLUMN_NOTE,"segundo nota");
        cv.put(NotesContract.COLUMN_DATE,"12/06/2017");
        list.add(cv);

         cv= new ContentValues();
        cv.put(NotesContract.COLUMN_TITLE,"tercero titulo");
        cv.put(NotesContract.COLUMN_NOTE,"tercero nota");

        cv.put(NotesContract.COLUMN_DATE,"9/06/2017");
        list.add(cv);

         cv= new ContentValues();
        cv.put(NotesContract.COLUMN_TITLE,"cuarto titulo");
        cv.put(NotesContract.COLUMN_NOTE,"cuarto nota");

        cv.put(NotesContract.COLUMN_DATE,"10/25/1995");
        list.add(cv);


        //db.delete(NotesContract.TABLE_NAME,null,null);

        //insert all guests in one transaction
        try
        {
            db.beginTransaction();
            //clear the table first
            //// TODO: delete commented 11/06/2017
           // db.delete (NotesContract.TABLE_NAME,null,null);
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
