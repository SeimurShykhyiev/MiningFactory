package com.miningfactory.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.miningfactory.player.Player;
import com.miningfactory.resources.ResourceStack;
import com.miningfactory.resources.ResourceType;

public class InventoryWindow {
    public void render(SpriteBatch batch, BitmapFont font, Player player, float startX, float startY) {
        float y = startY;
        font.setColor(Color.CYAN);
        font.draw(batch, "--- BACKPACK INVENTORY ---", startX, y);
        y -= 20;

        font.setColor(Color.LIGHT_GRAY);
        boolean empty = true;
        for (ResourceType type : ResourceType.values()) {
            ResourceStack stack = player.getBackpack().getInventory().getStacks().get(type);
            if (stack != null && stack.getAmount() > 0) {
                empty = false;
                font.draw(batch, type.getDisplayName() + ": " + stack.getAmount()
                        + " ($" + type.getBasePrice() + " ea)", startX, y);
                y -= 18;
            }
        }
        if (empty) {
            font.draw(batch, "(Backpack is empty)", startX, y);
        }
    }
}