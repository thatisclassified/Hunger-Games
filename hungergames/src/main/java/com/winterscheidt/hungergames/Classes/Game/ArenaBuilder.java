package com.winterscheidt.hungergames.Classes.Game;

import java.util.ArrayList;
import java.util.List;
import com.winterscheidt.hungergames.Classes.Entities.Entity;
import com.winterscheidt.hungergames.Classes.Entities.Player;
import com.winterscheidt.hungergames.Classes.Items.ActionItem;
import com.winterscheidt.hungergames.Classes.Items.Container;
import com.winterscheidt.hungergames.Classes.Items.Edible;
import com.winterscheidt.hungergames.Classes.Items.Item;
import com.winterscheidt.hungergames.Classes.Items.Weapon;
import com.winterscheidt.hungergames.Utils.CauseOfDeath;
import com.winterscheidt.hungergames.Utils.Inventory;

public class ArenaBuilder {
    public static void build(Game game) {
        buildDemo(game);
    }

    /* Welcome to the 53rd Annual Hunger Games! */
    public static void build53(Game game) {

    }

    // ALL DEMO CODE
    private static Weapon makeSword() {
        return new Weapon("Sword", "It has a firm hilt and a long, deadly blade.", 
        0.25, 0.7, 12, entity -> entity.getName() + " was stabbed to death.", 
        entity -> entity.getName() + " rests on the ground with many cuts and stab wounds.");
    }
    private static Weapon makeBow() {
        return new Weapon("Bow", "It's made of smooth, curved oak. The string is tight, powerful.",
            0.20, 0.6, 10, entity -> entity.getName() + " was shot with an arrow.", 
            entity -> entity.getGender().capSubject() + " lies in the grass. An arrow protrudes from " + entity.getGender().possessive() +
            " chest, a deep red stain around it.");
    }
    private static Weapon makeSpear() {
        return new Weapon("Spear", "A long, wooden shaft with a sharp metal tip.",
            0.18, 0.8, 15, entity -> entity.getName() + " was impaled on a spear.", 
            entity -> entity.getGender().capSubject() + " lies on " + entity.getGender().possessive() + " side, bearing a large gash wound.");
    }
    private static Weapon makeKnife() {
        return new Weapon("Knife", "A small, sharp blade with a sturdy handle.",
            0.15, 0.95, 10, entity -> entity.getName() + " was slashed to death.", 
            entity -> entity.getGender().capSubject() + " lies on the ground with multiple shallow cuts.");
    }
    private static Weapon makeAxe() {
        return new Weapon("Axe", "A heavy, double-bladed axe with a wooden handle.",
            0.32, 0.45, 17, entity -> entity.getName() + " was hacked to death.", 
            entity -> entity.getGender().capPossessive() + " body lies chopped and mangled, a large portion of " +
            "the grass stained red.");
    }
    private static Edible makeBread() {
        return new Edible("Bread", "It looks a little dry, but otherwise good.", 1, 20, -7, 15);
    }
    private static Edible makeBottle() {
        return new Edible("Purified Water", "The bottle looks delicate, but the water inside is crystal clear.", 1, 1, 20, 10);
    }
    private static Container makeBackpack() {
        Inventory contents = new Inventory(List.of(makeBread(), makeBottle()));
        contents.addItem(Math.random() >= 0.5 ? makeBread() : makeBottle());
        if (Math.random() >= 0.5) {
            contents.addItem(makeKnife());
        }
        return new Container("Backpack", "A small white backpack.", contents, true);
    }
    private static Edible makeGoodBerry() {
        return new Edible("Berries", "They're oval-shaped, with a deep red color.", (int) (Math.random() * 4 + 1), 10, 5, 5);
    }
    private static Edible makePoisonBerry() {
        return new Edible("Berries", "They're small and round, with a dark purple color.", (int) (Math.random() * 4 + 1), -5, 2, -30);
    }
    private static Edible makeBerry() {
        return Math.random() >= 0.5 ? makeGoodBerry() : makePoisonBerry();
    }
    public static void buildDemo(Game game) {
        // LOCATIONS
        List<Location> locs = game.getLocations();
        Location corn = new Location("Cornucopia", "In the midst of the trees lies a small clearing. Atop a bed of rotting leaves, " +
            "the silver Cornucopia gleams in the sunlight.", 0.0, List.of( makeKnife(), makeSpear(), makeSpear(),
             makeAxe(),  makeAxe(), makeBow(), makeBow(), makeBackpack(), makeBackpack(), makeBackpack(), makeBackpack(), makeBread(), makeBread(),
             makeBottle(), makeBottle())
        );

        Location creek = new Location("Creek", "Where the trees thin out a little, a gentle creek bubbles through the arena.",
        0.15, List.of(makeBerry(), makeBerry()));
        ActionItem creekItem = new ActionItem("Creek", "The water is a bit low, and probably not the cleanest. But--it's water.", 
        (entity, l_game) -> {
            if (entity instanceof Player) {
                l_game.getIO().broadcast("You drink some of the creek water.", entity);
                ((Player) entity).addThirst(-13);
                ((Player) entity).addHealth((-3), game, CauseOfDeath.POISON);
            }
        });
        creek.addDoubleConnection("to the Cornucopia.", corn, "toward the sound of running water.");

        Location cave = new Location("Cave", "The cave is damp and cold--but it's dark, and it's hidden.", 0.75, 
            List.of(makeBread()));

        Location woods = new Location("Woods", "The wind rustles through an army of mahogany and oak trees. " +
        "Dry leaves litter the ground, and in the distance lies the dark opening to a cave.", 0.25, List.of(makeBerry(), makeBerry(), 
        makeBerry(), makeBottle()));
        woods.addDoubleConnection("towards the sound of running water.", creek, "into the woods.");
        woods.addDoubleConnection("to the Cornucopia.", corn, "into the woods.");
        woods.addDoubleConnection("into the cave.", cave, "back into the woods.");

        Location dense = new Location("Dense Woods", "The trees grow thick here, and everything looks the smae. It'd be easy to get " +
            "lost--but just as easy to hide.", 0.6, List.of(Math.random() >= 0.5 ? makeSword() : makeBow(), 
            makeBerry(), makeBerry(), makeBerry()));
        dense.addDoubleConnection("towards the creek.", creek, "into the deep woods.");
        dense.addDoubleConnection("towards the Cornucopia.", corn, "into the deep woods.");

        Location briars = new Location("Briar Woods", "The floor here is coated in thorn bushes and brambles. It looks like many things " +
            " have been lost here before...", 0.3, List.of(makeAxe(), makeBackpack()));
        for (int i = 0; i < 7; i++) {
            if (Math.random() >= 0.65) {
                briars.addItem(makeGoodBerry());
            } else {
                briars.addItem(makePoisonBerry());
            }
        }
        briars.addDoubleConnection("deeper into the woods.", dense, "to a different part of the woods.");
        briars.addDoubleConnection("towards the Cornucopia.", corn, "into some thickets.");

        Location grove = new Location("Flower Grove", "The trees grow sparser here, and the green ground is covered in " +
            "bright flowers and berries.", 0.1, List.of(makeGoodBerry(), makeGoodBerry(), makeGoodBerry()));
        grove.addDoubleConnection("back towards the creek.", creek, "to a flowery grove.");
        grove.addDoubleConnection("into the woods", woods, "to a flowery grove.");

        // ENTITIES
        for (int i = 1; i <= 12; i++) {
            game.getEntities().add(new Player(i, corn));
        }
        game.setRemainingTributes(game.getEntities().size());
    }

