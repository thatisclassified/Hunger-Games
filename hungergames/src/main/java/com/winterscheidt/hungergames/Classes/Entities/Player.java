package com.winterscheidt.hungergames.Classes.Entities;

import java.util.List;

import com.winterscheidt.hungergames.Classes.Game.Game;
import com.winterscheidt.hungergames.Classes.Game.GameAction;
import com.winterscheidt.hungergames.Classes.Items.Item;
import com.winterscheidt.hungergames.Classes.Items.Weapon;
import com.winterscheidt.hungergames.Utils.CauseOfDeath;
import com.winterscheidt.hungergames.Utils.Gender;
import com.winterscheidt.hungergames.Utils.Helpers;
import com.winterscheidt.hungergames.Utils.Inventory;
import com.winterscheidt.hungergames.Interfaces.Killer;
import com.winterscheidt.hungergames.Interfaces.Attainable;
import com.winterscheidt.hungergames.Interfaces.Equippable;
import com.winterscheidt.hungergames.Interfaces.Usable;

public class Player extends Entity implements Killer {
    // Static Constants
    private static final double NUTRITION_THRESHOLD = 0.8;
    private static final double NUTRITION_DAMAGE = 0.8;

    // Fields
    private Inventory inventory = new Inventory();
    private int district = (int) (Math.random() * 12 + 1);
    private Equippable equippedItem;

    // Attributes
    private double aggression = 1;
    private double smarts = 1;
    private double strength = 1;
    private double lithe = 1;

    // Modifiers / Data
    private double hunger = 0;
    private double thirst = 0;

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
        this.hunger = Helpers.clamp(this.hunger + hunger, 0, 1);
    }
    public void addThirst(double thirst) {
        this.thirst = Helpers.clamp(this.thirst + thirst, 0, 1);
    }

    // Methods
    public boolean takeItem(Item item) {
        // Check if item is attainable
        if (item instanceof Attainable && this.inventory.getSize() < this.inventory.getMaxSize()) {
            this.location.getItems().remove(item);
            this.inventory.addItem(item);
            return true;
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

    // Game Management
    public void turnStart(Game game) {
        this.obscurity = this.location.getBaseObscurity();
        this.perception = this.smarts - 1;
        this.canMove = true;

        if (this.hunger >= NUTRITION_THRESHOLD) {
            this.addHealth(-NUTRITION_DAMAGE, game, CauseOfDeath.HUNGER);
        }
        if (this.thirst >= NUTRITION_THRESHOLD) {
            this.addHealth(-NUTRITION_DAMAGE, game, CauseOfDeath.THIRST);
        }
    }
    public void turnEnd(Game game) {
        this.hunger += 0.05;
        this.thirst += 0.05;
    }

    public void die(Game game, Killer cause) {
        // Need to implement this further
        this.description = cause.getKillDescription(this);
    }

    public String getKillMessage(Entity entity) {
        if (this.equippedItem instanceof Killer) {
            return ((Killer) this.equippedItem).getKillMessage(entity);
        } else {
            return entity.getName() + " was beaten to death by " + this.name;
        }
    }
    public String getKillDescription(Entity entity) {
        if (this.equippedItem instanceof Killer) {
            return ((Killer) this.equippedItem).getKillDescription(entity);
        } else {
            Gender g = entity.getGender();
            return entity.getName() + " lies limp on the ground. " + 
                g.capPossessive() + " body is heavily bruised, as if " +
                g.object() + " had a hand-to-hand struggle.";
        }
    }
}
