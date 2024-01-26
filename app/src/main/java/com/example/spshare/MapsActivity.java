package com.example.spshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import static com.example.spshare.ShowIndividualNote.REQUEST_LOCATION_PERMISSION;
import static com.example.spshare.ShowIndividualNote.latitude;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private String themeColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        themeColor = mSharedPref.getString(getString(R.string.key_color), "light");

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra(ShowIndividualNote.latitude, 0);
        longitude = intent.getDoubleExtra(ShowIndividualNote.longitude, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng currentLoc = new LatLng(latitude, longitude);
        float zoom = 15;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, zoom));

        try {
            // Check for app theme, if dark, set map style to dark mode.
            if (themeColor.equals("light")) {
                googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.map_style));
            } else {
                googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.map_style_dark));
            }
        } catch (Resources.NotFoundException e) {
            //Log.e("MAPP ERROR", "Can't find style. Error: ", e);
        }

        LatLng stdA1 = new LatLng(1.313716, 103.765623);
        LatLng stdA2 = new LatLng(1.308718, 103.779032);
        LatLng stdA3 = new LatLng(1.309769, 103.773818);
        LatLng stdA4 = new LatLng(1.308418, 103.775545);
        GroundOverlayOptions homeOverlay1 = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.studyarea))
                .position(stdA1,100);
        mMap.addGroundOverlay(homeOverlay1);
        GroundOverlayOptions homeOverlay2 = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.studyarea))
                .position(stdA2,100);
        mMap.addGroundOverlay(homeOverlay2);
        GroundOverlayOptions homeOverlay3 = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.studyarea))
                .position(stdA3,100);
        mMap.addGroundOverlay(homeOverlay3);
        GroundOverlayOptions homeOverlay4 = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.studyarea))
                .position(stdA4,100);
        mMap.addGroundOverlay(homeOverlay4);

        setPoiClick(mMap);

        setMapLongClick(mMap);

        enableMyLocation();
    }

    public void setMapLongClick(final GoogleMap map) {
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            }
        });
    }

    public void setPoiClick(final GoogleMap map) {
        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {
                Marker poiMarker = mMap.addMarker(new MarkerOptions()
                        .position(pointOfInterest.latLng)
                        .title(pointOfInterest.name));
            }
        });
    }

    private void enableMyLocation() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                    break;
                }
        }
    }
}
