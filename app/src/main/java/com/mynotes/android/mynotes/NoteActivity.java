package com.mynotes.android.mynotes;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    @BindView(R.id.note_back)RelativeLayout mBackground;
   // @BindView(R.id.action_attach)  MenuView.ItemView mAction_attach;
    //@BindView(R.id.action_color)  MenuView.ItemView mAction_color;

    String noteColor;

    MenuItem addImg;
    MenuItem changeColor;

    Boolean imgChange=false;





    private SQLiteDatabase mDb;

    static final String NOTE_STATE="noteEstate";
    static  final String TITLE_STATE="titleState";
    private static final int RESULT_LOAD_IMAGE=1;

    int noteId;
    String titleFromExtra;
    String noteTextFromExtra;
    String noteImgPathFromExtra;
    String noteColorFromExtra;
    int noteFavFromExtra;

    int count=0;

    String picturePath;
    Boolean isNewNote;




    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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

                addImg.setVisible(true);
                changeColor.setVisible(true);

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

                    //The mask makes sure you only get RRGGBB, and the %06X gives you zero-padded hex (always 6 chars long):
                    ColorDrawable notecolor=(ColorDrawable) mBackground.getBackground();
                    int myNoteColor=notecolor.getColor();


                    String hexColor = String.format("#%06X", (0xFFFFFF & myNoteColor));


                    cv.put(NotesContract.COLUMN_NOTE_COLOR, hexColor);
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


                        // finish();
                        Toast.makeText(NoteActivity.this, "Save!", Toast.LENGTH_SHORT).show();

                    }




                }else{//is an existing note, we update it


                    ContentValues cv = new ContentValues();
                    cv.put(NotesContract.COLUMN_TITLE, MtitleNOte.getText().toString());
                    cv.put(NotesContract.COLUMN_NOTE, Mnote.getText().toString());

                    //The mask makes sure you only get RRGGBB, and the %06X gives you zero-padded hex (always 6 chars long):
                    ColorDrawable notecolor=(ColorDrawable) mBackground.getBackground();
                    int myNoteColor=notecolor.getColor();


                    String hexColor = String.format("#%06X", (0xFFFFFF & myNoteColor));


                    cv.put(NotesContract.COLUMN_NOTE_COLOR, hexColor);

                    if (imgChange==true){
                        cv.put(NotesContract.COLUMN_IMG, picturePath);

                    }



                    DataUtils.getInstance(NoteActivity.this).updateNote(cv,noteId);

                    // finish();
                    Toast.makeText(NoteActivity.this, "Save!", Toast.LENGTH_SHORT).show();


                }










            }
        });




        //When activity is call from other activity
        if (intent!=null){

            if (intent.hasExtra("noteText") ){

                isNewNote=false;


               // addImg.setVisible(false);
                //changeColor.setVisible(false);


                noteId=intent.getIntExtra("noteid",0);
                titleFromExtra=intent.getStringExtra("noteTitle");
                noteTextFromExtra=intent.getStringExtra("noteText");
                noteImgPathFromExtra=intent.getStringExtra("noteImgPath");
                noteFavFromExtra=intent.getIntExtra("noteFav",0);
                noteColorFromExtra=intent.getStringExtra("noteColor");



                MtitleNOte.setText(titleFromExtra);
                Mnote.setText(noteTextFromExtra);

               // addImg.setVisible(true);
                //changeColor.setVisible(true);

                if (noteColorFromExtra==null || noteColorFromExtra==""){

                    MtitleNOte.setBackgroundColor(Color.parseColor("#ffff75" ));
                    Mnote.setBackgroundColor(Color.parseColor("#ffff75" ));


                }else {


                    MtitleNOte.setBackgroundColor(Color.parseColor(noteColorFromExtra ));
                    Mnote.setBackgroundColor(Color.parseColor(noteColorFromExtra ));

                    mBackground.setBackgroundColor(Color.parseColor(noteColorFromExtra));

                }



                //Mnote.setBackgroundTintList(contextInstance.getResources().getColorStateList(R.color.your_xml_name));


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


               // invalidateOptionsMenu();
              // editButton.performClick();





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





        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }




        });













    }


    @Override
    protected void onDestroy() {
        super.onDestroy();




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note,menu);
        addImg=menu.findItem(R.id.action_attach);
        changeColor=menu.findItem(R.id.action_color);


        menu.getItem(1).setVisible(false);//add img
        menu.getItem(2).setVisible(false);//change color

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        View favButton=findViewById(R.id.action_color);

        int id=item.getItemId();
        if (id==R.id.action_attach){

            activeGallery();

        }else if (id==R.id.share){

            shareText(titleFromExtra,noteTextFromExtra);


        }else if(id==R.id.action_color){

             String[] colors={
                   "#ffff75",
                    "#a0f8ff",
                    "#ec95ba",
                    "#c7f163"
            };


            if (count>=colors.length){
                count=0;
                Toast.makeText(this, String.valueOf(count), Toast.LENGTH_SHORT).show();



                    MtitleNOte.setBackgroundColor(Color.parseColor(colors[count] ));
                    Mnote.setBackgroundColor(Color.parseColor(colors[count] ));

                    mBackground.setBackgroundColor(Color.parseColor(colors[count] ));

                    noteColor=colors[count];


                    count++;



            }else {
                MtitleNOte.setBackgroundColor(Color.parseColor(colors[count] ));
                Mnote.setBackgroundColor(Color.parseColor(colors[count] ));

                mBackground.setBackgroundColor(Color.parseColor(colors[count] ));

                noteColor=colors[count];


                count++;


            }



        }


        return super.onOptionsItemSelected(item);
    }


    /*
    * img selected from galery
    *
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        NotesDbHelper dbHelper=new NotesDbHelper(NoteActivity.this);//create db
        mDb=dbHelper.getWritableDatabase();

        switch (requestCode){
            case RESULT_LOAD_IMAGE:
                if (requestCode == RESULT_LOAD_IMAGE &&
                        resultCode == RESULT_OK && null != data) {

                    imgChange=true;
                    Toast.makeText(this, "img change", Toast.LENGTH_SHORT).show();

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

    public void showPopup() {

       /* ImageView colorView;
        colorView=(ImageView)findViewById(R.id.note_color);



        PopupMenu popup = new PopupMenu(this, colorView);

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.color_picker, popup.getMenu());
        popup.show();
        */
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

    void handleSendText(Intent intent) {//shared from  other aplication to MyNotes


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
            Toast.makeText(this, "handleSendText", Toast.LENGTH_SHORT).show();
        }
    }

    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.N)
    void handleSendImage(Intent intent) {


//ClipData { image/* {U:content://media/external/images/media/44036} }

        picturePath=String.valueOf(intent.getClipData().getItemAt(0).getUri());


        //Toast.makeText(this, String.valueOf(String.valueOf(intent.getClipData().getItemAt(0).getUri())), Toast.LENGTH_LONG).show();


        Glide.with(this).load(picturePath).into(mNoteImg);

        /*Cursor cursor = getContentResolver()
                .query(selectedImage, filePathColumn, null, null,
                        null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String newpicturePath = cursor.getString(columnIndex);
        cursor.close();

//

        Glide.with(this).load(newpicturePath)


                .fitCenter()

                .into(mNoteImg);*/

        /*Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
       // Bitmap imgIntent=intent.getParcelableExtra(Intent.EXTRA_STREAM);


       // Bitmap img= BitmapFactory.decodeStream(imageUri);


        if (imageUri != null) {
           // Toast.makeText(this, "Imagen", Toast.LENGTH_SHORT).show();
        }

        */
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
