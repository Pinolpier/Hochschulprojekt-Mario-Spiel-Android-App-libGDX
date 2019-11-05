package de.hhn.aib.swlab.wise1920.group01.exercise2.controller;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import de.hhn.aib.swlab.wise1920.group01.exercise2.R;
import de.hhn.aib.swlab.wise1920.group01.exercise2.controller.extensions.FuelSearchPricesService;
import de.hhn.aib.swlab.wise1920.group01.exercise2.controller.extensions.PoiSearchService;
import de.hhn.aib.swlab.wise1920.group01.exercise2.controller.extensions.SearchService;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.MapObject;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.LocationHistoryReceivedInterface;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.Position;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.TimestampedPosition;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.UsersAroundReceivedInterface;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.FuelPricesReceivedInterface;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.PoisReceivedInterface;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.SearchResultsReceivedInterface;

import static android.os.Looper.getMainLooper;

public class MapFunctionality {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private MapView map;
    private SyncService sync;
    private FuelSearchPricesService fuelService;
    private SearchService searchService;
    private PoiSearchService poiSearchService;
    private ArrayList<Marker> searchMarkerArrayList, timeStampedMarkerList,fuelMarkerList, poiMarkerList;
    private Context context;
    private double latitude, longitude;
    private MapController mapController;
    private CountDownTimer timer;
    private long syncInterval = 60000;
    private Boolean tankSearch = true;
    private Marker marker;

