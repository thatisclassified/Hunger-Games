package com.winterscheidt.hungergames.Classes.Entities;

import com.winterscheidt.hungergames.Classes.Items.Item;
import com.winterscheidt.hungergames.Classes.Items.Weapon;
import com.winterscheidt.hungergames.Classes.Utils.Inventory;
import com.winterscheidt.hungergames.Interfaces.Attainable;

public class Player extends Entity {
    // Fields
    private Inventory inventory = new Inventory();
    private double hunger = 1;
    private double thirst = 1;
    private Weapon equippedWeapon;
    private double aggression;
    private double smarts;
    private double strength;

    // Methods
    public Inventory getInventory() {
        return this.inventory;
    }
    public boolean takeItem(Item item) {
        // Check if item is attainable
        if (item instanceof Attainable) {
            return this.inventory.addItem(item);
        }
        return false;
    }
}
