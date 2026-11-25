package com.miningfactory.resources;

public class ResourceStack {
    private final ResourceType type;
    private int amount;

    public ResourceStack(ResourceType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public ResourceType getType() { return type; }
    public int getAmount() { return amount; }

    public void add(int count) {
        this.amount += count;
    }

    public boolean remove(int count) {
        if (this.amount >= count) {
            this.amount -= count;
            return true;
        }
        return false;
    }
}