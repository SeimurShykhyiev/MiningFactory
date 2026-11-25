package com.miningfactory.economy;

import com.miningfactory.conveyor.Conveyor;
import com.miningfactory.conveyor.Direction;
import com.miningfactory.machines.AlloyChamber;
import com.miningfactory.machines.BrickFactory;
import com.miningfactory.machines.Crusher;
import com.miningfactory.machines.Furnace;
import com.miningfactory.machines.HardeningChamber;
import com.miningfactory.machines.OreBox;
import com.miningfactory.machines.Polisher;
import com.miningfactory.machines.Sieve;
import com.miningfactory.machines.Washer;
import com.miningfactory.world.Building;

public class MachineShop extends Shop {

    public enum CatalogItem {
        CONVEYOR("Conveyor", 10, "conveyor.png", "Переміщує ресурси\nміж механізмами"),
        ORE_BOX("Ore Box (Hopper)", 20, "ore_box.png", "Приймає руду з інвентарю\nта видає на конвеєр"),
        WASHER("Ore Washer", 40, "washer.png", "Очищає залізну руду"),
        POLISHER("Polisher", 80, "polisher.png", "Полірує очищену руду"),
        FURNACE("Smelting Furnace", 160, "furnace.png", "Плавить руду в злитки.\nПобічний вихід - камінь"),
        CRUSHER("Crusher", 110, "crusher.png", "Подрібнює камінь\nна кам'яний пил"),
        SIEVE("Sieve (5%/10%)", 200, "sieve.png", "Просіює пил. Шанс:\n5% Алмаз, 10% Смарагд"),
        BRICK_FACTORY("Brick Factory", 130, "brick_factory.png", "Пресує залишки\nпилу в цеглу"),
        HARDENING_CHAMBER("Hardening Chamber", 280, "hardener.png", "Загартовує\nзалізні злитки"),
        ALLOY_CHAMBER("Alloy Chamber", 450, "alloy_chamber.png", "З'єднує два метали\nу покращений сплав");

        private final String title;
        private final int cost;
        private final String textureName;
        private final String description;

        CatalogItem(String title, int cost, String textureName, String description) {
            this.title = title;
            this.cost = cost;
            this.textureName = textureName;
            this.description = description;
        }

        public String getTitle() { return title; }
        public int getCost() { return cost; }
        public String getTextureName() { return textureName; }
        public String getDescription() { return description; }
    }

    public MachineShop(MoneyManager moneyManager) {
        super(moneyManager);
    }

    @Override
    public String getShopName() {
        return "Магазин механізмів";
    }

    public Building purchaseBuilding(CatalogItem item, int gridX, int gridY, Direction dir) {
        if (!moneyManager.spendMoney(item.getCost())) {
            return null;
        }
        switch (item) {
            case CONVEYOR: return new Conveyor(gridX, gridY, dir);
            case ORE_BOX: return new OreBox(gridX, gridY, dir);
            case WASHER: return new Washer(gridX, gridY, dir);
            case POLISHER: return new Polisher(gridX, gridY, dir);
            case FURNACE: return new Furnace(gridX, gridY, dir);
            case CRUSHER: return new Crusher(gridX, gridY, dir);
            case SIEVE: return new Sieve(gridX, gridY, dir);
            case BRICK_FACTORY: return new BrickFactory(gridX, gridY, dir);
            case HARDENING_CHAMBER: return new HardeningChamber(gridX, gridY, dir);
            case ALLOY_CHAMBER: return new AlloyChamber(gridX, gridY, dir);
            default: return null;
        }
    }
}