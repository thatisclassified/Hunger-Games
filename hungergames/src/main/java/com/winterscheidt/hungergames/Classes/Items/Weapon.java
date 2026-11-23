package com.winterscheidt.hungergames.Classes.Items;

import java.util.function.Function;

import com.winterscheidt.hungergames.Classes.Entities.Entity;
import com.winterscheidt.hungergames.Interfaces.Attainable;
import com.winterscheidt.hungergames.Interfaces.Equippable;
import com.winterscheidt.hungergames.Interfaces.Interactable;
import com.winterscheidt.hungergames.Interfaces.Killer;

public class Weapon extends Item implements Equippable, Attainable, Killer {
    private double damage;
    private double accuracy;
    private int uses;

    private final Function<Entity, String> messageGenerator;
    private final Function<Entity, String> descriptionGenerator;

    public Weapon(String name, String description, double damage, double accuracy, int uses,
        Function<Entity, String> messageGenerator,
        Function<Entity, String> descriptionGenerator
    ) {
        super(name, description);
        this.damage = damage;
        this.accuracy = accuracy;
        this.messageGenerator = messageGenerator;
        this.descriptionGenerator = descriptionGenerator;
    }

    public double getDamage() {
        return this.damage;
    }
    public double getAccuracy() {
        return this.accuracy;
    }
    public int getUses() {
        return this.uses;
    }
    public void addUses(int uses) {
        this.uses += uses;
    }

    @Override
    public String getKillMessage(Entity entity) {
        return this.messageGenerator.apply(entity);
    }

    @Override
    public String getKillDescription(Entity entity) {
        return this.descriptionGenerator.apply(entity);
    }
}
