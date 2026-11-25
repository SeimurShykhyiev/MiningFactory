package com.miningfactory.machines;

import com.badlogic.gdx.graphics.Color;
import com.miningfactory.conveyor.Direction;
import com.miningfactory.economy.MoneyManager;
import com.miningfactory.resources.ResourceType;
import com.miningfactory.world.Map;

public class OreBox extends Machine {
    private int storedOreCount = 0;
    private final int maxCapacity = 100;

    public OreBox(int gridX, int gridY, Direction direction) {
        super("Ore Box (Hopper)", 20, 0.9f, gridX, gridY, direction,
                new Color(0.25f, 0.45f, 0.75f, 1f), "ore_box.png");
    }

    public int depositFromPlayer(int availableInBackpack) {
        int space = maxCapacity - storedOreCount;
        int toTransfer = Math.min(space, availableInBackpack);
        if (toTransfer > 0) {
            storedOreCount += toTransfer;
        }
        return toTransfer;
    }

    @Override
    public void update(float dt, Map map, MoneyManager moneyManager) {
        if (storedOreCount > 0) {
            processing = true;
            timer += dt;
            if (timer >= processTime) {
                int targetX = gridX + direction.getDx();
                int targetY = gridY + direction.getDy();
                if (map.pushResourceTo(targetX, targetY, ResourceType.IRON_ORE, moneyManager)) {
                    storedOreCount--;
                    timer = 0f;
                } else {
                    timer = processTime; // Ждём, пока освободится конвейер на выходе
                }
            }
        } else {
            processing = false;
            timer = 0f;
        }
    }

    @Override
    public boolean canAccept(ResourceType type) {
        return type == ResourceType.IRON_ORE && storedOreCount < maxCapacity;
    }

    @Override
    public boolean accept(ResourceType type) {
        if (canAccept(type)) {
            storedOreCount++;
            return true;
        }
        return false;
    }

    @Override
    public ResourceType getDisplayedResource() {
        return storedOreCount > 0 ? ResourceType.IRON_ORE : null;
    }

    public int getStoredOreCount() { return storedOreCount; }
    public int getMaxCapacity() { return maxCapacity; }
}