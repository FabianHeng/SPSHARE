package com.example.spshare;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NoteViewHolder> {
    private static final String TAG = NotesListAdapter.class.getSimpleName();
    private JSONObject notesList;
    private ArrayList<String> titles = new ArrayList<>();
    private JSONArray names;
    private LayoutInflater inflater;
    private NotesOpenHelper mDB;
    private String moduleName;

    public NotesListAdapter (Context context, JSONObject jsonObj, NotesOpenHelper mDB) {
        // store jsonobj from internet
        try {
            setItems(jsonObj, moduleName);
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage());
        }
        // get inflater for later use
        inflater = LayoutInflater.from(context);
        this.mDB = mDB;
    }

    @Override
    public NotesListAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate xml layout for single row item
        View itemView = inflater.inflate(R.layout.note_item, parent, false);
        // create view holder and return it
        return new NoteViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(NotesListAdapter.NoteViewHolder holder, int position) {
        // retrieve data from array and set as button text
        //Log.e(TAG, "COUNT = "+ getItemCount() + "position = " + position);
        holder.textView.setText(titles.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public void setItems (JSONObject notes, String moduleName) throws JSONException {
        notesList = notes;
        this.moduleName = moduleName;
        names = notesList.names();
        try {
            for (int i = 0; i < names.length(); i++) {
                titles.add(notesList.getJSONObject(names.getString(i)).getString("title"));
            }
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage());
            throw e;
        }
    }

    // ProductViewHolder
    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final String HOLDER_TAG = NoteViewHolder.class.getSimpleName();
        public final TextView textView;
        final NotesListAdapter mAdapter;

        public NoteViewHolder (View itemView, NotesListAdapter adapter) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.indv_note_textView);
            mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                // get json object at clicked position
                JSONObject obj = notesList.getJSONObject(names.getString(this.getAdapterPosition()));
                //Log.d(HOLDER_TAG, "Clicked" +
                //        this.getAdapterPosition() + " - " +
                //        obj.getString("title"));

                // go to individual note display page
                Context context = v.getContext();
                Intent intent = new Intent(context, ShowIndividualNote.class);
                intent.putExtra("NOTE_OBJECT", obj.toString());
                intent.putExtra("MODULE_NAME", moduleName);
                context.startActivity(intent);
            } catch (Exception e) {
                //Log.e(HOLDER_TAG, e.getMessage());
            }
        }
    }
}
