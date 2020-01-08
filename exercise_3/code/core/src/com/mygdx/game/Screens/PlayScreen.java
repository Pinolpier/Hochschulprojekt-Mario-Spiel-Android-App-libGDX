package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Player.Mario;
import com.mygdx.game.Player.Player;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.Sprites.Enemies.Enemy;
import com.mygdx.game.Sprites.Items.Item;
import com.mygdx.game.Sprites.Items.ItemDef;
import com.mygdx.game.Sprites.Items.Mushroom;
import com.mygdx.game.Tools.B2WorldCreator;
import com.mygdx.game.Tools.WorldContactListener;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import server.dtos.GameMessage;

public class PlayScreen implements Screen {
    private MarioBros game;
    private TextureAtlas atlas;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    private Player player;
    private Player player2;
    private Music music;
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;
    private int ownScore = 0;
    private int enemyScore = 0;
    private int positionTicks = 0;
    private boolean endMessageSent = true;

    public PlayScreen(MarioBros game, Boolean soundboolean) {
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        this.game = game;
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gamecam);
        hud = new Hud(game.batch);
        maploader = new TmxMapLoader();
        map = maploader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();
        creator = new B2WorldCreator(this);


        player = new Mario(this);
        player.setId(1);
        player2 = new Mario(this);
        player2.setId(2);

        world.setContactListener(new WorldContactListener());

        music = MarioBros.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        if (soundboolean) {
            music.play();
        }

        items = new Array<>();
        itemsToSpawn = new LinkedBlockingQueue<>();
    }

    public void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);
    }

    /**
     * if there is a item in the block, it will spawn
     */
    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type == Mushroom.class) {
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }


    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }

    public void handleInput(){
        if(endMessageSent){
        if(player.getCurrentState() != Mario.State.DEAD) {
            GameMessage sendMessage = new GameMessage("Movement", game.getAuth(), GameMessage.Status.OK, game.getGameID(), null);
            if (positionTicks % 20 == 0) {
                ArrayList<String> position = new ArrayList<>();
                position.add(player.getXPosition());
                position.add(player.getYPosition());
                position.add(player.getXVelocity());
                position.add(player.getYVelocity());
                sendMessage.setStringList(position);
            }
            positionTicks++;
            if (Gdx.input.justTouched()) {
                player.jump();
                sendMessage.setPayloadInteger(0);
                game.sendMessage(sendMessage);
            }
            if (Gdx.input.getPitch() < -10) {
                player.getB2body().applyLinearImpulse(new Vector2(0.1f, 0), player.getB2body().getWorldCenter(), true);
                sendMessage.setPayloadInteger(1);
                game.sendMessage(sendMessage);
            }
            if (Gdx.input.getPitch() > 20) {
                player.getB2body().applyLinearImpulse(new Vector2(-0.1f, 0), player.getB2body().getWorldCenter(), true);
                sendMessage.setPayloadInteger(2);
                game.sendMessage(sendMessage);
            }
        }
        }
    }

    public void update(float dt) {

        handleInput();
        handleSpawningItems();
        world.step(1 / 60f, 6, 2);
        player.update(dt);
        player2.update(dt);
        for (Enemy enemy : creator.getEnemies()) {
            enemy.update(dt);
            enemy.b2body.setActive(true);

        }

        for (Item item : items)
            item.update(dt);

        hud.update(dt);
        if (player.getCurrentState() != Mario.State.DEAD) {
            gamecam.position.x = player.getB2body().getPosition().x;
        }
        gamecam.update();
        renderer.setView(gamecam);

    }


    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();
        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        player2.draw(game.batch);
        for (Enemy enemy : creator.getEnemies())
            enemy.draw(game.batch);
        for (Item item : items)
            item.draw(game.batch);
        game.batch.end();
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if (gameOver() | gameWin()) {
            if (endMessageSent) {
                sendEndGameMessage();
                endMessageSent = false;
            }
        }
    }

    public boolean gameOver() {
        return player.getCurrentState() == Mario.State.DEAD && player.getStateTimer() > 3;
    }

    public boolean gameWin() {
        return player.getCurrentState() == Mario.State.WIN;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    public Hud getHud() {
        return hud;
    }

    public void receiveMessage(GameMessage gameMessage) {
        if (gameMessage != null && gameMessage.getType() != null) {
            switch (gameMessage.getType()) {
                case "Movement":
                    if (gameMessage.getStringList() != null) {
                        ArrayList<String> position = gameMessage.getStringList();
                        player2.setPosition(Float.parseFloat(position.get(0)), Float.parseFloat(position.get(1)));
                        player2.b2body.setLinearVelocity(Float.parseFloat(position.get(2)), Float.parseFloat(position.get(3)));
                    }
                    int status = gameMessage.getPayloadInteger();
                    switch (status) {
                        case 0:
                            player2.jump();
                            break;
                        case 1:
                            player2.getB2body().applyLinearImpulse(new Vector2(0.1f, 0), player2.getB2body().getWorldCenter(), true);
                            break;
                        case 2:
                            player2.getB2body().applyLinearImpulse(new Vector2(-0.1f, 0), player2.getB2body().getWorldCenter(), true);
                            break;
                    }
                    break;
                case "scoreRequest":
                    GameMessage scoreReport = new GameMessage("scoreReport", game.getAuth(), GameMessage.Status.OK, game.getGameID(), null);
                    scoreReport.setPayloadInteger(hud.getScore());
                    game.sendMessage(scoreReport);
                    break;
                case "WinnerEvaluation": {
                    int won = gameMessage.getPayloadInteger();
                    String player1score = gameMessage.getStringList().get(0);
                    String player2score = gameMessage.getStringList().get(1);
                    int p1score = Integer.parseInt(player1score), p2score = Integer.parseInt(player2score);
                    EndScreen endScreen = new EndScreen(game);
                    switch (won) {
                        case -1:
                            ownScore = (p1score > p2score) ? p2score : p1score;
                            enemyScore = (p1score > p2score) ? p1score : p2score;
                            endScreen.setPoints("" + ownScore);
                            endScreen.setEnemyPoints("" + enemyScore);
                            endScreen.setText("DEFEAT");
                            game.setScreen(endScreen);
                            break;
                        case 0:
                            ownScore = p1score;
                            enemyScore = p1score;
                            endScreen.setPoints("" + ownScore);
                            endScreen.setEnemyPoints("" + enemyScore);
                            endScreen.setText("DRAW");
                            game.setScreen(endScreen);
                            break;
                        case 1:
                            ownScore = (p1score < p2score) ? p2score : p1score;
                            enemyScore = (p1score < p2score) ? p1score : p2score;
                            endScreen.setPoints("" + ownScore);
                            endScreen.setEnemyPoints("" + enemyScore);
                            endScreen.setText("VICTORY");
                            game.setScreen(endScreen);
                            break;
                    }
                    break;
                }
                case "WinBecauseLeave": {
                    EndScreen endScreen = new EndScreen(game);
                    endScreen.setPoints("" + hud.getScore());
                    endScreen.setEnemyPoints("quitting coward");
                    endScreen.setText("VICTORY");
                    game.setScreen(endScreen);
                    break;
                }
            }
        }
    }

    private void sendEndGameMessage() {
        GameMessage endMessage = new GameMessage("endGame", game.getAuth(), GameMessage.Status.OK, game.getGameID(), null);
        endMessage.setPayloadInteger(hud.getScore());
        game.sendMessage(endMessage);
    }
}