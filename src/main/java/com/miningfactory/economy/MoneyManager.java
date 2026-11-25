package com.miningfactory.economy;

public class MoneyManager {
    private int balance;

    public MoneyManager(int initialBalance) {
        this.balance = initialBalance;
    }

    public boolean canAfford(int amount) {
        return balance >= amount;
    }

    public boolean spendMoney(int amount) {
        if (canAfford(amount)) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public void addMoney(int amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    public int getBalance() { return balance; }
}