package com.miningfactory.player;

import com.miningfactory.resources.Inventory;

public class Backpack {
    private static final int[] CAPACITIES = {20, 40, 80};
    private int level = 0;
    private final Inventory inventory;

    public Backpack() {
        this.inventory = new Inventory(CAPACITIES[0]);
    }

    public boolean canUpgrade() {
        return level < CAPACITIES.length - 1;
    }

    public void upgrade() {
        if (canUpgrade()) {
            level++;
            inventory.setCapacity(CAPACITIES[level]);
        }
    }

    public int getLevel() { return level; }
    public int getCapacity() { return inventory.getCapacity(); }
    public Inventory getInventory() { return inventory; }
}