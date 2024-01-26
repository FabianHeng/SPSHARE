package com.example.spshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import static com.example.spshare.MainActivity.hasConnection;

public class ShareActivity extends AppCompatActivity {

    final String TAG = "ShareActivity";

    private String name;

    private RequestQueue queue;

    private SavedNotesOpenHelper nDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Shared Preferences stuff.
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String themeColor = mSharedPref.getString(getString(R.string.key_color), "light");

        if (themeColor.equals("light")) {
            setTheme(R.style.AppTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_share);
        } else {
            setTheme(R.style.AppThemeDark);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_share);
        }

        Intent intent = getIntent();
        name = intent.getStringExtra(EnterNameActivity.EXTRA_TEXT);

        TextView username = (TextView) findViewById(R.id.share_note_username);
        username.setText("Hello, " + name);

        Spinner courses = (Spinner) findViewById(R.id.spinner_course);
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<String>(ShareActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.courses));
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courses.setAdapter(courseAdapter);

        courses.setOnItemSelectedListener(new CustomItemSelectedListener());

        nDB = new SavedNotesOpenHelper(this);
    }

    public void addNotes(View view) {
        String content = ((EditText) findViewById(R.id.edit_notes)).getText().toString();

        String title = ((EditText) findViewById(R.id.edit_title)).getText().toString();

        Spinner courses = (Spinner) findViewById(R.id.spinner_course);
        String coursename = courses.getSelectedItem().toString();

        Spinner modules = (Spinner) findViewById(R.id.spinner_module);
        String modulename = modules.getSelectedItem().toString();

        //Log.e("????", coursename + modulename + content + title + name);
        if (hasConnection) {
            //Log.d(TAG, "has connection send data");
            sendData(coursename, modulename, content, title, name);
        } else {
            //Log.d(TAG, "has no connection - saving data");
            // add notes to sqlite db
            saveDataForSending(coursename, modulename, content, title, name);
        }


        Intent successIntent = new Intent(this, ShareSucessActivity.class);
        startActivity(successIntent);
    }

    private void saveDataForSending(String coursename, String modulename, String content, String title, String name) {
        // save data to sqlite
        nDB.insert(coursename, modulename, content,  name, title);
    }


    class CustomItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Spinner courses = (Spinner) findViewById(R.id.spinner_course);
            String coursename = courses.getSelectedItem().toString();

            Spinner modules = (Spinner) findViewById(R.id.spinner_module);
            ArrayAdapter<String> moduleAdapter = new ArrayAdapter<>(ShareActivity.this,
                    android.R.layout.simple_list_item_1, MainActivity.mDB.queryModules(coursename));
            modules.setAdapter(moduleAdapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public void sendData(String coursename, String modulename, String content, String title, String username) {
        final String c = coursename;
        final String m = modulename;
        final String con = content;
        final String n = username;
        final String t = title;
        String url = "https://spshare-assignment.firebaseio.com/notes/" + coursename + "/" + modulename + ".json";

        // Request a string response from url.

        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d(TAG, "Response: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, error.getMessage());
            }
        }){

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    JSONObject object = new JSONObject();
                    object.put("title", t);
                    object.put("username", n);
                    object.put("content", con);

                    Log.d(TAG, object.toString());

                    // send the bytes
                    return object.toString().getBytes();

                } catch (Exception e) {
                    //Log.e(TAG, e.getMessage());
                }
                return super.getBody();
            }

            // set content type to json
            public String getBodyContentType () {
                return "application/json";
            }
        };

        queue = Volley.newRequestQueue(this);
        queue.add(req);
    }
}
