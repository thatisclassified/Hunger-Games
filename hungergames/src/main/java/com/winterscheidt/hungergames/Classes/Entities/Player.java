package com.winterscheidt.hungergames.Classes.Entities;

import com.winterscheidt.hungergames.Classes.Items.Item;
import com.winterscheidt.hungergames.Classes.Items.Weapon;
import com.winterscheidt.hungergames.Classes.Utils.Inventory;
import com.winterscheidt.hungergames.Interfaces.Attainable;
import com.winterscheidt.hungergames.Interfaces.Equippable;

public class Player extends Entity {
    // Fields
    private Inventory inventory = new Inventory();
    private double hunger = 1;
    private double thirst = 1;
    private Equippable equippedItem;
    private double aggression = 1;
    private double smarts = 1;
    private double strength = 1;

    // Getters
    public Inventory getInventory() {
        return this.inventory;
    }
    public double getHunger() {
        return this.hunger;
    }
    public double getThirst() {
        return this.thirst;
    }

    // Setters / Adders
    public void addHunger(double hunger) {
        this.hunger = Math.max(0, Math.min(1, this.hunger + hunger));
    }
    public void addThirst(double thirst) {
        this.thirst = Math.max(0, Math.min(1, this.thirst + thirst));
    }

    // Methods
    public boolean takeItem(Item item) {
        // Check if item is attainable
        if (item instanceof Attainable) {
            return this.inventory.addItem(item);
        }
        return false;
    }
    public void dropItem(Item item) {
        this.inventory.removeItem(item);
        this.location.addItem(item);
    }
    public boolean equipItem(Item item) {
        if (item instanceof Equippable) {
            this.equippedItem = (Equippable) item;
            return true;
        }
        return false;
    }
}
