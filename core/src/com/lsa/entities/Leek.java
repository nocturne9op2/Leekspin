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
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.lsa.game.Game;
import com.lsa.managers.BodyEditorLoader;

public class Leek {
    private World foregroundWorld;

    private Texture leekTexture;
    private Sprite leekSprite;
    private Body leekBody;

    private String wasInSector = "firstPosition";

    public Leek(World foregroundWorld) {
        this.foregroundWorld = foregroundWorld;

        leekTexture = Game.ast.get("img/leek_texture.png");
        leekTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        leekSprite = new Sprite(leekTexture);
        leekSprite.setSize(leekSprite.getWidth() * Game.worldWidthRatio, leekSprite.getHeight() * Game.worldWidthRatio);
        leekSprite.setOrigin(0, 0);

        leekBody = createLeekBody();
    }

    private Body createLeekBody() {
        Body leekBody;

        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("data/leek_body.json"));

        BodyDef bd = new BodyDef();
        bd.position.set(0, 0);
        bd.type = BodyType.DynamicBody;

        FixtureDef fd = new FixtureDef();
        fd.density = 0.1f;
        fd.friction = 0;
        fd.restitution = 0;

        leekBody = foregroundWorld.createBody(bd);
        leekBody.setTransform(-333.756f * Game.worldWidthRatio, (Game.screenHeight - 1931.39f) * Game.worldHeightRatio, -34 * MathUtils.degreesToRadians);

        loader.attachFixture(leekBody, "leek", fd, leekSprite.getWidth());

        return leekBody;
    }

    public void drawLeek(SpriteBatch sb) {
        leekSprite.setPosition(leekBody.getPosition().x, leekBody.getPosition().y);
        leekSprite.setRotation(leekBody.getAngle() * MathUtils.radiansToDegrees);
        leekSprite.draw(sb);
    }

    public boolean hasRotated() {
        float angle = (leekBody.getAngle() * MathUtils.radiansToDegrees + 90) % 360;
        if (angle < 0) {
            angle += 360;
        }

        //fail case
        if (angle < 180 && angle > 90 && wasInSector.equals("firstPosition")) {
            wasInSector = "counterClockwiseErrorCase";
        }

        //mid case
        if (angle < 270 && angle > 180 && (wasInSector.equals("firstPosition") | wasInSector.equals("reversalErrorCase"))) {
            wasInSector = "endPhase";
        }

        if (angle > 270 && wasInSector.equals("endPhase")) {
            wasInSector = "reversalErrorCase";
        }

        //initial and final case
        if (angle < 90 && (wasInSector.equals("endPhase") | wasInSector.equals("counterClockwiseErrorCase") | wasInSector.equals("reversalErrorCase"))) {
            if (wasInSector.equals("endPhase")) {
                wasInSector = "firstPosition";
                return true;
            } else {
                wasInSector = "firstPosition";
            }
        }

        return false;
    }

    public boolean isPresent() {
        if (leekBody.getPosition().y < -Game.worldHeight * 1) {
            foregroundWorld.setGravity(new Vector2(0, 0));
            leekBody.setAngularVelocity(0);
            leekBody.setLinearVelocity(0, 0);
            return false;
        }
        return true;
    }
}