package com.example.spshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class ShareSucessActivity extends AppCompatActivity {

    private static final String TAG = ShareSucessActivity.class.getSimpleName();
    private boolean hasConnection;
    private SavedNotesOpenHelper nDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Shared Preferences stuff.
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String themeColor = mSharedPref.getString(getString(R.string.key_color), "light");

        if (themeColor.equals("light")) {
            setTheme(R.style.AppTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_share_sucess);
        } else {
            setTheme(R.style.AppThemeDark);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_share_sucess);
        }
        nDB = new SavedNotesOpenHelper(this);
        tryConnect();
    }

    public void openEnterNameActivity(View view){
        Intent intent = new Intent(this, EnterNameActivity.class);
        startActivity(intent);
    }

    public void viewNotes(View view) {
        Intent intent = new Intent(this, SearchNotesActivity.class);
        startActivity(intent);
    }

    private void tryConnect() {
        hasConnection = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()){
            hasConnection = true;
            MainActivity.hasConnection = true;
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
}
