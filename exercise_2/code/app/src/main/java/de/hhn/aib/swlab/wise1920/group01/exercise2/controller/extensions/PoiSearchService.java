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
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.PoiDTO;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.PoiElementsDummy;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.PoiTagsDummy;
import de.hhn.aib.swlab.wise1920.group01.exercise2.model.sync.extensions.PoisReceivedInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class is responsible for the search of Points of Interests(pois) with its own retrofit adapter
 */
public class PoiSearchService {

    private Retrofit retrofit;
    private Context context;
    private PoiAPI api;
    private List<PoiDTO> searchResults;

    /**
     * Constructor for PoiSearchService
     * @param context
     */
    public PoiSearchService(Context context){
        this.context = context;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.overpass-api.de/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(PoiAPI.class);
    }

    /**
     * This method builds a query to get the pois in the given bounding box and saves the information
     * in an ArrayList
     * @param boundingBox               area in which pois should be searched in
     * @param poisReceivedInterface
     */
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
        Log.d("PoiSearchService: ", "getPois started!");
        Call<PoiElementsDummy> call = api.getSearchResult(data);
        call.enqueue(new Callback<PoiElementsDummy>() {
            @Override
            public void onResponse(Call<PoiElementsDummy> call, Response<PoiElementsDummy> response) {
                ArrayList searchResultList = new ArrayList<MapObject>();
                if(response.body()!=null){
                    if (response.body().getPoiDTOArray() != null) {
                        searchResults = response.body().getPoiDTOArray();
                    if (searchResults != null) {
                        for (int x = 0; x < searchResults.size(); x++) {
                            String label = "";
                            double lat;
                            double lng;
                            if (searchResults.get(x).getType().equals("relation")) {
                                PoiDTO dummy = findByRef(searchResults.get(x).getPoiMembers());
                                PoiTagsDummy tagsDummy = searchResults.get(x).getPoiTagsDummy();
                                lat = dummy.getLat();
                                lng = dummy.getLon();
                                label = tagsDummy.getName();
                                searchResultList.add(new MapObject(lat, lng, label, null));
                                searchResults.remove(x);
                            }
                        }
                        for (PoiDTO a : searchResults) {
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
                                PoiDTO dummy = findById(searchResults, id);
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
            }}
            @Override
            public void onFailure(Call<PoiElementsDummy> call, Throwable t) {
                Log.wtf("PoiSearchService: ","on Failure at getPois with: "+t.getMessage());
                //Toast.makeText(context, R.string.connectionOnFailureToastMessage, Toast.LENGTH_LONG).show();
                poisReceivedInterface.onFailure();

            }
        });
    }

    /**
     * If a poi of type "relation" is found, suitable ways should get deleted from list
     * @param poiDummies    list of PoiDummies of type "way"
     * @return              poi thats representable for all IDs of "relation" point
     */
    private PoiDTO findByRef(ArrayList<PoiDTO> poiDummies) {

        for(int x=0;x<searchResults.size();x++){
            if(poiDummies!=null)
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
     * If poi is of type "Way" all associated nodes should be found by ID
     * @param dummyArrayList    list with nodes
     * @param id                ID of node that should be searched
     * @return                  poi that is representable for all pois that get shown in area of "Way"
     */
    private PoiDTO findById(List<PoiDTO> dummyArrayList, long id) {
        for (PoiDTO dummy : dummyArrayList) {
            if(dummy.getId()==id){
                return dummy;
            }
        }
        return null;
    }
}
