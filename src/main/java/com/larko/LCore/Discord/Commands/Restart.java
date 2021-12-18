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


public class Restart implements CommandListener {
    public Restart() {}
    @Override
    public void onCommand(Member member, TextChannel textChannel, Message message, String[] args) {
        String userId = member.getId();

        LPlayer lplayer = LPlayer.findByDiscord(userId);
        if (lplayer == null) {
            message.reply("You haven't linked your minecraft LCore account yet, please link it by running `" + Utilities.config.getString("bot_prefix") + "link`").queue();
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(lplayer.getUuid());
        if (!player.isOp()) {
            message.reply("You need to be **OP** to use this command").queue();
            return;
        }
        Message restartMessage = message.reply("Contacting server...").complete();
        long start = System.currentTimeMillis();
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("LCore"), () ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart")
        );
        long finish = System.currentTimeMillis();
        restartMessage.editMessage("Issued restart command in `" + (finish - start) + "ms`. Server is currently restarting.").queue();
    }
}


