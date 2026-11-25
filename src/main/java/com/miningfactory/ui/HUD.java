package com.miningfactory.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.miningfactory.core.GameWorld;
import com.miningfactory.world.Building;
import com.miningfactory.world.Map;

public class HUD {
    public void render(SpriteBatch batch, BitmapFont font, GameWorld world, Building hoveredBuilding) {
        // Подписи 5 функциональных зон на карте
        font.setColor(Color.ORANGE);
        font.draw(batch, "1. MINE ZONE (Hold SPACE/E)", 1 * Map.TILE_SIZE + 6, 15 * Map.TILE_SIZE - 6);
        font.draw(batch, "Auto-Out ->", 3 * Map.TILE_SIZE + 8, 13 * Map.TILE_SIZE + 26);

        font.setColor(Color.CYAN);
        font.draw(batch, "4. STORAGE ZONE", 1 * Map.TILE_SIZE + 6, 10 * Map.TILE_SIZE - 6);

        font.setColor(Color.GREEN);
        font.draw(batch, "5. SELL ZONE (Walk in)", 1 * Map.TILE_SIZE + 6, 5 * Map.TILE_SIZE - 6);

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "2 & 3. PRODUCTION & CONVEYORS (LMB: Build/Rotate | RMB: Remove | R: Dir | F: Load/Take)",
                5 * Map.TILE_SIZE + 8, 16 * Map.TILE_SIZE - 10);

        // Верхний блок показателей на боковой панели
        float x = 940;
        float y = 702;

        font.setColor(Color.GOLD);
        font.draw(batch, "=== MINING FACTORY ===", x, y);
        y -= 24;

        font.setColor(Color.GREEN);
        font.draw(batch, "Money: $" + world.getMoneyManager().getBalance(), x, y);
        y -= 20;

        font.setColor(Color.WHITE);
        font.draw(batch, "Backpack: " + world.getPlayer().getBackpack().getInventory().getTotalCount()
                + " / " + world.getPlayer().getBackpack().getCapacity(), x, y);
        y -= 20;
        font.draw(batch, "Tool: " + world.getPlayer().getPickaxe().getName(), x, y);
        y -= 20;
        font.draw(batch, "Build Dir (R): " + world.getBuildDirection().getLabel(), x, y);

        // Строка инспектора и статуса внизу экрана
        if (hoveredBuilding != null) {
            font.setColor(Color.YELLOW);
            String resName = hoveredBuilding.getDisplayedResource() != null
                    ? hoveredBuilding.getDisplayedResource().getDisplayName() : "Empty";
            font.draw(batch, "Inspect: " + hoveredBuilding.getName()
                    + " | Resource: " + resName
                    + " | Out Dir: " + hoveredBuilding.getDirection().getLabel(), 15, 42);
        }

        font.setColor(Color.WHITE);
        font.draw(batch, "Status: " + world.getStatusMessage(), 15, 20);
    }
}