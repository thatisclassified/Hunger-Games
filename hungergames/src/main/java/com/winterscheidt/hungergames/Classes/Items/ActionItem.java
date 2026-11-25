package com.winterscheidt.hungergames.Classes.Items;

import java.util.function.Function;

import com.winterscheidt.hungergames.Classes.Entities.Entity;
import com.winterscheidt.hungergames.Classes.Entities.Player;
import com.winterscheidt.hungergames.Classes.Game.Game;
import com.winterscheidt.hungergames.Interfaces.EntityTextFunction;
import com.winterscheidt.hungergames.Interfaces.Interactable;
import com.winterscheidt.hungergames.Interfaces.ItemEffect;
import com.winterscheidt.hungergames.Interfaces.Killer;

public class ActionItem extends Item implements Interactable, Killer {
    private ItemEffect onInteract;
    private EntityTextFunction messageGenerator;
    private EntityTextFunction descriptionGenerator;

    public ActionItem(String name, String description, ItemEffect onInteract) {
        this(name, description, onInteract, entity -> "", entity -> "");
    }
    public ActionItem(String name, String description, ItemEffect onInteract,
        EntityTextFunction messageGenerator,
        EntityTextFunction descriptionGenerator
    ) {
        super(name, description);
        this.onInteract = onInteract;
        this.messageGenerator = messageGenerator;
        this.descriptionGenerator = descriptionGenerator;
    }

    public void interact(Player player, Game game) {
        if (this.onInteract != null) {
            this.onInteract.apply(player, game);
        }
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
