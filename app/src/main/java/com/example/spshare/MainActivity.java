package com.example.spshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.w3c.dom.Text;

import java.net.URL;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    protected static NotesOpenHelper mDB;
    protected static SavedNotesOpenHelper nDB;
    public static boolean hasConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Change back into main activity after the splash screen is shown.
        // Shared Preferences stuff.
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String themeColor = mSharedPref.getString(getString(R.string.key_color), "light");

        if (themeColor.equals("light")) {
            setTheme(R.style.AppTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        } else {
            setTheme(R.style.AppThemeDark);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        }

        mDB = new NotesOpenHelper(this);
        nDB = new SavedNotesOpenHelper(this);
        tryConnect();
    }

    public void openEnterNameActivity(View view) {
        Intent intent = new Intent(this, EnterNameActivity.class);
        startActivity(intent);
    }

    public void viewNotes(View view) {
        //Log.d(TAG, "viewNotes clicked.");
        if (hasConnection) {
            Intent intent = new Intent(this, SearchNotesActivity.class);
            startActivity(intent);
        } else {
            // toast for no internet
            //Log.d(TAG, "no internet conn.");
            Toast.makeText(this, "Please connect to the internet to view notes", Toast.LENGTH_LONG).show();
        }
    }

    private void tryConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()){
            hasConnection = true;
            //check if there is saved notes
            //Log.d(TAG, "SavedNotes size = " + nDB.getSize());
            if (nDB.getSize() > 0) {
                // update online db
                nDB.sendAllNotes();
            }
        } else {
            hasConnection = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_info:
                openInfo();
                return true;
            default:
            // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }
    public void openSettings(){
        Log.d(TAG, "settings clicked.");
        //Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        //startActivity(intent);
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openInfo() {
        Intent intent = new Intent(MainActivity.this, InfoActivity.class);
        startActivity(intent);
    }

}
