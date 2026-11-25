package com.miningfactory.core;

import com.miningfactory.conveyor.Direction;
import com.miningfactory.economy.MachineShop;
import com.miningfactory.economy.MoneyManager;
import com.miningfactory.economy.UpgradeShop;
import com.miningfactory.machines.OreBox;
import com.miningfactory.machines.ProcessingMachine;
import com.miningfactory.mining.Mine;
import com.miningfactory.player.Player;
import com.miningfactory.resources.ResourceType;
import com.miningfactory.world.Building;
import com.miningfactory.world.Map;
import com.miningfactory.world.Tile;

public class GameWorld {
    public static final int GRID_COLS = 21;
    public static final int GRID_ROWS = 16;

    private final Map map;
    private final Player player;
    private final Mine mine;
    private final MoneyManager moneyManager;
    private final MachineShop machineShop;
    private final UpgradeShop upgradeShop;

    private MachineShop.CatalogItem selectedCatalogItem = MachineShop.CatalogItem.CONVEYOR;
    private Direction buildDirection = Direction.RIGHT;
    private String statusMessage = "Видобувай залізну руду в Шахті (утримуй SPACE), продавай її в Зоні продажу та будуй свою фабрику!";

    public GameWorld() {
        this.map = new Map(GRID_COLS, GRID_ROWS);
        this.player = new Player(3.0f * Map.TILE_SIZE, 12.5f * Map.TILE_SIZE);
        this.mine = new Mine();
        this.moneyManager = new MoneyManager(0); // Начинаем с $0 (добыча -> продажа -> покупка)
        this.machineShop = new MachineShop(moneyManager);
        this.upgradeShop = new UpgradeShop(moneyManager);
    }

    public void update(float dt) {
        float speedMultiplier = player.getUpgrades().getWorkSpeedMultiplier();
        map.updateAll(dt * speedMultiplier, moneyManager);

        // Продажа ресурсов из рюкзака при входе игрока в SELL ZONE
        Tile playerTile = map.getTile(player.getGridX(), player.getGridY());
        if (playerTile != null && playerTile.getZoneType() == Tile.ZoneType.SELL) {
            int earned = player.sellAllResources(moneyManager);
            if (earned > 0) {
                statusMessage = "Ресурси з інвентарю було продано. +$" + earned + "!";
            }
        }
    }

    private OreBox findNearestOreBox(int radius) {
        int px = player.getGridX();
        int py = player.getGridY();
        OreBox closest = null;
        int minDistSq = Integer.MAX_VALUE;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                Tile t = map.getTile(px + dx, py + dy);
                if (t != null && t.getBuilding() instanceof OreBox) {
                    int distSq = dx * dx + dy * dy;
                    if (distSq < minDistSq) {
                        minDistSq = distSq;
                        closest = (OreBox) t.getBuilding();
                    }
                }
            }
        }
        return closest;
    }

    // Вызывается строго по нажатию клавиши F
    public void tryInteractNearbyOrHovered(Building hovered) {
        if (hovered instanceof OreBox) {
            interactWithBuilding(hovered);
            return;
        }

        OreBox nearbyBox = findNearestOreBox(2);
        if (nearbyBox != null) {
            interactWithBuilding(nearbyBox);
            return;
        }

        int px = player.getGridX();
        int py = player.getGridY();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Tile t = map.getTile(px + dx, py + dy);
                if (t != null && t.getBuilding() instanceof ProcessingMachine) {
                    interactWithBuilding(t.getBuilding());
                    return;
                }
            }
        }

        if (hovered != null) {
            interactWithBuilding(hovered);
        } else {
            statusMessage = "Move closer to an Ore Box or Machine and press [F]!";
        }
    }

    public void interactWithBuilding(Building building) {
        if (building == null) return;

        if (building instanceof OreBox) {
            OreBox box = (OreBox) building;
            int oreInBackpack = player.getBackpack().getInventory().getCount(ResourceType.IRON_ORE);
            if (oreInBackpack > 0) {
                int moved = box.depositFromPlayer(oreInBackpack);
                if (moved > 0) {
                    player.getBackpack().getInventory().removeResource(ResourceType.IRON_ORE, moved);
                    statusMessage = "Unloaded " + moved + " Iron Ore into Ore Box! (In Box: " + box.getStoredOreCount() + ")";
                } else {
                    statusMessage = "Ore Box is full!";
                }
            } else {
                statusMessage = "No Iron Ore in backpack! Mine it in the Mine Zone first (Hold SPACE).";
            }
            return;
        }

        if (building instanceof ProcessingMachine) {
            ProcessingMachine pm = (ProcessingMachine) building;
            if (pm.getOutputBuffer() != null) {
                if (player.getBackpack().getInventory().addResource(pm.getOutputBuffer(), 1)) {
                    ResourceType taken = pm.takeOutputManually();
                    statusMessage = "Collected " + taken.getDisplayName() + " from " + pm.getName();
                    return;
                }
            }
        }

        for (ResourceType type : ResourceType.values()) {
            if (player.getBackpack().getInventory().getCount(type) > 0 && building.canAccept(type)) {
                if (building.accept(type)) {
                    player.getBackpack().getInventory().removeResource(type, 1);
                    statusMessage = "Loaded " + type.getDisplayName() + " into " + building.getName();
                    return;
                }
            }
        }
    }

    public void placeOrRotateBuilding(int gx, int gy) {
        if (!map.isValid(gx, gy)) return;
        Tile tile = map.getTile(gx, gy);

        if (tile.getBuilding() != null) {
            tile.getBuilding().rotate();
            statusMessage = "Rotated " + tile.getBuilding().getName() + " -> " + tile.getBuilding().getDirection().getLabel();
            return;
        }

        if (!map.canBuildAt(gx, gy)) {
            statusMessage = "Cannot build inside Mine or Sell zones!";
            return;
        }

        Building created = machineShop.purchaseBuilding(selectedCatalogItem, gx, gy, buildDirection);
        if (created != null) {
            map.setBuilding(gx, gy, created);
            statusMessage = "Built " + created.getName() + " at (" + gx + ", " + gy + ")";
        } else {
            statusMessage = "Not enough money for " + selectedCatalogItem.getTitle() + " ($" + selectedCatalogItem.getCost() + ")";
        }
    }

    public void removeBuilding(int gx, int gy) {
        if (!map.isValid(gx, gy)) return;
        Tile tile = map.getTile(gx, gy);
        Building b = tile.getBuilding();
        if (b != null) {
            int refund = b.getCost() / 2;
            moneyManager.addMoney(refund);
            tile.setBuilding(null);
            statusMessage = "Demolished " + b.getName() + " (Refund +$" + refund + ")";
        }
    }

    public void rotateBuildDirection() {
        this.buildDirection = this.buildDirection.nextClockwise();
    }

    public Map getMap() { return map; }
    public Player getPlayer() { return player; }
    public Mine getMine() { return mine; }
    public MoneyManager getMoneyManager() { return moneyManager; }
    public MachineShop getMachineShop() { return machineShop; }
    public UpgradeShop getUpgradeShop() { return upgradeShop; }
    public MachineShop.CatalogItem getSelectedCatalogItem() { return selectedCatalogItem; }
    public void setSelectedCatalogItem(MachineShop.CatalogItem item) { this.selectedCatalogItem = item; }
    public Direction getBuildDirection() { return buildDirection; }
    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
}