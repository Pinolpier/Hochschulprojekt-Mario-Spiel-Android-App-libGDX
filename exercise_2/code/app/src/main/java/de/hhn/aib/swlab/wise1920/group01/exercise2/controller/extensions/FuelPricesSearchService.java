package de.hhn.aib.swlab.wise1920.group01.exercise2.controller.extensions;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hhn.aib.swlab.wise1920.group01.exercise2.R;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.MapObject;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.FuelAPI;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.FuelDTO;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.FuelPricesReceivedInterface;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.GasStationDummy;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class is responsible for the search of fuelprices with its own retrofit adapter
 */
public class FuelPricesSearchService {
    private Retrofit retrofit;
    private FuelAPI api;
    private final String apikey = "f29f90a7-c993-4544-ac05-6fc8670d9d62";
    private int radius=15;
    private Context context;
    /**
     * Constructor for class FuelPricesSearchService
     */
    public FuelPricesSearchService(Context context) {
        this.context = context;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://creativecommons.tankerkoenig.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(FuelAPI.class);
    }

    /**
     * This method builds a query to get the fuelprices around the current position of the user and saves the information
     * in an ArrayList
     * @param lat           latitude of current location
     * @param lng           longitude of current location
     * @param fuelPricesReceivedInterface
     */
    public void getFuelPrices(double lat, double lng, final FuelPricesReceivedInterface fuelPricesReceivedInterface) {
        Log.d("FuelSearchPriceService", "Fuel price search called!");
        HashMap<String, String> map = new HashMap<>();
        map.put("lat", Double.toString(lat));
        map.put("lng", Double.toString(lng));
        map.put("rad", ""+radius);
        map.put("type", "all");
        map.put("apikey", apikey);
        Call<FuelDTO> call = api.getSearchResult(map);
        call.enqueue(new Callback<FuelDTO>() {
            @Override
            public void onResponse(Call<FuelDTO> call, Response<FuelDTO> response) {
                ArrayList searchResultList = new ArrayList<MapObject>();
                assert response.body() != null;
                List<GasStationDummy> searchResults = response.body().getStationsAround();
                if (searchResults != null) {
                    for (GasStationDummy i : searchResults) {
                        String description = i.getStreet() + " " + i.getHouseNumber() + "\n" + i.getPostCode() + " " + i.getPlace();
                        String label = i.getName()+"\n"+"Diesel: " + i.getDiesel() + "\n" + "Super e5: " + i.getE5() + "\n" + "Super e10: " + i.getE10();
                        Log.d("FuelSearchPriceService", "Got a new gas station with parsed description: " + description);
                        searchResultList.add(new MapObject(i.getLatitude(), i.getLongitude(), label, description));
                    }
                    fuelPricesReceivedInterface.onSuccess(searchResultList);
                } else
                    Toast.makeText(context, "Liste ist leer", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<FuelDTO> call, Throwable t) {
                Log.wtf("FuelSearchPriceService: ", "Fatal Error in FuelSearchPriceService.search()!" + t.getMessage());
                fuelPricesReceivedInterface.onFailure();
            }
        });
    }
}
