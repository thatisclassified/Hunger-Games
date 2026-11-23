package com.winterscheidt.hungergames.Classes.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.winterscheidt.hungergames.Classes.Entities.Entity;
import com.winterscheidt.hungergames.Interfaces.GameIO;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

// Courtesy of Gemini
public class DiscordIO implements GameIO {
    private final JDA jda;
    private final TextChannel arenaChannel;
    private Game game;

    public DiscordIO(JDA jda, TextChannel arenaChannel) {
        this.jda = jda;
        this.arenaChannel = arenaChannel;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    // 1. Global broadcast -> Public Arena Channel
    @Override
    public void broadcast(String message) {
        if (arenaChannel != null && message != null && !message.isBlank()) {
            arenaChannel.sendMessage(message).queue();
        }
    }

    // 2. Single entity broadcast -> Private DM (only if human)
    @Override
    public void broadcast(String message, Entity entity) {
        if (entity == null || entity.isAI() || message == null || message.isBlank()) {
            return;
        }
        String discordId = entity.getDiscordUserId();
        if (discordId == null) {
            return;
        }

        jda.retrieveUserById(discordId).queue(
            user -> user.openPrivateChannel().queue(
                channel -> channel.sendMessage(message).queue(),
                error -> arenaChannel.sendMessage("⚠️ Could not DM " + entity.getName() + ". Please enable server DMs!").queue()
            )
        );
    }

    // 3. List of entities -> DM each human in the list
    @Override
    public void broadcast(String message, List<Entity> entities) {
        if (entities == null) return;
        for (Entity entity : entities) {
            broadcast(message, entity);
        }
    }

    // 4. Location broadcast -> DM everyone in that location
    @Override
    public void broadcast(String message, Location location) {
        broadcast(message, location, Collections.emptyList());
    }

    // 5. Location broadcast excluding 1 entity
    @Override
    public void broadcast(String message, Location location, Entity exclude) {
        List<Entity> excludedList = (exclude != null) ? List.of(exclude) : Collections.emptyList();
        broadcast(message, location, excludedList);
    }

    // 6. Location broadcast excluding a list of entities
    @Override
    public void broadcast(String message, Location location, List<Entity> exclude) {
        if (game == null || location == null) return;
        for (Entity entity : game.getEntities()) {
            if (entity.getLocation() == location && !exclude.contains(entity)) {
                broadcast(message, entity);
            }
        }
    }

    // 7. Display menu for current entity
    @Override
    public void displayMenu(List<GameAction> items) {
        if (game != null) {
            displayMenu(items, game.getCurrentEntity());
        }
    }

    // 8. Display menu with Discord Buttons in target entity's DM
    @Override
    public void displayMenu(List<GameAction> items, Entity entity) {
        if (entity == null || entity.isAI() || items == null || items.isEmpty()) {
            return;
        }
        String discordId = entity.getDiscordUserId();
        if (discordId == null) {
            return;
        }

        List<ActionRow> rows = buildButtonRows(items);

        jda.retrieveUserById(discordId).queue(
            user -> user.openPrivateChannel().queue(
                channel -> channel.sendMessage("**Choose your action:**")
                                  .setComponents(rows)
                                  .queue(),
                error -> arenaChannel.sendMessage("⚠️ Could not send turn menu to " + entity.getName() + "'s DMs!").queue()
            )
        );
    }

    // Helper: Packs up to 25 GameActions into Discord ActionRows (max 5 buttons per row)
    private List<ActionRow> buildButtonRows(List<GameAction> items) {
        List<ActionRow> rows = new ArrayList<>();
        List<Button> currentRow = new ArrayList<>();

        int limit = Math.min(items.size(), 25); // Discord hard cap of 25 buttons per message
        for (int i = 0; i < limit; i++) {
            GameAction action = items.get(i);
            String label = action.getLabel();

            Button btn = label.equals("Back")
                    ? Button.secondary("hg_act_" + i, label)
                    : Button.primary("hg_act_" + i, label);

            currentRow.add(btn);
            if (currentRow.size() == 5) {
                rows.add(ActionRow.of(currentRow));
                currentRow = new ArrayList<>();
            }
        }
        if (!currentRow.isEmpty()) {
            rows.add(ActionRow.of(currentRow));
        }
        return rows;
    }
}
}
