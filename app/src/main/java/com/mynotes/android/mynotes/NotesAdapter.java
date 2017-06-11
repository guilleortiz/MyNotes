package com.mynotes.android.mynotes;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mynotes.android.mynotes.data.NotesContract;

/**
 * Created by Guille on 10/06/2017.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private Cursor mcursor;
    private Context mcontext;

    final private NoteItemClickListener mOnClickListener;

    public interface NoteItemClickListener{
        void onNoteitemClick(int id);

    }

    public class  ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView notesTitle;
        TextView notesDate;



        public ViewHolder(View itemView) {
            super(itemView);
            notesTitle=(TextView) itemView.findViewById(R.id.itemTitle);
            notesDate=(TextView) itemView.findViewById(R.id.itemDate);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition=getAdapterPosition();
            mOnClickListener.onNoteitemClick(clickedPosition);
            Toast.makeText(mcontext, "from adaptes", Toast.LENGTH_SHORT).show();

        }
    }


    public NotesAdapter(Context mContext, Cursor cursor, NoteItemClickListener mOnClickListener) {

        this.mcontext=mContext;
        this.mcursor=cursor;
        //super(mcontext);
        this.mOnClickListener = mOnClickListener;
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

        mcursor.moveToPosition(position);

        final int id =mcursor.getInt(idIndex);
        String noteTitle=mcursor.getString(noteTitleIndex);
        String noteDate=mcursor.getString(noteDateIndex);

        //set values
        holder.itemView.setTag(id);
        holder.notesTitle.setText(noteTitle);
        holder.notesDate.setText(noteDate);





    }

    @Override
    public int getItemCount() {
        if(mcursor==null){

            return 0;
        }
        return  mcursor.getCount();
    }

}
