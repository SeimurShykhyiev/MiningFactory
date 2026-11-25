package com.miningfactory.economy;

public abstract class Shop {
    protected final MoneyManager moneyManager;

    public Shop(MoneyManager moneyManager) {
        this.moneyManager = moneyManager;
    }

    public abstract String getShopName();
}