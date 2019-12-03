package de.hhn.aib.swlab.wise1920.group01.exercise3.model;

import de.hhn.aib.swlab.wise1920.group01.exercise3.R;

public class Labyrinth {

    int[] tiles = {R.drawable.tile_00, R.drawable.tile_01};

    public Labyrinth() {
        char lab[][] = {
                {0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0}
        };
    }

    void init() {

    }
}

