package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Screens.PlayScreen;

public class Luigi {


    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private PlayScreen screen;
    private Animation luigiRun;
    private TextureRegion luigiJump;
    private TextureRegion luigiStand;
    private TextureRegion luigiDead;

    public Luigi(PlayScreen screen) {
        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        for (int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_luigi"), i * 16, 0, 16, 16));
        luigiRun = new Animation(0.1f, frames);
        frames.clear();

        //get jump animation frames and add them to luigiJump Animation
        luigiJump = new TextureRegion(screen.getAtlas().findRegion("little_luigi"), 80, 0, 16, 16);

        //create texture region for luigi standing
        luigiStand = new TextureRegion(screen.getAtlas().findRegion("little_luigi"), 0, 0, 16, 16);

        //create dead mario texture region
        luigiDead = new TextureRegion(screen.getAtlas().findRegion("little_luigi"), 96, 0, 16, 16);

        //define mario in Box2d
        defineMario();

        //set initial values for luigis location, width and height. And initial frame as luigiStand.
        setBounds(0, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        setRegion(luigiStand);
    }

    public enum State {FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD}
}
