package com.lsa.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.lsa.game.Game;
import com.lsa.managers.BodyEditorLoader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class SquigleSpawner {
    private World backgroundWorld;

    private Texture squigleTexture;
    private HashMap<Sprite, Body> squigles;

    private float squigleSpawnTime = 1.5f;
    private float squigleSpawnTimer = 0;

    private int squigleLimit = 25;

    public SquigleSpawner(World backgroundWorld) {
        this.backgroundWorld = backgroundWorld;

        squigleTexture = Game.ast.get("img/squigle_texture.png");
        squigleTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        squigles = new HashMap<Sprite, Body>();
    }

    public void drawSquigles(SpriteBatch sb) {
        for (Entry<Sprite, Body> entry : squigles.entrySet()) {
            Sprite s = entry.getKey();
            Body b = entry.getValue();

            s.setPosition(b.getPosition().x, b.getPosition().y);
            s.setRotation(b.getAngle() * MathUtils.radiansToDegrees);
            s.draw(sb);
        }
    }

    public void spawnSquigles(float dt) {
        squigleSpawnTimer += dt;

        if (squigleSpawnTimer > squigleSpawnTime) {
            if (squigles.size() < squigleLimit) {
                BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("data/squigle_body.json"));

                Sprite s = new Sprite(squigleTexture);
                float scale = MathUtils.random(0.5f, 1);
                s.setSize(s.getWidth() * Game.worldWidthRatio * scale, s.getHeight() * Game.worldWidthRatio * scale);
                s.setOrigin(0, 0);

                BodyDef bd = new BodyDef();
                bd.position.set(0, 0);
                bd.type = BodyDef.BodyType.DynamicBody;

                FixtureDef fd = new FixtureDef();
                fd.density = 0.05f;
                fd.friction = 0;
                fd.restitution = 0;

                Body b = backgroundWorld.createBody(bd);
                b.setBullet(true);
                float xPos = MathUtils.random(0, Game.worldWidth);
                float yPos = MathUtils.random(-Game.worldHeight * 0.5f, -Game.worldHeight * 1);
                float rotation = MathUtils.random(-180, 180) * MathUtils.degreesToRadians;
                float upwardsForce = MathUtils.random(50, 100);
                float torque = MathUtils.random(-15, 15);
                b.setTransform(xPos - s.getWidth() / 2, yPos, rotation);
                b.applyForceToCenter(0, upwardsForce, true);
                b.applyTorque(torque, true);

                loader.attachFixture(b, "squigle", fd, s.getWidth());

                squigles.put(s, b);
            } else {
                for (Entry<Sprite, Body> entry : squigles.entrySet()) {
                    Sprite s = entry.getKey();
                    Body b = entry.getValue();

                    if (b.getPosition().y > Game.worldHeight * 1.5) {
                        b.setAngularVelocity(0);
                        b.setLinearVelocity(0, 0);

                        float xPos = MathUtils.random(0, Game.worldWidth);
                        float yPos = MathUtils.random(-Game.worldHeight * 0.5f, -Game.worldHeight * 1);
                        float rotation = MathUtils.random(-180, 180) * MathUtils.degreesToRadians;
                        float upwardsForce = MathUtils.random(50, 100);
                        float torque = MathUtils.random(-15, 15);
                        b.setTransform(xPos - s.getWidth() / 2, yPos, rotation);
                        b.applyForceToCenter(0, upwardsForce, true);
                        b.applyTorque(torque, true);

                        break;
                    }
                }
            }

            squigleSpawnTimer = 0;
        }
    }

    public void despawnSquigles() {
        for (Iterator<Entry<Sprite, Body>> it = squigles.entrySet().iterator(); it.hasNext(); ) {
            Entry<Sprite, Body> entry = it.next();
            if (entry.getValue().getPosition().y > Game.worldHeight * 1.5 | entry.getValue().getPosition().y < -Game.worldHeight * 1.5) {
                it.remove();
            }
        }
    }

    public void normalizeGravity() {
        backgroundWorld.setGravity(new Vector2(0, -200));
    }

    public boolean hasNoMoreSquigles() {
        return squigles.size() == 0;
    }
}