package com.winterscheidt.hungergames.Classes.Game;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.winterscheidt.hungergames.Classes.Entities.Entity;
import com.winterscheidt.hungergames.Classes.Entities.Player;
import com.winterscheidt.hungergames.Classes.Items.Item;
import com.winterscheidt.hungergames.Interfaces.Attainable;
import com.winterscheidt.hungergames.Interfaces.Equippable;
import com.winterscheidt.hungergames.Interfaces.GameIO;
import com.winterscheidt.hungergames.Interfaces.Interactable;
import com.winterscheidt.hungergames.Interfaces.Usable;
import com.winterscheidt.hungergames.Utils.DetectionLevel;
import com.winterscheidt.hungergames.Utils.Helpers;

public class Game implements Serializable {
    private List<Location> locations = new ArrayList<>();
    private List<Entity> entities = new ArrayList<>();
    private transient List<GameAction> currentMenu;
    private Entity currentEntity;
    private int currentEntityIndex;
    private transient GameIO io = ConsoleIO.io;
    private int remainingTributes;
    private boolean gameOver = false;

    // Getters
    public void openMenu(List<GameAction> menu) {
        this.currentMenu = menu;
    }
    public List<Entity> getEntities() {
        return this.entities;
    }
    public List<Location> getLocations() {
        return this.locations;
    }
    public Entity getCurrentEntity() {
        return this.currentEntity;
    }
    public GameIO getIO() {
        return this.io;
    }
    public boolean isGameOver() {
        return this.gameOver;
    }

    // Setters
    public void setRemainingTributes(int tributes) {
        this.remainingTributes = tributes;
    }
    public void reduceTributes() {
        this.remainingTributes--;
    }
    public void setIO(GameIO io) {
        this.io = io;
    }

    // Constructors
    public Game(GameIO io) {
        this.io = io;
        ArenaBuilder.build(this);
        if (this.io instanceof DiscordIO) {
            ((DiscordIO) this.io).setGame(this);
        }
    }

    // TURN MANAGERS
    public void startTurn(int index) {
        currentEntityIndex = index;
        while (!this.gameOver) {
            currentEntity = entities.get(currentEntityIndex);
            currentEntity.turnStart(this);
            if (currentEntity.getHealth() > 0) {
                openMenu(getMainActions(currentEntity));

                if (currentEntity.isAI()) {
                    startChoice();
                } else {            
                    io.broadcast("It's your turn, " + currentEntity.getName(), currentEntity);
                    io.broadcast("You have " + Helpers.convertPercent(currentEntity.getHealth()) + 
                    " health.", currentEntity);
                    startChoice();
                    return;
                }
            }
            if (remainingTributes <= 1) {
                gameOver = true;
            }
        }
    }

    public void runAction(int index) {
        runAction(currentMenu.get(index));
    }
    public void runAction(GameAction action) {
        action.execute();
        if (action.opensSubMenu()) {
            startChoice();
        } else if (!action.endsTurn()) {
            openMenu(getMainActions(currentEntity));
            startChoice();
        } else {
            endTurn();
        }
    }

    public void startChoice() {
        if (currentEntity.isAI()) {
            GameAction choice = currentEntity.chooseAction(currentMenu, this);
            runAction(choice);
        } else {
            io.displayMenu(currentMenu, currentEntity);
        }
    }

    public void endTurn() {
        currentEntity.turnEnd(this);
        currentEntityIndex = (currentEntityIndex + 1) % entities.size();
        if (!currentEntity.isAI()) {
            startTurn(currentEntityIndex);
        }
    }


    // MENU DEFINITIONS

    public List<GameAction> getMoveActions(Entity entity) {
        List<GameAction> actions = new ArrayList<>();
        for (String locName : entity.getLocation().getConnections().keySet()) {
            actions.add(new GameAction("Move " + locName, false, false,
            () -> {
                entity.move(locName);
                io.broadcast("Moved " + locName + ".", entity);
            }));
        }
        actions.add(new GameAction("Back", false, true, () -> openMenu(getMainActions(entity))));
        return actions;
    }

    public List<GameAction> getInspectActions(Player entity) {
        List<GameAction> actions = new ArrayList<>();
        if (!entity.isAI()) {
            for (Item item : entity.getLocation().getItems()) {
                actions.add(new GameAction("Inspect " + item.getName(), false, false, 
                () -> { 
                    io.broadcast(item.getDescription(), entity);
                }));
            }
        }
        actions.add(new GameAction("Back", false, true, () -> openMenu(getMainActions(entity))));
        return actions;
    }

    public List<GameAction> getInteractActions(Player entity) {
        List<GameAction> actions = new ArrayList<>();
        for (Item item : entity.getLocation().getItems()) {
            if (item instanceof Attainable) {
                actions.add(new GameAction("Pick up " + item.getName(), true, false, () -> {
                    entity.takeItem(item);
                    io.broadcast("Took " + item.getName() + ".", entity);
                }));
            }
            if (item instanceof Interactable) {
                actions.add(new GameAction("Interact with " + item.getName(), true, false, () -> {
                ((Interactable) item).interact(entity, this);
                io.broadcast("Interacted with " + item.getName(), entity);
            }));
            }
        }
        actions.add(new GameAction("Back", false, true, () -> openMenu(getMainActions(entity))));
        return actions;
    }

