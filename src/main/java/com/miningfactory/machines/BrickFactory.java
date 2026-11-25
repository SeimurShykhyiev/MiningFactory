package com.miningfactory.machines;

import com.badlogic.gdx.graphics.Color;
import com.miningfactory.conveyor.Direction;
import com.miningfactory.resources.ResourceType;

public class BrickFactory extends ProcessingMachine {
    public BrickFactory(int gridX, int gridY, Direction direction) {
        super("Brick Factory", 130, 2.2f,
                ResourceType.STONE_DUST, ResourceType.BRICK,
                gridX, gridY, direction, new Color(0.70f, 0.30f, 0.25f, 1f), "brick_factory.png");
    }

    @Override
    public boolean canAccept(ResourceType type) {
        // Принимает каменную пыль для кирпича, а также пропускает транзитом алмаз/изумруд после сита
        return inputSlot == null && (type == ResourceType.STONE_DUST
                || type == ResourceType.DIAMOND
                || type == ResourceType.EMERALD);
    }

    @Override
    protected boolean hasRequiredInputs() {
        return inputSlot != null;
    }

    @Override
    protected void produceOutput() {
        if (inputSlot == ResourceType.STONE_DUST) {
            outputBuffer = ResourceType.BRICK;
        } else {
            outputBuffer = inputSlot; // Драгоценный камень проходит дальше на продажу без потерь
        }
        inputSlot = null;
    }
}