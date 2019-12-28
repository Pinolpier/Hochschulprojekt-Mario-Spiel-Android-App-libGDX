package server;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.mygdx.game.R;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import server.dtos.User;

public class UserService {
    private UserAPI api;
    private Retrofit retrofit;
    private User user;
    private Context context;

    public UserService(Context context) {
        this.context = context;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://user.ex3.swlab-hhn.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(UserAPI.class);
    }

    public void register(final String username, final String password, final RegistrationProcessedInterface registrationProcessedInterface) {
        user = new User(username, password);
        Call<ResponseBody> call = api.register(user);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() != 409 && !response.isSuccessful()) {
                    Log.wtf("Sync Service: ", "An unexpected HTTP Response Code indicating an error has been returned by the webservice: Response Code is " + response.code());
                }
                if (response.code() == 409) {
                    Toast.makeText(context, R.string.usernameNotAvailableToastMessage, Toast.LENGTH_LONG).show();
                    Log.d("Sync Service: ", "Username was not available. Couldn't complete registration.");
                }
                if (response.isSuccessful() && response.code() != 200) {
                    Log.wtf("Sync Service: ", "An unexpected HTTP Response Code indicating successful registration has been returned by the webservice: Response Code is " + response.code());
                }
                if (response.code() == 200) {
                    Toast.makeText(context, R.string.registrationSuccessfulToastMessage, Toast.LENGTH_LONG).show();
                    registrationProcessedInterface.onSuccess(username, password);
                    return;
                }
                registrationProcessedInterface.onFailure();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, R.string.connectionOnFailureToastMessage, Toast.LENGTH_LONG).show();
                Log.wtf("Sync Service: ", "A serious error with the webservice occurred during registration, error:" + t.getMessage());
                registrationProcessedInterface.onFailure();
            }
        });
    }

    public void login(final String username, String password, final LoginProcessedInterface loginProcessedInterface) {
        user = null;
        user = new User(username, password);
        Call<User> call = api.login(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful() && response.code() != 403) {
                    Log.wtf("Sync Service: ", "An unexpected HTTP Response Code indicating an error has been returned by the webservice: Response Code is " + response.code());
                }
                if (response.code() == 403) {
                    Toast.makeText(context, R.string.loginFailedToastMessage, Toast.LENGTH_LONG).show();
                    Log.d("Auth Service: ", "Login unsuccessful. Got HTTP return 403 forbidden.");
                }
                if (response.isSuccessful() && response.code() != 200) {
                    Log.wtf("Sync Service: ", "An unexpected HTTP Response Code indicating successful login has been returned by the webservice: Response Code is " + response.code());
                }
                if (response.code() == 200) {
                    Toast.makeText(context, R.string.loginSuccessfulToastMessage, Toast.LENGTH_SHORT).show();
                    String auth = response.headers().get("Authorization");
                    loginProcessedInterface.onSuccess(auth);
                    return;
                }
                loginProcessedInterface.onFailure();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(context, R.string.connectionOnFailureToastMessage, Toast.LENGTH_LONG).show();
                Log.wtf("Sync Service: ", "A serious error with the webservice occurred during login, error:" + t.getMessage());
                loginProcessedInterface.onFailure();
            }
        });
    }
}