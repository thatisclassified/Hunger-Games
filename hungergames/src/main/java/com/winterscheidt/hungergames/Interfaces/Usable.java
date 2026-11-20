package com.winterscheidt.hungergames.Interfaces;

import com.winterscheidt.hungergames.Classes.Entities.Player;
import com.winterscheidt.hungergames.Classes.Game.Game;

public interface Usable {
    void use(Player player, Game game); // Is a game pass necessary here?
}
