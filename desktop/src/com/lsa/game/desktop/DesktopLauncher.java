package com.lsa.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.title = "Leekspin";
        config.useHDPI = true;
        config.overrideDensity = 72;
        config.width = 1440 / 3;
        config.height = 2560 / 3;
        config.backgroundFPS = 60;
        config.foregroundFPS = 60;
        config.resizable = false;

        //new LwjglApplication(new Game(null, null), config);
    }
}
