package de.hhn.aib.swlab.wise1920.group01.exercise2.controller.extensions;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import org.osmdroid.util.BoundingBox;
import java.util.ArrayList;
import java.util.List;

import de.hhn.aib.swlab.wise1920.group01.exercise2.R;
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

/**
 * Die Klasse Repräsentiert die Suche nach Points of Interessts für
 * die OpenStreetMap
 * @author mhaering
 * @version 1.0
 *
 */
public class PoiSearchService {

    private Retrofit retrofit;
    private Context context;
    private PoiAPI api;
    private List<PoiDummy> searchResults;

    public PoiSearchService(Context context){
        this.context = context;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.overpass-api.de/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(PoiAPI.class);
    }
    public void getPois(BoundingBox boundingBox, final PoisReceivedInterface poisReceivedInterface){
        String bounding = boundingBox.getLatSouth()+","+boundingBox.getLonWest()+","+boundingBox.getLatNorth()+","+boundingBox.getLonEast();
        String data = "[out:json][timeout:25];\n" +
                "// gather results\n" +
                "(\n" +
                "  // query part for: “\"sehenswürdigkeit\"”\n" +
                "  node[\"tourism\"=\"attraction\"]("+bounding+");\n" +
                "  way[\"tourism\"=\"attraction\"]("+bounding+");\n" +
                "  relation[\"tourism\"=\"attraction\"]("+bounding+");\n" +
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
                if(response.body().getPoiDummyArray()!=null) {
                    searchResults = response.body().getPoiDummyArray();
                    if (searchResults != null) {
                        for (int x = 0; x < searchResults.size(); x++) {
                            String label = "";
                            double lat;
                            double lng;
                            if (searchResults.get(x).getType().equals("relation")) {
                                PoiDummy dummy = findByRef(searchResults.get(x).getPoiMembers());
                                PoiTagsDummy tagsDummy = searchResults.get(x).getPoiTagsDummy();
                                lat = dummy.getLat();
                                lng = dummy.getLon();
                                label = tagsDummy.getName();
                                searchResultList.add(new MapObject(lat, lng, label, null));
                                searchResults.remove(x);
                            }
                        }
                        for (PoiDummy a : searchResults) {
                            String label = "";
                            double lat;
                            double lng;
                            if (a.getType().equals("way")) {
                                if (a.getPoiTagsDummy() != null) {
                                    PoiTagsDummy tagsDummy = a.getPoiTagsDummy();
                                    label = tagsDummy.getName();
                                }

                                ArrayList<Long> secondList = a.getNodesArrayList();
                                long id = secondList.get(0);
                                PoiDummy dummy = findById(searchResults, id);
                                lat = dummy.getLat();
                                lng = dummy.getLon();
                                searchResultList.add(new MapObject(lat, lng, label, null));
                            } else if (a.getType().equals("node")) {
                                if (a.getPoiTagsDummy() != null) {
                                    PoiTagsDummy tag = a.getPoiTagsDummy();
                                    if (tag.getName() != null) {
                                        label = tag.getName();
                                    }
                                    if (a.getLat() != null) {
                                        searchResultList.add(new MapObject(a.getLat(), a.getLon(), label, null));
                                    }
                                }
                            }
                        }
                        poisReceivedInterface.onSuccess(searchResultList);

                    } else {
                        Toast.makeText(context,R.string.noPoisFound, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<PoiElementsDummy> call, Throwable t) {
                Toast.makeText(context,R.string.noPoisFound, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Wenn ein Poi vom Typ "relation" ist sollen die dazu passende ways gefunden
     * und aus der Liste gelöscht werden.
     * @param poiDummies die Liste der zugehörigen PoiDummies des Types "way"
     * @return den Poi der Stellvertretend für alle im ID Bereich des "relation" Punktes gesetzt werden soll
     */
    private PoiDummy findByRef(ArrayList<PoiDummy> poiDummies){

        for(int x=0;x<searchResults.size();x++){
                if (searchResults.get(x).getId()==poiDummies.get(0).getRef()){
                    long id = searchResults.get(x).getNodesArrayList().get(0);
                    for (int index = 0;index<searchResults.size();index++){
                        for (int ways=0;ways<poiDummies.size();ways++){
                            if (searchResults.get(index).getId()==poiDummies.get(ways).getRef()){
                                searchResults.remove(index);
                            }
                        }
                    }
                    return findById(searchResults,id);
                }
            }
        return null;
    }

    /**
     * Wenn ein Poi vom Typ "Way" ist sollen hier der Zugehörige node anhand der ID gefunden werden
     * @param dummyArrayList Liste mit den Nodes
     * @param id des Nodes, welcher gesucht werden soll
     * @return den Poi der stellvertretend für alle Poi's im Bereich des "Way" angezeigt werden soll
     */
    private PoiDummy findById(List<PoiDummy> dummyArrayList ,long id){
        for (PoiDummy dummy : dummyArrayList){
            if(dummy.getId()==id){
                return dummy;
            }
        }
        return null;
    }
}
