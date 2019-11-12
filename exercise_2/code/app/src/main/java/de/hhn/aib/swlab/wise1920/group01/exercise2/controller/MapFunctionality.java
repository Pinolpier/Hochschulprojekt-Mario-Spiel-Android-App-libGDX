package de.hhn.aib.swlab.wise1920.group01.exercise2.controller;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.PolyOverlayWithIW;

import java.util.ArrayList;

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

public class MapFunctionality<privaet> {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private MapView map;
    private SyncService sync;
    private FuelSearchPricesService fuelService;
    private SearchService searchService;
    private ArrayList<Marker> searchMarkerArrayList, timeStampedMarkerList,fuelMarkerList, poiMarkerList;
    private final Context context;
    private double latitude, longitude;
    private MapController mapController;
    private CountDownTimer timer;
    private long syncInterval = 60000;
    private Boolean tankSearch = false;
    private Boolean poiSearch = false;
    private Marker marker;
    private ArrayList<GeoPoint> track;
    private SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    public MapFunctionality(final MapView map, Bundle bundle, final Context context) {
        this.context = context;
        callPermissions();
        this.map = map;
        sync = new SyncService(context, bundle.getString("jwt"), bundle.getString("id"), bundle.getString("username"), bundle.getString("description"), bundle.getString("password"), bundle.getDouble("privacy"));
        fuelService = new FuelSearchPricesService(context);
        searchService = new SearchService();
        searchMarkerArrayList = new ArrayList<>();
        timeStampedMarkerList = new ArrayList<>(); fuelMarkerList = new ArrayList<>(); poiMarkerList = new ArrayList<>();
        track = new ArrayList<>();

        mapController = (MapController) map.getController();
        mapController.setZoom(17);
        GeoPoint startPoint = new GeoPoint(49.122831, 9.210871); //Koordinaten der Hochschule Heilbronn
        mapController.setCenter(startPoint);
        marker = new Marker(map);
        marker.setSnippet("My current Location");
        marker.setIcon(context.getDrawable((R.drawable.ic_location_on_red_24dp)));

        timer = new CountDownTimer(Long.MAX_VALUE, syncInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                    if(tankSearch){
                        getFuelPrices(new Position(latitude,longitude));
                    }
                    if(poiSearch) {
                        getPoi();
                    }
                    getUsersAround();
                }

            @Override
            public void onFinish() {
                Log.wtf("Map functionality ", "Lol, the time has ended, the universe should have ended before this message can be shown...");
            }
        };
        timer.start();

