package de.hhn.aib.swlab.wise1920.group01.exercise2.controller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import de.hhn.aib.swlab.wise1920.group01.exercise2.R;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.LoginProcessedInterface;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.RegistrationProcessedInterface;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.User;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.UserAPI;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthService {
    private UserAPI api;
    private Retrofit retrofit;
    private Context context;
    private User user;

    public AuthService(Context context) {
        this.context = context;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://swlab.iap.hs-heilbronn.de/ex2/api/v0.3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(UserAPI.class);
    }

    /**
     * Used to register a new user at the webservice. If the username is blocked (meaning already used by another user) a toast message will be shown
     * to tell the user about the problem and to suggest to choose another username or trying login if the register button was choosen accidentally.
     * If the registration was successful the user will be logged in automatically afterwards.
     *
     * @param username                       The username to register at the server, must not be {code null}
     * @param password                       The password to register at the server, must not be {code null}
     * @param description                    The user's own description, may be {code null} - empty strings are treated as {code null}
     * @param registrationProcessedInterface
     */
    public void register(final String username, final String password, String description, final RegistrationProcessedInterface registrationProcessedInterface) {
        user = new User(username, password);
        if ("".equals(description)) {
            description = null;
        }
        if (description != null) {
            user.setDescription(description);
        }
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
                Log.wtf("Sync Service: ", "A serious error with the webservice occurred during registration, error:" + t.getMessage());
                registrationProcessedInterface.onFailure();
            }
        });
    }

    /**
     * Used to login a user at the webservice. If the login is not successful a Toast will be shown telling to double check password and username and that an initial registration is needed.
     * If the login is successful the userID and the returned JWT are stored for later use.
     *
     * @param username                The username to use for login at the server.
     * @param password                The password to use for login at the server.
     * @param loginProcessedInterface
     */
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
                    Toast.makeText(context, "@string/loginFailedToastMessage", Toast.LENGTH_LONG).show();
                    Log.d("Auth Service: ", "Login unsuccessful. Got HTTP return 403 forbidden.");
                }
                if (response.isSuccessful() && response.code() != 200) {
                    Log.wtf("Sync Service: ", "An unexpected HTTP Response Code indicating successful login has been returned by the webservice: Response Code is " + response.code());
                }
                if (response.code() == 200) {
                    Toast.makeText(context, R.string.loginSuccessfulToastMessage, Toast.LENGTH_SHORT).show();
                    User respondedUser = response.body();
                    user.setId(respondedUser.getId());
                    if (!respondedUser.getUsername().equals(username)) {
                        Log.wtf("Sync Service: ", "The username of the responded user after login is not the username that has been used for login!");
                    }
                    user.setUsername(respondedUser.getUsername());
                    user.setDescription(respondedUser.getDescription());
                    user.setPrivacyRadius(respondedUser.getPrivacyRadius());
                    List<String> jwts = response.headers().values("Authorization");
                    if (jwts.size() != 1) {
                        Log.wtf("Sync Service: ", "Multiple values have been returned in the header with key 'Authorization' - can't identify the jwt token!");
                    } else {
                        user.setJwtAuthorization(jwts.get(0));
                    }
                    loginProcessedInterface.onSuccess();
                    return;
                }
                loginProcessedInterface.onFailure();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.wtf("Sync Service: ", "A serious error with the webservice occurred during login, error:" + t.getMessage());
                loginProcessedInterface.onFailure();
            }
        });
    }

    public String getJWT() {
        if (user != null) {
            if (user.getJwtAuthorization() != null) {
                return user.getJwtAuthorization();
            } else {
                Log.wtf("Auth Service: ", "Auth service has an user object that is not null, but the JWT in it is null. Returning null as JWT!");
            }
        }
        return null;
    }

    public String getUserID() {
        if (user != null) {
            if (user.getId() != null) {
                return user.getId();
            } else {
                Log.wtf("Auth Service: ", "Auth service has an user object that is not null, but the UserID in it is null. Returning null as UserID!");
            }
        }
        return null;
    }

    public String getUsername() {
        if (user != null) {
            if (user.getUsername() != null) {
                return user.getUsername();
            } else {
                Log.wtf("Auth Service: ", "Auth service has an user object that is not null, but the username in it is null. Returning null as username!");
            }
        }
        return null;
    }

    public String getDescription() {
        if (user != null) {
            if (user.getDescription() != null) {
                return user.getDescription();
            } else {
                Log.wtf("Auth Service: ", "Auth service has an user object that is not null, but the description in it is null. Returning null as description!");
            }
        }
        return null;
    }

    public Double getPrivacyRadius() {
        if (user != null) {
            if (user.getPrivacyRadius() != null) {
                return user.getPrivacyRadius();
            } else {
                Log.wtf("Auth Service: ", "Auth service has an user object that is not null, but the privacyRadius in it is null. Returning null as privacyRadius!");
            }
        }
        return null;
    }

    public String getPassword() {
        if (user != null) {
            if (user.getPassword() != null) {
                return user.getPassword();
            } else {
                Log.wtf("Auth Service: ", "Auth service has an user object that is not null, but the username in it is null. Returning null as username!");
            }
        }
        return null;
    }
}