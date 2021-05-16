package com.larko.LCore.Discord.Commands;


import com.github.stackovernorth.jda.commandhandler.listener.CommandListener;

import com.larko.LCore.Structures.LPlayer;
import com.larko.LCore.Utils.AuthUtils;
import net.dv8tion.jda.api.entities.*;


public class Unlink implements CommandListener {
    public Unlink() {}
    @Override
    public void onCommand(Member member, TextChannel textChannel, Message message, String[] args) {
        LPlayer lPlayer = LPlayer.findByDiscord(member.getId());
        if (lPlayer == null) {
            message.reply("No minecraft LCore account linked to your account found").queue();
            return;
        }
        if (AuthUtils.unlinkDiscordAccount(lPlayer.getUuid())) {
            message.reply("Your minecraft LCore account has been disconnected from this discord account").queue();
        } else {
            message.reply("Could not disconnect LCore account").queue();
        }
    }

}
