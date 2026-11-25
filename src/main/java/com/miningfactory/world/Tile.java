package com.miningfactory.world;

public class Tile {
    public enum ZoneType {
        MINE,
        PRODUCTION,
        CONVEYOR,
        STORAGE,
        SELL
    }

    private final int x;
    private final int y;
    private final ZoneType zoneType;
    private Building building;

    public Tile(int x, int y, ZoneType zoneType) {
        this.x = x;
        this.y = y;
        this.zoneType = zoneType;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public ZoneType getZoneType() { return zoneType; }
    public Building getBuilding() { return building; }
    public void setBuilding(Building building) { this.building = building; }
}