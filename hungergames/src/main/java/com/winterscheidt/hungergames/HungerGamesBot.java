package com.winterscheidt.hungergames;

import com.winterscheidt.hungergames.Classes.Entities.Entity;
import com.winterscheidt.hungergames.Classes.Entities.Player;
import com.winterscheidt.hungergames.Classes.Game.DiscordIO;
import com.winterscheidt.hungergames.Classes.Game.Game;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

// Courtesy of Gemini
public class HungerGamesBot extends ListenerAdapter {
    private Game activeGame;
    private DiscordIO discordIO;

    public static void main(String[] args) throws InterruptedException {
        // Store your bot token in an environment variable or paste it here temporarily for local testing
        String token = System.getenv("DISCORD_BOT_TOKEN");
        if (token == null || token.isBlank()) {
            token = "YOUR_BOT_TOKEN_HERE";
        }

        HungerGamesBot bot = new HungerGamesBot();

        JDA jda = JDABuilder.createDefault(token)
                .addEventListeners(bot)
                .build();

        jda.awaitReady();

        // Register slash commands globally (may take a few minutes to propagate the very first time)
        jda.updateCommands().addCommands(
                Commands.slash("hg-create", "Create a new Hunger Games lobby in this channel"),
                Commands.slash("hg-join", "Join the active Hunger Games lobby as a tribute")
                        .addOption(OptionType.STRING, "name", "Custom tribute name (defaults to your Discord name)", false),
                Commands.slash("hg-start", "Start the Hunger Games match!")
        ).queue();

        System.out.println("Hunger Games Bot is online and ready!");
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        if (command.equals("hg-create")) {
            if (!(event.getChannel() instanceof TextChannel channel)) {
                event.reply("Please run `/hg-create` inside a server text channel!").setEphemeral(true).queue();
                return;
            }

            this.discordIO = new DiscordIO(event.getJDA(), channel);
            this.activeGame = new Game(discordIO);
            this.discordIO.setGame(activeGame);

            event.reply("🏹 **A new Hunger Games lobby has opened!** Type `/hg-join` to enter the arena, then `/hg-start` when everyone is ready!").queue();

        } else if (command.equals("hg-join")) {
            if (activeGame == null || activeGame.isGameOver()) {
                event.reply("No active lobby right now. Run `/hg-create` first!").setEphemeral(true).queue();
                return;
            }

            User user = event.getUser();
            String userId = user.getId();

            // Prevent joining twice
            for (Entity e : activeGame.getEntities()) {
                if (userId.equals(e.getDiscordUserID())) {
                    event.reply("You are already registered as **" + e.getName() + "**!").setEphemeral(true).queue();
                    return;
                }
            }

            // Find an open AI Player slot to claim
            Player openSlot = null;
            for (Entity e : activeGame.getEntities()) {
                if (e instanceof Player p && p.isAI()) {
                    openSlot = p;
                    break;
                }
            }

            if (openSlot == null) {
                event.reply("Sorry, all tribute slots in this arena are full!").setEphemeral(true).queue();
                return;
            }

            String customName = event.getOption("name") != null
                    ? event.getOption("name").getAsString()
                    : user.getEffectiveName();

            openSlot.setName(customName);
            openSlot.setAI(false);
            openSlot.setDiscordUserID(userId);

            // Verify DMs work before confirming
            user.openPrivateChannel().queue(
                dm -> dm.sendMessage("🔥 You have volunteered as **" + customName + "**! Your turn menus will appear here.").queue(),
                err -> event.getChannel().sendMessage("⚠️ <@" + userId + ">, I couldn't DM you! Please enable Direct Messages from server members before `/hg-start`.").queue()
            );

            event.reply("✅ **" + customName + "** (<@" + userId + ">) has entered the arena!").queue();

        } else if (command.equals("hg-start")) {
            if (activeGame == null) {
                event.reply("No lobby found! Run `/hg-create` first.").setEphemeral(true).queue();
                return;
            }

            event.reply("🎺 **May the odds be ever in your favor! The Games have begun!**").queue();
            activeGame.startTurn(0);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        if (!buttonId.startsWith("hg_act_")) {
            return;
        }

        if (activeGame == null || activeGame.isGameOver()) {
            event.reply("This match has already ended.").setEphemeral(true).queue();
            return;
        }

        Entity current = activeGame.getCurrentEntity();
        String clickerId = event.getUser().getId();

        // Security check: ensure only the active player can trigger their turn
        if (current == null || !clickerId.equals(current.getDiscordUserID())) {
            event.reply("It's not your turn right now!").setEphemeral(true).queue();
            return;
        }

        int actionIndex = Integer.parseInt(buttonId.substring("hg_act_".length()));

        // Immediately remove buttons from the old message so the user can't double-click
        event.editComponents().queue();

        try {
            activeGame.runAction(actionIndex);
        } catch (Exception e) {
            e.printStackTrace();
            event.getHook().sendMessage("⚠️ An error occurred processing that action. Please try again.").setEphemeral(true).queue();
        }
    }
}