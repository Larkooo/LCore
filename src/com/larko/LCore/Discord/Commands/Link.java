package com.larko.LCore.Discord.Commands;

import com.github.stackovernorth.jda.commandhandler.api.command.CommandBuilder;
import com.github.stackovernorth.jda.commandhandler.api.handler.CommandHandler;
import com.github.stackovernorth.jda.commandhandler.api.handler.CommandHandlerBuilder;
import com.github.stackovernorth.jda.commandhandler.listener.CommandListener;
import com.larko.LCore.Discord.Bot;
import com.larko.LCore.Main;
import com.larko.LCore.Structures.LPlayer;
import com.larko.LCore.Utils.AuthUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Link implements CommandListener {
    public Link() {}
    @Override
    public void onCommand(Member member, TextChannel textChannel, Message message, String[] args) {
        if (args.length < 1) {
            message.reply("Please type your minecraft username").queue();
            return;
        };

        String minecraftUsername = args[0].trim();
        for (LPlayer player : LPlayer.getPlayers()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(player.getUuid().toString()));
            if (offlinePlayer == null) continue;
            if (!offlinePlayer.getName().equalsIgnoreCase(minecraftUsername)) continue;
            User user = member.getUser();
            if (player.getLinkedDiscordId() != null) {
                if (player.getLinkedDiscordId().equals(user.getId())) {
                    message.reply("This account is already linked to your discord account").queue();
                } else {
                    message.reply("This account is already linked to another discord account").queue();
                }
                return;
            }
            PrivateChannel privateChannel = user.openPrivateChannel().complete();
            Message sentMessage = privateChannel.sendMessage("Please type your password under this message. You have 15 seconds").complete();
            TimerTask task = new TimerTask(){
                public void run(){
                    JDA bot = Bot.getInstance();
                    Message latestMessage = privateChannel.getHistoryAfter(sentMessage, 1).complete().getRetrievedHistory().get(0);
                    if (latestMessage.getAuthor() != user) {
                        privateChannel.sendMessage("Timeout. You can retry linking your minecraft LCore account by running the command again.").queue();
                    }
                    if (BCrypt.checkpw(latestMessage.getContentRaw(), player.getHashedPassword())) {
                        if (player.setLinkedDiscordId(user.getId())) {
                            latestMessage.reply("Correct password. I linked your minecraft LCore account to your discord").queue();
                            return;
                        }
                        latestMessage.reply("An error occured, could not link your account").queue();

                    } else {
                        latestMessage.reply("Bad password. You can retry linking your minecraft LCore account by running the command again.").queue();
                    }

                }
            };
            Timer timer = new Timer();
            // The delay period is calculated in milliseconds iirc
            timer.schedule(task, 15*1000);
            message.reply("I sent you a DM, please type your password in there so I can verify the account is yours").queue();
            return;
        }
        message.reply("Cannot find your account in LCore database (un vieux JSON car j'ai la flemme de faire une DB mdr)").queue();
    }
}
