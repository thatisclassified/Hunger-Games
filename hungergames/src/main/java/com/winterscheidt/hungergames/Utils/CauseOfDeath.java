package com.winterscheidt.hungergames.Utils;

import com.winterscheidt.hungergames.Classes.Entities.Entity;
import com.winterscheidt.hungergames.Interfaces.Killer;

import java.util.function.Function;

public enum CauseOfDeath implements Killer {
    HUNGER(entity -> entity.getName() + " starved to death."),
    THIRST(entity -> entity.getName() + " succumbed to dehydration."),
    POISON(entity -> entity.getName() + " collapsed from lethal poison."),
    FORCEFIELD(entity -> entity.getName() + " was blasted back by the arena forcefield.");

    private final Function<Entity, String> messageGenerator;

    CauseOfDeath(Function<Entity, String> messageGenerator) {
        this.messageGenerator = messageGenerator;
    }

    @Override
    public String getKillMessage(Entity entity) {
        return this.messageGenerator.apply(entity);
    }
}