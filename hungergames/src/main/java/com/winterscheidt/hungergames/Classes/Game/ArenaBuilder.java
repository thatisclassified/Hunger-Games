package com.winterscheidt.hungergames.Classes.Game;

import java.util.List;
import com.winterscheidt.hungergames.Classes.Entities.Entity;
import com.winterscheidt.hungergames.Classes.Entities.Player;
import com.winterscheidt.hungergames.Classes.Items.Edible;
import com.winterscheidt.hungergames.Classes.Items.Weapon;

public class ArenaBuilder {
    public static void build(Game game) {
        buildTest(game);
    }

    /* Welcome to the 53rd Annual Hunger Games! */
    public static void build53(Game game) {

    }

    // NOTE: This constructor only intended for ConsoleIO!!!
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
        Player Katniss = new Player("Katniss", "Brave", false, cornucopia);
        entities.add(Cato);
        entities.add(Rue);
        entities.add(Katniss);

        game.setRemainingTributes(entities.size());
    }
}
