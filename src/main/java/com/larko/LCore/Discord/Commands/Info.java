package com.larko.LCore.Discord.Commands;

import com.github.stackovernorth.jda.commandhandler.listener.CommandListener;
import com.larko.LCore.Structures.LPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;

public class Info implements CommandListener {
    public Info() {}
    @Override
    public void onCommand(Member member, TextChannel textChannel, Message message, String[] args) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("LCore");
        embedBuilder.setDescription("Connected to LCore\n`" + LPlayer.getPlayers().size() + "` registered players\n`" + Bukkit.getOnlinePlayers().size() + "` connected players");

    }
}
