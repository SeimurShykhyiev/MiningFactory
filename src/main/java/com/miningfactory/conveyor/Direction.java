package com.miningfactory.conveyor;

public enum Direction {
    UP(0, 1, "UP"),
    RIGHT(1, 0, "RIGHT"),
    DOWN(0, -1, "DOWN"),
    LEFT(-1, 0, "LEFT");

    private final int dx;
    private final int dy;
    private final String label;

    Direction(int dx, int dy, String label) {
        this.dx = dx;
        this.dy = dy;
        this.label = label;
    }

    public int getDx() { return dx; }
    public int getDy() { return dy; }
    public String getLabel() { return label; }

    public Direction nextClockwise() {
        Direction[] vals = values();
        return vals[(this.ordinal() + 1) % vals.length];
    }
}