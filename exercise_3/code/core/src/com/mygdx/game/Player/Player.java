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
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Sprites.Enemies.Enemy;
import com.mygdx.game.Sprites.Enemies.Turtle;

public class Player extends Sprite {


    public String getXVelocity() {
        return "" + b2body.getLinearVelocity().x;
    }

    public Body b2body;
    public World world;
    public State currentState;
    public State previousState;
    public PlayScreen screen;
    private TextureRegion PlayerStand;
    private Animation PlayerRun;
    private TextureRegion PlayerJump;
    private TextureRegion PlayerDead;
    private TextureRegion bigPlayerStand;
    private TextureRegion bigPlayerJump;
    private Animation bigPlayerRun;
    private Animation growMario;
    private float stateTimer;
    private boolean runningRight;
    private boolean PlayerIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigPlayer;
    private boolean timeToRedefinePlayer;
    private boolean PlayerIsDead;
    private boolean marioReachedGoal;
    private int id=0;
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

    public String getXPosition(){
        return ""+b2body.getPosition().x;
    }
    public String getYPosition(){
        return ""+b2body.getPosition().y;
    }

    public String getYVelocity() {
        return "" + b2body.getLinearVelocity().y;
    }

    public boolean tooFast() {
        return b2body.getLinearVelocity().len() >= 2;
    }

    public enum State {FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD, WIN}


    public void defineBigPlayer(){
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
        timeToDefineBigPlayer = false;
    }

    public void die() {
        if (isDead()) {
            if (id == 1) {
                MarioBros.manager.get("audio/music/mario_music.ogg", Music.class).stop();
                MarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
                PlayerIsDead = true;
                addScore(-500);

                Filter filter = new Filter();
                filter.maskBits = MarioBros.NOTHING_BIT;

                for (Fixture fixture : b2body.getFixtureList()) {
                    fixture.setFilterData(filter);
                }

                b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            }
        }
    }

    public void addScore(int value){
        if(id==1)
        Hud.addScore(value);
    }

    public void win() {
        if(isDead()) {
            if(id==1) marioReachedGoal = true;
        }
    }

    public boolean isDead(){
        return !PlayerIsDead;
    }
    public float getStateTimer(){
        return stateTimer;
    }
    public boolean isBig(){
        return PlayerIsBig;
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
            if (PlayerIsBig) {
                PlayerIsBig = false;
                timeToRedefinePlayer = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                MarioBros.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            } else {
                die();
            }
        }
    }

    public void redefinePlayer(){
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
        timeToRedefinePlayer = false;
    }

    /**
     * Updates the Player position and size
     * @param dt deltaTime
     */
    public void update(float dt){
        if (screen.getHud().isTimeUp() && isDead()) {
            die();
        }

        if(PlayerIsBig)
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / MarioBros.PPM);
        else
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);

        setRegion(getFrame(dt));
        if(timeToDefineBigPlayer)
            defineBigPlayer();
        if(timeToRedefinePlayer)
            redefinePlayer();

    }

    public TextureRegion getFrame(float dt){
        currentState = getState();
        TextureRegion region;

        switch(currentState){
            case DEAD:
                region = PlayerDead;
                break;
            case GROWING:
                region = (TextureRegion) growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer)) {
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
                region = PlayerIsBig ? bigPlayerJump : PlayerJump;
                break;
            case RUNNING:
                region = (TextureRegion) (PlayerIsBig ? bigPlayerRun.getKeyFrame(stateTimer, true) : PlayerRun.getKeyFrame(stateTimer, true));
                break;
            case FALLING:
            case STANDING:
            default:
                region = PlayerIsBig ? bigPlayerStand : PlayerStand;
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
        if(PlayerIsDead)
            return State.DEAD;
        else if(runGrowAnimation)
            return State.GROWING;
        else if((b2body.getLinearVelocity().y > 0 && currentState == State.JUMPING) || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if(b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if(b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else if(marioReachedGoal)
            return State.WIN;
        else
            return State.STANDING;
    }

    public void grow(){
        if( !isBig() ) {
            runGrowAnimation = true;
            PlayerIsBig = true;
            timeToDefineBigPlayer = true;
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
    public Animation getPlayerRun() {
        return PlayerRun;
    }
    public void setPlayerRun(Animation playerRun) {
        this.PlayerRun = playerRun;
    }
    public TextureRegion getPlayerJump() {
        return PlayerJump;
    }
    public void setPlayerJump(TextureRegion playerJump) {
        this.PlayerJump = playerJump;
    }
    public TextureRegion getPlayerDead() {
        return PlayerDead;
    }
    public void setPlayerDead(TextureRegion playerDead) {
        this.PlayerDead = playerDead;
    }
    public TextureRegion getBigPlayerStand() {
        return bigPlayerStand;
    }
    public void setBigPlayerStand(TextureRegion bigPlayerStand) {
        this.bigPlayerStand = bigPlayerStand;
    }
    public TextureRegion getBigPlayerJump() {
        return bigPlayerJump;
    }
    public void setBigPlayerJump(TextureRegion bigPlayerJump) {
        this.bigPlayerJump = bigPlayerJump;
    }
    public Animation getBigPlayerRun() {
        return bigPlayerRun;
    }
    public void setBigPlayerRun(Animation bigPlayerRun) {
        this.bigPlayerRun = bigPlayerRun;
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
    public boolean isPlayerIsBig() {
        return PlayerIsBig;
    }
    public void setPlayerIsBig(boolean playerIsBig) {
        this.PlayerIsBig = playerIsBig;
    }
    public boolean isRunGrowAnimation() {
        return runGrowAnimation;
    }
    public void setRunGrowAnimation(boolean runGrowAnimation) {
        this.runGrowAnimation = runGrowAnimation;
    }
    public boolean isTimeToDefineBigPlayer() {
        return timeToDefineBigPlayer;
    }
    public void setTimeToDefineBigPlayer(boolean timeToDefineBigPlayer) {
        this.timeToDefineBigPlayer = timeToDefineBigPlayer;
    }
    public boolean isTimeToRedefinePlayer() {
        return timeToRedefinePlayer;
    }
    public void setTimeToRedefinePlayer(boolean timeToRedefinePlayer) {
        this.timeToRedefinePlayer = timeToRedefinePlayer;
    }
    public boolean isPlayerIsDead() {
        return PlayerIsDead;
    }
    public void setPlayerIsDead(boolean playerIsDead) {
        this.PlayerIsDead = playerIsDead;
    }
    public PlayScreen getScreen() {
        return screen;
    }
    public void setScreen(PlayScreen screen) {
        this.screen = screen;
    }
    public TextureRegion getPlayerStand() {
        return PlayerStand;
    }
    public void setPlayerStand(TextureRegion playerStand) {
        this.PlayerStand = playerStand;
    }
    public Array<TextureRegion> getFrames() {
        return frames;
    }
    public void setFrames(Array<TextureRegion> frames) {
        this.frames = frames;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
