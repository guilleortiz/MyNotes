package com.mynotes.android.mynotes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mynotes.android.mynotes.data.DataUtils;
import com.mynotes.android.mynotes.data.NotesContract;
import com.mynotes.android.mynotes.data.NotesDbHelper;


public class MainActivity extends AppCompatActivity
        implements NotesAdapter.NoteItemClickListener {
    //dev branch

    private SQLiteDatabase mDb;

    private RecyclerView mRecyclerView;
    private NotesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private NotesAdapter.NoteItemClickListener mOnClickListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String orden="date";


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

        mLayoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);



        mAdapter=new NotesAdapter(this,orden, this);
        mRecyclerView.setAdapter(mAdapter);





        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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






/*

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            void refreshItems() {
                // Load items
                // ...
                mAdapter.notifyDataSetChanged();

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {

            mDb.delete(NotesContract.TABLE_NAME,NotesContract._ID+" = 1", null);

            //mDb.delete (NotesContract.TABLE_NAME,null,null);
            setAdapter();
            return true;
        }
        if (id== R.id.action_sort){
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

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        Toast.makeText(this, "resume", Toast.LENGTH_SHORT).show();

        mAdapter.swapCursor(DataUtils.getInstance(MainActivity.this).getAllNotes("date"));

        //mAdapter.notifyItemRemoved(0);

        setAdapter();
        super.onResume();





     //  mAdapter.swapCursor(getAll());

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        setAdapter();
        mAdapter.notifyDataSetChanged();
        //Toast.makeText(this, "restart", Toast.LENGTH_SHORT).show();
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
        int mFavNote=c.getInt(c.getColumnIndex(NotesContract.COLUMN_FAV));

        openNote.putExtra("noteFav",mFavNote);
        openNote.putExtra("noteid",mNoteId);
        openNote.putExtra("noteTitle",mNoteTitle);
        openNote.putExtra("noteText",mNote);
        openNote.putExtra("noteImgPath",mImgPath);

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
}
