package de.hhn.aib.swlab.wise1920.group01.exercise2.controller.extensions;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import org.osmdroid.util.BoundingBox;
import java.util.ArrayList;
import java.util.List;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.MapObject;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.PoiAPI;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.PoiDummy;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.PoiElementsDummy;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.PoiTagsDummy;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.PoisReceivedInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PoiSearchService {

    private Retrofit retrofit;
    private Context context;
    private PoiAPI api;

    public PoiSearchService(Context context){
        this.context = context;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.overpass-api.de/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(PoiAPI.class);
    }
    public void getPois(BoundingBox boundingBox, final PoisReceivedInterface poisReceivedInterface){
        //String bounding = boundingBox.getLatNorth()+","+boundingBox.getLonWest()+","+boundingBox.getLatSouth()+","+boundingBox.getLonEast();
        String data = "[out:json][timeout:25];\n" +
                "// gather results\n" +
                "(\n" +
                "  // query part for: “\"sehenswürdigkeit\"”\n" +
                "  node[\"tourism\"=\"attraction\"](49.133767131768884,9.196586608886719,49.171435238357176,9.242849349975586);\n" +
                "  way[\"tourism\"=\"attraction\"](49.133767131768884,9.196586608886719,49.171435238357176,9.242849349975586);\n" +
                "  relation[\"tourism\"=\"attraction\"](49.133767131768884,9.196586608886719,49.171435238357176,9.242849349975586);\n" +
                ");\n" +
                "// print results\n" +
                "out body;\n" +
                ">;\n" +
                "out skel qt;";
        Log.d("PoiSearchService: ","getPois gestartet!");
        Call<PoiElementsDummy> call = api.getSearchResult(data);
        call.enqueue(new Callback<PoiElementsDummy>() {
            @Override
            public void onResponse(Call<PoiElementsDummy> call, Response<PoiElementsDummy> response) {
                ArrayList<MapObject> searchResultList = new ArrayList();
                List<PoiDummy> searchResults = response.body().getPoiDummyArray();
                if (searchResults != null) {
                    if (searchResults.size() >= 0) {
                        for (PoiDummy a : searchResults) {
                            if(a.getPoiTagsList()!=null){
                            PoiTagsDummy tag = a.getPoiTagsList();
                            String label="";
                            if (tag.getName()!=null) {label = tag.getName();}
                            if(a.getLat()!=null){
                            searchResultList.add(new MapObject(a.getLat(), a.getLon(), label, null));
                        }}}
                        poisReceivedInterface.onSuccess(searchResultList);
                    } else
                        Toast.makeText(context, "Poi Liste ist leer!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<PoiElementsDummy> call, Throwable t) {
                Toast.makeText(context, "Fehler beim abfragen der Pois", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
