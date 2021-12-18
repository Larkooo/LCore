package com.larko.LCore.Discord.Commands;


import com.github.stackovernorth.jda.commandhandler.listener.CommandListener;

import com.larko.LCore.Discord.Bot;
import com.larko.LCore.Structures.Claim;
import com.larko.LCore.Structures.Home;
import com.larko.LCore.Structures.LPlayer;
import com.larko.LCore.Utils.AuthUtils;
import com.larko.LCore.Utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;


public class Players implements CommandListener {
    public Players() {}
    @Override
    public void onCommand(Member member, TextChannel textChannel, Message message, String[] args) {
        Player players[] = Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Players");
        embedBuilder.setColor(Bot.primaryColor);
        embedBuilder.setDescription(
                players.length + " connected player(s)\n" +
                "```prolog\n" +
                Arrays.stream(players).map(player -> {
                    return player.getEntityId() + ". " + player.getName();
                }).collect(Collectors.joining("\n")) +
                "```"
                );
        embedBuilder.setFooter("LCore - TPS : " + MinecraftServer.getServer().recentTps[0]);
        message.reply(embedBuilder.build()).queue();
    }
}


