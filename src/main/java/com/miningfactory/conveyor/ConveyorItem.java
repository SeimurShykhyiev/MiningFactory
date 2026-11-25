package com.miningfactory.conveyor;

import com.miningfactory.resources.Resource;
import com.miningfactory.resources.ResourceType;

public class ConveyorItem {
    private final Resource resource;
    private float progress; // от 0.0 до 1.0

    public ConveyorItem(ResourceType type) {
        this.resource = new Resource(type);
        this.progress = 0f;
    }

    public Resource getResource() { return resource; }
    public ResourceType getResourceType() { return resource.getType(); }
    public float getProgress() { return progress; }

    public void advance(float deltaProgress) {
        this.progress = Math.min(1.0f, this.progress + deltaProgress);
    }

    public boolean isReadyToTransfer() {
        return progress >= 1.0f;
    }
}