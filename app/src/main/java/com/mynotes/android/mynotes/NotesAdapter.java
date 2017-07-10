package com.mynotes.android.mynotes;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mynotes.android.mynotes.data.DataUtils;
import com.mynotes.android.mynotes.data.NotesContract;

import java.util.ArrayList;

/**
 * Created by Guille on 10/06/2017.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private Cursor mcursor;
    private Context mcontext;

    final private NoteItemClickListener mOnClickListener;




    public Cursor swapCursor (Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)

        if (mcursor == c) {
            return null ;// bc nothing has changed
        }

        Cursor temp = c;
        mcursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            notifyDataSetChanged();
        }
        return temp;
    }
    public NotesAdapter(Context mContext, String order, NoteItemClickListener listener) {

        mcontext=mContext;
        mcursor= DataUtils.getInstance(mContext).getAllNotes(order);

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
        int notePreviewIndex=mcursor.getColumnIndex(NotesContract.COLUMN_NOTE);
        int noteDateIndex=mcursor.getColumnIndex(NotesContract.COLUMN_DATE);
        int noteFavIndex=mcursor.getColumnIndex(NotesContract.COLUMN_FAV);

        int noteFotoIndex=mcursor.getColumnIndex(NotesContract.COLUMN_IMG);

        mcursor.moveToPosition(position);

        final int id =mcursor.getInt(idIndex);
        String noteTitle=mcursor.getString(noteTitleIndex);
        String notePreview=mcursor.getString(notePreviewIndex);
        String noteDate=mcursor.getString(noteDateIndex);
        String noteFoto=mcursor.getString(noteFotoIndex);
        int noteFav=mcursor.getInt(noteDateIndex);


        //set values
        holder.itemView.setTag(id);
        holder.notesTitle.setText(noteTitle);
        holder.notesDate.setText(noteDate);
        if (noteFav==1){
            holder.notesfav.setChecked(true);
        }else {
           // holder.notesfav.setChecked(false);
        }


        Glide.with(mcontext).load(noteFoto).into(holder.notesFoto);

        holder.notestextPreview.setText(shortNote(notePreview));



    }

    public String shortNote(String fullNote){
        //adding ... to the preview, fix adding max length to texview




        //fullNote=fullNote+"...";



        return fullNote;
    }

    public interface NoteItemClickListener{
        void onNoteitemClick(int id,String title);

    }

    public class  ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView notesTitle;
        TextView notesDate;
        TextView notestextPreview;
        ImageView notesFoto;
        CheckBox notesfav;



        public ViewHolder(View itemView) {
            super(itemView);
            notesTitle=(TextView) itemView.findViewById(R.id.itemTitle);
            notestextPreview=(TextView) itemView.findViewById(R.id.notePreview);

            notesDate=(TextView) itemView.findViewById(R.id.itemDate);
            notesFoto=(ImageView)itemView.findViewById(R.id.foto);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition=getAdapterPosition();


            //mcursor.moveToPosition(clickedPosition);//returns boolean

            ArrayList<String> mArrayList = new ArrayList<String>();

            mcursor.moveToFirst();
            while(!mcursor.isAfterLast()) {
                mArrayList.add(mcursor.getString(mcursor.getColumnIndex(NotesContract.COLUMN_TITLE))); //add the item
               // mArrayList.add(mcursor.getString(mcursor.getColumnIndex(NotesContract.COLUMN_NOTE)));
                mcursor.moveToNext();
            }


            mOnClickListener.onNoteitemClick(clickedPosition,mArrayList.get(clickedPosition));


        }
    }




    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */






    @Override
    public int getItemCount() {
        if(mcursor==null){

            return 0;
        }
        return  mcursor.getCount();
    }








}
