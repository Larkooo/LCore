package com.larko.LCore.Discord.Commands;


import com.github.stackovernorth.jda.commandhandler.listener.CommandListener;

import com.larko.LCore.Structures.Claim;
import com.larko.LCore.Structures.Home;
import com.larko.LCore.Structures.LPlayer;
import com.larko.LCore.Utils.AuthUtils;
import com.larko.LCore.Utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


public class Players implements CommandListener {
    public Players() {}
    @Override
    public void onCommand(Member member, TextChannel textChannel, Message message, String[] args) {
        ArrayList<LPlayer> players = LPlayer.getPlayers();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Players");
        embedBuilder.setDescription(
                connectedPlayers.length + " connected players\n" +
                "```" +
                Arrays.stream(connectedPlayers).map(player -> {
                return player.getEntityId() + ". " + player.getName()  + "\n";
                }) +
                "```"
                );
        embedBuilder.setFooter("LCore");
        message.reply(embedBuilder.build()).queue();
    }
}


