package com.winterscheidt.hungergames.Classes.Entities;

import java.util.List;

import com.winterscheidt.hungergames.Classes.Game.Game;
import com.winterscheidt.hungergames.Classes.Game.GameAction;
import com.winterscheidt.hungergames.Classes.Items.Item;
import com.winterscheidt.hungergames.Classes.Items.Weapon;
import com.winterscheidt.hungergames.Classes.Utils.Inventory;
import com.winterscheidt.hungergames.Interfaces.Aggressive;
import com.winterscheidt.hungergames.Interfaces.Attainable;
import com.winterscheidt.hungergames.Interfaces.Equippable;
import com.winterscheidt.hungergames.Interfaces.Usable;

public class Player extends Entity implements Aggressive {
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
    public Item getEquipped() {
        return (Item) this.equippedItem;
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
            Item temp = (Item) this.equippedItem;
            this.equippedItem = (Equippable) item;
            this.inventory.removeItem(item);
            if (temp != null) {
                this.inventory.addItem(temp);
            }
            return true;
        }
        return false;
    }
    public boolean unequipItem() {
        if (this.equippedItem != null && this.inventory.addItem((Item) this.equippedItem)) {
            this.equippedItem = null;
            return true;
        }
        return false;
    }
    public boolean useItem(Item item, Game game) {
        if (item instanceof Usable) {
            ((Usable) item).use(this, game);
            return true;
        }
        return false;
    }
}
