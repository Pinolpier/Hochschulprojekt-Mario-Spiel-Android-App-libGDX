package com.mygdx.game.Sprites.TileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Player.Player;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.Screens.PlayScreen;

public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Player mario) {
        if(mario.isBig()) {
            setCategoryFilter(MarioBros.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            MarioBros.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
        MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
    }

}
