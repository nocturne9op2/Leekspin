package com.lsa.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
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

public class GameOverState extends GameState implements InputProcessor {
    private SpriteBatch batch;
    private Stage backStage;
    private Stage stage;

    //flags
    private boolean toMain = false;
    private boolean actionAdded = false;
    private boolean adProcessing = false;

    //text components
    private Text spins;
    private Text spinsAmount;
    private Text roundSpins;
    private Text firstTimeRow;
    private Text secondTimeRow;
    private Text thirdTimeRow;
    private Text ok;

    //actor components
    private Texture tex;
    private Actor blankOverlayActor;
    private Actor blankWhiteActor;

    //audio components
    private Sound bambooHit;

    public GameOverState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void init() {
        batch = new SpriteBatch();
        backStage = new Stage(new ScreenViewport());
        stage = new Stage(new ScreenViewport());

        //game data update
        if (Game.dat.greaterRoundSpins(Game.dat.getTentativeRoundSpins())) {
            Game.dat.replaceMaxRoundSpins(Game.dat.getTentativeRoundSpins());
        }

        if (Game.dat.greaterTime(Game.dat.getTentativeTime())) {
            Game.dat.replaceMaxTime(Game.dat.getTentativeTime());
        }

        //text components
        initializeText();
        timeConversion();

        //actor components
        initializeActors();

        //audio components
        initializeAudio();

        //extra
        Game.playServices.submitScore((int) Game.dat.getTotalSpins());

        //input components
        Gdx.input.setInputProcessor(this);

        bambooHit.play();
    }

    private void initializeText() {
        spins = new Text("spins:", 300, 120, 267.068f, stage);
        spinsAmount = new Text(Long.toString(Game.dat.getTotalSpins()), 250, 120, 607.502f, stage);

        long roundSpinsAmount = Game.dat.getTentativeRoundSpins();
        String roundSpinsText = (roundSpinsAmount == 1) ? roundSpinsAmount + " spin in" : roundSpinsAmount + " spins in";
        roundSpins = new Text(roundSpinsText, 150, 135, 1051.603f, stage);

        firstTimeRow = new Text("", 150, 135, 1251.945f, stage);
        secondTimeRow = new Text("", 150, 135, 1452.287f, stage);
        thirdTimeRow = new Text("", 150, 135, 1652.629f, stage);

        ok = new Text("tap to continue", 150, 135, 2066.738f, stage);
    }

    private void timeConversion() {
        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        long totalSeconds = Game.dat.getTentativeTime();
        long seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        long totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        long minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        long hours = totalMinutes / MINUTES_IN_AN_HOUR;

        if (hours == 0 && minutes == 0) {
            String secondsText = (seconds == 1) ? seconds + " second" : seconds + " seconds";
            firstTimeRow.setText(secondsText);
        } else if (hours == 0) {
            String minutesText = (minutes == 1) ? minutes + " minute and" : minutes + " minutes and";
            firstTimeRow.setText(minutesText);
            String secondsText = (seconds == 1) ? seconds + " second" : seconds + " seconds";
            secondTimeRow.setText(secondsText);
        } else {
            String hoursText = (hours == 1) ? hours + " hour" : hours + " hours";
            firstTimeRow.setText(hoursText);
            String minutesText = (minutes == 1) ? minutes + " minute and" : minutes + " minutes and";
            secondTimeRow.setText(minutesText);
            String secondsText = (seconds == 1) ? seconds + " second" : seconds + " seconds";
            thirdTimeRow.setText(secondsText);
        }
    }

    private void initializeActors() {
        tex = Game.ast.get("img/white_panel.png");
        blankWhiteActor = new Image(tex);
        blankWhiteActor.setSize(Game.GAME_WIDTH, Game.GAME_HEIGHT);

        backStage.addActor(blankWhiteActor);

        tex = Game.ast.get("img/blank_panel.png");
        blankOverlayActor = new Image(tex);
        blankOverlayActor.setSize(Game.GAME_WIDTH, Game.GAME_HEIGHT);
        blankOverlayActor.getColor().a = 0;

        stage.addActor(blankOverlayActor);
    }

    private void initializeAudio() {
        bambooHit = Game.ast.get("audio/bamboo_hit.ogg");
    }

    @Override
    public void update(float dt) {
        backStage.act(dt);
        stage.act(dt);

        if (toMain) {
            if (!actionAdded) {
                blankOverlayActor.addAction(Actions.fadeIn(0.2f, Interpolation.pow2Out));
                actionAdded = true;
            } else if (!adProcessing && blankOverlayActor.getColor().a > 0.9999) {
                bambooHit.stop();
                Game.adHandler.displayInterstitial();
                adProcessing = true;
            } else if (adProcessing && ((Game.adHandler.isLoaded() && Game.adHandler.isClosed()) | !Game.adHandler.isLoaded())) {
                Game.adHandler.resetFlags();
                Game.adHandler.loadInterstitial();
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        toMain = true;
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public void setInput(String input) {
    }

    @Override
    public void dispose() {
        batch.dispose();
        backStage.dispose();
        stage.dispose();
        spins.dispose();
        spinsAmount.dispose();
        roundSpins.dispose();
        firstTimeRow.dispose();
        secondTimeRow.dispose();
        thirdTimeRow.dispose();
        ok.dispose();
    }
}