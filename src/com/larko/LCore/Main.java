package com.larko.LCore;

import com.larko.LCore.Auth.AuthModule;
import com.larko.LCore.Discord.Bot;
import com.larko.LCore.Structures.LPlayer;
import com.larko.LCore.World.ClaimModule;
import com.larko.LCore.World.HomeModule;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.larko.LCore.Utils.Utilities;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {
    //public static JSONArray cachedPlayersData;
    @Override
    public void onEnable() {
        Utilities.dataFolder = getDataFolder();
        Utilities.config = this.getConfig();

        Utilities.config.addDefault("bot_token", Utilities.tokenConfigPlaceholder);
        Utilities.config.addDefault("bot_prefix", "l*");
        Utilities.config.addDefault("activity_title", null);
        Utilities.config.options().copyDefaults(true);
        saveConfig();

        new Bot();

        // Caching players
        JSONParser jsonParser = new JSONParser();

        try {
            JSONArray players = (JSONArray) jsonParser.parse(new FileReader(new File(Utilities.dataFolder, "players.json")));
            //cachedPlayersData = (JSONArray) players;
            for (int i = 0; i < players.size(); i++) {
                JSONObject player = (JSONObject) players.get(i);
                LPlayer.fromJSON(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        getServer().getPluginManager().registerEvents(new AuthModule(), this);
        getServer().getPluginManager().registerEvents(new ClaimModule(), this);
        // Home
        getCommand("sethome").setExecutor(new HomeModule());
        getCommand("home").setExecutor(new HomeModule());
        getCommand("delhome").setExecutor(new HomeModule());
        getCommand("homes").setExecutor(new HomeModule());

        // Claim
        getCommand("claim").setExecutor(new ClaimModule());
        getCommand("unclaim").setExecutor(new ClaimModule());
        getCommand("claims").setExecutor(new ClaimModule());
        getCommand("addtoclaim").setExecutor(new ClaimModule());
        getCommand("removefromclaim").setExecutor(new ClaimModule());
    }

    @Override
    public void onDisable() {
        JDA bot = Bot.getInstance();
        if (bot != null) bot.shutdownNow();
    }
}
