package de.hhn.aib.swlab.wise1920.group01.exercise2.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import de.hhn.aib.swlab.wise1920.group01.exercise2.R;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.MapObject;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.LocationHistoryReceivedInterface;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.MapObjectDummy;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.Position;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.TimestampedPosition;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.User;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.UserAPI;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.UsersAroundReceivedInterface;
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
    private CountDownTimer timer;
    private long syncInterval;
    private SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    public SyncService(final Context context, String jwt, String id, String username, String description, String password, Double privacy) {
        this.context = context;
        user = new User(username, password);
        user.setJwtAuthorization(jwt);
        user.setId(id);
        user.setDescription(description);
        user.setPrivacyRadius(privacy);
        user.setPosition(new Position(49.122831, 9.210871));
        retrofit = new Retrofit.Builder()
                .baseUrl("https://swlab.iap.hs-heilbronn.de/ex2/api/v0.3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(UserAPI.class);
        //TODO syncInterval aus den Settings bekommen und dementsprechend setzten!
        setSyncInterval(60000L); //Standardwert Sync alle 5 Minuten bis Verknüpfung mit Settings

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d("SyncService", "listenerFired");
                switch (key) {
                    case "list_radius":
                        Log.d("cngRadius", sharedPreferences.getString(key, "-1"));
                        changePrivacyRadius(Double.valueOf(sharedPreferences.getString(key, "-1")));
                        break;
                    case "list_interval":
                        Log.d("cngInterval", sharedPreferences.getString(key, "1800"));
                        setSyncInterval(Long.parseLong(sharedPreferences.getString(key, "1800")));
                        break;
                    case "text_newpassword":
                        if (!sharedPreferences.getString(key, "").isEmpty()) {
                            Log.d("cngPassword", sharedPreferences.getString(key, ""));
                            changePassword(sharedPreferences.getString(key, ""));
                        } else {
                            Toast.makeText(context, "Passwort darf nicht leer sein, bitte ein gültiges Passwort eingeben", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case "text_userdesc":
                        Log.d("cngDesc", sharedPreferences.getString(key, ""));
                        changeDescription(sharedPreferences.getString(key, ""));
                        break;
                }
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);
        setPasswordField(password);
        setDescField(description);
        //setRadius();
        //setInterval();
    }

    public long getSyncInterval() {
        return syncInterval;
    }

    public void setSyncInterval(long syncInterval) {
        this.syncInterval = syncInterval;
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(Long.MAX_VALUE, syncInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!sendLocation(user.getPosition())) {
                    Toast.makeText(context, R.string.sendLocationFailed, Toast.LENGTH_LONG).show();
                    Log.e("Sync Service", "Couldn't send location because user object didn't exist or was invalid");
                }
                Log.d("SyncService"," Sync successful");
            }

            @Override
            public void onFinish() {
                Log.wtf("Sync Service: ", "Lol, the time has ended, the universe should have ended before this message can be shown...");
            }
        };
        timer.start();
    }

    public String getDescription() {
        return user.getDescription();
    }

    public Double getPrivacyRadius() {
        return user.getPrivacyRadius();
    }

    public String getUsername() {
        return user.getUsername();
    }

    public String getPassword() {
        return user.getPassword();
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
    public boolean changePrivacyRadius(Double newPrivacyRadius) {
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
                    Log.wtf("Sync Service", "An unexpected HTTP Response Code indicating an error has been returned by the webservice while update: Response Code is " + response.code());
                    return;
                }
                if (response.isSuccessful() && response.code() != 304 && response.code() != 200) {
                    Log.wtf("Sync Service", "An unexpected HTTP Response Code indicating successful update has been returned by the webservice: Response Code is " + response.code());
                    return;
                }
                if (response.code() == 403) {
                    Toast.makeText(context, R.string.updateFailedInvalidJwtTokenToastMessage, Toast.LENGTH_LONG).show();
                    Log.wtf("Sync Service", "Invalid JWT token has been used while updating user!");
                    //TODO Login Activity anzeigen, da ungueltiger JWT Token
                    return;
                }
                if (response.code() == 304) {
                    Log.d("Sync Service: ", "No changes made to the user during update.");
                    return;
                }
                if (response.code() == 200) {
                    Toast.makeText(context, R.string.updateSuccessfulToastMessage, Toast.LENGTH_SHORT).show();
                    Log.d("Sync Service", "Changes to user submitted to the webservice successfully.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.wtf("Sync Service: ", "A serious error with the webservice occurred during update, error:" + t.getMessage());
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
            Log.wtf("Sync Service", "Something went wrong, either user or ID or JWT is null: " + user);
            return false;
        }
        user.setPosition(position);
        Call<ResponseBody> call = api.setLocation(user.getJwtAuthorization(), user.getId(), position);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful() && response.code() != 403) {
                    Log.wtf("Sync Service", "An unexpected HTTP Response Code indicating an error has been returned by the webservice while sendLocation: Response Code is " + response.code());
                    return;
                }
                if (response.isSuccessful() && response.code() != 200) {
                    Log.wtf("Sync Service", "An unexpected HTTP Response Code indicating successfully sending location has been returned by the webservice: Response Code is " + response.code());
                    return;
                }
                if (response.code() == 403) {
                    Toast.makeText(context, R.string.sendLocationFailedInvalidJwtTokenToastMessage, Toast.LENGTH_LONG).show();
                    Log.wtf("Sync Service", "Invalid JWT token has been used while sending location!");
                    //TODO Login Activity anzeigen, da ungueltiger JWT Token
                    return;
                }
                if (response.code() == 200) {
                    Log.d("Sync Service", "Sent location successfully!");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.wtf("Sync Service: ", "A serious error with the webservice occurred during sendLocation, error:" + t.getMessage());
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
    public void getUsersAround(int radius, UsersAroundReceivedInterface usersAroundReceivedInterface) {
        getUsersAround(user.getPosition(), radius, usersAroundReceivedInterface);
    }

    /**
     * used to receive all users around the specified location within a specified radius.
     *
     * @param position the location around which all available users should be returned.
     * @param radius   the radius in which all users available will be returned.
     * @return an array containing all information to display the users around on the map.
     */
    public void getUsersAround(Position position, int radius, final UsersAroundReceivedInterface usersAroundReceivedInterface) {
        final ArrayList<MapObject> usersAroundList = new ArrayList<>();
        Call<List<MapObjectDummy>> call = api.getEverythingAround(user.getJwtAuthorization(), radius, position.getLatitude(), position.getLongitude());
        call.enqueue(new Callback<List<MapObjectDummy>>() {
            @Override
            public void onResponse(Call<List<MapObjectDummy>> call, Response<List<MapObjectDummy>> response) {
                if (!response.isSuccessful() && response.code() != 403) {
                    Log.wtf("Sync Service", "An unexpected HTTP Response Code indicating an error has been returned by the webservice while getUsersAround: Response Code is " + response.code());
                    usersAroundReceivedInterface.onFailure();
                    return;
                }
                if (response.isSuccessful() && response.code() != 200) {
                    Log.wtf("Sync Service", "An unexpected HTTP Response Code indicating success has been returned by the webservice while getUsersAround: Response Code is " + response.code());
                    usersAroundReceivedInterface.onFailure();
                    return;
                }
                if (response.code() == 403) {
                    Toast.makeText(context, R.string.getUsersFailedInvalidJwtTokenToastMessage, Toast.LENGTH_LONG).show();
                    Log.wtf("Sync Service", "Invalid JWT token has been used while getting users around!");
                    //TODO Login Activity anzeigen, da ungueltiger JWT Token
                    usersAroundReceivedInterface.onFailure();
                    return;
                }
                if (response.code() == 200) {
                    Log.d("Sync Service", "Received locations successfully!");
                    List<MapObjectDummy> usersAround = response.body();
//                    Log.d("Sync Service", "Size of the \"List<MapObjectDummy> usersAround = response.body();\": " + usersAround.size());
                    for (MapObjectDummy i : usersAround) {
                        usersAroundList.add(new MapObject(i.getPosition().getLatitude(), i.getPosition().getLongitude(), i.getName(), i.getDescription()));
                    }
//                    Log.d("Sync Service: ", "Size of the \"final ArrayList<MapObject> usersAroundList = new ArrayList<>();\": " + usersAroundList.size());
//                    Log.d("Sync Service", "getUsersAround: " + Arrays.toString(usersAroundList.toArray()));
                    usersAroundReceivedInterface.onSuccess(usersAroundList);
                }
            }

            @Override
            public void onFailure(Call<List<MapObjectDummy>> call, Throwable t) {
                Log.wtf("Sync Service: ", "A serious error with the webservice occurred during getUsersAround, error:" + t.getMessage());
                usersAroundReceivedInterface.onFailure();
            }
        });
    }

    /**
     * Used to get all past locations of the logged in user since the param
     *
     * @param since The earliest time from when on the location history should be returned. May be {@code null} in that case the 1st January 2010 will 00:00:00.0000 GMT will be used
     * @return All locations where the user has been since the param. Order from oldest (index 0) to the newest positions.
     */
    public void getLocationHistory(Long since, final LocationHistoryReceivedInterface locationHistoryReceivedInterface) {
        final ArrayList<TimestampedPosition> locationHistoryList = new ArrayList<>();
        if (since == null) {
            since = 1262304000000L; //1st of January 2010
        }
        Call<List<TimestampedPosition>> call = api.getLocationHistory(user.getJwtAuthorization(), user.getId(), since);
        call.enqueue(new Callback<List<TimestampedPosition>>() {
            @Override
            public void onResponse(Call<List<TimestampedPosition>> call, Response<List<TimestampedPosition>> response) {
                if (!response.isSuccessful() && response.code() != 403) {
                    Log.wtf("Sync Service", "An unexpected HTTP Response Code indicating an error has been returned by the webservice while getLocationHistory: Response Code is " + response.code());
                    locationHistoryReceivedInterface.onFailure();
                    return;
                }
                if (response.isSuccessful() && response.code() != 200) {
                    Log.wtf("Sync Service", "An unexpected HTTP Response Code indicating success has been returned by the webservice getLocationHistory: Response Code is " + response.code());
                    locationHistoryReceivedInterface.onFailure();
                    return;
                }
                if (response.code() == 403) {
                    Toast.makeText(context, R.string.getUsersFailedInvalidJwtTokenToastMessage, Toast.LENGTH_LONG).show();
                    Log.wtf("Sync Service", "Invalid JWT token has been used while receiving location history!");
                    //TODO Login Activity anzeigen, da ungueltiger JWT Token
                    locationHistoryReceivedInterface.onFailure();
                    return;
                }
                if (response.code() == 200) {
                    Log.d("Sync Service", "Received location history successfully!");
                    List<TimestampedPosition> usersAround = response.body();
                    for (TimestampedPosition i : usersAround) {
                        locationHistoryList.add(i);
                    }
                    Log.wtf("Sync Service: ", ""+locationHistoryList.size());
                    locationHistoryReceivedInterface.onSuccess(locationHistoryList);
                }
            }

            @Override
            public void onFailure(Call<List<TimestampedPosition>> call, Throwable t) {
                Log.wtf("Sync Service: ", "A serious error with the webservice occurred during getLocationHistory, error:" + t.getMessage());
                locationHistoryReceivedInterface.onFailure();
            }
        });
    }

    private long getInterval() {
        return Long.parseLong(prefs.getString("list_interval", "-1"));
    }

    private void setRadius(int radius) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("list_radius", String.valueOf(radius));
        editor.apply();
    }

    private void setPasswordField(String password) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("text_newpassword", password);
        editor.apply();
    }

    private void setDescField(String description) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("text_userdesc", description);
        editor.apply();
    }

    private void setRadius() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("list_radius", String.valueOf(getPrivacyRadius()));
        editor.apply();
    }

    private void setInterval() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("list_radius", String.valueOf(getInterval()));
        editor.apply();
    }
}