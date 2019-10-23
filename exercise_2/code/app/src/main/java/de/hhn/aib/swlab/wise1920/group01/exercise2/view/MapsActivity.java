package de.hhn.aib.swlab.wise1920.group01.exercise2.view;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import de.hhn.aib.swlab.wise1920.group01.exercise2.R;

public class MapsActivity extends AppCompatActivity {

    MapView map;
    MapController mapController;
    private double latitude, longitude;
    private Marker marker;
    private FusedLocationProviderClient fusedLocationClient;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!check_permissions())
        {
            enable_service();
        }
        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                       latitude = location.getLatitude();
                       longitude = location.getLongitude();
                       System.out.println(latitude + " " + longitude);
                    }
                });
        setContentView(R.layout.activity_maps);
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        mapController = (MapController) map.getController();
        mapController.setZoom(17);

        GeoPoint startpoint = new GeoPoint(49.122831, 9.210871);
        mapController.setCenter(startpoint);
        marker = new Marker(map);
        marker.setIcon(getDrawable((R.drawable.ic_location_on_red_24dp)));
        map.invalidate();
    }


    public void enable_service()
    {
        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
    }

    private boolean check_permissions()
    {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                enable_service();
            } else {
                check_permissions();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return true;
    }

    public void setCurrentPosition(Intent intent)
    {

        GeoPoint gPt = new GeoPoint(latitude,longitude);

        Log.e("gps",longitude + " " + latitude);

        marker.setPosition(gPt);
        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);
        //mapController.setCenter(gPt);
        map.invalidate();
    }

    public void setCenter(View v)
    {
        GeoPoint centerPoint = new GeoPoint(latitude,longitude);
        mapController.setCenter(centerPoint);
        map.invalidate();
    }
}