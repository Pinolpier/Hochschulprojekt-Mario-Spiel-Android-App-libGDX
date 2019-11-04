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

public class SearchService {
    private Retrofit retrofit;
    private SearchAPI api;

    public SearchService() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://nominatim.openstreetmap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(SearchAPI.class);
    }

    public void search(String searchTerm, final SearchResultsReceivedInterface searchResultsReceivedInterface) {
        HashMap<String, String> map = new HashMap<String, String>();
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
                searchResultsReceivedInterface.onSuccess((MapObject[]) searchResultList.toArray());
            }
            @Override
            public void onFailure(Call<List<SearchResultDummy>> call, Throwable t) {
                Log.wtf("SearchService: ", "Fatal Error in SearchService.search()!");
                searchResultsReceivedInterface.onFailure();
            }
        });
    }
}