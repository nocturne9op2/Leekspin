package com.lsa.managers;

public interface PlayServices {
    void signIn();

    void submitScore(int score);

    void showScore();

    boolean isSignedIn();

    boolean isConnecting();
}
