package com.lsa.gamestates;

import com.lsa.managers.GameStateManager;

public abstract class GameState {
    protected GameStateManager gsm;

    protected GameState(GameStateManager gsm) {
        this.gsm = gsm;
        init();
    }

    public abstract void init();

    public abstract void update(float dt);

    public abstract void draw();

    public abstract void setInput(String input);

    public abstract void dispose();
}
