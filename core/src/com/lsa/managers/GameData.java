package com.lsa.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameData {
    private Preferences prefs;

    private long totalSpins;

    private long tentativeRoundSpins;
    private long maxRoundSpins;

    private long tentativeTime;
    private long maxTime;

    public GameData() {
        init();
    }

    private void init() {
        prefs = Gdx.app.getPreferences("My Preferences");

        if (prefs.contains("spins")) {
            totalSpins = prefs.getLong("spins");
        } else {
            prefs.putLong("spins", 0);
            prefs.flush();
            totalSpins = prefs.getLong("spins");
        }

        if (prefs.contains("maxRoundSpins")) {
            maxRoundSpins = prefs.getLong("maxRoundSpins");
        } else {
            prefs.putLong("maxRoundSpins", 0);
            prefs.flush();
            maxRoundSpins = prefs.getLong("maxRoundSpins");
        }

        if (prefs.contains("maxTime")) {
            maxTime = prefs.getLong("maxTime");
        } else {
            prefs.putLong("maxTime", 0);
            prefs.flush();
            maxTime = prefs.getLong("maxTime");
        }
    }

    public long getTotalSpins() {
        return totalSpins;
    }

    public void replaceTotalSpins(long spins) {
        totalSpins = spins;
        prefs.putLong("spins", totalSpins);
        prefs.flush();
    }

    public long getTentativeRoundSpins() {
        return tentativeRoundSpins;
    }

    public void setTentativeRoundSpins(long spins) {
        tentativeRoundSpins = spins;
    }

    public boolean greaterRoundSpins(long spins) {
        return spins > maxRoundSpins;
    }

    public void replaceMaxRoundSpins(long spins) {
        maxRoundSpins = spins;
        prefs.putLong("maxRoundSpins", maxRoundSpins);
        prefs.flush();
    }

    public long getTentativeTime() {
        return tentativeTime;
    }

    public void setTentativeTime(long time) {
        tentativeTime = time;
    }

    public boolean greaterTime(long time) {
        return time > maxTime;
    }

    public void replaceMaxTime(long time) {
        maxTime = time;
        prefs.putLong("maxTime", maxTime);
        prefs.flush();
    }
}