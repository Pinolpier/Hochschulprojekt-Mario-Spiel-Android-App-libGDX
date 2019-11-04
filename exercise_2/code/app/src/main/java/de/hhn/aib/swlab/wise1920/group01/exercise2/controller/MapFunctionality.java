package de.hhn.aib.swlab.wise1920.group01.exercise2.controller;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Calendar;

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

public class MapFunctionality {
    private MapView map;
    private SyncService sync;
    private FuelSearchPricesService fuelService;
    private SearchService searchService;
    private ArrayList<Marker> markerArrayList, timeStampedList;
    private Context context;
    private double latitude, longitude;

    public MapFunctionality(MapView map, Bundle bundle, Context context) {
        this.map = map;
        sync = new SyncService(context, bundle.getString("jwt"), bundle.getString("id"), bundle.getString("username"), bundle.getString("description"), bundle.getString("password"), bundle.getDouble("privacy"));
        fuelService = new FuelSearchPricesService(context);
        searchService = new SearchService();
        markerArrayList = new ArrayList<>();
        timeStampedList = new ArrayList<>();
        this.context = context;
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
        calendar.set(2019,10,01);
        sync.getLocationHistory(calendar.getTimeInMillis(), new LocationHistoryReceivedInterface() {
            @Override
            public void onSuccess(TimestampedPosition[] locationHistory) {
                //deleteSearchMarkers(timeStampedList);
                for(int x = 0;x<locationHistory.length;x++){
                    Marker timeStampedMarker = new Marker(map);
                    timeStampedMarker.setTitle(locationHistory[x].toString());
                    timeStampedMarker.setPosition(new GeoPoint(locationHistory[x].getLatitude(),locationHistory[x].getLongitude()));
                    timeStampedMarker.setIcon(context.getDrawable(R.drawable.ic_pin_drop_blue_24dp));
                    timeStampedMarker.setAnchor(0.5f, 0.5f);
                    map.getOverlays().add(timeStampedMarker);
                    timeStampedList.add(timeStampedMarker);
                }
                map.invalidate();
            }

            @Override
            public void onFailure() {
                Toast.makeText(context,"Fehler",Toast.LENGTH_LONG).show();
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