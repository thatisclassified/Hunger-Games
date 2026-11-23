package com.winterscheidt.hungergames.Classes.Entities;

import java.util.List;

import com.winterscheidt.hungergames.Classes.Game.Game;
import com.winterscheidt.hungergames.Classes.Game.GameAction;
import com.winterscheidt.hungergames.Classes.Game.Location;
import com.winterscheidt.hungergames.Classes.Items.Item;
import com.winterscheidt.hungergames.Classes.Items.Weapon;
import com.winterscheidt.hungergames.Utils.CauseOfDeath;
import com.winterscheidt.hungergames.Utils.DetectionLevel;
import com.winterscheidt.hungergames.Utils.Gender;
import com.winterscheidt.hungergames.Utils.Helpers;
import com.winterscheidt.hungergames.Utils.Inventory;
import com.winterscheidt.hungergames.Utils.Nutrition;
import com.winterscheidt.hungergames.Interfaces.Killer;
import com.winterscheidt.hungergames.Interfaces.Attainable;
import com.winterscheidt.hungergames.Interfaces.Equippable;
import com.winterscheidt.hungergames.Interfaces.Usable;

public class Player extends Entity implements Killer {
    // Static Constants
    private static final double MELEE_DAMAGE = 0.08;
    private static final double MELEE_ACCURACY = 0.9;

    // Fields
    private Inventory inventory = new Inventory();
    private int district = (int) (Math.random() * 12 + 1);
    private Equippable equippedItem;

    // Attributes
    private double aggression = 1;
    private double smarts = 1;
    private double strength = 1;

    // Modifiers / Data
    private double hunger = 0;
    private double thirst = 0;

    // Cosntructors
    // Later I want some static arrays to randomize this
    public Player(String name, String description, boolean AI, Location location) {
        this(name, description, AI, location, (int) (Math.random() * 12 + 1), Gender.randomGender(), 
        0.75 + Math.random() / 2, 0.75 + Math.random() / 2, 0.75 + Math.random() / 2);
    }
    public Player(String name, boolean AI, Location location, Gender gender) {
        this(name, gender.subject() + " looks healthy.", AI, location, (int) (Math.random() * 12 + 1), gender, 
         0.75 + Math.random() / 2, 0.75 + Math.random() / 2, 0.75 + Math.random() / 2);
        
    }
    // And one for all the attributes
    public Player(String name, String description, boolean AI, Location location, int district, Gender gender, double aggression, double smarts, double strength) {
        this.name = name;
        this.description = description;
        this.AI = AI;
        this.location = location;
        this.district = district;
        this.gender = gender;
        this.aggression = aggression;
        this.smarts = smarts;
        this.strength = strength;
    }

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
        this.obscurity = this.location.getBaseObscurity() * this.agility;
        this.perception = this.smarts - 1;
        this.perceptionMod = Math.random();

        Nutrition hungerState = Nutrition.getHunger(hunger);
        Nutrition thirstState = Nutrition.getThirst(thirst);

        switch(hungerState) {
            case HUNGRY:
                game.getIO().broadcast("You're starting to get hungry.", this);
                break;
            case VERY_HUNGRY:
                game.getIO().broadcast("Your stomach growls. You haven't eaten in a while...", this);
                break;
            case STARVING:
                game.getIO().broadcast("You're starving.", this);
                this.addHealth(-Nutrition.HUNGER_DAMAGE, game, CauseOfDeath.HUNGER);
                break;
        }

