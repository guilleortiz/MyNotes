package com.mynotes.android.mynotes.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Guille on 10/06/2017.
 */

public class NotesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="mynotes.db";
    private static final int DATABASE_VERSION=9;

    public NotesDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {



        final String CREATE_TABLE = "CREATE TABLE "  + NotesContract.TABLE_NAME + " (" +
                NotesContract._ID               + " INTEGER PRIMARY KEY, " +
                NotesContract.COLUMN_TITLE + " TEXT, " +
                NotesContract.COLUMN_NOTE + " TEXT , " +
                NotesContract.COLUMN_LINK + " TEXT , " +
                NotesContract.COLUMN_IMG + " TEXT , " +
                NotesContract.COLUMN_DATA_TYPE + " TEXT , " +
                NotesContract.COLUMN_DATE    + " TEXT);";


        sqLiteDatabase.execSQL(CREATE_TABLE);



    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+NotesContract.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}

