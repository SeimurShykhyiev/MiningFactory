package com.miningfactory.world;

import com.miningfactory.economy.MoneyManager;
import com.miningfactory.resources.ResourceType;

public class Map {
    public static final int TILE_SIZE = 64;
    private final int cols;
    private final int rows;
    private final Tile[][] tiles;

    public Map(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        this.tiles = new Tile[cols][rows];
        initZones();
    }

    private void initZones() {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                Tile.ZoneType zone = Tile.ZoneType.CONVEYOR; 

                if (x >= 1 && x <= 4 && y >= 11 && y <= 14) {
                    zone = Tile.ZoneType.MINE;       
                } else if (x >= 1 && x <= 4 && y >= 1 && y <= 4) {
                    zone = Tile.ZoneType.SELL;       
                } else if (x >= 6 && x <= 17 && y >= 2 && y <= 11) {
                    zone = Tile.ZoneType.PRODUCTION; 
                }

                tiles[x][y] = new Tile(x, y, zone);
            }
        }
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < cols && y >= 0 && y < rows;
    }

    public Tile getTile(int x, int y) {
        return isValid(x, y) ? tiles[x][y] : null;
    }

    public boolean canBuildAt(int x, int y) {
        if (!isValid(x, y)) return false;
        Tile.ZoneType z = tiles[x][y].getZoneType();
        return z != Tile.ZoneType.MINE && z != Tile.ZoneType.SELL && tiles[x][y].getBuilding() == null;
    }

    public void setBuilding(int x, int y, Building building) {
        if (isValid(x, y)) {
            tiles[x][y].setBuilding(building);
        }
    }

    public boolean pushResourceTo(int x, int y, ResourceType resourceType, MoneyManager moneyManager) {
        if (!isValid(x, y) || resourceType == null) return false;

        Tile tile = tiles[x][y];

        // Автопродажа при поступлении ресурса по конвейеру в Зону Продажи
        if (tile.getZoneType() == Tile.ZoneType.SELL) {
            if (resourceType.getBasePrice() > 0) {
                moneyManager.addMoney(resourceType.getBasePrice());
            }
            return true;
        }

        Building target = tile.getBuilding();
        if (target != null && target.canAccept(resourceType)) {
            return target.accept(resourceType);
        }
        return false;
    }

    public void updateAll(float dt, MoneyManager moneyManager) {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                Building b = tiles[x][y].getBuilding();
                if (b != null) {
                    b.update(dt, this, moneyManager);
                }
            }
        }
    }

    public int getCols() { return cols; }
    public int getRows() { return rows; }
}
