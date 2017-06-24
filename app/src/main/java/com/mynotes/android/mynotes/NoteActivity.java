package com.mynotes.android.mynotes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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




        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);


        ButterKnife.bind(this);

        //Mnote=(EditText)findViewById(R.id.Note);
        //MtitleNOte=(EditText)findViewById(R.id.NoteTt);



        if(savedInstanceState!=null){

           // MtitleNOte.setText(savedInstanceState.getString(TITLE_STATE));
            //Mnote.setText(savedInstanceState.getString(NOTE_STATE));
        }




        //When activity is cal from other activity
        if (intent!=null){

            if (intent.hasExtra("noteTitle")){

                String titleFromExtra=intent.getStringExtra("noteTitle");
                String noteTextFromExtra=intent.getStringExtra("noteText");

                MtitleNOte.setText(titleFromExtra);
                Mnote.setText(noteTextFromExtra);



            }

        }



        //when app is call using an intentFilter

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
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

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            Mnote.setText(sharedText);
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

       outState.putString(NOTE_STATE,Mnote.getText().toString());

        outState.putString(TITLE_STATE,MtitleNOte.getText().toString());

        super.onSaveInstanceState(outState, outPersistentState);
    }
}
