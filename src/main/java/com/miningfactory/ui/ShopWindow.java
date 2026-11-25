package com.miningfactory.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.miningfactory.core.GameWorld;
import com.miningfactory.economy.MachineShop;
import com.miningfactory.player.Player;

public class ShopWindow {
    public float render(SpriteBatch batch, BitmapFont font, GameWorld world, float startX, float startY) {
        float y = startY;
        font.setColor(Color.CYAN);
        font.draw(batch, "--- EQUIPMENT SHOP (Keys 1-9) ---", startX, y);
        y -= 20;

        MachineShop.CatalogItem[] items = MachineShop.CatalogItem.values();
        for (int i = 0; i < items.length; i++) {
            MachineShop.CatalogItem item = items[i];
            if (item == world.getSelectedCatalogItem()) {
                font.setColor(Color.YELLOW);
            } else {
                font.setColor(Color.WHITE);
            }
            font.draw(batch, "[" + (i + 1) + "] " + item.getTitle() + " - $" + item.getCost(), startX, y);
            y -= 18;
        }

        y -= 8;
        font.setColor(Color.CYAN);
        font.draw(batch, "--- PLAYER UPGRADES ---", startX, y);
        y -= 20;

        Player p = world.getPlayer();
        font.setColor(Color.WHITE);

        String bpCost = p.getBackpack().canUpgrade()
                ? "$" + world.getUpgradeShop().getBackpackUpgradeCost(p) : "MAX (80)";
        font.draw(batch, "[Z] Backpack (" + p.getBackpack().getCapacity() + "): " + bpCost, startX, y);
        y -= 18;

        String pkCost = p.getPickaxe().canUpgrade()
                ? "$" + world.getUpgradeShop().getPickaxeUpgradeCost(p) : "MAX";
        font.draw(batch, "[X] Pickaxe Lvl " + (p.getPickaxe().getLevel() + 1) + ": " + pkCost, startX, y);
        y -= 18;

        String spCost = p.getUpgrades().canUpgradeWorkSpeed()
                ? "$" + world.getUpgradeShop().getWorkSpeedUpgradeCost(p) : "MAX";
        font.draw(batch, "[C] Work Speed Lvl " + (p.getUpgrades().getWorkSpeedLevel() + 1) + ": " + spCost, startX, y);
        y -= 24;

        return y;
    }
}