    public static void buildTest(Game game) {
        // ITEMS & WEAPONS
        Weapon sword = new Weapon("Sword", "Very Sharp", 0.15, 0.7, 12,
            entity -> entity.getName() + " was stabbed to death.", entity -> entity.getName() + " lies on the ground with many stab wounds."
        );
        Weapon sword2 = new Weapon("Sword", "Very Sharp", 0.15, 0.7, 12,
            entity -> entity.getName() + " was stabbed to death.", entity -> entity.getName() + " lies on the ground with many stab wounds."
        );
        Weapon sword3 = new Weapon("Sword", "Very Sharp", 0.15, 0.7, 12,
            entity -> entity.getName() + " was stabbed to death.", entity -> entity.getName() + " lies on the ground with many stab wounds."
        );

        // Ultimately, these should probably use a static helper function; "newBerry()"
        Edible berry = new Edible("Berry", "Round and Red", 1, 0.1, 0.1, 0.2);
        Edible berry2 = new Edible("Berry", "Round and Red", 1, 0.1, 0.1, 0.2);
        Edible berry3 = new Edible("Berry", "Round and Red", 1, 0.1, 0.1, 0.2);
        Edible berry4 = new Edible("Berry", "Round and Red", 1, 0.1, 0.1, 0.2);

        // LOCATIONS
        List<Location> locations = game.getLocations();
        Location cornucopia = new Location("Cornucopia", "A lush field with some packs and weapons in the middle.");
        cornucopia.addItem(sword);
        cornucopia.addItem(sword2);
        cornucopia.addItem(berry);
        locations.add(cornucopia);
        Location field = new Location("Field", "A green grassy field with dead flowers.");
        field.addItem(berry2);
        field.addItem(berry3);
        field.addItem(berry4);
        field.addItem(sword3);
        locations.add(field);
        cornucopia.addDoubleConnection(" to the field", field, " to the cornucopia");

        // ENTITIES
        List<Entity> entities = game.getEntities();
        Player Cato = new Player("Cato", "Muscular", true, cornucopia);
        Player Rue = new Player("Rue", "Small", true, field);
        Player Katniss = new Player("Katniss", "Brave", true, cornucopia);
        entities.add(Cato);
        entities.add(Rue);
        entities.add(Katniss);

        game.setRemainingTributes(entities.size());
    }
}
