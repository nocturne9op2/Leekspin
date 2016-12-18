package com.lsa.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.lsa.entities.Text;
import com.lsa.game.Game;
import com.lsa.managers.GameStateManager;

public class LoadingState extends GameState {
    private SpriteBatch batch;
    private Stage backStage;
    private Stage stage;

    //flags
    private boolean actionAdded = false;

    //text components
    private Text loading;

    //actor components
    private Texture tex;
    private Actor blankOverlayActor;
    private Actor gradientActor;

    public LoadingState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void init() {
        batch = new SpriteBatch();
        backStage = new Stage(new ScreenViewport());
        stage = new Stage(new ScreenViewport());

        //text components
        initializeText();

        //actor components
        initializeActors();

        queueAssets();
    }

    private void initializeText() {
        loading = new Text("loading...", 300, 120, 267.068f, stage);
    }

    private void initializeActors() {
        tex = new Texture(Gdx.files.internal("img/gradient_panel.png"));
        tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        gradientActor = new Image(tex);
        gradientActor.setSize(Game.GAME_WIDTH, Game.GAME_HEIGHT);

        backStage.addActor(gradientActor);

        tex = new Texture(Gdx.files.internal("img/blank_panel.png"));
        blankOverlayActor = new Image(tex);
        blankOverlayActor.setSize(Game.GAME_WIDTH, Game.GAME_HEIGHT);
        blankOverlayActor.getColor().a = 0;

        stage.addActor(blankOverlayActor);
    }

    private void queueAssets() {
        Game.ast.load("audio/pop.ogg", Sound.class);
        Game.ast.load("audio/bamboo_hit.ogg", Sound.class);

        Game.ast.load("audio/first_section.ogg", Music.class);
        Game.ast.load("audio/second_section.ogg", Music.class);

        Game.ast.load("img/blank_panel.png", Texture.class);
        Game.ast.load("img/white_panel.png", Texture.class);
        Game.ast.load("img/gradient_panel.png", Texture.class);
        Game.ast.load("img/leek_texture.png", Texture.class);
        Game.ast.load("img/squigle_texture.png", Texture.class);
    }

    @Override
    public void update(float dt) {
        backStage.act(dt);
        stage.act(dt);

        if (Game.ast.update() && !Game.playServices.isConnecting()) {
            if (!actionAdded) {
                blankOverlayActor.addAction(Actions.delay(1, (Actions.fadeIn(0.2f, Interpolation.pow2Out))));
                actionAdded = true;
            } else if (blankOverlayActor.getColor().a > 0.9999) {
                gsm.setState(GameStateManager.PLAY);
            }
        }
    }

    @Override
    public void draw() {
        backStage.draw();
        stage.draw();
    }

    @Override
    public void setInput(String input) {
    }

    @Override
    public void dispose() {
        batch.dispose();
        backStage.dispose();
        stage.dispose();
        loading.dispose();
        tex.dispose();
    }
}