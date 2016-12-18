package com.lsa.game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.lsa.managers.AdHandler;
import com.lsa.managers.PlayServices;

import java.io.File;

public class AndroidLauncher extends AndroidApplication implements PlayServices, AdHandler {
    private final static int requestCode = 1;
    private final static String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-6596022849334796/5168407468";

    private GameHelper gameHelper;

    private InterstitialAd interstitialAd;
    private boolean isLoaded = false;
    private boolean isClosed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useGyroscope = false;

        //Game Helper
        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        gameHelper.enableDebugLog(false);
        GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
            @Override
            public void onSignInFailed() {
            }

            @Override
            public void onSignInSucceeded() {
            }
        };
        gameHelper.setup(gameHelperListener);

        //Interstitial Ad
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(AD_UNIT_ID_INTERSTITIAL);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                isLoaded = true;
            }

            @Override
            public void onAdClosed() {
                isClosed = true;
            }
        });
        interstitialAd.loadAd(new AdRequest.Builder().build());

        initialize(new Game(this, this), config);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameHelper.onStart(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gameHelper.onActivityResult(requestCode, resultCode, data);

        if (Game.gsm != null) {
            Game.gsm.getState().setInput("normal");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameHelper.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            trimCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void trimCache() {
        try {
            File dir = getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    //PlayServices Methods
    @Override
    public void signIn() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameHelper.beginUserInitiatedSignIn();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void submitScore(int score) {
        if (isSignedIn()) {
            Games.Leaderboards.submitScore(gameHelper.getApiClient(),
                    getString(R.string.leaderboard_top_spins), score);
        }
    }

    @Override
    public void showScore() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isSignedIn()) {
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
                            getString(R.string.leaderboard_top_spins)), requestCode);
                } else {
                    signIn();
                }
            }
        }, 250);
    }

    @Override
    public boolean isSignedIn() {
        return gameHelper.isSignedIn();
    }

    @Override
    public boolean isConnecting() {
        return gameHelper.isConnecting();
    }

    //AdHandler Methods
    @Override
    public void displayInterstitial() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadInterstitial() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void resetFlags() {
        isLoaded = false;
        isClosed = false;
    }
}