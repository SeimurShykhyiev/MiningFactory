package com.miningfactory.player;

public class PlayerUpgrade {
    private static final float[] WORK_SPEED_MULTIPLIERS = {1.0f, 1.3f, 1.65f};
    private int workSpeedLevel = 0;

    public boolean canUpgradeWorkSpeed() {
        return workSpeedLevel < WORK_SPEED_MULTIPLIERS.length - 1;
    }

    public void upgradeWorkSpeed() {
        if (canUpgradeWorkSpeed()) {
            workSpeedLevel++;
        }
    }

    public int getWorkSpeedLevel() { return workSpeedLevel; }
    public float getWorkSpeedMultiplier() { return WORK_SPEED_MULTIPLIERS[workSpeedLevel]; }
}