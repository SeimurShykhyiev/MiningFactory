package com.miningfactory.resources;

import com.badlogic.gdx.graphics.Color;

public enum ResourceType {
    IRON_ORE("Iron Ore", 5, new Color(0.55f, 0.32f, 0.22f, 1f), "02_iron_ore.png"),
    CLEAN_ORE("Clean Ore", 12, new Color(0.70f, 0.48f, 0.35f, 1f), "01_copper_ore.png"),
    POLISHED_ORE("Polished Ore", 25, new Color(0.85f, 0.65f, 0.50f, 1f), "03_ice_ore.png"),
    IRON_INGOT("Iron Ingot", 55, new Color(0.78f, 0.82f, 0.88f, 1f), "04_iron_ingot.png"),
    STONE("Stone", 0, new Color(0.45f, 0.45f, 0.45f, 1f), "05_stone.png"),
    STONE_DUST("Stone Dust", 2, new Color(0.68f, 0.68f, 0.62f, 1f), "06_gravel.png"),
    BRICK("Brick", 18, new Color(0.76f, 0.28f, 0.20f, 1f), "09_clay_brick.png"),
    DIAMOND("Diamond", 250, new Color(0.20f, 0.90f, 1.00f, 1f), "07_blue_crystal.png"),
    EMERALD("Emerald", 140, new Color(0.15f, 0.85f, 0.35f, 1f), "08_emerald.png"),
    HARDENED_METAL("Hardened Metal", 110, new Color(0.35f, 0.50f, 0.70f, 1f), "10_tempered_metal_ingot.png"),
    IMPROVED_ALLOY("Improved Alloy", 260, new Color(0.95f, 0.78f, 0.15f, 1f), "11_gold_alloy_ingot.png");

    private final String displayName;
    private final int basePrice;
    private final Color color;
    private final String texturePath;

    ResourceType(String displayName, int basePrice, Color color, String texturePath) {
        this.displayName = displayName;
        this.basePrice = basePrice;
        this.color = color;
        this.texturePath = texturePath;
    }

    public String getDisplayName() { return displayName; }
    public int getBasePrice() { return basePrice; }
    public Color getColor() { return color; }
    public String getTexturePath() { return texturePath; }
}