package com.miningfactory.machines;

import com.badlogic.gdx.graphics.Color;
import com.miningfactory.conveyor.Direction;
import com.miningfactory.economy.MoneyManager;
import com.miningfactory.resources.ResourceType;
import com.miningfactory.world.Map;

public class Furnace extends ProcessingMachine {
    private ResourceType sideOutputBuffer = null;

    public Furnace(int gridX, int gridY, Direction direction) {
        super("Smelting Furnace", 160, 3.0f,
                ResourceType.POLISHED_ORE, ResourceType.IRON_INGOT,
                gridX, gridY, direction, new Color(0.85f, 0.38f, 0.15f, 1f), "furnace.png");
    }

    public Direction getSideDirection() {
        return direction.nextClockwise();
    }

    @Override
    public void update(float dt, Map map, MoneyManager moneyManager) {
        // Выталкиваем побочный продукт (Камень) через боковой выход
        if (sideOutputBuffer != null) {
            Direction sideDir = getSideDirection();
            int sideX = gridX + sideDir.getDx();
            int sideY = gridY + sideDir.getDy();
            if (map.pushResourceTo(sideX, sideY, sideOutputBuffer, moneyManager)) {
                sideOutputBuffer = null;
            }
        }

        // Выталкиваем основной продукт (Железный слиток)
        if (outputBuffer != null) {
            int mainX = gridX + direction.getDx();
            int mainY = gridY + direction.getDy();
            if (map.pushResourceTo(mainX, mainY, outputBuffer, moneyManager)) {
                outputBuffer = null;
            }
        }

        // Работаем, если оба выходных буфера свободны
        if (outputBuffer == null && sideOutputBuffer == null && hasRequiredInputs()) {
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

    @Override
    protected void produceOutput() {
        inputSlot = null;
        outputBuffer = ResourceType.IRON_INGOT;
        sideOutputBuffer = ResourceType.STONE;
    }

    public ResourceType getSideOutputBuffer() { return sideOutputBuffer; }
}