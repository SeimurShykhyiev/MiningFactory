package com.miningfactory.resources;

import com.badlogic.gdx.graphics.Color;

public class Resource {
    private final ResourceType type;

    public Resource(ResourceType type) {
        this.type = type;
    }

    public ResourceType getType() { return type; }
    public String getName() { return type.getDisplayName(); }
    public int getPrice() { return type.getBasePrice(); }
    public Color getColor() { return type.getColor(); }
}