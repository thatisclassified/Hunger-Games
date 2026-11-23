package com.winterscheidt.hungergames.Classes.Entities;

import com.winterscheidt.hungergames.Interfaces.Inspectable;
import com.winterscheidt.hungergames.Interfaces.Killer;
import com.winterscheidt.hungergames.Utils.DetectionLevel;
import com.winterscheidt.hungergames.Utils.Gender;
import com.winterscheidt.hungergames.Utils.Helpers;

import java.io.Serializable;
import java.util.List;

import com.winterscheidt.hungergames.Classes.Game.Game;
import com.winterscheidt.hungergames.Classes.Game.GameAction;
import com.winterscheidt.hungergames.Classes.Game.Location;

public abstract class Entity implements Inspectable, Serializable {
    // Fields
    protected String name;
    protected String description;
    protected double health = 1;
    protected Location location;
    protected boolean AI = true;
    protected boolean canMove = true;
    protected double agility = 1;
    protected double obscurity = 0;
    protected double perception = 0;
    protected double perceptionMod = 0;
    protected Gender gender = Gender.randomGender();

    // Getters and Setters
    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }
    public double getObscurity() {
        return this.obscurity;
    }
    public double getPerception() {
        return this.perception;
    }
    public double getAgility() {
        return this.agility;
    }
    public boolean isAI() {
        return this.AI;
    }
    public Location getLocation() {
        return this.location;
    }
    public Gender getGender() {
        return this.gender;
    }
    public boolean canMove() {
        return this.canMove;
    }
    public void setObscurity(double obscurity) {
        this.obscurity = obscurity;
    }
    public void lerpObscurity(double amount) {
        this.obscurity = Helpers.lerp(this.obscurity, amount);
    }
    public void setPerception(double perception) {
        this.perception = perception;
    }
    public void lerpPerception(double amount) {
        this.perception = Helpers.lerp(this.perception, amount);
    }
    public DetectionLevel getDetection(Entity entity) {
        // Roll perception: base perception + random roll (e.g., 0.0 to 1.0)
        double roll = this.getPerception() + this.perceptionMod; 
        double effectiveObscurity = Math.min(0.95, entity.getObscurity());
        
        double margin = roll - effectiveObscurity;

        if (margin >= 0.4) {
            return DetectionLevel.RECOGNIZED; // "You see Cato hiding near the rocks."
        } else if (margin >= 0.0) {
            return DetectionLevel.SPOTTED;    // "You spot a shadowy figure moving in the brush."
        } else {
            return DetectionLevel.HIDDEN;     // Completely unseen!
        }
    }

    // Movement Methods
    public boolean move(String location) {
        if (this.location.getConnections().containsKey(location)) {
            this.move(this.location.getConnections().get(location));
            return true;
        }
        return false;
    }
    public boolean move(Location location) {
        if (this.location.getConnections().containsValue(location)) {
            this.obscurity -= this.location.getBaseObscurity();
            this.location = location;
            this.obscurity += this.location.getBaseObscurity();
            this.canMove = false;
            return true;
        }
        return false;
    }

    // Health Methods
    public double getHealth() {
        return this.health;
    }
    public void setHealth(double health) {
        this.health = Helpers.clamp(health, 0, 1);
    }
    public void addHealth(double health) {
        this.health = Helpers.clamp(this.health + health, 0, 1);
    }
    public void addHealth(double health, Game game, Killer cause) {
        this.addHealth(health);
        if (this.health <= 0) {
            this.die(game, cause);
        }
    }

    // Game Managers
    public abstract void die(Game game, Killer cause);
    public abstract void turnStart(Game game);
    public abstract void turnEnd(Game game);
    public abstract GameAction chooseAction(List<GameAction> actions, Game game);
    protected abstract double scoreAction(GameAction action, Game game);

    public abstract double attack(Entity entity, Game game);
}
