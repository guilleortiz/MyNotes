package com.mynotes.android.mynotes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mynotes.android.mynotes.data.DataUtils;
import com.mynotes.android.mynotes.data.NotesContract;
import com.mynotes.android.mynotes.data.NotesDbHelper;


public class MainActivity extends AppCompatActivity
        implements NotesAdapter.NoteItemClickListener,GoogleApiClient.OnConnectionFailedListener {
    //dev branch

    private SQLiteDatabase mDb;

    private RecyclerView mRecyclerView;
    private NotesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private NotesAdapter.NoteItemClickListener mOnClickListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String orden="fav";

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);

       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        NotesDbHelper dbHelper=new NotesDbHelper(this);//create db

        mDb=dbHelper.getWritableDatabase();



        mRecyclerView=(RecyclerView)findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);


        int mNoOfColumns = calculateNoOfColumns(getApplicationContext());

        mLayoutManager=new StaggeredGridLayoutManager(mNoOfColumns, StaggeredGridLayoutManager.VERTICAL);

        //mLayoutManager = new GridLayoutManager(this, mNoOfColumns);


       // mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setLayoutManager(mLayoutManager);





        mAdapter=new NotesAdapter(this,orden, this);
        mRecyclerView.setAdapter(mAdapter);
        //Toast.makeText(this, "mAdapter= "+String.valueOf(mAdapter), Toast.LENGTH_LONG).show();





        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(MainActivity.this, "layout count = "+mLayoutManager.getItemCount(), Toast.LENGTH_LONG).show();
                // 08/08/17
                Context context = MainActivity.this;
                Class destinationActivity = NoteActivity.class;

                Intent startActivityIntent = new Intent(context, destinationActivity);

                startActivityIntent.putExtra("noteStatus","newNote");


                startActivity(startActivityIntent);




            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {


                int id=(int)viewHolder.itemView.getTag();

                //viewHolder.getAdapterPosition() position of list



               // DataUtils.getInstance(MainActivity.this).deleteNote(id,MainActivity.this);

                mDb.delete(NotesContract.TABLE_NAME,NotesContract._ID+" = "+id, null);

               mAdapter.swapCursor(DataUtils.getInstance(MainActivity.this).getAllNotes("date"));
              //  mAdapter.notifyDataSetChanged();
               // onResume();

            }
        }).attachToRecyclerView(mRecyclerView);








       // SearchView searchView=(SearchView) findViewById(R.id.search).getAct








/*

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            void refreshItems() {
                // Load items
                // ...
               // mAdapter.notifyDataSetChanged();
                setAdapter();

                // Load complete
                onItemsLoadComplete();
            }

            void onItemsLoadComplete() {
                // Update the adapter and notify data set changed
                // ...

                // Stop refresh animation
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
                Toast.makeText(MainActivity.this, "refresh", Toast.LENGTH_SHORT).show();
            }
        });
        */


        // Configure sign-in to request the user's ID, email address, and basic profile. ID and
// basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

// Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
         mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();




                //3singIn();




    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from
        //   GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                // Get account information
                String mFullName = acct.getDisplayName();
                String mEmail = acct.getEmail();
                Toast.makeText(this, mFullName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void singIn(){

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }



    private Cursor getAll() {

        Cursor cursor=mDb.query(
                NotesContract.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                NotesContract.COLUMN_TITLE);

       // DatabaseUtils.dumpCursorToString(cursor);



        return mDb.query(
                NotesContract.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                NotesContract.COLUMN_DATE
        );
    }

    public void setAdapter(){//temporal trick to update the cursor...

        mAdapter=new NotesAdapter(this,orden, this);
        mRecyclerView.setAdapter(mAdapter);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchItem=menu.findItem(R.id.action_search);
        final SearchView searchView=(SearchView) MenuItemCompat.getActionView(searchItem);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               // Toast.makeText(MainActivity.this, "PRECount= "+mAdapter.getItemCount()+" string= "+mAdapter.toString(), Toast.LENGTH_LONG).show();

                    mAdapter=new NotesAdapter(MainActivity.this,query,MainActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
               // Toast.makeText(MainActivity.this, "Count= "+mAdapter.getItemCount()+" string= "+mAdapter.toString(), Toast.LENGTH_LONG).show();



                return false;
            }



            @Override
            public boolean onQueryTextChange(String newText) {

                mAdapter=new NotesAdapter(MainActivity.this,orden,MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);

                return false;
            }



        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

       /* if (id== R.id.action_sort){

            if(orden=="date"){
                mAdapter=new NotesAdapter(this,"fav",this);
                mRecyclerView.setAdapter(mAdapter);
                orden="fav";
                Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show();

            }else {
                mAdapter=new NotesAdapter(this,"date",this);
                mRecyclerView.setAdapter(mAdapter);
                orden="date";
                Toast.makeText(this, "Date", Toast.LENGTH_SHORT).show();

            }

        }*/

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
       // Toast.makeText(this, "resume", Toast.LENGTH_SHORT).show();

       // mAdapter.swapCursor(DataUtils.getInstance(MainActivity.this).getAllNotes("date"));

        //mAdapter.notifyItemRemoved(0);

        setAdapter();
        super.onResume();





     //  mAdapter.swapCursor(getAll());

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        setAdapter();
      //08/08/2017  mAdapter.notifyDataSetChanged();
        //Toast.makeText(this, "restart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        singIn();

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");


       // myRef.setValue("Hello, World!");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int nUsers= (int) dataSnapshot.getChildrenCount();
                //String [] users=dataSnapshot.child("0");
                //
                // String value = dataSnapshot.getValue(String.class);

                Toast.makeText(MainActivity.this, "Value is: " + String.valueOf(nUsers), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(MainActivity.this, "Failed to read value error= "+ error.toException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNoteitemClick(int id, String title) {


        //open new notes activity
        Intent openNote=new Intent(MainActivity.this,NoteActivity.class);

        //Cursor c = DataUtils.getInstance(MainActivity.this).queryNoteById(id);

       Cursor c=getData(title);

        c.moveToFirst();
        int mNoteId=c.getInt(c.getColumnIndex(NotesContract._ID));
        String mNoteTitle= c.getString(c.getColumnIndex(NotesContract.COLUMN_TITLE));
        String mNote= c.getString(c.getColumnIndex(NotesContract.COLUMN_NOTE));
        String mImgPath= c.getString(c.getColumnIndex(NotesContract.COLUMN_IMG));
        String mNoteColor=c.getString(c.getColumnIndex(NotesContract.COLUMN_NOTE_COLOR));
        int mFavNote=c.getInt(c.getColumnIndex(NotesContract.COLUMN_FAV));

        openNote.putExtra("noteFav",mFavNote);
        openNote.putExtra("noteid",mNoteId);
        openNote.putExtra("noteTitle",mNoteTitle);
        openNote.putExtra("noteText",mNote);
        openNote.putExtra("noteImgPath",mImgPath);
        openNote.putExtra("noteColor",mNoteColor);

        startActivity(openNote);



    }





    private Cursor getData(String title) {
        String mtitle=title;

        String [] whereArgs=new String[]{
                mtitle
        };


        return mDb.query(
                NotesContract.TABLE_NAME,
                null,
                NotesContract.COLUMN_TITLE+" = ? ",
                whereArgs,
                null,
                null,
                null
        );
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
