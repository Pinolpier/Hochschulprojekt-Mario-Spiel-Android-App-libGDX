package de.hhn.aib.swlab.wise1920.group01.exercise2.view;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import de.hhn.aib.swlab.wise1920.group01.exercise2.controller.SyncService;
import de.hhn.aib.swlab.wise1920.group01.exercise2.controller.extensions.SearchService;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.MapObject;

public class MapsActivity extends AppCompatActivity {

    MapView map;
    MapController mapController;
    private double latitude, longitude;
    private Marker marker;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private SyncService sync;
    private SearchView searchView;
    private ArrayList<Marker> markerArrayList;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        sync = new SyncService(this, bundle.getString("jwt"), bundle.getString("id"), bundle.getString("username"), bundle.getString("description"), bundle.getString("password"), bundle.getDouble("privacy"));
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
        callPermissions();
        setContentView(R.layout.activity_maps);
        map = findViewById(R.id.map);
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

        GeoPoint startpoint = new GeoPoint(49.122831, 9.210871);
        mapController.setCenter(startpoint);
        marker = new Marker(map);
        marker.setSnippet("My current Location");
        marker.setIcon(getDrawable((R.drawable.ic_location_on_red_24dp)));
        map.invalidate();

        searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                deleteSearchMarkers();
                SearchService searchService = new SearchService();
                Toast.makeText(MapsActivity.this, searchView.getQuery().toString(), Toast.LENGTH_SHORT).show();
                searchService.search(searchView.getQuery().toString(),MapsActivity.this);
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
                    sync.sendLocation(latitude,longitude);
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
        map.invalidate();
    }

    public void setSearchResults(MapObject[] searchResults) {

        for(int counter=0;counter<searchResults.length;counter++){
            GeoPoint searchPoint = new GeoPoint(searchResults[counter].getLatitude(),searchResults[counter].getLongitude());
            Marker searchMarker = new Marker(map);
            searchMarker.setIcon(getDrawable(R.drawable.ic_pin_drop_blue_24dp));
            searchMarker.setPosition(searchPoint);
            searchMarker.setAnchor(0.5f,0.5f);
            searchMarker.setSnippet(searchResults[counter].getLabel());
            map.getOverlays().add(searchMarker);
            markerArrayList.add(searchMarker);
        }
        GeoPoint centerPoint = new GeoPoint(searchResults[0].getLatitude(),searchResults[0].getLongitude());
        mapController.setCenter(centerPoint);
        map.invalidate();
    }

    public void deleteSearchMarkers(){
        if(!markerArrayList.isEmpty()){
            for(Marker marker:markerArrayList){
                map.getOverlays().remove(marker);
            }
        }

    }
}