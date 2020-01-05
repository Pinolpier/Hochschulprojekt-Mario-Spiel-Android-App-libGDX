package com.mygdx.game.Sprites.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Player.Player;
import com.mygdx.game.Screens.PlayScreen;

/**
 * defines the pits/holes in de Map
 */
public class Pit extends InteractiveTileObject {
    public Pit(PlayScreen screen, MapObject object){
        super(screen,object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.PIT_BIT);
    }

    /**
     *player dies, if he falls in de pit
     * @param player which falls in the pit
     */
    @Override
    public void onHeadHit(Player player) {
        player.die();
    }
}
