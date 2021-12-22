package com.larko.LCore.Discord.Commands;

import com.github.stackovernorth.jda.commandhandler.listener.CommandListener;
import com.larko.LCore.Discord.Bot;
import com.larko.LCore.Structures.LPlayer;
import com.larko.LCore.Utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

public class Info implements CommandListener {
    public Info() {}
    @Override
    public void onCommand(Member member, TextChannel textChannel, Message message, String[] args) {
        JDA jda = message.getJDA();
        Server server = Bukkit.getServer();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("LCore");
        embedBuilder.setDescription("Connected to LCore :ballot_box_with_check:\n`" + LPlayer.getPlayers().size() + "` registered players\n`" + Bukkit.getOnlinePlayers().size() + "` connected players");

        embedBuilder.addField("Bot", "WS Gateway ping : `" + jda.getGatewayPing() + "ms`\n" +
                "Rest ping : `" + jda.getRestPing().complete() + "`ms\n", true);
        embedBuilder.addField("LCore", "LCoins total supply : `" + LPlayer.getPlayers().stream().mapToDouble(LPlayer::getLCoins).sum() + " LCoins`\n" +
                "Total registered claims : `" + LPlayer.getPlayers().stream().mapToInt((p) -> p.getClaims().size()).sum() + "`\n" +
                "Total registered homes : `" + LPlayer.getPlayers().stream().mapToInt((p) -> p.getClaims().size()).sum() + "`\n", true);
        embedBuilder.addField("Server", "Name : " + Bukkit.getName() + "\n" +
                "TPS : `" + Bukkit.getTPS()[0] + "`\n" +
                "Version : `" + Bukkit.getVersion() + "`\n" +
                "Bukkit version : `" + Bukkit.getBukkitVersion() + "`\n" +
                "Minecraft version : `" + Bukkit.getMinecraftVersion() + "`\n", true);

        // science
        try {
            Utilities.generateScienceGraph();
            embedBuilder.setImage("attachment://" + "science.png");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        embedBuilder.setFooter("LCore");
        embedBuilder.setColor(Bot.primaryColor);

        message.reply(embedBuilder.build()).addFile(new File(Utilities.dataFolder, "science.png"), "science.png").queue();
    }
}
