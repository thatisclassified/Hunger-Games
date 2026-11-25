package com.winterscheidt.hungergames.Classes.Items;

import com.winterscheidt.hungergames.Classes.Entities.Entity;
import com.winterscheidt.hungergames.Classes.Entities.Player;
import com.winterscheidt.hungergames.Classes.Game.Game;
import com.winterscheidt.hungergames.Interfaces.Attainable;
import com.winterscheidt.hungergames.Interfaces.Killer;
import com.winterscheidt.hungergames.Interfaces.Usable;
import com.winterscheidt.hungergames.Utils.CauseOfDeath;

public class Edible extends Item implements Usable, Attainable, Killer {
    private int uses;
    private double food;
    private double water;
    private double health;

    // Constructor
    public Edible(String name, String description, int uses, double food, double water, double health) {
        super(name, description);
        this.uses = uses;
        this.food = food;
        this.water = water;
        this.health = health;
    }

    // Method
    public void use(Player player, Game game) {
        if (this.uses > 0) {
            player.addHunger(-this.food);
            player.addThirst(-this.water);
            player.addHealth(this.health, game, this);
            this.uses--;
        }
    }
    public int getUses() {
        return this.uses;
    }

    @Override
    public String getKillMessage(Entity entity) {
        return CauseOfDeath.POISON.getKillMessage(entity);
    }

    @Override
    public String getKillDescription(Entity entity) {
        return CauseOfDeath.POISON.getKillDescription(entity);
    }
}
