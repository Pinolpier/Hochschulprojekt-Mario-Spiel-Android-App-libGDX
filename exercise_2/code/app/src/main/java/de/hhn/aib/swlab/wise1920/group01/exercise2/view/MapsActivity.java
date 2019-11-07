package de.hhn.aib.swlab.wise1920.group01.exercise2.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.views.MapView;


import de.hhn.aib.swlab.wise1920.group01.exercise2.R;
import de.hhn.aib.swlab.wise1920.group01.exercise2.controller.MapFunctionality;

/**
 * The class represents the view on a Map with a search button and a center Button
 */
public class MapsActivity extends AppCompatActivity {

    MapView map;
    private SearchView searchView;
    private MapFunctionality controller;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_maps);
        map = findViewById(R.id.map);
        controller = new MapFunctionality(map, getIntent().getExtras(), this);
        map.setTileSource(new XYTileSource("Mapnik",
                0, 19, 256, ".png", new String[] {
                "https://swlab-maps.hhn.sisrv.de/" },"© OpenStreetMap contributors",
                new TileSourcePolicy(10,
                        TileSourcePolicy.FLAG_NO_BULK
                                | TileSourcePolicy.FLAG_NO_PREVENTIVE
                                | TileSourcePolicy.FLAG_USER_AGENT_MEANINGFUL
                                | TileSourcePolicy.FLAG_USER_AGENT_NORMALIZED
                )));
        map.setMultiTouchControls(true);
        map.getOverlays().clear();
        map.invalidate();

        searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                controller.search(query);
                Toast.makeText(MapsActivity.this,query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return true;
    }

    /**
     * This Method calls the Controller to center the map to tha actual location of the User
     * It gets calles when de User push the center button in the View
     * @param v View of the actual Activity
     */
    public void setCenter(View v){
        controller.setCenter();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.getPoi();
        System.out.println("testtttt");
        return super.onTouchEvent(event);
    }
}