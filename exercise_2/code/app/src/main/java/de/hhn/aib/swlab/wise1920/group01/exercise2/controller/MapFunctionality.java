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
import java.util.Calendar;

import de.hhn.aib.swlab.wise1920.group01.exercise2.R;
import de.hhn.aib.swlab.wise1920.group01.exercise2.controller.extensions.FuelPricesSearchService;
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

/**
 * This class is responsible for all the functionality-methods behind the MapsActivity view
 */
public class MapFunctionality {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private final MapView map;
    private final SyncService sync;
    private final FuelPricesSearchService fuelService;
    private final SearchService searchService;
    private ArrayList<Marker> searchMarkerArrayList, timeStampedMarkerList,fuelMarkerList, poiMarkerList,usersAroundMarker;
    private final Context context;
    private double latitude, longitude;
    private final MapController mapController;
    private CountDownTimer timer;
    private long syncInterval = 60000;
    private Boolean fuelSearch = false;
    private Boolean poiSearch = false;
    private Boolean historyBoolean = false;
    private Boolean onDestroyBoolean = true;
    private Marker marker;
    private ArrayList<GeoPoint> track;
    private SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private PolyOverlayWithIW roadOverlay;

    /**
     * Constructor for class MapFunctionality
     * @param map       mapview from MapsActivity
     * @param bundle    bundle with key for Syncservice
     * @param context   context from MapsActivity
     */
    public MapFunctionality(final MapView map, Bundle bundle, final Context context) {
        this.context = context;
        callPermissions();
        this.map = map;
        sync = new SyncService(context, bundle.getString("jwt"), bundle.getString("id"), bundle.getString("username"), bundle.getString("description"), bundle.getString("password"), bundle.getDouble("privacy"));
        fuelService = new FuelPricesSearchService(context);
        searchService = new SearchService();
        searchMarkerArrayList = new ArrayList<>();
        timeStampedMarkerList = new ArrayList<>(); fuelMarkerList = new ArrayList<>(); poiMarkerList = new ArrayList<>(); usersAroundMarker = new ArrayList<>();
        track = new ArrayList<>();

        mapController = (MapController) map.getController();
        mapController.setZoom(15);
        GeoPoint startPoint = new GeoPoint(49.122831, 9.210871); //Koordinaten der Hochschule Heilbronn
        mapController.setCenter(startPoint);
        marker = new Marker(map);
        marker.setTitle("My current Location");
        marker.setIcon(context.getDrawable((R.drawable.ic_location_on_red_24dp)));

        timer = new CountDownTimer(Long.MAX_VALUE, syncInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                    if(fuelSearch){
                        getFuelPrices(new Position(latitude,longitude));
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
        switchPoi(prefs.getBoolean("switch_poi", false));
        switchFuelprice(prefs.getBoolean("switch_fuelprice", false));
        switchLocHistory(prefs.getBoolean("switch_locationhistory", false));

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d("MapFunctionality", "listenerFired");
                switch (key) {
                    case "list_radius":
                        Log.d("cngRadius", sharedPreferences.getString(key, "-1"));
                        getUsersAround();
                        break;
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
                        switchLocHistory(sharedPreferences.getBoolean(key, false));
                        break;
                    case "list_locationhistorytimeframe":
                        Log.d("cngLocHistoryTimeFrame", sharedPreferences.getString(key, "604800"));
                        getLocationHistory();
                        break;
                }
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Shows all users around your current location on the map with a green marker
     */
    private void getUsersAround() {
        sync.getUsersAround(Integer.parseInt(prefs.getString("list_radius","-1")), new UsersAroundReceivedInterface() {
            @Override
            public void onSuccess(ArrayList<MapObject> usersAround) {
                if (onDestroyBoolean) {
                    if (usersAround.size() >= 1) {
                        for (int counter = 0; counter < usersAround.size(); counter++) {
                            Marker searchMarker = new Marker(map);
                            searchMarker.setIcon(context.getDrawable(R.drawable.ic_location_usersaround_green_24dp));
                            searchMarker.setPosition(usersAround.get(counter));
                            searchMarker.setAnchor(0.5f, 0.5f);
                            searchMarker.setTitle(usersAround.get(counter).getLabel());
                            searchMarker.setSnippet(usersAround.get(counter).getDescription());
                            map.getOverlays().add(searchMarker);
                            usersAroundMarker.add(searchMarker);
                        }
                        map.invalidate();
                    } else
                        Toast.makeText(context, R.string.noUsersAround, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure() {
                Toast.makeText(context,R.string.usersAroundFailure,Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Shows all timestamps from own locationhistory on the map with orange markers and connects them with orange lines (sort by time)
     */
    private void getLocationHistory() {
        if (historyBoolean) {
            Calendar calendar = Calendar.getInstance();
            Long time = calendar.getTimeInMillis() -getLocHistoryTimeframe();
            sync.getLocationHistory(time, new LocationHistoryReceivedInterface() {
                @Override
                public void onSuccess(ArrayList<TimestampedPosition> locationHistory) {
                    Log.d("MapsActivity", "requestLocationHistory has been called. Length of the array arg is: " + locationHistory.size());
                    if (onDestroyBoolean) {
                        if (locationHistory.size() >= 1) {
                            for (int counter = 0; counter < locationHistory.size(); counter++) {
                                Marker locationHistoryMarker = new Marker(map);
                                locationHistoryMarker.setIcon(context.getDrawable(R.drawable.ic_locationhistory_24dp));
                                GeoPoint gpt = new GeoPoint(locationHistory.get(counter).getLatitude(), locationHistory.get(counter).getLongitude());
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
                            roadOverlay = RoadManager.buildRoadOverlay(road);
                            roadOverlay.getOutlinePaint().setColor(Color.rgb(255, 165, 0));
                            roadOverlay.getOutlinePaint().setStrokeWidth(5);
                            map.getOverlays().add(roadOverlay);
                            map.invalidate();
                        } else
                            Toast.makeText(context, R.string.noLocationHistory, Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure() {
                    Toast.makeText(context, R.string.locationHistoryFailure, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Shows all Points of Interests (pois) around the current location on the map with a blue marker
     */
    private void getPoi(){
        if(poiSearch) {
            PoiSearchService poiSearchService = new PoiSearchService(context);
            poiSearchService.getPois(map.getBoundingBox(), new PoisReceivedInterface() {
                @Override
                public void onSuccess(ArrayList<MapObject> poiArrayList) {
                    if (onDestroyBoolean) {
                        deleteSearchMarkers(poiMarkerList);
                        if (poiArrayList.size() >= 1) {
                            for (int counter = 0; counter < poiArrayList.size(); counter++) {
                                Marker poiMarker = new Marker(map);
                                poiMarker.setIcon(context.getDrawable(R.drawable.ic_star_gold_24dp));
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
                }

                @Override
                public void onFailure() {
                    Toast.makeText(context, R.string.poiOnFailure, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Shows all fuelprices around the users current location with a red marker in the shape of a gas station symbol
     * @param position      current position of the user
     */
    private void getFuelPrices(Position position) {
        if(fuelSearch){
        fuelService.getFuelPrices(position.getLatitude(), position.getLongitude(), new FuelPricesReceivedInterface() {
            @Override
            public void onSuccess(ArrayList<MapObject> fuelPrices) {
                if (onDestroyBoolean) {
                    if (fuelPrices.size() >= 1) {
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
                    } else
                        Toast.makeText(context, R.string.onFailureFuelSearch, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure() {
                Toast.makeText(context,R.string.onFailureFuelSearch, Toast.LENGTH_LONG).show();
            }
        });
        }
    }

    /**
     * Sends a Query of the searchterm and shows all results on the map with a golden marker in shape of a star
     * @param searchTerm        input of the user in searchview
     */
    public void search(String searchTerm) {
        searchService.search(searchTerm, new SearchResultsReceivedInterface() {
            @Override
            public void onSuccess(ArrayList<MapObject> searchResultsList) {
                if (onDestroyBoolean) {
                    if (searchResultsList.size() >= 1) {
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
                        mapController.setCenter(new GeoPoint(searchResultsList.get(0).getLatitude(), searchResultsList.get(0).getLongitude()));
                        map.invalidate();
                    } else
                        Toast.makeText(context, R.string.noSearchResults, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure() {
            Toast.makeText(context,R.string.searchFunctionFailure,Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Deletes roadoverlay from the map (lines between the locationhistory markers)
     */
    private void deleteHistoryLines(){
        map.getOverlays().remove(roadOverlay);
    }

    /**
     * Deletes markers from the map
     * @param markers       all markers that should be deleted
     */
    private void deleteSearchMarkers(ArrayList<Marker> markers){
        if(!markers.isEmpty()){
            for(Marker marker:markers){
                map.getOverlays().remove(marker);
            }
            markers.clear();
        }
    }

    /**
     * Center the view of the map to the current position of the user
     */
    public void setCenter()
    {
        mapController.setCenter(new GeoPoint(latitude,longitude));
        map.invalidate();
    }

    /**
     * Asks user for locationpermissions to give access and make app eligible of detecting location of users phone
     */
    private void callPermissions()
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

    /**
     * Determines current location of the user and saves position in local variable "latitude" and "longitude"
     */
    private void requestLocationUpdates()
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
                    sync.changeLocation(latitude, longitude);
                    setCurrentPosition();
                }
            }, getMainLooper());
        }
        else
        {
            callPermissions();
        }
    }

    /**
     * Set current position marker on the current location
     */
    private void setCurrentPosition()
    {
        GeoPoint gPt = new GeoPoint(latitude,longitude);
        marker.setPosition(gPt);
        marker.setAnchor(0.5f,0.5f);
        marker.setSnippet(sync.getDescription());
        map.getOverlays().add(marker);
        map.invalidate();
    }

    /**
     * Returns current timeframe value of locationhistory timeframe from the settings
     * @return      Long value of timeframe in settings
     */
    private Long getLocHistoryTimeframe() {
        return Long.valueOf(prefs.getString("list_locationhistorytimeframe", "604800000"));
    }

    /**
     * Show pois depending of the switch state in settings
     * @param b     switchstate in settings
     */
    private void switchPoi(Boolean b) {
        if (b) {
            poiSearch = true;
            getPoi();
        } else {
            poiSearch = false;
            deleteSearchMarkers(poiMarkerList);
        }
    }

    /**
     * Show fuelprices depending of the switch state in settings
     * @param b     switchstate in settings
     */
    private void switchFuelprice(Boolean b) {
        if (b) {
            fuelSearch = true;
            getFuelPrices(new Position(latitude, longitude));
        } else {
            fuelSearch = false;
            deleteSearchMarkers(fuelMarkerList);
        }
    }

    /**
     * Show locationhistory depending of the switch state in settings
     * @param b     switchstate in settings
     */
    private void switchLocHistory(Boolean b) {
        if (b) {
            historyBoolean = true;
            getLocationHistory();
        } else {
            historyBoolean = false;
            deleteSearchMarkers(timeStampedMarkerList);
            deleteHistoryLines();
        }
    }

    public void onCloeSearchView() {
        deleteSearchMarkers(searchMarkerArrayList);
    }

    public void onDestroy(){
        onDestroyBoolean = false;
    }
}