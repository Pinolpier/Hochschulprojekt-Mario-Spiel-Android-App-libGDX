package com.mygdx.game.Sprites.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Player.Player;

/**
 * This class represents the Goal of the game.
 * If the player hit the goal, the game ends
 */
public class Goal extends InteractiveTileObject{

    public Goal(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.GOAL_BIT);
    }

    @Override
    public void onHeadHit(Player player) {
        if(player.getId()==1) {
            Hud.addScore(500);
            player.win();
        }
    }
}
