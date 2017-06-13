package com.mynotes.android.mynotes;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mynotes.android.mynotes.data.NotesContract;

/**
 * Created by Guille on 10/06/2017.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private Cursor mcursor;
    private Context mcontext;

    final private NoteItemClickListener mOnClickListener;

    public interface NoteItemClickListener{
        void onNoteitemClick(int index);

    }

    public class  ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView notesTitle;
        TextView notesDate;
        TextView notesDataType;



        public ViewHolder(View itemView) {
            super(itemView);
            notesTitle=(TextView) itemView.findViewById(R.id.itemTitle);
            notesDataType=(TextView) itemView.findViewById(R.id.itemDataType);
            notesDate=(TextView) itemView.findViewById(R.id.itemDate);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition=getAdapterPosition();
            mOnClickListener.onNoteitemClick(clickedPosition);


        }
    }


    public NotesAdapter(Context mContext, Cursor cursor, NoteItemClickListener listener) {

        mcontext=mContext;
        mcursor=cursor;
        //super(mcontext);
        mOnClickListener = listener;
    }


    @Override//holds the view for each note
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mcontext)
                .inflate(R.layout.note_item,parent,false);


        return  new ViewHolder(view);
    }

    @Override//replace with data orm dataset
    public void onBindViewHolder(ViewHolder holder, int position) {

        int idIndex =mcursor.getColumnIndex(NotesContract._ID);//we get the id to future swipe delete
        int noteTitleIndex=mcursor.getColumnIndex(NotesContract.COLUMN_TITLE);
        int noteDateIndex=mcursor.getColumnIndex(NotesContract.COLUMN_DATE);
        int noteDataTypeIndex=mcursor.getColumnIndex(NotesContract.COLUMN_DATA_TYPE);

        mcursor.moveToPosition(position);

        final int id =mcursor.getInt(idIndex);
        String noteTitle=mcursor.getString(noteTitleIndex);
        String noteDataType=mcursor.getString(noteDataTypeIndex);
        String noteDate=mcursor.getString(noteDateIndex);


        //set values
        holder.itemView.setTag(id);
        holder.notesTitle.setText(noteTitle);
        holder.notesDataType.setText(noteDataType);
        holder.notesDate.setText(noteDate);





    }

    @Override
    public int getItemCount() {
        if(mcursor==null){

            return 0;
        }
        return  mcursor.getCount();
    }



    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mcursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mcursor;
        this.mcursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }




}
