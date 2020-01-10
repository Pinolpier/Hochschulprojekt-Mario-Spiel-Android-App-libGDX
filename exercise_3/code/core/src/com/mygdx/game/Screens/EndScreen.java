package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MarioBros;

/**
 * This class represents screen shown when something ends the game
 */
public class EndScreen implements Screen {
    private Viewport viewport;
    private Stage stage;
    private MarioBros game;
    private Label points;
    private Label enemy;
    private Label textLabel;


    public EndScreen(MarioBros MarioGame){
        this.game = MarioGame;

        viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport,(game).batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        textLabel = new Label("GAME OVER", font);
        Label playAgainLabel = new Label("Click to Exit game", font);
        points = new Label("Your points: ",font);
        enemy = new Label("Enemy points: ",font);

        table.add(textLabel).expandX();
        table.row();
        table.add(playAgainLabel).expandX().padTop(10f);
        table.add(points).expandX();
        table.add(enemy).expandX();
        stage.addActor(table);
    }

    public void setPoints(String ownPoints) {
        points.setText("Your points: "+ ownPoints);
    }
    public void setEnemyPoints(String enemyPoints) {
        enemy.setText("Enemy points: "+enemyPoints);
    }
    public void setText(String text){
        textLabel.setText(text);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(Gdx.input.justTouched()) {
            game.back2HomeActivty();
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

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
        stage.dispose();
    }
}