        map.addMapListener(new DelayedMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                getPoi();
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                getPoi();
                return false;
            }
        },1000));

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d("MapFunctionality", "listenerFired");
                switch (key) {
                    case "switch_poi":
                        Log.d("cngPOI", "" + sharedPreferences.getBoolean(key, false));
                        switchPoi(sharedPreferences.getBoolean("switch_poi", false));
                        break;
                    case "switch_fuelprice":
                        Log.d("cngFuelprice", "" + sharedPreferences.getBoolean(key, false));
                        switchFuelprice(sharedPreferences.getBoolean(key, false));
                        break;
                    case "switch_locationhistory":
                        Log.d("cngLocHistory", "" + sharedPreferences.getBoolean(key, false));
                        switchLocHisory(sharedPreferences.getBoolean(key, false));
                        break;
                    case "list_locationhistorytimeframe":
                        Log.d("cngLocHistoryTimeFrame", sharedPreferences.getString(key, "604800"));
                        //TODO locHistoryTimeframe
                        break;
                }
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    private void getUsersAround() {
        //TODO Set the correct radius or use an appropriate constant value that should be defined as constant.
        sync.getUsersAround(10, new UsersAroundReceivedInterface() {
            @Override
            public void onSuccess(ArrayList<MapObject> usersAround) {
                if(usersAround.size()>=1) {
                    for (int counter = 0; counter < usersAround.size(); counter++) {
                        Marker searchMarker = new Marker(map);
                        searchMarker.setIcon(context.getDrawable(R.drawable.ic_location_usersaround_green_24dp));
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

    private void getLocationHistory() {
        //TODO Set the earliest timestamp in milliseconds unix otherwise 1st january 2010 will be used.
        deleteSearchMarkers(timeStampedMarkerList);
        sync.getLocationHistory(null, new LocationHistoryReceivedInterface() {
            @Override
            public void onSuccess(ArrayList<TimestampedPosition> locationHistory) {
                Log.d("MapsActivity", "requestLocationHistory has been called. Length of the array arg is: " + locationHistory.size());
                if(locationHistory.size()>=1) {
                    for (int counter = 0; counter < locationHistory.size(); counter++) {
                        //GeoPoint searchPoint = new GeoPoint(searchResults[counter].getLatitude(), searchResults[counter].getLongitude());
                        Marker locationHistoryMarker = new Marker(map);
                        locationHistoryMarker.setIcon(context.getDrawable(R.drawable.ic_locationhistory_24dp));
                        GeoPoint gpt = new GeoPoint(locationHistory.get(counter).getLatitude(),locationHistory.get(counter).getLongitude());
                        track.add(gpt);

                        locationHistoryMarker.setPosition(gpt);
                        locationHistoryMarker.setAnchor(0.5f, 0.5f);
                        locationHistoryMarker.setTitle(locationHistory.get(counter).getDateString());
                        map.getOverlays().add(locationHistoryMarker);
                        timeStampedMarkerList.add(locationHistoryMarker);

                    }
                    RoadManager roadManager = new OSRMRoadManager(context);
                    roadManager.addRequestOption("routeType=pedestrian");
                    Road road = roadManager.getRoad(track);
                    PolyOverlayWithIW roadOverlay = RoadManager.buildRoadOverlay(road);
                    roadOverlay.getOutlinePaint().setColor(Color.GRAY);
                    roadOverlay.getOutlinePaint().setStrokeWidth(5);
                    map.getOverlays().add(roadOverlay);
                    map.invalidate();
                }
                else
                    Toast.makeText(context,R.string.noLocationHistory,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure() {
            Toast.makeText(context,R.string.locationHistoryFailure, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getPoi(){
        if(poiSearch) {
            deleteSearchMarkers(poiMarkerList);
            PoiSearchService poiSearchService = new PoiSearchService(context);
            poiSearchService.getPois(map.getBoundingBox(), new PoisReceivedInterface() {
                @Override
                public void onSuccess(ArrayList<MapObject> poiArrayList) {
                    if (poiArrayList.size() >= 1) {
                        for (int counter = 0; counter < poiArrayList.size(); counter++) {
                            Marker poiMarker = new Marker(map);
                            poiMarker.setIcon(context.getDrawable(R.drawable.ic_pin_drop_blue_24dp));
                            poiMarker.setPosition(poiArrayList.get(counter));
                            poiMarker.setAnchor(0.5f, 0.5f);
                            poiMarker.setTitle(poiArrayList.get(counter).getLabel());
                            poiMarker.setSnippet(poiArrayList.get(counter).getDescription());
                            map.getOverlays().add(poiMarker);
                            poiMarkerList.add(poiMarker);
                        }
                        map.invalidate();
                    } else
                        Toast.makeText(context, R.string.noPoisFound, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(context, R.string.noPoisFound, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getFuelPrices(Position position) {
        deleteSearchMarkers(fuelMarkerList);
        fuelService.getFuelPrices(position.getLatitude(), position.getLongitude(), new FuelPricesReceivedInterface() {
            @Override
            public void onSuccess(ArrayList<MapObject> fuelPrices) {
                if(fuelPrices.size()>=1) {
                    for (int counter = 0; counter < fuelPrices.size(); counter++) {
                        Marker fuelMarker = new Marker(map);
                        fuelMarker.setIcon(context.getDrawable(R.drawable.ic_local_gas_station_red_24dp));
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
        deleteSearchMarkers(searchMarkerArrayList);
        searchService.search(searchTerm, new SearchResultsReceivedInterface() {
            @Override
            public void onSuccess(ArrayList<MapObject> searchResultsList) {
                if(searchResultsList.size()>=1) {
                    for (int counter = 0; counter < searchResultsList.size(); counter++) {
                        Marker searchMarker = new Marker(map);
                        searchMarker.setIcon(context.getDrawable(R.drawable.ic_star_gold_24dp));
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
                    Toast.makeText(context,R.string.noSearchResults,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
            Toast.makeText(context,R.string.searchFunctionFailure,Toast.LENGTH_LONG).show();
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
        poiSearch = true;
        getPoi();
        getLocationHistory();
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
            locationRequest.setFastestInterval(100);
            locationRequest.setInterval(1000);
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

    private Long getLocHistoryTimeframe() {
        return Long.valueOf(prefs.getString("list_locationhistorytimeframe", "604800000"));
    }

    private void switchPoi(Boolean b) {
        if (b) {
            poiSearch = true;
            getPoi();
        } else {
            poiSearch = false;
            getPoi();
            deleteSearchMarkers(poiMarkerList);
        }
    }

    private void switchFuelprice(Boolean b) {
        if (b) {
            tankSearch = true;
            getFuelPrices(new Position(latitude, longitude));
        } else {
            tankSearch = false;
            getFuelPrices(new Position(latitude, longitude));
            deleteSearchMarkers(fuelMarkerList);
        }
    }

    private void switchLocHisory(Boolean b) {
        if (b) {
            getLocationHistory();
        } else {
            deleteSearchMarkers(timeStampedMarkerList);
        }
    }
}