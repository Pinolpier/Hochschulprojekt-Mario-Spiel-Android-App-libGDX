package de.hhn.aib.swlab.wise1920.group01.exercise2.controller;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.TilesOverlay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;

import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import de.hhn.aib.swlab.wise1920.group01.exercise2.R;
import de.hhn.aib.swlab.wise1920.group01.exercise2.controller.extensions.FuelSearchPricesService;
import de.hhn.aib.swlab.wise1920.group01.exercise2.controller.extensions.SearchService;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.MapObject;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.LocationHistoryReceivedInterface;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.Position;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.TimestampedPosition;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.UsersAroundReceivedInterface;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.FuelPricesReceivedInterface;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.SearchResultsReceivedInterface;

import static android.os.Looper.getMainLooper;

public class MapFunctionality {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private MapView map;
    private SyncService sync;
    private FuelSearchPricesService fuelService;
    private SearchService searchService;
    private ArrayList<Marker> markerArrayList, timeStampedList;
    private Context context;
    private double latitude, longitude;
    MapController mapController;
    CountDownTimer timer;
    private long syncInterval = 60000;
    Boolean tankSearch = true;
    Position position;
    private Marker marker;

    public MapFunctionality(MapView map, Bundle bundle, Context context) {
        this.map = map;
        sync = new SyncService(context, bundle.getString("jwt"), bundle.getString("id"), bundle.getString("username"), bundle.getString("description"), bundle.getString("password"), bundle.getDouble("privacy"));
        fuelService = new FuelSearchPricesService(context);
        searchService = new SearchService();
        markerArrayList = new ArrayList<>();
        timeStampedList = new ArrayList<>();
        this.context = context;
        mapController = new MapController(map);
        marker = new Marker(map);
        marker.setSnippet("My current Location");
        marker.setIcon(context.getDrawable((R.drawable.ic_location_on_red_24dp)));
        callPermissions();

        timer = new CountDownTimer(Long.MAX_VALUE, syncInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                    if(tankSearch){
                       // getFuelPrices(new Position(latitude,longitude));
                    }
                }

            @Override
            public void onFinish() {
                Log.wtf("Sync Service: ", "Lol, the time has ended, the universe should have ended before this message can be shown...");
            }
        };
        timer.start();
    }
    public void setCenter()
    {
        GeoPoint centerPoint = new GeoPoint(latitude,longitude);
        mapController.setCenter(centerPoint);
        map.invalidate();
    }
    public void callPermissions()
    {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        Permissions.check(context, permissions, "Location permissions are required to get User Location",
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

    public void requestLocationUpdates()
    {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            fusedLocationClient = new FusedLocationProviderClient(context);
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

    public void setCurrentPosition()
    {
        GeoPoint gPt = new GeoPoint(latitude,longitude);
        //Log.e("gps",longitude + " " + latitude);
        marker.setPosition(gPt);
        marker.setAnchor(0.5f,0.5f);
        map.getOverlays().add(marker);
        map.invalidate();
    }

    public void requestUsersAround() {
        //TODO Set the correct radius or use an appropriate constant value that should be defined as constant.
        sync.getUsersAround(10, new UsersAroundReceivedInterface() {
            @Override
            public void onSuccess(ArrayList<MapObject> usersAround) {
                setSearchResults(usersAround);
            }
            @Override
            public void onFailure() {
                Toast.makeText(context,"Fehler bei UsersArround",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void requestLocationHistory() {
        //TODO Set the earliest timestamp in milliseconds unix otherwise 1st january 2010 will be used.
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019,11,01);
        sync.getLocationHistory(calendar.getTimeInMillis(), new LocationHistoryReceivedInterface() {
            @Override
            public void onSuccess(ArrayList<TimestampedPosition> locationHistory) {
                deleteSearchMarkers(timeStampedList);
                Log.d("MapsActivity", "setSearchResults has been called. Length of the array arg is: " + locationHistory.size());
                if(locationHistory.size()>=1) {
                    for (int counter = 0; counter < locationHistory.size(); counter++) {
                        //GeoPoint searchPoint = new GeoPoint(searchResults[counter].getLatitude(), searchResults[counter].getLongitude());
                        Marker searchMarker = new Marker(map);
                        searchMarker.setIcon(context.getDrawable(R.drawable.ic_pin_drop_blue_24dp));
                        GeoPoint gpt = new GeoPoint(locationHistory.get(counter).getLatitude(),locationHistory.get(counter).getLongitude());
                        searchMarker.setPosition(gpt);
                        searchMarker.setAnchor(0.5f, 0.5f);
                        searchMarker.setTitle(locationHistory.get(counter).getDateString());
                        map.getOverlays().add(searchMarker);
                        markerArrayList.add(searchMarker);
                    }
                    map.invalidate();
                }
                else
                    Toast.makeText(context,"Suchbegriff konnte nicht gefunden werden!",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure() {
            Toast.makeText(context,"Fehler", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getFuelPrices(Position position) {
        fuelService.getFuelPrices(position.getLatitude(), position.getLongitude(), new FuelPricesReceivedInterface() {
            @Override
            public void onSuccess(ArrayList<MapObject> fuelPrices) {
                setSearchResults(fuelPrices);
            }

            @Override
            public void onFailure() {
                Toast.makeText(context,"Fehler", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void search(String searchTerm) {
        searchService.search(searchTerm, new SearchResultsReceivedInterface() {
            @Override
            public void onSuccess(ArrayList<MapObject> searchResultsList) {
            setSearchResults(searchResultsList);
            }

            @Override
            public void onFailure() {
            Toast.makeText(context,"no response",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setSearchResults(ArrayList<MapObject> searchResults){
        deleteSearchMarkers(markerArrayList);
        Log.d("MapsActivity", "setSearchResults has been called. Length of the array arg is: " + searchResults.size());
        if(searchResults.size()>=1) {
            for (int counter = 0; counter < searchResults.size(); counter++) {
                //GeoPoint searchPoint = new GeoPoint(searchResults[counter].getLatitude(), searchResults[counter].getLongitude());
                Marker searchMarker = new Marker(map);
                searchMarker.setIcon(context.getDrawable(R.drawable.ic_pin_drop_blue_24dp));
                searchMarker.setPosition(searchResults.get(counter));
                searchMarker.setAnchor(0.5f, 0.5f);
                searchMarker.setTitle(searchResults.get(counter).getLabel());
                searchMarker.setSnippet(searchResults.get(counter).getDescription());
                map.getOverlays().add(searchMarker);
                markerArrayList.add(searchMarker);
            }
            map.invalidate();
        }
        else
            Toast.makeText(context,"Suchbegriff konnte nicht gefunden werden!",Toast.LENGTH_SHORT).show();
    }

    public void deleteSearchMarkers(ArrayList<Marker> markers){
        if(!markers.isEmpty()){
            for(Marker marker:markers){
                map.getOverlays().remove(marker);
            }
            markers.clear();
        }
    }
}