        switch(thirstState) {
            case THIRSTY:
                game.getIO().broadcast("You're starting to get thirsty.", this);
                break;
            case VERY_THIRSTY:
                game.getIO().broadcast("Your throat is burning. You haven't had water in a while...", this);
                break;
            case PARCHED:
                game.getIO().broadcast("You're dying of thirst.", this);
                this.addHealth(-Nutrition.THIRST_DAMAGE, game, CauseOfDeath.THIRST);
                break;
        }
    }
    public void turnEnd(Game game) {
        this.hunger += 0.05;
        this.thirst += 0.05;
        this.canMove = true;
    }

    @Override
    public void die(Game game, Killer cause) {
        // Need to implement this further
        this.description = cause.getKillDescription(this);
        game.getIO().broadcast("BOOM!");
        game.reduceTributes();
    }

    @Override
    public String getKillMessage(Entity entity) {
        if (this.equippedItem instanceof Killer) {
            return ((Killer) this.equippedItem).getKillMessage(entity);
        } else {
            return entity.getName() + " was beaten to death by " + this.name;
        }
    }

    @Override
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

    @Override
    public double attack(Entity entity, Game game) {
        Weapon weapon = (Weapon) this.equippedItem;
        if (weapon != null) {
            weapon.addUses(-1); // Handle breakage if durability <= 0
        }

        double baseAcc = (weapon != null) ? weapon.getAccuracy() : MELEE_ACCURACY; // Unarmed accuracy
        double rawHitChance = (baseAcc / entity.getAgility()) - (entity.getObscurity() * 0.25);
        double hitChance = Math.max(0.10, Math.min(0.95, rawHitChance));

        if (Math.random() > hitChance) {
            return 0;
        }

        double baseDmg = (weapon != null) ? weapon.getDamage() : MELEE_DAMAGE;
        double variance = 0.9 + (Math.random() * 0.2);
        double finalDamage = baseDmg * this.strength * variance;

        // Attacking breaks stealth
        // this.setObscurity(this.location.getBaseObscurity());

        entity.addHealth(-finalDamage, game, this);

        return finalDamage;
    }

    // Courtesy of Gemini
    @Override
    public GameAction chooseAction(List<GameAction> actions, Game game) {
        if (actions.size() == 1) {
            return actions.get(0); // Only one choice (e.g., "Back")
        }

        GameAction bestAction = actions.get(0);
        double bestScore = -1.0;

        for (GameAction action : actions) {
            double score = scoreAction(action, game);
            if (score > 0) {
                // Add slight randomness (0 to 15) so tributes aren't 100% predictable
                score += Math.random() * 15.0;
            }
            if (score > bestScore) {
                bestScore = score;
                bestAction = action;
            }
        }
        return bestAction;
    }

    @Override
    protected double scoreAction(GameAction action, Game game) {
        String label = action.getName();

        if (label.equals("Back")) {
            return 0.0;
        }
        if (label.startsWith("Unequip")) {
            return 0.0;
        }
        if (label.startsWith("Equip")) {
            return (this.equippedItem == null) ? 85.0 : 15.0;
        }
        if (label.startsWith("Use")) {
            double urgency = (1.0 - this.health) * 80.0 + (this.hunger * 50.0) + (this.thirst * 50.0);
            return (urgency > 25.0) ? urgency : 5.0;
        }
        if (label.equals("Use / Equip...")) {
            if (this.inventory.getSize() == 0) return 0.0;
            if (this.equippedItem == null) return 85.0;
            return ((1.0 - this.health) > 0.3 || this.hunger > 0.5 || this.thirst > 0.5) ? 65.0 : 5.0;
        }
        if (label.equals("Pick Up / Interact...") || label.startsWith("Pick up") || label.startsWith("Interact")) {
            if (this.location.getItems().isEmpty() || this.inventory.getSize() >= this.inventory.getMaxSize()) {
                return 0.0;
            }
            return (this.equippedItem == null) ? 75.0 : 40.0;
        }
        if (label.equals("Attack...") || label.startsWith("Attack ")) {
            boolean hasTarget = false;
            for (Entity entity : game.getEntities()) {
                if (entity.getLocation() == this.location && entity != this) {
                    hasTarget = true;
                    break;
                }
            }
            if (!hasTarget) return 0.0;
            double score = 20.0 + (this.aggression * 45.0) - ((1.0 - this.health) * 50.0);
            if (this.equippedItem != null) score += 25.0;
            return Math.max(5.0, score);
        }
        if (label.equals("Move...") || label.startsWith("Move ")) {
            double score = 25.0 + ((1.0 - this.health) * 45.0) + ((1.0 - this.aggression) * 15.0);
            if (this.location.getItems().isEmpty()) score += 15.0;
            return score;
        }
        if (label.equals("Hide")) {
            return 10.0 + (this.smarts * 15.0) + ((1.0 - this.health) * 35.0);
        }
        if (label.equals("Rest")) {
            return (this.health < 0.8) ? 15.0 + ((1.0 - this.health) * 35.0) : 5.0;
        }

        return 10.0; // Default fallback for any custom actions
    }
}
