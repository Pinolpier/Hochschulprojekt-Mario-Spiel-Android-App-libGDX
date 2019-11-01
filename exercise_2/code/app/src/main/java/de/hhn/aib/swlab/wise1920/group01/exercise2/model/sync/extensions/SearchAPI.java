package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface SearchAPI {
    @GET("search")
    Call<List<SearchResultDummy>> getSearchResult(@QueryMap Map<String, String> parameters);
}