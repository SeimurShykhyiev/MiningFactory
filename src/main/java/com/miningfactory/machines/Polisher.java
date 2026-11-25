package com.miningfactory.machines;

import com.badlogic.gdx.graphics.Color;
import com.miningfactory.conveyor.Direction;
import com.miningfactory.resources.ResourceType;

public class Polisher extends ProcessingMachine {
    public Polisher(int gridX, int gridY, Direction direction) {
        super("Polisher", 80, 2.2f,
                ResourceType.CLEAN_ORE, ResourceType.POLISHED_ORE,
                gridX, gridY, direction, new Color(0.30f, 0.70f, 0.75f, 1f), "polisher.png");
    }
}