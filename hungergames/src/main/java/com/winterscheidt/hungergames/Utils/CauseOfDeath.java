package com.winterscheidt.hungergames.Utils;

import com.winterscheidt.hungergames.Classes.Entities.Entity;
import com.winterscheidt.hungergames.Interfaces.EntityTextFunction;
import com.winterscheidt.hungergames.Interfaces.Killer;

import java.util.function.Function;

public enum CauseOfDeath implements Killer {
    HUNGER(entity -> entity.getName() + " starved to death.",
           entity -> {
            Gender g = entity.getGender();
            return g.capSubject() + " lies curled in a ball. " +
            g.capObject() + " looks very thin, " + g.possessive() +
            " skin clinging loosely to " + g.possessive() +
            " frame. " + g.possessive() + " gaunt face is twisted in pain.";
           }),
    THIRST(entity -> entity.getName() + " succumbed to dehydration.",
            entity -> {
                Gender g = entity.getGender();
                return g.capSubject() + " lies on the ground, " +
                g.possessive() + " lips cracked and dry. ";
            }),
    POISON(entity -> entity.getName() + " collapsed from lethal poison.", 
            entity -> {
                Gender g = entity.getGender();
                return g.capSubject() + " lies on the ground, " +
                g.possessive() + " face tinted a shade of purple. " +
                g.possessive() + " eyes stare ahead blankly; " + g.object() +
                " seems to have been caught completely by surprise.";
            }),
    FORCEFIELD(entity -> entity.getName() + " was blasted back by the arena forcefield.",
            entity -> {
                Gender g = entity.getGender();
                return g.capSubject() + " lies on the ground, " +
                g.possessive() + " body charred and twisted. Guess " +
                g.object() + " got too close to the forcefield...";
            });

    private final EntityTextFunction messageGenerator;
    private final EntityTextFunction descriptionGenerator;

    CauseOfDeath(EntityTextFunction messageGenerator, EntityTextFunction descriptionGenerator) {
        this.messageGenerator = messageGenerator;
        this.descriptionGenerator = descriptionGenerator;
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