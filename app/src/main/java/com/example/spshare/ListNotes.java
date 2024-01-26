package com.example.spshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

public class ListNotes extends AppCompatActivity {
    private static final String TAG = ListNotes.class.getSimpleName();
    private String reqURL = "https://spshare-assignment.firebaseio.com/notes/";
    RecyclerView recyclerView;
    JSONObject notesList = new JSONObject();
    NotesListAdapter mAdapter;
    NotesOpenHelper mDB;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Shared Preferences stuff.
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String themeColor = mSharedPref.getString(getString(R.string.key_color), "light");

        if (themeColor.equals("light")) {
            setTheme(R.style.AppTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_notes_list);
        } else {
            setTheme(R.style.AppThemeDark);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_notes_list);
        }


        //Log.d(TAG, "Started NotesList activity");
        queue = SearchNotesActivity.queue;
        // get extras sent from SearchNotesActivity
        Bundle extras = this.getIntent().getExtras();
        String courseName = extras.get("courseName").toString();
        String moduleName = extras.get("moduleName").toString();

        setupRecycler();
        mDB = MainActivity.mDB;
        try {
            getNotesFromDB(queue, courseName, moduleName);
        } catch (Exception e) {
            //Log.e(TAG, "??????????????????????????????");
        }
    }

    private void setupRecycler() {
        recyclerView = findViewById(R.id.recyclerview);
        mAdapter = new NotesListAdapter(this, notesList, mDB);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getNotesFromDB(RequestQueue queue, final String course, final String module) {
        // request string response based on user's input
        reqURL += course + "/" + module + ".json";
        StringRequest strReq = new StringRequest(Request.Method.GET, reqURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d(TAG, "Gotten response from: " + reqURL);
                        //Log.d(TAG, response);
                        // If there is no notes in that course + module,
                        // show toast message
                        if (response.equals("null") || response == null) {
                            Toast.makeText(getApplicationContext(), "There are no notes available for this module", Toast.LENGTH_LONG).show();
                        } else {
                            parseData(response, module);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e(TAG, error.getMessage());
            }}
        );

        // add request to queue
        queue.add(strReq);

    }

    private void parseData(String response, String module) {
        try {
            JSONObject object = new JSONObject(response);
            mAdapter.setItems(object, module);
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            //e.printStackTrace();
            //Log.e(TAG, e.getMessage());
            finish();
            return;
        }
    }
}
