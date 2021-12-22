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
import org.json.simple.JSONObject;

import java.util.Arrays;


public class Exec implements CommandListener {
    public Exec() {}
    @Override
    public void onCommand(Member member, TextChannel textChannel, Message message, String[] args) {
        if (args.length < 1) {
            message.reply("Please specify the command you'd like to execute on the server").queue();
            return;
        }
        String command = args[0];
        String userId = member.getId();

        LPlayer lplayer = LPlayer.findByDiscord(userId);
        if (lplayer == null) {
            message.reply("You haven't linked your minecraft LCore account yet, please link it by running `" + Utilities.config.getString("bot_prefix") + "link`").queue();
            return;
        }
        OfflinePlayer player = Bukkit.getPlayer(lplayer.getUuid());
        if (!player.isOp()) {
            message.reply("You need to be **OP** to use this command").queue();
            return;
        }
        long start = System.currentTimeMillis();

        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("LCore"), () ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.join(" ", args)));
        long finish = System.currentTimeMillis();
        message.reply("Executed command `" + command + "` in `" + (finish - start) + "ms`").queue();
    }
}


