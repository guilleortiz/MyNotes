package com.mynotes.android.mynotes.data;

import android.provider.BaseColumns;

/**
 * Created by Guille on 10/06/2017.
 */

public class NotesContract implements BaseColumns {

    public static  final String   TABLE_NAME="myNotes";
    public static final String COLUMN_TITLE="Title";
    public static final String COLUMN_NOTE="Note";
    public static final String COLUMN_DATE="Date";
    public static final String COLUMN_IMG="Img";
    public static final String COLUMN_LINK="Link";
    public static final String COLUMN_DATA_TYPE="DataType";



}
