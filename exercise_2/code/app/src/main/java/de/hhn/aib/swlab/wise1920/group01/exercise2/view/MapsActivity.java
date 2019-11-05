package de.hhn.aib.swlab.wise1920.group01.exercise2.view;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

import de.hhn.aib.swlab.wise1920.group01.exercise2.R;
import de.hhn.aib.swlab.wise1920.group01.exercise2.controller.MapFunctionality;
import de.hhn.aib.swlab.wise1920.group01.exercise2.controller.extensions.FuelSearchPricesService;
import de.hhn.aib.swlab.wise1920.group01.exercise2.controller.extensions.SearchService;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.MapObject;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.Position;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.FuelPricesReceivedInterface;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.SearchResultsReceivedInterface;

public class MapsActivity extends AppCompatActivity {

    MapView map;
    MapController mapController;
    private double latitude, longitude;
    private Marker marker;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private SearchView searchView;
    private ArrayList<Marker> markerArrayList;
    private MapFunctionality controller;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        callPermissions();
        setContentView(R.layout.activity_maps);
        map = findViewById(R.id.map);
        controller = new MapFunctionality(map, getIntent().getExtras(), this);
        map.setTileSource(new XYTileSource("Mapnik",
                0, 19, 256, ".png", new String[] {
                "https://swlab-maps.hhn.sisrv.de/" },"© OpenStreetMap contributors",
                new TileSourcePolicy(10,
                        TileSourcePolicy.FLAG_NO_BULK
                                | TileSourcePolicy.FLAG_NO_PREVENTIVE
                                | TileSourcePolicy.FLAG_USER_AGENT_MEANINGFUL
                                | TileSourcePolicy.FLAG_USER_AGENT_NORMALIZED
                )));

        map.setMultiTouchControls(true);
        mapController = (MapController) map.getController();
        mapController.setZoom(17);

        GeoPoint startpoint = new GeoPoint(49.122831, 9.210871); //Koordinaten der Hochschule Heilbronn
        mapController.setCenter(startpoint);
        marker = new Marker(map);
        marker.setSnippet("My current Location");
        marker.setIcon(getDrawable((R.drawable.ic_location_on_red_24dp)));
        map.invalidate();

        searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                controller.search(query);
                Toast.makeText(MapsActivity.this,query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
            markerArrayList = new ArrayList<>();
    }

    public void requestLocationUpdates()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            fusedLocationClient = new FusedLocationProviderClient(this);
            locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setFastestInterval(2000);
            locationRequest.setInterval(4000);
            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    latitude = locationResult.getLastLocation().getLatitude();
                    longitude = locationResult.getLastLocation().getLongitude();
                    //sync.sendLocation(latitude,longitude);
                    setCurrentPosition();
                }
            }, getMainLooper());
        }
        else
        {
            callPermissions();
        }
    }

    public void callPermissions()
    {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        Permissions.check(this, permissions, "Location permissions are required to get User Location",
                new Permissions.Options().setSettingsDialogTitle("Warning").setRationaleDialogTitle("Info"),
                new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        requestLocationUpdates();
                    }
                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        super.onDenied(context, deniedPermissions);
                        callPermissions();
                    }
                });
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

    public void setCurrentPosition()
    {
        GeoPoint gPt = new GeoPoint(latitude,longitude);
        //Log.e("gps",longitude + " " + latitude);
        marker.setPosition(gPt);
        marker.setAnchor(0.5f,0.5f);
        map.getOverlays().add(marker);
        map.invalidate();
    }
    public void setCenter(View v)
    {
        GeoPoint centerPoint = new GeoPoint(latitude,longitude);
        mapController.setCenter(centerPoint);
        Position position = new Position(latitude,longitude);
        //controller.getFuelPrices(position);
        //controller.requestUsersAround();
        controller.requestLocationHistory();
        map.invalidate();
    }
}