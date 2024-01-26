package com.example.spshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EnterNameActivity extends AppCompatActivity {

    // Define EXTRA_TEXT for passing variables
    public static final String EXTRA_TEXT = "com.example.spshare.EXTRA_TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Shared Preferences stuff.
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String themeColor = mSharedPref.getString(getString(R.string.key_color), "light");
        // Check shared preference username.
        String username = mSharedPref.getString(getString(R.string.key_name), "Anonymous");

        if (themeColor.equals("light")) {
            setTheme(R.style.AppTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_enter_name);
            EditText editText = (EditText) findViewById(R.id.name);
            editText.setText(username);
        } else {
            setTheme(R.style.AppThemeDark);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_enter_name);
            EditText editText = (EditText) findViewById(R.id.name);
            editText.setText(username);
        }
        
        Button button = (Button) findViewById(R.id.to_share_activity_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShareActivity();
            }
        });
    }

    public void openShareActivity(){
        EditText editText = (EditText) findViewById(R.id.name);
        String name = editText.getText().toString();

        Intent intent = new Intent(this, ShareActivity.class);
        intent.putExtra(EXTRA_TEXT, name);
        startActivity(intent);
    }
}
