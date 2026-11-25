package com.miningfactory.conveyor;

import com.badlogic.gdx.graphics.Color;
import com.miningfactory.economy.MoneyManager;
import com.miningfactory.resources.ResourceType;
import com.miningfactory.world.Building;
import com.miningfactory.world.Map;

public class Conveyor extends Building {
    private ConveyorItem currentItem;
    private final float speed = 1.8f; // тайлов в секунду

    public Conveyor(int gridX, int gridY, Direction direction) {
        super("Conveyor", 10, gridX, gridY, direction, new Color(0.28f, 0.28f, 0.33f, 1f));
    }

    @Override
    public void update(float dt, Map map, MoneyManager moneyManager) {
        if (currentItem != null) {
            currentItem.advance(dt * speed);
            if (currentItem.isReadyToTransfer()) {
                int targetX = gridX + direction.getDx();
                int targetY = gridY + direction.getDy();
                if (map.pushResourceTo(targetX, targetY, currentItem.getResourceType(), moneyManager)) {
                    currentItem = null;
                }
            }
        }
    }

    @Override
    public boolean canAccept(ResourceType type) {
        return currentItem == null && type != null;
    }

    @Override
    public boolean accept(ResourceType type) {
        if (canAccept(type)) {
            this.currentItem = new ConveyorItem(type);
            return true;
        }
        return false;
    }

    @Override
    public ResourceType getDisplayedResource() {
        return currentItem != null ? currentItem.getResourceType() : null;
    }

    @Override
    public float getProgressRatio() {
        return currentItem != null ? currentItem.getProgress() : 0f;
    }
}