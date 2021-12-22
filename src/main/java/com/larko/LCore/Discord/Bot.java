package com.larko.LCore.Discord;

import com.github.stackovernorth.jda.commandhandler.api.command.CommandBuilder;
import com.github.stackovernorth.jda.commandhandler.api.handler.CommandHandler;
import com.github.stackovernorth.jda.commandhandler.api.handler.CommandHandlerBuilder;
import com.larko.LCore.Discord.Commands.*;
import com.larko.LCore.Structures.Claim;
import com.larko.LCore.Utils.Utilities;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Bot {
    public static Color primaryColor = new Color(236, 115, 248);
    public static Timer activityUpdateTimer;
    private static JDA bot;
    private String prefix;

    public Bot() {
        try {
            final String token = Utilities.config.getString("bot_token");
            if (token == Utilities.tokenConfigPlaceholder) return;
            // get prefix from config
            prefix = Utilities.config.getString("bot_prefix");
            JDABuilder builder = JDABuilder.createDefault(token);

            final String activityTitle = Utilities.config.getString("activity_title");

            if (activityTitle != null) {
                builder.setActivity(Activity.playing(activityTitle));
            } else {
                builder.setActivity(Activity.watching(Bukkit.getServer().getOnlinePlayers().size() + " players"));
            };
            builder.addEventListeners(new Events());

            bot = builder.build();

            CommandHandler commandHandler = new CommandHandlerBuilder(bot)
                    .setPrefix(prefix)
                    .build();

            commandHandler.addCommand(
                    new CommandBuilder("link", new Link())
                            .setAlias("connect")
                            .setDescription("Link your minecraft LCore account to your discord")
                            .build()
            );

            commandHandler.addCommand(
                    new CommandBuilder("unlink", new Unlink())
                            .setAlias("disconnect")
                            .setDescription("Disconnect the minecraft LCore account linked to your discord")
                            .build()
            );

            commandHandler.addCommand(
                    new CommandBuilder("me", new Me())
                            .setAlias("profile")
                            .setDescription("Get information about your minecraft LCore account")
                            .build()
            );

            commandHandler.addCommand(
                    new CommandBuilder("homes", new Homes())
                            .setDescription("Get the claims linked to your minecraft LCore account")
                            .build()
            );

            commandHandler.addCommand(
                    new CommandBuilder("claims", new Claims())
                            .setDescription("Get the homes linked to your minecraft LCore account")
                            .build()
            );
            commandHandler.addCommand(
                    new CommandBuilder("exec", new Exec())
                            .setDescription("Execute a command on the server")
                            .build()
            );
            commandHandler.addCommand(
                    new CommandBuilder("players", new Players())
                            .setDescription("Retrieve server player list")
                            .build()
            );
            commandHandler.addCommand(
                    new CommandBuilder("reload", new Reload())
                            .setDescription("Reload the server")
                            .build()
            );
            commandHandler.addCommand(
                    new CommandBuilder("restart", new Restart())
                            .setDescription("Restart the server")
                            .build()
            );
            commandHandler.addCommand(
                    new CommandBuilder("info", new Info())
                            .setDescription("General info")
                            .build()
            );

            // Background tasks. ex. refresh player count
            if (bot != null && Utilities.config.getString("activity_title") == null) {
                TimerTask task = new TimerTask(){
                    public void run(){
                        JDA bot = Bot.getInstance();

                        bot.getPresence().setActivity(Activity.watching(Bukkit.getOnlinePlayers().size() + " players"));
                    }
                };

                activityUpdateTimer = new Timer();
                // The delay period is calculated in milliseconds iirc
                // refresh every 15 seconds (discord ratelimit)
                activityUpdateTimer.schedule(task, 0, 15*1000);
            }
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static JDA getInstance() {
        return bot;
    }
}
