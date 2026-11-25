package com.miningfactory.mining;

import com.miningfactory.resources.ResourceType;

public class OreDeposit {
    private final ResourceType oreType;
    private final float baseMineTime;

    public OreDeposit(ResourceType oreType, float baseMineTime) {
        this.oreType = oreType;
        this.baseMineTime = baseMineTime;
    }

    public ResourceType extractOre() {
        return oreType;
    }

    public float getBaseMineTime() {
        return baseMineTime;
    }
}