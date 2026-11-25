package com.miningfactory.machines;

import com.badlogic.gdx.graphics.Color;
import com.miningfactory.conveyor.Direction;
import com.miningfactory.resources.ResourceType;

public class Crusher extends ProcessingMachine {
    public Crusher(int gridX, int gridY, Direction direction) {
        super("Crusher", 110, 2.0f,
                ResourceType.STONE, ResourceType.STONE_DUST,
                gridX, gridY, direction, new Color(0.50f, 0.45f, 0.40f, 1f), "crusher.png");
    }
}