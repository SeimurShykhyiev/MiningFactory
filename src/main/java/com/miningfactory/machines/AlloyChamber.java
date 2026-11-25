package com.miningfactory.machines;

import com.badlogic.gdx.graphics.Color;
import com.miningfactory.conveyor.Direction;
import com.miningfactory.resources.ResourceType;

public class AlloyChamber extends ProcessingMachine {
    private ResourceType secondInputSlot = null;

    public AlloyChamber(int gridX, int gridY, Direction direction) {
        super("Alloy Chamber", 450, 4.0f,
                ResourceType.HARDENED_METAL, ResourceType.IMPROVED_ALLOY,
                gridX, gridY, direction, new Color(0.65f, 0.25f, 0.75f, 1f), "alloy_chamber.png");
    }

    private boolean isMetalComponent(ResourceType type) {
        return type == ResourceType.IRON_INGOT || type == ResourceType.HARDENED_METAL;
    }

    @Override
    public boolean canAccept(ResourceType type) {
        return isMetalComponent(type) && (inputSlot == null || secondInputSlot == null);
    }

    @Override
    public boolean accept(ResourceType type) {
        if (!isMetalComponent(type)) return false;
        if (inputSlot == null) {
            inputSlot = type;
            return true;
        } else if (secondInputSlot == null) {
            secondInputSlot = type;
            return true;
        }
        return false;
    }

    @Override
    protected boolean hasRequiredInputs() {
        return inputSlot != null && secondInputSlot != null;
    }

    @Override
    protected void produceOutput() {
        inputSlot = null;
        secondInputSlot = null;
        outputBuffer = ResourceType.IMPROVED_ALLOY;
    }
}