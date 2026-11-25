package com.miningfactory.player;

public class Pickaxe {
    private static final String[] NAMES = {"Basic Pickaxe", "Improved Pickaxe", "Professional Pickaxe"};
    private static final float[] SPEED_MULTIPLIERS = {1.0f, 1.6f, 2.4f};
    private int level = 0;

    public boolean canUpgrade() {
        return level < NAMES.length - 1;
    }

    public void upgrade() {
        if (canUpgrade()) {
            level++;
        }
    }

    public int getLevel() { return level; }
    public String getName() { return NAMES[level]; }
    public float getSpeedMultiplier() { return SPEED_MULTIPLIERS[level]; }
}