package com.mynotes.android.mynotes;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mynotes.android.mynotes.data.NotesContract;
import com.mynotes.android.mynotes.data.NotesDbHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mynotes.android.mynotes.data.DataUtils.dbUpdateImg;

public class NoteActivity extends AppCompatActivity {

    @BindView(R.id.Note) EditText Mnote;
    @BindView(R.id.NoteTt) EditText MtitleNOte;
    @BindView(R.id.noteImg)ImageView mNoteImg;



    private SQLiteDatabase mDb;

    static final String NOTE_STATE="noteEstate";
    static  final String TITLE_STATE="titleState";
    private static final int RESULT_LOAD_IMAGE=1;
    int noteId;
    String picturePath;


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

                noteId=intent.getIntExtra("noteid",0);
                String titleFromExtra=intent.getStringExtra("noteTitle");
                String noteTextFromExtra=intent.getStringExtra("noteText");
                String noteImgPathFromExtra=intent.getStringExtra("noteImgPath");



                MtitleNOte.setText(titleFromExtra);
                Mnote.setText(noteTextFromExtra);


                // Storage Permissions
                 int REQUEST_EXTERNAL_STORAGE = 1;
                String[] PERMISSIONS_STORAGE = {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };

                int permission = ActivityCompat.checkSelfPermission(NoteActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            NoteActivity.this,
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                }


              //  mNoteImg.setImageBitmap(BitmapFactory.decodeFile(noteImgPathFromExtra));

                Glide.with(this).load(noteImgPathFromExtra).into(mNoteImg);


               // Toast.makeText(this, "path from db= "+noteImgPathFromExtra, Toast.LENGTH_LONG).show();



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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if (id==R.id.action_attach){

           // Toast.makeText(this, "Attaching img comming soon!", Toast.LENGTH_SHORT).show();
            activeGallery();

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        NotesDbHelper dbHelper=new NotesDbHelper(NoteActivity.this);//create db
        mDb=dbHelper.getWritableDatabase();

        switch (requestCode){
            case RESULT_LOAD_IMAGE:
                if (requestCode == RESULT_LOAD_IMAGE &&
                        resultCode == RESULT_OK && null != data) {

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver()
                            .query(selectedImage, filePathColumn, null, null,
                                    null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    Toast.makeText(this, picturePath+" id= "+noteId, Toast.LENGTH_SHORT).show();

                    //image.setPath(picturePath);

                   dbUpdateImg(mDb,noteId,picturePath,NoteActivity.this);


                }




        }


    }
    //TODO:1 save o exit activity....

    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            Mnote.setText(sharedText);
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        Bitmap imgIntent=intent.getParcelableExtra(Intent.EXTRA_STREAM);

       // Bitmap img= BitmapFactory.decodeStream(imageUri);

        if (imageUri != null) {
            Toast.makeText(this, "Imagen", Toast.LENGTH_SHORT).show();
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
