package com.miningfactory.player;

import com.badlogic.gdx.math.MathUtils;
import com.miningfactory.conveyor.Direction;
import com.miningfactory.economy.MoneyManager;
import com.miningfactory.mining.Mine;
import com.miningfactory.resources.ResourceStack;
import com.miningfactory.world.Map;

public class Player {
    private float x;
    private float y;
    private final float moveSpeed = 190f;
    private float miningProgress = 0f;
    private Direction facingDirection = Direction.DOWN;

    private final Backpack backpack;
    private final Pickaxe pickaxe;
    private final PlayerUpgrade upgrades;

    public Player(float startX, float startY) {
        this.x = startX;
        this.y = startY;
        this.backpack = new Backpack();
        this.pickaxe = new Pickaxe();
        this.upgrades = new PlayerUpgrade();
    }

    public void move(float dx, float dy, float dt, int maxWidth, int maxHeight) {
        if (dx > 0) facingDirection = Direction.RIGHT;
        else if (dx < 0) facingDirection = Direction.LEFT;
        else if (dy > 0) facingDirection = Direction.UP;
        else if (dy < 0) facingDirection = Direction.DOWN;

        if (dx != 0 && dy != 0) {
            dx *= 0.7071f;
            dy *= 0.7071f;
        }
        x = MathUtils.clamp(x + dx * moveSpeed * dt, 18, maxWidth - 18);
        y = MathUtils.clamp(y + dy * moveSpeed * dt, 18, maxHeight - 18);
    }

    public boolean mineManually(float dt, Mine mine) {
        if (backpack.getInventory().getTotalCount() >= backpack.getCapacity()) {
            miningProgress = 0f;
            return false;
        }
        float totalSpeed = pickaxe.getSpeedMultiplier() * upgrades.getWorkSpeedMultiplier();
        miningProgress += dt * totalSpeed;
        if (miningProgress >= mine.getDeposit().getBaseMineTime()) {
            miningProgress = 0f;
            return backpack.getInventory().addResource(mine.getDeposit().extractOre(), 1);
        }
        return false;
    }

    public void resetMiningProgress() {
        this.miningProgress = 0f;
    }

    public int sellAllResources(MoneyManager moneyManager) {
        int earned = 0;
        for (ResourceStack stack : backpack.getInventory().getStacks().values()) {
            int count = stack.getAmount();
            int price = stack.getType().getBasePrice();
            if (count > 0 && price > 0) {
                earned += count * price;
                stack.remove(count);
            }
        }
        if (earned > 0) {
            moneyManager.addMoney(earned);
        }
        return earned;
    }

    public int getGridX() { return (int) (x / Map.TILE_SIZE); }
    public int getGridY() { return (int) (y / Map.TILE_SIZE); }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getMiningProgress() { return miningProgress; }
    public Direction getFacingDirection() { return facingDirection; }
    public Backpack getBackpack() { return backpack; }
    public Pickaxe getPickaxe() { return pickaxe; }
    public PlayerUpgrade getUpgrades() { return upgrades; }
}