package com.miningfactory.machines;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.miningfactory.conveyor.Direction;
import com.miningfactory.resources.ResourceType;

public class Sieve extends ProcessingMachine {
    public Sieve(int gridX, int gridY, Direction direction) {
        super("Sieve (5%/10%)", 200, 2.5f,
                ResourceType.STONE_DUST, ResourceType.STONE_DUST,
                gridX, gridY, direction, new Color(0.75f, 0.65f, 0.25f, 1f), "sieve.png");
    }

    @Override
    protected void produceOutput() {
        inputSlot = null;
        float roll = MathUtils.random(0f, 100f);
        if (roll < 5.0f) {
            outputBuffer = ResourceType.DIAMOND;     // 5% шанс алмаза
        } else if (roll < 15.0f) {
            outputBuffer = ResourceType.EMERALD;     // 10% шанс изумруда
        } else {
            outputBuffer = ResourceType.STONE_DUST;  // 85% остается каменная пыль
        }
    }
}