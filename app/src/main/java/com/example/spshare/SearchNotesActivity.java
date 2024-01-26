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
import android.widget.Spinner;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SearchNotesActivity extends AppCompatActivity {
    private static final String TAG = SearchNotesActivity.class.getSimpleName();
    private Spinner courseSpinner, moduleSpinner;
    private NotesOpenHelper mDB = MainActivity.mDB;

    public static RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Shared Preferences stuff.
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String themeColor = mSharedPref.getString(getString(R.string.key_color), "light");

        if (themeColor.equals("light")) {
            setTheme(R.style.AppTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_notes);
        } else {
            setTheme(R.style.AppThemeDark);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_notes);
        }

        courseSpinner = findViewById(R.id.course_name_search);
        moduleSpinner = findViewById(R.id.module_name_search);
        addSpinnerItems();

        // create queue for requests
        queue = Volley.newRequestQueue(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.search_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "FAB Clicked");

                // get user input from spinner ***
                String courseName = courseSpinner.getSelectedItem().toString();
                String moduleName = moduleSpinner.getSelectedItem().toString();

                // search for the notes under user specified fields
                // start NotesList Activity and pass it courseName and moduleName
                Intent intent = new Intent(view.getContext(), ListNotes.class);
                intent.putExtra("courseName", courseName);
                intent.putExtra("moduleName", moduleName);
                startActivity(intent);
            }
        });
    }

    private class CustomOnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String coursename = courseSpinner.getSelectedItem().toString();

            ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchNotesActivity.this,
                    R.layout.support_simple_spinner_dropdown_item, MainActivity.mDB.queryModules(coursename));
            moduleSpinner.setAdapter(adapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // do nothing
        }
    }

    private void addSpinnerItems() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.courses));
        courseSpinner.setAdapter(adapter);
        courseSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
}
