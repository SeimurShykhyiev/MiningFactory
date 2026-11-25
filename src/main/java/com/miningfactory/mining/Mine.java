package com.miningfactory.mining;

import com.miningfactory.resources.ResourceType;

public class Mine {
    private final OreDeposit deposit;

    public Mine() {
        this.deposit = new OreDeposit(ResourceType.IRON_ORE, 0.75f);
    }

    public OreDeposit getDeposit() {
        return deposit;
    }
}