package com.mynotes.android.mynotes;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.mynotes.android.mynotes.data.NotesContract;
import com.mynotes.android.mynotes.data.NotesDbHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteActivity extends AppCompatActivity {

    @BindView(R.id.Note) EditText Mnote;
    @BindView(R.id.NoteTt) EditText MtitleNOte;



    private SQLiteDatabase mDb;

    static final String NOTE_STATE="noteEstate";
    static  final String TITLE_STATE="titleState";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        //TODO 2 implement buterknife
        ButterKnife.bind(this);

        //Mnote=(EditText)findViewById(R.id.Note);
        //MtitleNOte=(EditText)findViewById(R.id.NoteTt);



        if(savedInstanceState!=null){

           // MtitleNOte.setText(savedInstanceState.getString(TITLE_STATE));
            //Mnote.setText(savedInstanceState.getString(NOTE_STATE));
        }



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingSaveActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NotesDbHelper dbHelper=new NotesDbHelper(NoteActivity.this);//create db
                mDb=dbHelper.getWritableDatabase();

                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String date = df.format(Calendar.getInstance().getTime());

                List<ContentValues> list= new ArrayList<ContentValues>();




                ContentValues cv= new ContentValues();
                cv.put(NotesContract.COLUMN_TITLE,MtitleNOte.getText().toString());
                cv.put(NotesContract.COLUMN_NOTE,Mnote.getText().toString());
                cv.put(NotesContract.COLUMN_DATE,date);
                list.add(cv);

                //insert all guests in one transaction
                try
                {
                    mDb.beginTransaction();
                    //clear the table first
                   // mDb.delete (NotesContract.TABLE_NAME,null,null);
                    //go through the list and add one by one
                    for(ContentValues c:list){
                        mDb.insert(NotesContract.TABLE_NAME, null, c);
                    }
                    mDb.setTransactionSuccessful();

                }
                catch (SQLException e) {
                    //too bad :(
                }
                finally
                {
                    mDb.endTransaction();

                    finish();
                }


            }
        });



    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

       outState.putString(NOTE_STATE,Mnote.getText().toString());

        outState.putString(TITLE_STATE,MtitleNOte.getText().toString());

        super.onSaveInstanceState(outState, outPersistentState);
    }
}
