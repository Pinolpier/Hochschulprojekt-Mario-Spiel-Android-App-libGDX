package de.hhn.aib.swlab.wise1920.group01.exercise2.controller.extensions;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hhn.aib.swlab.wise1920.group01.exercise2.model.MapObject;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.SearchAPI;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.SearchResultDummy;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.SearchResultsReceivedInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class is responsible for the search of pois depending on searchterm from user with its own retrofit adapter
 */
public class SearchService {
    private Retrofit retrofit;
    private SearchAPI api;

    /**
     * Constructor for SearchService
     */
    public SearchService() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://nominatim.openstreetmap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(SearchAPI.class);
    }

    /**
     * This method builds a query to get the MapObjects that are fitting for the searchterm from the user and puts them
     * in a HashMap
     * @param searchTerm Type of content requested by user
     * @param searchResultsReceivedInterface
     */
    public void search(String searchTerm, final SearchResultsReceivedInterface searchResultsReceivedInterface) {
        HashMap<String, String> map = new HashMap<>();
        map.put("q", searchTerm);
        map.put("format", "json");
        map.put("namedetails","1");
        Call<List<SearchResultDummy>> call = api.getSearchResult(map);
        call.enqueue(new Callback<List<SearchResultDummy>>() {
            @Override
            public void onResponse(Call<List<SearchResultDummy>> call, Response<List<SearchResultDummy>> response) {
                ArrayList<MapObject> searchResultList = new ArrayList<>();
                List<SearchResultDummy> searchResults = response.body();
                for (SearchResultDummy i : searchResults) {
                    searchResultList.add(new MapObject(i.getLatitude(), i.getLongitude(),null, i.getDescription()));
                }
                searchResultsReceivedInterface.onSuccess(searchResultList);
            }
            @Override
            public void onFailure(Call<List<SearchResultDummy>> call, Throwable t) {
                Log.wtf("SearchService: ", "Fatal Error in SearchService.search()!");
                searchResultsReceivedInterface.onFailure();
            }
        });
    }
}