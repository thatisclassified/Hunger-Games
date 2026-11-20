package com.winterscheidt.hungergames.Interfaces;

import com.winterscheidt.hungergames.Classes.Entities.Player;
import com.winterscheidt.hungergames.Classes.Game.Game;

@FunctionalInterface
public interface ItemEffect {
    void apply(Player player, Game game);
}
