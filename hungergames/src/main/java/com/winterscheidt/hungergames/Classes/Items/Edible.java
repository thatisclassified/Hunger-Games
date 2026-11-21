package com.winterscheidt.hungergames.Classes.Items;

import com.winterscheidt.hungergames.Classes.Entities.Player;
import com.winterscheidt.hungergames.Classes.Game.Game;
import com.winterscheidt.hungergames.Interfaces.Attainable;
import com.winterscheidt.hungergames.Interfaces.Usable;

public class Edible extends Item implements Usable, Attainable {
    private int uses;
    private double food;
    private double water;

    // Method
    public void use(Player player, Game game) {
        if (this.uses > 0) {
            player.addHunger(-this.food);
            player.addThirst(-this.water);
            this.uses--;
        }
    }
}
