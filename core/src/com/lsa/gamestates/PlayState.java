package com.lsa.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.lsa.entities.Leek;
import com.lsa.entities.SquigleSpawner;
import com.lsa.entities.Text;
import com.lsa.game.Game;
import com.lsa.managers.GameStateManager;

public class PlayState extends GameState implements InputProcessor {
    private final static float timeStep = 1 / 60f;
    private final static int velocityIterations = 8;
    private final static int positionIterations = 3;
    private float worldStepAccumulator = 0;

    private SpriteBatch batch;
    private Stage backStage;
    private Stage stage;

    //flags
    private boolean gameStart = false;
    private boolean gameOver = false;
    private boolean musicStarted = false;
    private boolean gravityNormalized = false;

    //game data
    private long totalSpins;
    private long roundSpins;
    private float time;

    //text components
    private Text spins;
    private Text spinsAmount;
    private Text highscores;

    //actor components
    private Texture tex;
    private Actor gradientActor;

    //audio components
    private Sound pop;
    private Music firstSection;
    private Music secondSection;

    //background components
    private World backgroundWorld;

    private SquigleSpawner squigleSpawner;

    //foreground components
    private World foregroundWorld;

    private Leek leek;

    private Body ground;

    private MouseJointDef jointDef;
    private MouseJoint joint;

    private Vector3 tmp = new Vector3();
    private Vector2 tmp2 = new Vector2();
    private QueryCallback queryCallback = new QueryCallback() {
        @Override
        public boolean reportFixture(Fixture fixture) {
            if (!fixture.testPoint(tmp.x, tmp.y)) {
                return true;
            }

            if (!gravityNormalized) {
                foregroundWorld.setGravity(new Vector2(0, -75));
                gameStart = true;
                gravityNormalized = true;
            }

            jointDef.bodyB = fixture.getBody();
            jointDef.target.set(tmp.x, tmp.y);
            joint = (MouseJoint) foregroundWorld.createJoint(jointDef);
            return false;
        }
    };

    //input components
    private InputMultiplexer multiplexer;

