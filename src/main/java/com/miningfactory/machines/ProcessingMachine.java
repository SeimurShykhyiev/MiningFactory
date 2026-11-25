package com.miningfactory.machines;

import com.badlogic.gdx.graphics.Color;
import com.miningfactory.conveyor.Direction;
import com.miningfactory.economy.MoneyManager;
import com.miningfactory.resources.ResourceType;
import com.miningfactory.world.Map;

public abstract class ProcessingMachine extends Machine {
    protected final ResourceType expectedInput;
    protected final ResourceType defaultOutput;
    protected ResourceType inputSlot = null;
    protected ResourceType outputBuffer = null;

    public ProcessingMachine(String name, int cost, float processTime,
                             ResourceType expectedInput, ResourceType defaultOutput,
                             int gridX, int gridY, Direction direction, Color color, String texturePath) {
        super(name, cost, processTime, gridX, gridY, direction, color, texturePath);
        this.expectedInput = expectedInput;
        this.defaultOutput = defaultOutput;
    }

    @Override
    public void update(float dt, Map map, MoneyManager moneyManager) {
        // 1. Пытаемся вытолкнуть готовый продукт на соседний тайл по направлению выхода
        if (outputBuffer != null) {
            int targetX = gridX + direction.getDx();
            int targetY = gridY + direction.getDy();
            if (map.pushResourceTo(targetX, targetY, outputBuffer, moneyManager)) {
                outputBuffer = null;
            }
        }

        // 2. Если выход свободен и есть сырье на входе — перерабатываем
        if (outputBuffer == null && hasRequiredInputs()) {
            processing = true;
            timer += dt;
            if (timer >= processTime) {
                timer = 0f;
                processing = false;
                produceOutput();
            }
        } else {
            processing = false;
            timer = 0f;
        }
    }

    protected boolean hasRequiredInputs() {
        return inputSlot != null && inputSlot == expectedInput;
    }

    protected void produceOutput() {
        inputSlot = null;
        outputBuffer = defaultOutput;
    }

    @Override
    public boolean canAccept(ResourceType type) {
        return inputSlot == null && type == expectedInput;
    }

    @Override
    public boolean accept(ResourceType type) {
        if (canAccept(type)) {
            inputSlot = type;
            return true;
        }
        return false;
    }

    public ResourceType takeOutputManually() {
        if (outputBuffer != null) {
            ResourceType res = outputBuffer;
            outputBuffer = null;
            return res;
        }
        return null;
    }

    @Override
    public ResourceType getDisplayedResource() {
        return outputBuffer != null ? outputBuffer : inputSlot;
    }

    public ResourceType getInputSlot() { return inputSlot; }
    public ResourceType getOutputBuffer() { return outputBuffer; }
}