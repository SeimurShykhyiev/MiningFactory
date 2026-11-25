package com.miningfactory.resources;

import java.util.EnumMap;
import java.util.Map;

public class Inventory {
    private final EnumMap<ResourceType, ResourceStack> stacks;
    private int capacity;

    public Inventory(int initialCapacity) {
        this.capacity = initialCapacity;
        this.stacks = new EnumMap<>(ResourceType.class);
        for (ResourceType type : ResourceType.values()) {
            stacks.put(type, new ResourceStack(type, 0));
        }
    }

    public boolean addResource(ResourceType type, int amount) {
        if (type == null || getTotalCount() + amount > capacity) {
            return false;
        }
        stacks.get(type).add(amount);
        return true;
    }

    public boolean removeResource(ResourceType type, int amount) {
        if (type == null) return false;
        return stacks.get(type).remove(amount);
    }

    public int getCount(ResourceType type) {
        return stacks.get(type).getAmount();
    }

    public int getTotalCount() {
        int sum = 0;
        for (ResourceStack stack : stacks.values()) {
            sum += stack.getAmount();
        }
        return sum;
    }

    public void clear() {
        for (ResourceStack stack : stacks.values()) {
            stack.remove(stack.getAmount());
        }
    }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public Map<ResourceType, ResourceStack> getStacks() { return stacks; }
}