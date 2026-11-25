package com.miningfactory.core;

public class Game extends com.badlogic.gdx.Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
        super.dispose();
    }
}