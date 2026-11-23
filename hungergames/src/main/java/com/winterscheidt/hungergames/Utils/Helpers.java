package com.winterscheidt.hungergames.Utils;

public class Helpers {
    public static double clamp (double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }
    public static double clamp (int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
    public static double lerp (double base, double dest, double amount) {
        return base + (dest - base) * amount;
    }
    public static double lerp(double base, double amount) {
        return lerp(base, 1, amount);
    }
    public static int convertPercent(double percent) {
        return (int) Math.round(percent * 100); 
    }
}
