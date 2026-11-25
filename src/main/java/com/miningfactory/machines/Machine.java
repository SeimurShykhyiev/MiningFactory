package com.miningfactory.machines;

import com.badlogic.gdx.graphics.Color;
import com.miningfactory.conveyor.Direction;
import com.miningfactory.world.Building;

public abstract class Machine extends Building {
    protected final float processTime;
    protected float timer = 0f;
    protected boolean processing = false;
    protected final String texturePath;

    public Machine(String name, int cost, float processTime, int gridX, int gridY,
                   Direction direction, Color color, String texturePath) {
        super(name, cost, gridX, gridY, direction, color);
        this.processTime = processTime;
        this.texturePath = texturePath;
    }

    @Override
    public float getProgressRatio() {
        return processTime > 0 ? Math.min(1.0f, timer / processTime) : 0f;
    }

    public boolean isProcessing() { return processing; }
    public String getTexturePath() { return texturePath; }
}