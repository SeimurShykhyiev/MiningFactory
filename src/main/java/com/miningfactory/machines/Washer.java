package com.miningfactory.machines;

import com.badlogic.gdx.graphics.Color;
import com.miningfactory.conveyor.Direction;
import com.miningfactory.resources.ResourceType;

public class Washer extends ProcessingMachine {
    public Washer(int gridX, int gridY, Direction direction) {
        super("Ore Washer", 40, 2.0f,
                ResourceType.IRON_ORE, ResourceType.CLEAN_ORE,
                gridX, gridY, direction, new Color(0.20f, 0.55f, 0.85f, 1f), "washer.png");
    }
}