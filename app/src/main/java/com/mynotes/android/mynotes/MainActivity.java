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
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private NotesAdapter.NoteItemClickListener mOnClickListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);


        setContentView(R.layout.activity_main);

       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        NotesDbHelper dbHelper=new NotesDbHelper(this);//create db

        mDb=dbHelper.getWritableDatabase();
        DataUtils.insertFakeData(mDb);


        mRecyclerView=(RecyclerView)findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);



        Cursor cursor=getAll();

        mAdapter=new NotesAdapter(this,cursor, this);//mOnClickListener?? TODO 1 //
        mRecyclerView.setAdapter(mAdapter);





        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = MainActivity.this;
                Class destinationActivity = NoteActivity.class;

                Intent startActivityIntent = new Intent(context, destinationActivity);


                startActivity(startActivityIntent);




            }
        });
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mDb.delete (NotesContract.TABLE_NAME,null,null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {


        super.onResume();
        Toast.makeText(this, "resume", Toast.LENGTH_SHORT).show();

       // mAdapter.swapCursor(getAll());

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mAdapter.notifyDataSetChanged();
        Toast.makeText(this, "restart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoteitemClick(int index) {//This callback is invoked when you click on an item in the list.
        Toast.makeText(this,  String.valueOf(index), Toast.LENGTH_SHORT).show();

        //open new notes activity
        Intent openNote=new Intent(MainActivity.this,NoteActivity.class);

        //openNote.putExtra()

        startActivity(openNote);




    }

}
