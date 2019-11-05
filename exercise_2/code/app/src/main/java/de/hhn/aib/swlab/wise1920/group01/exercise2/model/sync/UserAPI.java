package de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserAPI {
    @POST("user")
    Call<ResponseBody> register(@Body User user);

    @POST("user/login")
    Call<User> login(@Body User user);

    @PUT("user/{userID}")
    Call<ResponseBody> updateUser(@Header("Authorization") String jwt,
                                  @Path("userID") String id,
                                  @Body User user);

    @POST("user/{userID}/location")
    Call<ResponseBody> setLocation(@Header("Authorization") String jwt,
                                   @Path("userID") String id,
                                   @Body Position position);

    @GET("location/{radius}/{lat}/{long}")
    Call<List<MapObjectDummy>> getEverythingAround(@Header("Authorization") String jwt,
                                                   @Path("radius") Integer radius,
                                                   @Path("lat") Double latitude,
                                                   @Path("long") Double longitude);

    @GET("user/{userID}/location")
    Call<List<TimestampedPosition>> getLocationHistory(@Header("Authorization") String jwt,
                                                       @Path("userID") String id,
                                                       @Query("minDate") Long timestamp);
}