package de.hhn.aib.swlab.wise1920.group01.exercise2.controller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.hhn.aib.swlab.wise1920.group01.exercise2.model.MapObject;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.MapObjectDummy;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.Position;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.User;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.UserAPI;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SyncService {
    private UserAPI api;
    private Retrofit retrofit;
    private Context context;
    private User user;

    public SyncService(Context context) {
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
     * @param username    The username to register at the server, must not be {code null}
     * @param password    The password to register at the server, must not be {code null}
     * @param description The user's own description, may be {code null} - empty strings are treated as {code null}
     */
    public void register(String username, String password, String description) {
        user = new User(username, password);
        if (description == "") {
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
                    return;
                }
                if (response.code() == 409) {
                    Toast.makeText(context, "@string/usernameNotAvailableToastMessage", Toast.LENGTH_LONG);
                    Log.d("Sync Service: ", "Username was not available. Couldn't complete registration.");
                    return;
                }
                if (response.isSuccessful() && response.code() != 200) {
                    Log.wtf("Sync Service: ", "An unexpected HTTP Response Code indicating successful registration has been returned by the webservice: Response Code is " + response.code());
                    return;
                }
                if (response.code() == 200) {
                    Toast.makeText(context, "@string/registrationSuccessfulToastMessage", Toast.LENGTH_LONG);
                    login(user.getUsername(), user.getPassword());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.wtf("Sync Service: ", "A serious error with the webservice occurred, error:" + t.getMessage());
            }
        });
    }

    /**
     * Used to login a user at the webservice. If the login is not successful a Toast will be shown telling to double check password and username and that an initial registration is needed.
     * If the login is successful the userID and the returned JWT are stored for later use.
     *
     * @param username The username to use for login at the server.
     * @param password The password to use for login at the server.
     */
    public void login(final String username, String password) {
        user = new User(username, password);
        Call<User> call = api.login(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful() && response.code() != 403) {
                    Log.wtf("Sync Service: ", "An unexpected HTTP Response Code indicating an error has been returned by the webservice: Response Code is " + response.code());
                    return;
                }
                if (response.code() == 403) {
                    Toast.makeText(context, "@string/loginFailedToastMessage", Toast.LENGTH_LONG);
                    Log.d("Sync Service: ", "Login unsuccessful. Got HTTP return 403 forbidden.");
                    return;
                }
                if (response.isSuccessful() && response.code() != 200) {
                    Log.wtf("Sync Service: ", "An unexpected HTTP Response Code indicating successful login has been returned by the webservice: Response Code is " + response.code());
                    return;
                }
                if (response.code() == 200) {
                    Toast.makeText(context, "@string/loginSuccessfulToastMessage", Toast.LENGTH_SHORT);
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
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.wtf("Sync Service: ", "A serious error with the webservice occurred, error:" + t.getMessage());
            }
        });
    }

    /**
     * Used to change the logged in user's username
     * If no user is logged in nothing will be done and {code false} will be returned.
     *
     * @param newUsername the new username to send to the server
     * @return {code true}, if a user is logged in and the operation was successful; {code false} if no users is logged in
     */
    public boolean changeUsername(String newUsername) {
        if (user == null || user.getId() == null || user.getJwtAuthorization() == null) {
            return false;
        }
        user.setUsername(newUsername);
        update();
        return true;
    }

    /**
     * Used to change the logged in user's password
     * If no user is logged in nothing will be done and {code false} will be returned.
     *
     * @param newPassword the new password to send to the server
     * @return {code true}, if a user is logged in and the operation was successful; {code false} if no users is logged in
     */
    public boolean changePassword(String newPassword) {
        if (user == null || user.getId() == null || user.getJwtAuthorization() == null) {
            return false;
        }
        user.setPassword(newPassword);
        update();
        return true;
    }

    /**
     * Used to change the logged in user's description
     * If no user is logged in nothing will be done and {code false} will be returned.
     *
     * @param newDescription the new description to send to the server
     * @return {code true}, if a user is logged in and the operation was successful; {code false} if no users is logged in
     */
    public boolean changeDescription(String newDescription) {
        if (user == null || user.getId() == null || user.getJwtAuthorization() == null) {
            return false;
        }
        user.setDescription(newDescription);
        update();
        return true;
    }

    /**
     * Used to change the logged in user's privacyRadius
     * If no user is logged in nothing will be done and {code false} will be returned.
     *
     * @param newPrivacyRadius the new privacy radius to send to the server
     * @return {code true}, if a user is logged in and the operation was successful; {code false} if no users is logged in
     */
    public boolean changePrivacyRadius(Integer newPrivacyRadius) {
        if (user == null || user.getId() == null || user.getJwtAuthorization() == null) {
            return false;
        }
        user.setPrivacyRadius(newPrivacyRadius);
        update();
        return true;
    }

    /**
     * used internally to send updates to the user to the webservice.
     */
    private void update() {
        User toSend = new User(user.getUsername(), user.getPassword());
        toSend.setDescription(user.getDescription());
        toSend.setPrivacyRadius(user.getPrivacyRadius());
        Call<ResponseBody> call = api.updateUser(user.getJwtAuthorization(), user.getId(), toSend);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful() && response.code() != 304 && response.code() != 403) {
                    Log.wtf("Sync Service: ", "An unexpected HTTP Response Code indicating an error has been returned by the webservice: Response Code is " + response.code());
                    return;
                }
                if (response.isSuccessful() && response.code() != 304 && response.code() != 200) {
                    Log.wtf("Sync Service: ", "An unexpected HTTP Response Code indicating successful update has been returned by the webservice: Response Code is " + response.code());
                    return;
                }
                if (response.code() == 403) {
                    Toast.makeText(context, "@string/updateFailedInvalidJwtTokenToastMessage", Toast.LENGTH_LONG);
                    Log.wtf("Sync Service: ", "Invalid JWT token has been used while updating user!");
                    //TODO Login Activity anzeigen, da ungueltiger JWT Token
                    return;
                }
                if (response.code() == 304) {
                    Log.d("Sync Service: ", "No changes made to the user during update.");
                    return;
                }
                if (response.code() == 200) {
                    Toast.makeText(context, "@string/updateSuccessfulToastMessage", Toast.LENGTH_SHORT);
                    Log.d("Symc Service: ", "Changes to users submitted to the webservice successfully.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.wtf("Sync Service: ", "A serious error with the webservice occurred, error:" + t.getMessage());
            }
        });
    }

    /**
     * updates the users position and sends it to the webservice. Internally {code sendLocation(Position position)} is used..
     *
     * @param latitude  the new position's latitude
     * @param longitude the new position's longitude
     * @return if updating was successful {code true} will be returned. Otherwise {code false} will be returned.
     */
    public boolean sendLocation(Double latitude, Double longitude) {
        return sendLocation(new Position(latitude, longitude));
    }

    /**
     * updates the users position and sends it to the webservice.
     * @param position the new position
     * @return if updating was successful {code true} will be returned. Otherwise {code false} will be returned.
     */
    public boolean sendLocation(Position position) {
        if (user == null || user.getId() == null || user.getJwtAuthorization() == null) {
            return false;
        }
        user.setPosition(position);
        Call<ResponseBody> call = api.setLocation(user.getJwtAuthorization(), user.getId(), position);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful() && response.code() != 403) {
                    Log.wtf("Sync Service: ", "An unexpected HTTP Response Code indicating an error has been returned by the webservice: Response Code is " + response.code());
                    return;
                }
                if (response.isSuccessful() && response.code() != 200) {
                    Log.wtf("Sync Service: ", "An unexpected HTTP Response Code indicating successfully sending location has been returned by the webservice: Response Code is " + response.code());
                    return;
                }
                if (response.code() == 403) {
                    Toast.makeText(context, "@string/sendLocationFailedInvalidJwtTokenToastMessage", Toast.LENGTH_LONG);
                    Log.wtf("Sync Service: ", "Invalid JWT token has been used while sending location!");
                    //TODO Login Activity anzeigen, da ungueltiger JWT Token
                    return;
                }
                if (response.code() == 200) {
                    Log.d("Sync Service: ", "Sent location successfully!");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.wtf("Sync Service: ", "A serious error with the webservice occurred, error:" + t.getMessage());
            }
        });
        return true;
    }

    /**
     * used to receive all users around the users location within a specified radius. Internally {code getUsersAround(Position position, int radius)} is used.
     *
     * @param radius the radius in which all users available will be returned.
     * @return an array containing all information to display the users around on the map.
     */
    public MapObject[] getUsersAround(int radius) {
        return getUsersAround(user.getPosition(), radius);
    }

    /**
     * used to receive all users around the specified location within a specified radius.
     *
     * @param position the location around which all available users should be returned.
     * @param radius   the radius in which all users available will be returned.
     * @return an array containing all information to display the users around on the map.
     */
    public MapObject[] getUsersAround(Position position, int radius) {
        final ArrayList<MapObject> usersAroundList = new ArrayList<>();
        Call<List<MapObjectDummy>> call = api.getEverythingAround(user.getJwtAuthorization(), radius, position.getLatitude(), position.getLongitude());
        call.enqueue(new Callback<List<MapObjectDummy>>() {
            @Override
            public void onResponse(Call<List<MapObjectDummy>> call, Response<List<MapObjectDummy>> response) {
                if (!response.isSuccessful() && response.code() != 403) {
                    Log.wtf("Sync Service: ", "An unexpected HTTP Response Code indicating an error has been returned by the webservice: Response Code is " + response.code());
                    return;
                }
                if (response.isSuccessful() && response.code() != 200) {
                    Log.wtf("Sync Service: ", "An unexpected HTTP Response Code indicating successfully sending location has been returned by the webservice: Response Code is " + response.code());
                    return;
                }
                if (response.code() == 403) {
                    Toast.makeText(context, "@string/getUsersFailedInvalidJwtTokenToastMessage", Toast.LENGTH_LONG);
                    Log.wtf("Sync Service: ", "Invalid JWT token has been used while sending location!");
                    //TODO Login Activity anzeigen, da ungueltiger JWT Token
                    return;
                }
                if (response.code() == 200) {
                    Log.d("Sync Service: ", "Received locations successfully!");
                    List<MapObjectDummy> usersAround = response.body();
                    for (MapObjectDummy i : usersAround) {
                        usersAroundList.add(new MapObject(i.getPosition()[0], i.getPosition()[1], i.getName(), i.getDescription()));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MapObjectDummy>> call, Throwable t) {
                Log.wtf("Sync Service: ", "A serious error with the webservice occurred, error:" + t.getMessage());
            }
        });
        return usersAroundList.toArray(new MapObject[usersAroundList.size()]);
    }
}