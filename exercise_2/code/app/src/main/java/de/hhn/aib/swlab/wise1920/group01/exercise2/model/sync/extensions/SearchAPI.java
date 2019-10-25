package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions;

import java.util.List;

import de.hhn.aib.swlab.wise1920.group01.exercise2.model.MapObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchAPI {


    @GET("search")
    Call<List<MapObject>> getSearchResult(@Query)
}
