package com.larko.LCore.Discord.Commands;


import com.github.stackovernorth.jda.commandhandler.listener.CommandListener;

import com.larko.LCore.Structures.Claim;
import com.larko.LCore.Structures.LPlayer;
import com.larko.LCore.Utils.AuthUtils;
import com.larko.LCore.Utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONObject;


public class Claims implements CommandListener {
    public Claims() {}
    @Override
    public void onCommand(Member member, TextChannel textChannel, Message message, String[] args) {
        String userId = member.getId();

        LPlayer lplayer = AuthUtils.findPlayerWithDiscord(userId);
        OfflinePlayer player = Bukkit.getPlayer(lplayer.getUuid());
        if (lplayer == null) {
            message.reply("You haven't linked your minecraft LCore account yet, please link it by running `" + Utilities.config.getString("bot_prefix") + "link`").queue();
            return;
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(player.getName());
        embedBuilder.setTitle("Claims");
        int count = 1;
        for (Claim claim : lplayer.getClaims()) {
            embedBuilder.addField("#" + count + " Claim", "Position : `" + claim.getPosition().toString() + "`\nRadius : `" + claim.getRadius() + "`"  , true);
            count++;
        }
        embedBuilder.setFooter("LCore - " + lplayer.getUuid().toString());
        message.reply(embedBuilder.build()).queue();
    }
}


