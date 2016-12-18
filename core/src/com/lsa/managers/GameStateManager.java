package com.lsa.managers;

import com.lsa.gamestates.GameOverState;
import com.lsa.gamestates.GameState;
import com.lsa.gamestates.LoadingState;
import com.lsa.gamestates.PlayState;

public class GameStateManager {
    public final static int LOADING = 0;
    public final static int PLAY = 1;
    public final static int GAMEOVER = 2;

    private GameState gameState;

    public GameStateManager() {
        setState(LOADING);
    }

    public GameState getState() {
        return gameState;
    }

    public void setState(int state) {
        dispose();

        switch (state) {
            case LOADING:
                gameState = new LoadingState(this);
                break;
            case PLAY:
                gameState = new PlayState(this);
                break;
            case GAMEOVER:
                gameState = new GameOverState(this);
                break;
        }
    }

    public void update(float dt) {
        gameState.update(dt);
    }

    public void draw() {
        gameState.draw();
    }

    public void dispose() {
        if (gameState != null) {
            gameState.dispose();
        }
    }
}