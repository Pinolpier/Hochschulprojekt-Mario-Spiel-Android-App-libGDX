package de.hhn.aib.swlab.wise1920.group01.exercise2.controller.extensions;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hhn.aib.swlab.wise1920.group01.exercise2.model.MapObject;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.FuelAPI;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.FuelDummy;
import de.hhn.aib.swlab.wise1920.group01.exercise2.view.MapsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FuelSearchPricesService {
    private Retrofit retrofit;
    private FuelAPI api;
    private final String apikey = "f29f90a7-c993-4544-ac05-6fc8670d9d62";

    public FuelSearchPricesService(){
        retrofit = new Retrofit.Builder()
                .baseUrl("https://creativecommons.tankerkoenig.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(FuelAPI.class);
    }

    public void search(double lat, double lng, final MapsActivity activity){
        HashMap<String,String> map = new HashMap<>();
        map.put("lat",""+lat);
        map.put("lng",""+lng);
        map.put("rad","10");
        map.put("type","all");
        map.put("apikey",apikey);
        Call<List<FuelDummy>> call = api.getSearchResult("list.php?",map);
        call.enqueue(new Callback<List<FuelDummy>>() {
            @Override
            public void onResponse(Call<List<FuelDummy>> call, Response<List<FuelDummy>> response) {
                ArrayList<MapObject> searchResultList = new ArrayList();
                List<FuelDummy> searchResults = response.body();
                if(searchResults!=null){
                for(FuelDummy i : searchResults){

                    String description =i.getName()+"/n"+i.getStreet()+" "+i.getHouseNumber()+"/n"+i.getPlace()+" "+i.getPlace()+"/n"+"Diesel: "+i.getDiesel()+"/n"+"e5: "+i.getE5()+"/n"+"e10: "+i.getE10();
                    searchResultList.add(new MapObject(i.getLatitude(),i.getLongitude(),description));
                }
                activity.setSearchResults(searchResultList.toArray(new MapObject[searchResultList.size()]));
            }
            else
                Toast.makeText(activity,"Liste ist leer",Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onFailure(Call<List<FuelDummy>> call, Throwable t) {
                Log.wtf("SearchService: ", "Fatal Error in SearchService.search()!");
                Toast.makeText(activity,"Fehler"+t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
