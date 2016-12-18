package com.lsa.game;

//created by justin del rosario

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.lsa.managers.AdHandler;
import com.lsa.managers.GameData;
import com.lsa.managers.GameStateManager;
import com.lsa.managers.PlayServices;

public class Game extends ApplicationAdapter {
    public final static float screenWidth = 1440;
    public final static float screenHeight = 2560;

    //dimensions
    public static int GAME_WIDTH;
    public static int GAME_HEIGHT;

    public static float screenWidthRatio;
    public static float screenHeightRatio;

    public static float PPM;

    public static float worldWidth;
    public static float worldHeight;

    public static float worldWidthRatio;
    public static float worldHeightRatio;

    //objects
    public static AssetManager ast;
    public static GameData dat;
    public static OrthographicCamera cam;
    public static FreeTypeFontGenerator gen;
    public static GameStateManager gsm;

    public static PlayServices playServices;
    public static AdHandler adHandler;

    public Game(PlayServices playServices, AdHandler adHandler) {
        this.playServices = playServices;
        this.adHandler = adHandler;
    }

    @Override
    public void create() {
        //dimensions
        GAME_WIDTH = Gdx.graphics.getWidth();
        GAME_HEIGHT = Gdx.graphics.getHeight();

        screenWidthRatio = GAME_WIDTH / screenWidth;
        screenHeightRatio = GAME_HEIGHT / screenHeight;

        PPM = 32 * GAME_WIDTH / (screenWidth / 3);

        worldWidth = GAME_WIDTH / PPM;
        worldHeight = GAME_HEIGHT / PPM;

        worldWidthRatio = GAME_WIDTH / (screenWidth * PPM);
        worldHeightRatio = GAME_HEIGHT / (screenHeight * PPM);

        //objects
        ast = new AssetManager();

        dat = new GameData();

        cam = new OrthographicCamera();
        cam.setToOrtho(false, worldWidth, worldHeight);

        gen = new FreeTypeFontGenerator(Gdx.files.internal("font/Jaapokki-Regular_edit.otf"));

        gsm = new GameStateManager();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gsm.update(Gdx.graphics.getDeltaTime());

        gsm.draw();
    }

    @Override
    public void dispose() {
        ast.dispose();
        gen.dispose();
        gsm.dispose();
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
}