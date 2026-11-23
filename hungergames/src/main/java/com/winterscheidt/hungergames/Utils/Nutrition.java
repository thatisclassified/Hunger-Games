package com.winterscheidt.hungergames.Utils;

public enum Nutrition {
    SATISFIED(0.3),
    HUNGRY(0.6),
    VERY_HUNGRY(0.8),
    STARVING(1),
    THIRSTY(0.6),
    VERY_THIRSTY(0.8),
    PARCHED(1);

    public static double HUNGER_DAMAGE = 0.05;
    public static double THIRST_DAMAGE = 0.06;

    private double threshold;
    private Nutrition(double threshold) {
        this.threshold = threshold;
    }

    public static Nutrition getHunger(double hunger) {
        if (hunger <= SATISFIED.threshold) {
            return SATISFIED;
        } else if (hunger <= HUNGRY.threshold) {
            return SATISFIED;
        } else if (hunger <= VERY_HUNGRY.threshold) {
            return VERY_HUNGRY;
        } else {
            return STARVING;
        }
    }
    public static Nutrition getThirst(double thirst) {
        if (thirst <= SATISFIED.threshold) {
            return SATISFIED;
        } else if (thirst <= THIRSTY.threshold) {
            return THIRSTY;
        } else if (thirst <= VERY_THIRSTY.threshold) {
            return VERY_THIRSTY;
        } else {
            return PARCHED;
        }
    }
}
