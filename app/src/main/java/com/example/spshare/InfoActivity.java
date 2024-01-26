package com.example.spshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.GridView;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {

    String[] infoTitles;
    String[] infoDes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Shared Preferences stuff.
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String themeColor = mSharedPref.getString(getString(R.string.key_color), "light");

        if (themeColor.equals("light")) {
            setTheme(R.style.AppTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_info);
        } else {
            setTheme(R.style.AppThemeDark);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_info);
        }

        Resources res = getResources();
        infoTitles = res.getStringArray(R.array.info_title);
        infoDes = res.getStringArray(R.array.info_des);

        GridView grid = (GridView) findViewById(R.id.infoGrid);

        GridAdapter myAdapter = new GridAdapter(getApplicationContext(), infoTitles, infoDes);
        grid.setAdapter(myAdapter);
    }
}
