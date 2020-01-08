package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Screens.CountdownScreen;
import com.mygdx.game.Screens.EndScreen;
import com.mygdx.game.Screens.PlayScreen;

import server.BackendCommunicator;
import server.dtos.GameMessage;

/**
 * Main Game class to start the hole Game
 */
public class MarioBros extends Game {
    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 208;
    public static final float PPM = 100;

    public static final short NOTHING_BIT = 0;
    public static final short GROUND_BIT = 1;
    public static final short MARIO_BIT = 2;
    public static final short BRICK_BIT = 4;
    public static final short COIN_BIT = 8;
    public static final short DESTROYED_BIT = 16;
    public static final short OBJECT_BIT = 32;
    public static final short ENEMY_BIT = 64;
    public static final short ENEMY_HEAD_BIT = 128;
    public static final short ITEM_BIT = 256;
    public static final short MARIO_HEAD_BIT = 512;
    public static final short PIT_BIT = 1024;
    public static final short GOAL_BIT = 2048;

    public SpriteBatch batch;
    public static AssetManager manager;

    private PlayScreen playScreen;
    private CountdownScreen countdownScreen;

    private int level;

    //reference that needs to be kept to the android module.
    private BackendCommunicator backendCommunicator;
    private String username, password, auth, gameID;
    private boolean countdownEnded = false;
    private boolean soundboolean, quited = false;

    public MarioBros(String auth, String username, String password, String gameID, Boolean soundonoff, BackendCommunicator backendCommunicator, int level) {
        this.backendCommunicator = backendCommunicator;
        this.auth = auth;
        this.username = username;
        this.password = password;
        this.gameID = gameID;
        this.soundboolean = soundonoff;
        this.level = level;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        manager = new AssetManager();
        manager.load("audio/music/mario_music.ogg", Music.class);
        manager.load("audio/sounds/coin.wav", Sound.class);
        manager.load("audio/sounds/bump.wav", Sound.class);
        manager.load("audio/sounds/breakblock.wav", Sound.class);
        manager.load("audio/sounds/powerup_spawn.wav", Sound.class);
        manager.load("audio/sounds/powerup.wav", Sound.class);
        manager.load("audio/sounds/powerdown.wav", Sound.class);
        manager.load("audio/sounds/stomp.wav", Sound.class);
        manager.load("audio/sounds/mariodie.wav", Sound.class);
        manager.finishLoading();

        playScreen = new PlayScreen(this, soundboolean, level);
        countdownScreen = new CountdownScreen(this);
        setScreen(countdownScreen);
    }

    @Override
    public void dispose() {
        super.dispose();
        manager.dispose();
        batch.dispose();
    }

    @Override
    public void render() {
        super.render();
    }

    public void receiveMessage(GameMessage gameMessage) {
        if (countdownEnded) {
            if (playScreen != null) {
                playScreen.receiveMessage(gameMessage);
            }
        } else {
            if (gameMessage.getType().equals("Countdown") && !quited) {
                int secondsLeft = gameMessage.getPayloadInteger();
                switch (secondsLeft) {
                    case 3:
                        countdownScreen.setCountdownLabel("3");
                        break;
                    case 2:
                        countdownScreen.setCountdownLabel("2");
                        break;
                    case 1:
                        countdownScreen.setCountdownLabel("1");
                        break;
                    case 0:
                        countdownScreen.setCountdownLabel("0");
                        countdownEnded = true;

                        setScreen(playScreen);
                        break;
                }
            } else if (gameMessage.getType().equals("WinBecauseLeave")) {
                quited = true;
                EndScreen endScreen = new EndScreen(this);
                endScreen.setEnemyPoints("quitting coward");
                endScreen.setText("VICTORY");
                setScreen(endScreen);
            }
        }
    }

    public void back2HomeActivty() {
        backendCommunicator.stopGame(soundboolean);
    }

    public void sendMessage(GameMessage gameMessage) {
        backendCommunicator.sendMessage(gameMessage);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }
}