package com.lsa.entities;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.lsa.game.Game;

public class Text {
    private Stage stage;

    private String text;
    private int size;
    private float xPos, yPos;

    private BitmapFont font;
    private Label label;
    private float relXPos, relYPos;

    public Text(String text, int size, float xPos, float yPos, Stage stage) {
        this.stage = stage;

        this.text = text;
        this.size = size;
        this.xPos = xPos;
        this.yPos = yPos;

        initializeFont();
        initializeLabel();
    }

    private void initializeFont() {
        FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
        fontParameter.size = (int) (size * Game.screenWidthRatio);
        font = Game.gen.generateFont(fontParameter);
        font.setColor(5 / 255f, 5 / 255f, 5 / 255f, 1);
        font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    }

    private void initializeLabel() {
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, text);

        label = new Label(text, new Label.LabelStyle(font, font.getColor()));
        label.setHeight(layout.height);
        label.setPosition((int) (xPos * Game.screenWidthRatio), (int) ((Game.screenHeight - yPos) * Game.screenHeightRatio), Align.topLeft);

        relXPos = label.getX();
        relYPos = label.getY();

        stage.addActor(label);
    }

    public Label getLabel() {
        return label;
    }

    public float getRelXPos() {
        return relXPos;
    }

    public float getRelYPos() {
        return relYPos;
    }

    public void setText(String text) {
        label.setText(text);
    }

    public void maximizeLabelSize() {
        label.sizeBy(0, 100 * Game.screenHeightRatio);
        label.setPosition((int) (xPos * Game.screenWidthRatio),
                (int) (((Game.screenHeight - yPos) * Game.screenHeightRatio) + (100 * Game.screenHeightRatio * 0.5)), Align.topLeft);

        relXPos = label.getX();
        relYPos = label.getY();
    }

    public void dispose() {
        font.dispose();
    }
}