package com.mynotes.android.mynotes.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guille on 10/06/2017.
 */

public class DataUtils {

    private static DataUtils sInstance;
    private SQLiteDatabase mDb;


    private NotesDbHelper mNotesDbHelper;

/**
 * synchronized methods enable a simple strategy for preventing thread
 * interference and memory consistency errors: if an object is visible
 * to more than one thread, all reads or writes to that object's
 * variables are done through synchronized methods.
 */


    public static synchronized DataUtils getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataUtils(context.getApplicationContext());
        }

        return sInstance;
    }





    private DataUtils(Context context) {
        mNotesDbHelper = new NotesDbHelper(context);

        mDb = mNotesDbHelper.getWritableDatabase();
    }

    /**
     * Return a {@link Cursor} that contains every note in the db
     * @param sortOrder Sort order for the query
     * @return {@link Cursor} containing all the results.
     */

    public Cursor getAllNotes(String sortOrder) {
        Cursor cursor=null;


        if (sortOrder=="date"){


            cursor=mDb.query(
                    NotesContract.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    NotesContract.COLUMN_DATE+" DESC");

        }else if (sortOrder=="fav"){


              cursor=mDb.query(
                    NotesContract.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    NotesContract.COLUMN_FAV);


        }



        return cursor;
    }

    public void updateNote(ContentValues cv,int noteId){

        try {

            mDb.beginTransaction();

            List<ContentValues> list = new ArrayList<ContentValues>();


            list.add(cv);


            String [] whereArgs=new String[]{
                    String.valueOf(noteId)
            };

            for(ContentValues c:list){
                mDb.update(NotesContract.TABLE_NAME, c,NotesContract._ID+" = "+noteId,null);
            }
            mDb.setTransactionSuccessful();

        } catch (SQLException e) {
            //too bad :(
        } finally {
            mDb.endTransaction();


        }




    }



    /**
     * Return a {@link Cursor} that contains a single Note for the given unique id.
     *
     * @param id Unique identifier for the Note record.
     * @return {@link Cursor} containing the Note result.
     */
    public Cursor queryNoteById(int id) {



        Cursor notes = getAllNotes("date");

        notes.moveToPosition(id);

        return notes;





    }


























    public  static void insertFakeData(SQLiteDatabase db){

        if(db==null) {
            return;
        }

        List<ContentValues> list= new ArrayList<ContentValues>();

        ContentValues cv= new ContentValues();
        cv.put(NotesContract.COLUMN_TITLE,"Primer titulo");
        cv.put(NotesContract.COLUMN_NOTE,"Primera nota");
        cv.put(NotesContract.COLUMN_DATA_TYPE,"5");
        //cv.put(NotesContract.COLUMN_IMG);
        cv.put(NotesContract.COLUMN_DATE,"10/06/2017");
        list.add(cv);

         cv= new ContentValues();
        cv.put(NotesContract.COLUMN_TITLE,"segundo titulo");
        cv.put(NotesContract.COLUMN_NOTE,"segundo nota");
        cv.put(NotesContract.COLUMN_DATA_TYPE,"8");
        cv.put(NotesContract.COLUMN_DATE,"12/06/2017");
        list.add(cv);

         cv= new ContentValues();
        cv.put(NotesContract.COLUMN_TITLE,"tercero titulo");
        cv.put(NotesContract.COLUMN_NOTE,"tercero nota");
        cv.put(NotesContract.COLUMN_DATA_TYPE,"4");

        cv.put(NotesContract.COLUMN_DATE,"9/06/2017");
        list.add(cv);

         cv= new ContentValues();
        cv.put(NotesContract.COLUMN_TITLE,"cuarto titulo");
        cv.put(NotesContract.COLUMN_NOTE,"cuarto nota");
        cv.put(NotesContract.COLUMN_DATA_TYPE,"2");

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




    public static void dbUpdateImg(SQLiteDatabase db, int id, String filepath, Context context){



        List<ContentValues> list= new ArrayList<ContentValues>();

        ContentValues cv= new ContentValues();
        cv.put(NotesContract.COLUMN_IMG,filepath);

        list.add(cv);

        //insert all guests in one transaction
        try
        {
            db.beginTransaction();
            //clear the table first
            // mDb.delete (NotesContract.TABLE_NAME,null,null);
            //go through the list and add one by one

            String [] whereArgs=new String[]{
                    String.valueOf(id)
            };

            for(ContentValues c:list){
                db.update(NotesContract.TABLE_NAME, c,NotesContract._ID+" = "+id,null);
            }
            db.setTransactionSuccessful();

        }
        catch (SQLException e) {
            Toast.makeText(context, "error= "+e, Toast.LENGTH_SHORT).show();
        }
        finally
        {
            db.endTransaction();

            Toast.makeText(context, "update img ok", Toast.LENGTH_SHORT).show();

        }
    }








}