    public MapFunctionality(MapView map, Bundle bundle, Context context) {
        this.map = map;
        sync = new SyncService(context, bundle.getString("jwt"), bundle.getString("id"), bundle.getString("username"), bundle.getString("description"), bundle.getString("password"), bundle.getDouble("privacy"));
        fuelService = new FuelSearchPricesService(context);
        searchService = new SearchService();
        poiSearchService = new PoiSearchService(context);
        searchMarkerArrayList = new ArrayList<>();
        timeStampedMarkerList = new ArrayList<>(); fuelMarkerList = new ArrayList<>(); poiMarkerList = new ArrayList<>();

        this.context = context;
        mapController = (MapController) map.getController();
        mapController.setZoom(17);
        GeoPoint startpoint = new GeoPoint(49.122831, 9.210871); //Koordinaten der Hochschule Heilbronn
        mapController.setCenter(startpoint);
        marker = new Marker(map);
        marker.setSnippet("My current Location");
        marker.setIcon(context.getDrawable((R.drawable.ic_location_on_red_24dp)));
        callPermissions();

        timer = new CountDownTimer(Long.MAX_VALUE, syncInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                    if(tankSearch){
                        getFuelPrices(new Position(latitude,longitude));

                    }
                }

            @Override
            public void onFinish() {
                Log.wtf("Map functionality ", "Lol, the time has ended, the universe should have ended before this message can be shown...");
            }
        };
        timer.start();
    }


    public void requestUsersAround() {
        //TODO Set the correct radius or use an appropriate constant value that should be defined as constant.
        sync.getUsersAround(10, new UsersAroundReceivedInterface() {
            @Override
            public void onSuccess(ArrayList<MapObject> usersAround) {
                if(usersAround.size()>=1) {
                    for (int counter = 0; counter < usersAround.size(); counter++) {
                        Marker searchMarker = new Marker(map);
                        searchMarker.setIcon(context.getDrawable(R.drawable.ic_pin_drop_blue_24dp));
                        searchMarker.setPosition(usersAround.get(counter));
                        searchMarker.setAnchor(0.5f, 0.5f);
                        searchMarker.setTitle(usersAround.get(counter).getLabel());
                        searchMarker.setSnippet(usersAround.get(counter).getDescription());
                        map.getOverlays().add(searchMarker);
                        searchMarkerArrayList.add(searchMarker);
                    }
                    map.invalidate();
                }
                else
                    Toast.makeText(context,R.string.noUsersAround,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure() {
                Toast.makeText(context,R.string.usersAroundFailure,Toast.LENGTH_LONG).show();
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
                deleteSearchMarkers(timeStampedMarkerList);
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
                        searchMarkerArrayList.add(searchMarker);
                    }
                    map.invalidate();
                }
                else
                    Toast.makeText(context,"LocationHistory is empty",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure() {
            Toast.makeText(context,"Fehler beim Versuch die LocationHistory zu bekommen", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getPois(){
        poiSearchService.getPois(map.getBoundingBox(), new PoisReceivedInterface() {
            @Override
            public void onSuccess(ArrayList<MapObject> poiElements) {
                if(poiElements.size()>=1) {
                    for (int counter = 0; counter < poiElements.size(); counter++) {
                        Marker poiMarker = new Marker(map);
                        poiMarker.setIcon(context.getDrawable(R.drawable.ic_pin_drop_blue_24dp));
                        poiMarker.setPosition(poiElements.get(counter));
                        poiMarker.setAnchor(0.5f, 0.5f);
                        poiMarker.setTitle(poiElements.get(counter).getLabel());
                        poiMarker.setSnippet(poiElements.get(counter).getDescription());
                        map.getOverlays().add(poiMarker);
                        poiMarkerList.add(poiMarker);
                    }
                    map.invalidate();
                }
                else
                    Toast.makeText(context,"Poi Liste ist leer",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(context, "Fehler bei den Pois", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFuelPrices(Position position) {
        fuelService.getFuelPrices(position.getLatitude(), position.getLongitude(), new FuelPricesReceivedInterface() {
            @Override
            public void onSuccess(ArrayList<MapObject> fuelPrices) {
                if(fuelPrices.size()>=1) {
                    for (int counter = 0; counter < fuelPrices.size(); counter++) {
                        Marker fuelMarker = new Marker(map);
                        fuelMarker.setIcon(context.getDrawable(R.drawable.ic_pin_drop_blue_24dp));
                        fuelMarker.setPosition(fuelPrices.get(counter));
                        fuelMarker.setAnchor(0.5f, 0.5f);
                        fuelMarker.setTitle(fuelPrices.get(counter).getLabel());
                        fuelMarker.setSnippet(fuelPrices.get(counter).getDescription());
                        map.getOverlays().add(fuelMarker);
                        fuelMarkerList.add(fuelMarker);
                    }
                    map.invalidate();
                }
                else
                    Toast.makeText(context,R.string.onFailureFuelSearch,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(context,R.string.onFailureFuelSearch, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void search(String searchTerm) {
        searchService.search(searchTerm, new SearchResultsReceivedInterface() {
            @Override
            public void onSuccess(ArrayList<MapObject> searchResultsList) {
                if(searchResultsList.size()>=1) {
                    for (int counter = 0; counter < searchResultsList.size(); counter++) {
                        Marker searchMarker = new Marker(map);
                        searchMarker.setIcon(context.getDrawable(R.drawable.ic_pin_drop_blue_24dp));
                        searchMarker.setPosition(searchResultsList.get(counter));
                        searchMarker.setAnchor(0.5f, 0.5f);
                        searchMarker.setTitle(searchResultsList.get(counter).getLabel());
                        searchMarker.setSnippet(searchResultsList.get(counter).getDescription());
                        map.getOverlays().add(searchMarker);
                        searchMarkerArrayList.add(searchMarker);
                    }
                    map.invalidate();
                }
                else
                    Toast.makeText(context,"Suchbegriff konnte nicht gefunden werden!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
            Toast.makeText(context,"Suche konnte nicht ausgeführt werden",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteSearchMarkers(ArrayList<Marker> markers){
        if(!markers.isEmpty()){
            for(Marker marker:markers){
                map.getOverlays().remove(marker);
            }
            markers.clear();
        }
    }

    public void setCenter()
    {
        GeoPoint centerPoint = new GeoPoint(latitude,longitude);
        mapController.setCenter(centerPoint);
        map.invalidate();
        getPois();
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
        marker.setPosition(gPt);
        marker.setAnchor(0.5f,0.5f);
        map.getOverlays().add(marker);
        map.invalidate();
    }
}