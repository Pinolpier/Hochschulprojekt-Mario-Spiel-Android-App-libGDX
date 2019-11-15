package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface PoiAPI {
    @FormUrlEncoded
    @POST("interpreter")
    Call<PoiElementsDummy> getSearchResult(@Field("data")String data);
}

