package com.mygdx.game.Sprites.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Player.Player;

public class Goal extends InteractiveTileObject{

    public Goal(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.GOAL_BIT);
    }

    @Override
    public void onHeadHit(Player mario) {
        if(mario.getId()==1) {
            Hud.addScore(500);
            mario.win();
        }
    }
}
