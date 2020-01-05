package com.mygdx.game.Player;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Sprites.Enemies.Enemy;
import com.mygdx.game.Sprites.Enemies.Turtle;

public class Player extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD, WIN };
    public Body b2body;
    public World world;
    public State currentState;
    public State previousState;
    public PlayScreen screen;
    private TextureRegion marioStand;
    private Animation marioRun;
    private TextureRegion marioJump;
    private TextureRegion marioDead;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation bigMarioRun;
    private Animation growMario;
    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean marioIsDead;
    Array<TextureRegion> frames;
    public Player(){
        frames = new Array<>();
    }


    public void defineMario(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.PIT_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT |
                MarioBros.GOAL_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }

    public void defineBigMario(){
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0, 10 / MarioBros.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT |
                MarioBros.PIT_BIT |
                MarioBros.GOAL_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }

    public void die() {
        if (!isDead()) {
            MarioBros.manager.get("audio/music/mario_music.ogg", Music.class).stop();
            MarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
            marioIsDead = true;
            Filter filter = new Filter();
            filter.maskBits = MarioBros.NOTHING_BIT;

            for (Fixture fixture : b2body.getFixtureList()) {
                fixture.setFilterData(filter);
            }

            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
        }
    }

    public boolean isDead(){
        return marioIsDead;
    }
    public float getStateTimer(){
        return stateTimer;
    }
    public boolean isBig(){
        return marioIsBig;
    }
    public void jump(){
        if ( currentState != State.JUMPING ) {
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            currentState = State.JUMPING;
        }
    }

    /**
     * defines what happens when player touches a enemy
     * @param enemy which gets hit
     */
    public void hit(Enemy enemy){
        if(enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.STANDING_SHELL)
            ((Turtle) enemy).kick(enemy.b2body.getPosition().x > b2body.getPosition().x ? Turtle.KICK_RIGHT : Turtle.KICK_LEFT);
        else {
            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                MarioBros.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            } else {
                die();
            }
        }
    }

    public void redefineMario(){
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT |
                MarioBros.PIT_BIT |
                MarioBros.GOAL_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);
        timeToRedefineMario = false;
    }

    /**
     * Updates the Player position and size
     * @param dt
     */
    public void update(float dt){
        if (screen.getHud().isTimeUp() && !isDead()) {
            die();
        }

        if(marioIsBig)
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / MarioBros.PPM);
        else
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);

        setRegion(getFrame(dt));
        if(timeToDefineBigMario)
            defineBigMario();
        if(timeToRedefineMario)
            redefineMario();

    }

    public TextureRegion getFrame(float dt){
        currentState = getState();
        TextureRegion region;

        switch(currentState){
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = (TextureRegion) growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer)) {
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = (TextureRegion) (marioIsBig ? bigMarioRun.getKeyFrame(stateTimer, true) : marioRun.getKeyFrame(stateTimer, true));
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : marioStand;
                break;
        }

        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }

        else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;

    }

    public State getState(){
        if(marioIsDead)
            return State.DEAD;
        else if(runGrowAnimation)
            return State.GROWING;
        else if((b2body.getLinearVelocity().y > 0 && currentState == State.JUMPING) || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if(b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if(b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    public void grow(){
        if( !isBig() ) {
            runGrowAnimation = true;
            marioIsBig = true;
            timeToDefineBigMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() * 2);
            MarioBros.manager.get("audio/sounds/powerup.wav", Sound.class).play();
        }
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }
    public Body getB2body() {
        return b2body;
    }
    public void setB2body(Body b2body) {
        this.b2body = b2body;
    }
    public World getWorld() {
        return world;
    }
    public void setWorld(World world) {
        this.world = world;
    }
    public State getCurrentState() {
        return currentState;
    }
    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }
    public State getPreviousState() {
        return previousState;
    }
    public void setPreviousState(State previousState) {
        this.previousState = previousState;
    }
    public Animation getMarioRun() {
        return marioRun;
    }
    public void setMarioRun(Animation marioRun) {
        this.marioRun = marioRun;
    }
    public TextureRegion getMarioJump() {
        return marioJump;
    }
    public void setMarioJump(TextureRegion marioJump) {
        this.marioJump = marioJump;
    }
    public TextureRegion getMarioDead() {
        return marioDead;
    }
    public void setMarioDead(TextureRegion marioDead) {
        this.marioDead = marioDead;
    }
    public TextureRegion getBigMarioStand() {
        return bigMarioStand;
    }
    public void setBigMarioStand(TextureRegion bigMarioStand) {
        this.bigMarioStand = bigMarioStand;
    }
    public TextureRegion getBigMarioJump() {
        return bigMarioJump;
    }
    public void setBigMarioJump(TextureRegion bigMarioJump) {
        this.bigMarioJump = bigMarioJump;
    }
    public Animation getBigMarioRun() {
        return bigMarioRun;
    }
    public void setBigMarioRun(Animation bigMarioRun) {
        this.bigMarioRun = bigMarioRun;
    }
    public Animation getGrowMario() {
        return growMario;
    }
    public void setGrowMario(Animation growMario) {
        this.growMario = growMario;
    }
    public void setStateTimer(float stateTimer) {
        this.stateTimer = stateTimer;
    }
    public boolean isRunningRight() {
        return runningRight;
    }
    public void setRunningRight(boolean runningRight) {
        this.runningRight = runningRight;
    }
    public boolean isMarioIsBig() {
        return marioIsBig;
    }
    public void setMarioIsBig(boolean marioIsBig) {
        this.marioIsBig = marioIsBig;
    }
    public boolean isRunGrowAnimation() {
        return runGrowAnimation;
    }
    public void setRunGrowAnimation(boolean runGrowAnimation) {
        this.runGrowAnimation = runGrowAnimation;
    }
    public boolean isTimeToDefineBigMario() {
        return timeToDefineBigMario;
    }
    public void setTimeToDefineBigMario(boolean timeToDefineBigMario) {
        this.timeToDefineBigMario = timeToDefineBigMario;
    }
    public boolean isTimeToRedefineMario() {
        return timeToRedefineMario;
    }
    public void setTimeToRedefineMario(boolean timeToRedefineMario) {
        this.timeToRedefineMario = timeToRedefineMario;
    }
    public boolean isMarioIsDead() {
        return marioIsDead;
    }
    public void setMarioIsDead(boolean marioIsDead) {
        this.marioIsDead = marioIsDead;
    }
    public PlayScreen getScreen() {
        return screen;
    }
    public void setScreen(PlayScreen screen) {
        this.screen = screen;
    }
    public TextureRegion getMarioStand() {
        return marioStand;
    }
    public void setMarioStand(TextureRegion marioStand) {
        this.marioStand = marioStand;
    }
    public Array<TextureRegion> getFrames() {
        return frames;
    }
    public void setFrames(Array<TextureRegion> frames) {
        this.frames = frames;
    }
}
