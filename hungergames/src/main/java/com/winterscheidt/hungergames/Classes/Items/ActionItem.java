package com.winterscheidt.hungergames.Classes.Items;

import com.winterscheidt.hungergames.Classes.Entities.Player;
import com.winterscheidt.hungergames.Classes.Game.Game;
import com.winterscheidt.hungergames.Interfaces.ItemEffect;

public class ActionItem extends Item {
    private ItemEffect onInteract;

    @Override 
    public void interact(Player player, Game game) {
        if (this.onInteract != null) {
            this.onInteract.apply(player, game);
        }
    }
}
