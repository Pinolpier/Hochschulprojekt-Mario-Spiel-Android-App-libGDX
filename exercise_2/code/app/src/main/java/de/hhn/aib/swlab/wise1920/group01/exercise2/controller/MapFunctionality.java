package de.hhn.aib.swlab.wise1920.group01.exercise2.controller;

import android.content.Context;
import android.os.Bundle;

import org.osmdroid.views.MapView;

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

    public MapFunctionality(MapView map, Bundle bundle, Context context) {
        this.map = map;
        sync = new SyncService(context, bundle.getString("jwt"), bundle.getString("id"), bundle.getString("username"), bundle.getString("description"), bundle.getString("password"), bundle.getDouble("privacy"));
        fuelService = new FuelSearchPricesService(context);
        searchService = new SearchService();
    }

    private void requestUsersAround() {
        //TODO Set the correct radius or use an appropriate constant value that should be defined as constant.
        sync.getUsersAround(10, new UsersAroundReceivedInterface() {
            @Override
            public void onSuccess(MapObject[] usersAround) {
                //TODO display users around on the map
            }

            @Override
            public void onFailure() {
                //TODO failure handling if users can't be received
            }
        });
    }

    private void requestLocationHistory() {
        //TODO Set the earliest timestamp in milliseconds unix otherwise 1st january 2010 will be used.
        sync.getLocationHistory(null, new LocationHistoryReceivedInterface() {
            @Override
            public void onSuccess(TimestampedPosition[] locationHistory) {
                //TODO display historical locations
            }

            @Override
            public void onFailure() {
                //TODO failure handling if locationHistory can't be received
            }
        });
    }

    private void getFuelPrices(Position position) {
        fuelService.getFuelPrices(position.getLatitude(), position.getLongitude(), new FuelPricesReceivedInterface() {
            @Override
            public void onSuccess(MapObject[] fuelPrices) {
                //TODO display gas stations and prices
            }

            @Override
            public void onFailure() {
                //TODO failure handling if fuel prices can't be received.
            }
        });
    }

    private void search(String searchTerm) {
        searchService.search(searchTerm, new SearchResultsReceivedInterface() {
            @Override
            public void onSuccess(MapObject[] searchResultsList) {
                //TODO display searchResults
            }

            @Override
            public void onFailure() {
                //TODO failure handling if searchResults can't be received.
            }
        });
    }
}