    public List<GameAction> getInventoryActions(Player entity) {
        // This is empty until I add a second item:
        // io.broadcast("ITEMS: " + entity.getInventory().getItems());
        List<GameAction> actions = new ArrayList<>();
        if (entity.getEquipped() != null && entity.getInventory().getSize() < entity.getInventory().getMaxSize()) {
            actions.add(new GameAction("Unequip " + entity.getEquipped().getName(), false, false,
            () -> { 
                String itemName = entity.getEquipped().getName();
                if (entity.unequipItem()) { 
                    io.broadcast(itemName + " unequipped.", entity); 
                }
            }));
        }
        for (Item item : entity.getInventory().getItems()) {
            if (item instanceof Equippable) {
                actions.add(new GameAction("Equip " + item.getName(), false, false,
                () -> { 
                    if (entity.equipItem(item)) {
                        io.broadcast(item.getName() + " equipped.", entity); 
                    }
                }));
            }
            if (item instanceof Usable) {
                actions.add(new GameAction("Use " + item.getName(), false, false,
                () -> { 
                    if (entity.useItem(item, this)) {
                        io.broadcast("Used " + item.getName(), entity);
                    }
                }));
            }
        }
        actions.add(new GameAction("Back", false, true, () -> openMenu(getMainActions(entity))));
        return actions;
    }

    public List<GameAction> getEntityActions(Entity entity) {
        List<GameAction> actions = new ArrayList<>();
        Location entLoc = entity.getLocation();
        for (Entity ent2 : entities) {
            // Check for same location
            if (ent2 != entity && ent2.getLocation() == entLoc) {
                DetectionLevel detection = entity.getDetection(ent2);
            
                actions.add(new GameAction("Attack " + (detection == DetectionLevel.RECOGNIZED ?
                ent2.getName() : "Obscured Figure"), true, false, () -> {
                    io.broadcast("You attack " + ent2.getName(), entity);

                    // Is there a weapon?
                    String with = "";
                    if (entity instanceof Player) {
                        if (((Player) entity).getEquipped() != null) {
                            with = " with a " + ((Player) entity).getEquipped().getName();
                        }
                    }

                    // Broadcast to everyone in the area, besides the attacker and victim
                    io.broadcast(entity.getName() + " attacks you" + with + "!", ent2);
                    List<Entity> exclude = new ArrayList<Entity>();
                    exclude.add(entity);
                    exclude.add(ent2);
                    io.broadcast(entity.getName() + " attacks " + ent2.getName() + with + ".", exclude);
                    double damage = entity.attack(ent2, this);

                    if (damage == 0) {
                        io.broadcast("The attack misses!", entity.getLocation(), ent2);
                        io.broadcast("You were able to dodge!", ent2);
                    } else {
                        io.broadcast("You dealt " + Helpers.convertPercent(damage) + " damage.", entity);
                        io.broadcast(entity.getGender().capSubject() + " dealt " + 
                        Helpers.convertPercent(damage) + " damage to you.", ent2);
                        io.broadcast("The attack lands.", entity.getLocation(), exclude);
                    }
                }));
                // Might add some more actions later, ie, talk()
            }
        }
        actions.add(new GameAction("Back", false, true, () -> openMenu(getMainActions(entity))));
        return actions;
    }

    public List<GameAction> getMainActions(int index) {
        return getMainActions(this.entities.get(index));
    }
    public List<GameAction> getMainActions(Entity entity) {
        List<GameAction> actions = new ArrayList<>();
        Player player = entity instanceof Player ? (Player) entity : null;

        // LOOK AROUND
        if (player != null && !entity.isAI()) {
            actions.add(new GameAction("Look Around", false, false,
            () -> { 
                io.broadcast(player.getLocation().getFullDescription(this, entity), entity); 
                openMenu(getMainActions(entity));
            }));
        }

        // MOVE
        if (entity.canMove()) {
            actions.add(new GameAction("Move...", false, true,
            () -> openMenu(getMoveActions(entity))));
        }

        // INSPECT
        if (player != null) { 
            if (!entity.isAI()) {
                actions.add(new GameAction("Inspect...", false, true,
                () -> openMenu(getInspectActions(player))));
            }
            actions.add(new GameAction("Pick Up / Interact...", false,  true,
            () -> openMenu(getInteractActions(player))));
            actions.add(new GameAction("Use / Equip...", false, true,
            () -> openMenu(getInventoryActions(player))));
        }
            
        // ATTACK
        actions.add(new GameAction("Attack...", false, true,
        () -> openMenu(getEntityActions(entity))));
        
        // REST
        actions.add(new GameAction("Rest", true, false, () -> {
            entity.lerpObscurity(0.2);
            entity.addHealth(0.05);
            if (player != null) {
                player.addHunger(-0.02);
                player.addThirst(-0.02);
            }
            io.broadcast("You were able to heal up by resting awhile.");
        }));

        // HIDE
        actions.add(new GameAction("Hide", true, false,
        () -> {
            entity.lerpObscurity(0.6);
            io.broadcast("Hidden.", entity);
        }));

        return actions;
    }
}
