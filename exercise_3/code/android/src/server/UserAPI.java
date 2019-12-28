package server;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import server.dtos.User;

public interface UserAPI {
    @POST("user")
    Call<ResponseBody> register(@Body User user);

    @POST("user/login")
    Call<User> login(@Body User user);
}