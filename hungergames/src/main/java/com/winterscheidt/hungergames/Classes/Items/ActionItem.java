package com.winterscheidt.hungergames.Classes.Items;

import com.winterscheidt.hungergames.Classes.Entities.Player;
import com.winterscheidt.hungergames.Classes.Game.Game;
import com.winterscheidt.hungergames.Interfaces.Interactable;
import com.winterscheidt.hungergames.Interfaces.ItemEffect;
import com.winterscheidt.hungergames.Interfaces.Killer;

public class ActionItem extends Item implements Interactable, Killer {
    private ItemEffect onInteract;

    public void interact(Player player, Game game) {
        if (this.onInteract != null) {
            this.onInteract.apply(player, game);
        }
    }
}
