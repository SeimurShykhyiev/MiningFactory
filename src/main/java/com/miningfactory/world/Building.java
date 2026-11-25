package com.miningfactory.world;

import com.badlogic.gdx.graphics.Color;
import com.miningfactory.conveyor.Direction;
import com.miningfactory.economy.MoneyManager;
import com.miningfactory.resources.ResourceType;

public abstract class Building {
    protected final String name;
    protected final int cost;
    protected final int gridX;
    protected final int gridY;
    protected Direction direction;
    protected final Color color;

    public Building(String name, int cost, int gridX, int gridY, Direction direction, Color color) {
        this.name = name;
        this.cost = cost;
        this.gridX = gridX;
        this.gridY = gridY;
        this.direction = direction;
        this.color = color;
    }

    public abstract void update(float dt, Map map, MoneyManager moneyManager);
    public abstract boolean canAccept(ResourceType type);
    public abstract boolean accept(ResourceType type);
    public abstract ResourceType getDisplayedResource();
    public abstract float getProgressRatio();

    public void rotate() {
        this.direction = this.direction.nextClockwise();
    }

    public String getName() { return name; }
    public int getCost() { return cost; }
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
    public Direction getDirection() { return direction; }
    public Color getColor() { return color; }
}