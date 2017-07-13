package com.mynotes.android.mynotes;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.mynotes.android.mynotes.data.DataUtils;
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
    String titleFromExtra;
    String noteTextFromExtra;
    String noteImgPathFromExtra;
    int noteFavFromExtra;

    String picturePath;
    Boolean isNewNote;



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




        //When activity is call from other activity
        if (intent!=null){

            if (intent.hasExtra("noteText") ){

                isNewNote=false;


                noteId=intent.getIntExtra("noteid",0);
                titleFromExtra=intent.getStringExtra("noteTitle");
                noteTextFromExtra=intent.getStringExtra("noteText");
                noteImgPathFromExtra=intent.getStringExtra("noteImgPath");
                noteFavFromExtra=intent.getIntExtra("noteFav",0);



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


               Glide.with(this).load(noteImgPathFromExtra)
                       .into(mNoteImg);
                //Mnote.setCompoundDrawables(getResources().getDrawable(R.drawable.clips),getResources().getDrawable(R.drawable.clips),null,null);

                /*
                Glide.with(left.getContext())
                        .load(((FixturesListObject) object).getHomeIcon())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>(100,100) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                left.setCompoundDrawablesWithIntrinsicBounds(null, new BitmapDrawable(left.getResources(),resource), null, null);
                            }
                        });

                */

            }else /*if(intent.hasExtra("noteStatus"))*/{//call from mainActivity

                isNewNote=true;

                MtitleNOte.setFocusable(true);
                MtitleNOte.setClickable(true);
                MtitleNOte.setEnabled(true);
                MtitleNOte.setFocusableInTouchMode(true);

                Mnote.setFocusable(true);
                Mnote.setClickable(true);
                Mnote.setEnabled(true);
                Mnote.setFocusableInTouchMode(true);


                //set focus to note and display keyboard
                MtitleNOte.requestFocus();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(MtitleNOte, InputMethodManager.SHOW_IMPLICIT);



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





       //final FloatingActionsMenu fab = (FloatingActionsMenu) findViewById(R.id.floatingSaveActionButton);



        FloatingActionsMenu fab = (FloatingActionsMenu) findViewById(R.id.floatingctionButton);


        final View favButton,editButton, savebutton;


        favButton=findViewById(R.id.accion_Fav);
        editButton = findViewById(R.id.accion_edit);
        savebutton = findViewById(R.id.accion_save);

        fab.bringToFront();



        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (noteFavFromExtra==0){//if is note fav the update to fav



                    ContentValues cv = new ContentValues();
                    cv.put(NotesContract.COLUMN_FAV,1);


                    DataUtils.getInstance(NoteActivity.this).updateNote(cv,noteId);

                    Toast.makeText(NoteActivity.this, "Note add to fav", Toast.LENGTH_SHORT).show();


                }else {//update to no fav

                    ContentValues cv = new ContentValues();
                    cv.put(NotesContract.COLUMN_FAV,0);

                    DataUtils.getInstance(NoteActivity.this).updateNote(cv,noteId);

                    Toast.makeText(NoteActivity.this, "Note delete form fav", Toast.LENGTH_SHORT).show();



                }










            }
        });


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                 //   Toast.makeText(NoteActivity.this, "edit", Toast.LENGTH_SHORT).show();

                MtitleNOte.setFocusable(true);
                MtitleNOte.setClickable(true);
                MtitleNOte.setEnabled(true);
                MtitleNOte.setFocusableInTouchMode(true);

                Mnote.setFocusable(true);
                Mnote.setClickable(true);
                Mnote.setEnabled(true);
                Mnote.setFocusableInTouchMode(true);


                //set focus to note and display keyboard
                Mnote.requestFocus();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(Mnote, InputMethodManager.SHOW_IMPLICIT);




            }
        });

       /* deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


           // DataUtils.getInstance(NoteActivity.this).deleteNote(noteId,NoteActivity.this);
                mDb.delete(NotesContract.TABLE_NAME,NotesContract._ID+" = "+noteId, null);

                Toast.makeText(NoteActivity.this, "delete", Toast.LENGTH_SHORT).show();
                finish();



            }
        });
        */

        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isNewNote==true){

                   // Toast.makeText(NoteActivity.this, "New note Save", Toast.LENGTH_SHORT).show();


                    NotesDbHelper dbHelper = new NotesDbHelper(NoteActivity.this);//create db
                    mDb = dbHelper.getWritableDatabase();

                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    String date = df.format(Calendar.getInstance().getTime());

                    List<ContentValues> list = new ArrayList<ContentValues>();


                    ContentValues cv = new ContentValues();
                    cv.put(NotesContract.COLUMN_TITLE, MtitleNOte.getText().toString());
                    cv.put(NotesContract.COLUMN_NOTE, Mnote.getText().toString());
                    cv.put(NotesContract.COLUMN_DATE, date);
                    cv.put(NotesContract.COLUMN_IMG, picturePath);
                    list.add(cv);

                    //insert all guests in one transaction
                    try {
                        mDb.beginTransaction();
                        //clear the table first
                        // mDb.delete (NotesContract.TABLE_NAME,null,null);
                        //go through the list and add one by one
                        for (ContentValues c : list) {
                            mDb.insert(NotesContract.TABLE_NAME, null, c);
                        }
                        mDb.setTransactionSuccessful();

                    } catch (SQLException e) {
                        //too bad :(
                    } finally {
                        mDb.endTransaction();


                        finish();

                    }




                }else{//is an existing note, we update it


                    ContentValues cv = new ContentValues();
                    cv.put(NotesContract.COLUMN_TITLE, MtitleNOte.getText().toString());
                    cv.put(NotesContract.COLUMN_NOTE, Mnote.getText().toString());
                    cv.put(NotesContract.COLUMN_IMG, picturePath);

                   DataUtils.getInstance(NoteActivity.this).updateNote(cv,noteId);

                    finish();


                }










            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }




        });









/*
        final Interpolator interpolador = AnimationUtils.loadInterpolator(getBaseContext(),
                android.R.interpolator.fast_out_slow_in);

        fab.animate()
                .scaleX(0)
                .scaleY(0)
                .setInterpolator(interpolador)
                .setDuration(600)
                .setStartDelay(1000)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fab.animate()
                                .scaleY(1)
                                .scaleX(1)
                                .setInterpolator(interpolador)
                                .setDuration(600)
                                .start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
        */


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();




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

            activeGallery();

        }else if (id==R.id.share){

            shareText(titleFromExtra,noteTextFromExtra);


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
                    //Toast.makeText(this, picturePath+" id= "+noteId, Toast.LENGTH_SHORT).show();



                   dbUpdateImg(mDb,noteId,picturePath,NoteActivity.this);

                  //update img
                    Glide.with(this).load(picturePath).into(mNoteImg);


                }




        }


    }


    private void shareText(String title,String textToShare) {
        String mimeType = "text/plain";



        ShareCompat.IntentBuilder
            /* The from method specifies the Context from which this share is coming from */
                .from(this)
                .setType(mimeType)
                .setChooserTitle(title)
                .setText(title+": \n"+textToShare)
                .startChooser();
    }




    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    void handleSendText(Intent intent) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {


            /*
            Intent main_intent = new Intent(this, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(main_intent);
            */

        }



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
           // Toast.makeText(this, "Imagen", Toast.LENGTH_SHORT).show();
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