    public PlayState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void init() {
        batch = new SpriteBatch();
        backStage = new Stage(new ScreenViewport());
        stage = new Stage(new ScreenViewport());

        //game data update
        totalSpins = Game.dat.getTotalSpins();
        roundSpins = 0;
        time = 0;

        //text components
        initializeText();

        //actor components
        initializeActors();

        //audio components
        initializeAudio();

        //background components
        backgroundWorld = new World(new Vector2(0, 0), false);

        squigleSpawner = new SquigleSpawner(backgroundWorld);

        //foreground components
        foregroundWorld = new World(new Vector2(0, 0), true);

        leek = new Leek(foregroundWorld);

        ground = createGround();

        createMouseJoint();

        //extra
        Game.playServices.submitScore((int) Game.dat.getTotalSpins());

        //input components
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void initializeText() {
        spins = new Text("spins:", 300, 120, 267.068f, stage);
        spinsAmount = new Text(Long.toString(totalSpins), 250, 120, 607.502f, stage);
        highscores = new Text("highscores", 150, 120, 892.097f, stage);

        highscores.maximizeLabelSize();
        highscores.getLabel().addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (Gdx.input.getInputProcessor() != null) {
                    setInput("stage");

                    highscores.getLabel().addAction(Actions.moveTo(highscores.getRelXPos(), highscores.getRelYPos() + (30 * Game.screenHeightRatio)));

                    return true;
                }
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                setInput("cancel");

                highscores.getLabel().addAction(Actions.moveTo(highscores.getRelXPos(), highscores.getRelYPos()));

                pop.play();

                Game.playServices.showScore();
                Game.playServices.submitScore((int) Game.dat.getTotalSpins());
            }
        });
    }

    private void initializeActors() {
        tex = Game.ast.get("img/gradient_panel.png");
        tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        gradientActor = new Image(tex);
        gradientActor.setSize(Game.GAME_WIDTH, Game.GAME_HEIGHT);

        backStage.addActor(gradientActor);
    }

    private void initializeAudio() {
        pop = Game.ast.get("audio/pop.ogg");
        firstSection = Game.ast.get("audio/first_section.ogg");
        secondSection = Game.ast.get("audio/second_section.ogg");

        firstSection.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                secondSection.play();
                secondSection.setLooping(true);
            }
        });
    }

    private Body createGround() {
        Body ground;

        BodyDef def = new BodyDef();
        def.type = BodyType.StaticBody;

        EdgeShape groundShape = new EdgeShape();
        groundShape.set(0, -Game.worldHeight * 2, Game.worldWidth, -Game.worldHeight * 2);

        ground = foregroundWorld.createBody(def);
        ground.createFixture(groundShape, 0);

        groundShape.dispose();

        return ground;
    }

    private void createMouseJoint() {
        jointDef = new MouseJointDef();
        jointDef.bodyA = ground;
        jointDef.collideConnected = true;
        jointDef.frequencyHz = 50000;
        jointDef.dampingRatio = 0;
        jointDef.maxForce = 20000;
    }

    @Override
    public void update(float dt) {
        stepWorlds(dt);

        backStage.act(dt);
        stage.act(dt);

        if (gameStart) {
            //add game time
            time += dt;

            if (!musicStarted) {
                firstSection.play();
                highscores.getLabel().clearListeners();
                highscores.getLabel().addAction(Actions.fadeOut(0.2f, Interpolation.pow2Out));
                musicStarted = true;
            }

            //check leek rotation
            if (leek.hasRotated()) {
                roundSpins++;
                totalSpins++;
                spinsAmount.setText(Long.toString(totalSpins));
                spinsAmount.getLabel().addAction(new SequenceAction(Actions.moveTo(spinsAmount.getRelXPos(), spinsAmount.getRelYPos() + (30 * Game.screenHeightRatio)),
                        Actions.moveTo(spinsAmount.getRelXPos(), spinsAmount.getRelYPos())));

                Game.dat.replaceTotalSpins(totalSpins);

                pop.play();
            }

            //check leek presence
            if (!leek.isPresent()) {
                squigleSpawner.normalizeGravity();

                Game.dat.setTentativeRoundSpins(roundSpins);
                Game.dat.setTentativeTime(Math.round(time));

                gameStart = false;
                gameOver = true;
            }
        }

        if (gameOver) {
            squigleSpawner.despawnSquigles();
            if (squigleSpawner.hasNoMoreSquigles()) {
                firstSection.stop();
                secondSection.stop();
                gsm.setState(GameStateManager.GAMEOVER);
            }
        } else {
            squigleSpawner.spawnSquigles(dt);
        }
    }

    private void stepWorlds(float dt) {
        float frameTime = Math.min(dt, 0.25f);
        worldStepAccumulator += frameTime;
        while (worldStepAccumulator >= timeStep) {
            backgroundWorld.step(timeStep, velocityIterations, positionIterations);
            foregroundWorld.step(timeStep, velocityIterations, positionIterations);
            worldStepAccumulator -= timeStep;
        }
    }

    @Override
    public void draw() {
        //draw gradient
        backStage.draw();

        //draw squigles
        batch.setProjectionMatrix(Game.cam.combined);
        batch.begin();
        squigleSpawner.drawSquigles(batch);
        batch.end();

        //draw text
        stage.draw();

        //draw leek
        batch.setProjectionMatrix(Game.cam.combined);
        batch.begin();
        leek.drawLeek(batch);
        batch.end();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pointer == 0) {
            Game.cam.unproject(tmp.set(screenX, screenY, 0));
            foregroundWorld.QueryAABB(queryCallback, tmp.x, tmp.y, tmp.x, tmp.y);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer == 0) {
            if (joint == null) {
                return false;
            }

            foregroundWorld.destroyJoint(joint);
            joint = null;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (pointer == 0) {
            if (joint == null) {
                return false;
            }

            Game.cam.unproject(tmp.set(screenX, screenY, 0));
            joint.setTarget(tmp2.set(tmp.x, tmp.y));
        }
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
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void setInput(String input) {
        if (input.equals("normal")) {
            Gdx.input.setInputProcessor(multiplexer);
        } else if (input.equals("stage")) {
            Gdx.input.setInputProcessor(stage);
        } else if (input.equals("cancel")) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        backStage.dispose();
        stage.dispose();
        spins.dispose();
        spinsAmount.dispose();
        highscores.dispose();
        backgroundWorld.dispose();
        foregroundWorld.dispose();
    }
}