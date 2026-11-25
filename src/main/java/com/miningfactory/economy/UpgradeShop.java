package com.miningfactory.economy;

import com.miningfactory.player.Player;

public class UpgradeShop extends Shop {

    public UpgradeShop(MoneyManager moneyManager) {
        super(moneyManager);
    }

    @Override
    public String getShopName() {
        return "Player Upgrades";
    }

    public int getBackpackUpgradeCost(Player player) {
        return (player.getBackpack().getLevel() + 1) * 100;
    }

    public int getPickaxeUpgradeCost(Player player) {
        return (player.getPickaxe().getLevel() + 1) * 150;
    }

    public int getWorkSpeedUpgradeCost(Player player) {
        return (player.getUpgrades().getWorkSpeedLevel() + 1) * 200;
    }

    public boolean buyBackpackUpgrade(Player player) {
        if (!player.getBackpack().canUpgrade()) return false;
        int cost = getBackpackUpgradeCost(player);
        if (moneyManager.spendMoney(cost)) {
            player.getBackpack().upgrade();
            return true;
        }
        return false;
    }

    public boolean buyPickaxeUpgrade(Player player) {
        if (!player.getPickaxe().canUpgrade()) return false;
        int cost = getPickaxeUpgradeCost(player);
        if (moneyManager.spendMoney(cost)) {
            player.getPickaxe().upgrade();
            return true;
        }
        return false;
    }

    public boolean buyWorkSpeedUpgrade(Player player) {
        if (!player.getUpgrades().canUpgradeWorkSpeed()) return false;
        int cost = getWorkSpeedUpgradeCost(player);
        if (moneyManager.spendMoney(cost)) {
            player.getUpgrades().upgradeWorkSpeed();
            return true;
        }
        return false;
    }
}