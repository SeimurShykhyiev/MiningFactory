package com.miningfactory.machines;

import com.badlogic.gdx.graphics.Color;
import com.miningfactory.conveyor.Direction;
import com.miningfactory.resources.ResourceType;

public class HardeningChamber extends ProcessingMachine {
    public HardeningChamber(int gridX, int gridY, Direction direction) {
        super("Hardening Chamber", 280, 3.2f,
                ResourceType.IRON_INGOT, ResourceType.HARDENED_METAL,
                gridX, gridY, direction, new Color(0.30f, 0.40f, 0.75f, 1f), "hardener.png");
    }
}