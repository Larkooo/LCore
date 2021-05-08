package com.larko.LCore.Discord;

import com.github.stackovernorth.jda.commandhandler.api.command.CommandBuilder;
import com.github.stackovernorth.jda.commandhandler.api.handler.CommandHandler;
import com.github.stackovernorth.jda.commandhandler.api.handler.CommandHandlerBuilder;
import com.larko.LCore.Discord.Commands.Claims;
import com.larko.LCore.Discord.Commands.Homes;
import com.larko.LCore.Discord.Commands.Link;
import com.larko.LCore.Structures.Claim;
import com.larko.LCore.Utils.Utilities;
import com.sun.javaws.exceptions.MissingFieldException;
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
import java.util.Timer;
import java.util.TimerTask;

public class Bot {
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
                    new CommandBuilder("homes", new Homes())
                            .setDescription("Get the claims linked to your minecraft LCore account")
                            .build()
            );

            commandHandler.addCommand(
                    new CommandBuilder("claims", new Claims())
                            .setDescription("Get the homes linked to your minecraft LCore account")
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

                Timer timer = new Timer();
                // The delay period is calculated in milliseconds iirc
                // refresh every 15 seconds (discord ratelimit)
                timer.schedule(task, 0, 15*1000);
            }
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static JDA getInstance() {
        return bot;
    }
}
