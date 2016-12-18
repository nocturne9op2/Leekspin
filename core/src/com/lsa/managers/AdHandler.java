package com.lsa.managers;

public interface AdHandler {
    void displayInterstitial();

    void loadInterstitial();

    boolean isLoaded();

    boolean isClosed();

    void resetFlags();
}
