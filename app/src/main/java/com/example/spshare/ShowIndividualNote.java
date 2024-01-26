package com.example.spshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.SharedMemory;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

public class ShowIndividualNote extends AppCompatActivity {

    // Define EXTRA_TEXT for passing variables
    public static final String latitude = "com.example.spshare.LAT";
    public static final String longitude = "com.example.spshare.LONG";

    private static final String TAG = ShowIndividualNote.class.getSimpleName();
    private TextView title, modName, username, content;
    FusedLocationProviderClient flpc;

    //private Button mapButton = findViewById(R.id.btn_view_map);
    private Bundle b;

    static final int REQUEST_LOCATION_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Shared Preferences stuff.
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String themeColor = mSharedPref.getString(getString(R.string.key_color), "light");

        if (themeColor.equals("light")) {
            setTheme(R.style.AppTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show_individual_note);
        } else {
            setTheme(R.style.AppThemeDark);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show_individual_note);
        }

        // get extras from intent
        b = this.getIntent().getExtras();

        // get xml view objects
        title = this.findViewById(R.id.indv_note_title);
        modName = this.findViewById(R.id.indv_note_modName);
        username = this.findViewById(R.id.indv_note_username);
        content = this.findViewById(R.id.indv_note_content);

        flpc = LocationServices.getFusedLocationProviderClient(this);

        try {
            JSONObject noteObject = new JSONObject(b.get("NOTE_OBJECT").toString());
            setViewText(noteObject);
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage());
        }

    }

    // Get location method.
    public void getLocation(final View view) {
        //Log.d(TAG, "location retrieval button clicked");
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            //Log.d(TAG, "Location retrieval started");

            Task t = flpc.getLastLocation();

            // get data from async task created by flpc
            t.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        //Log.d(TAG, "LAT = " + location.getLatitude());
                        //Log.d(TAG, "LONG = " + location.getLongitude());
                        //Log.d(TAG, "TIME = " + location.getTime());

                        Intent intent = new Intent(view.getContext(), MapsActivity.class);
                        intent.putExtra(latitude, location.getLatitude());
                        intent.putExtra(longitude, location.getLongitude());
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to get location. Please try again", Toast.LENGTH_SHORT).show();
                        //Log.e(TAG, "Unable to get location!");
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION: {
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Button b = (Button) findViewById(R.id.btn_view_map);
                    b.performClick();
                } else {
                    Toast.makeText(this, "Location service is denied.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default: {break;}
        }
    }


    private void setViewText(JSONObject noteObject) throws JSONException {
        // JSONObject has content, title and username
        // get module name from intent's extra
        title.setText(noteObject.getString("title"));
        modName.setText(b.getString("MODULE_NAME"));
        username.setText(noteObject.getString("username"));
        content.setText(noteObject.getString("content"));
    }


}
