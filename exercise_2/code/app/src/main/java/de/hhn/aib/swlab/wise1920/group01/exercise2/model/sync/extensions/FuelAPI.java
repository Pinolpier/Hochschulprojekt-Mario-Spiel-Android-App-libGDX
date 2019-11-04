package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface FuelAPI {
    @GET("json/{path}")
    Call<FuelDummy> getSearchResult(@Path("path") String path,
                                    @QueryMap Map<String, String> parameters);
}