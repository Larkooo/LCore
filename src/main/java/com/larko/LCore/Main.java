package com.larko.LCore;

import com.larko.LCore.Auth.AuthModule;
import com.larko.LCore.Economy.MoneyModule;
import com.larko.LCore.Economy.ShopModule;
import com.larko.LCore.Structures.LPlayer;
import com.larko.LCore.Structures.Shop;
import com.larko.LCore.World.ClaimModule;
import com.larko.LCore.World.HomeModule;
import com.larko.LCore.World.PlayerModule;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.larko.LCore.Utils.Utilities;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main extends JavaPlugin {
    //public static JSONArray cachedPlayersData;
    public static Timer scienceTimer;

    @Override
    public void onEnable() {
        Utilities.dataFolder = getDataFolder();
        Utilities.config = this.getConfig();

        Utilities.config.addDefault("bot_token", Utilities.tokenConfigPlaceholder);
        Utilities.config.addDefault("bot_prefix", "l*");
        Utilities.config.addDefault("activity_title", null);
        Utilities.config.options().copyDefaults(true);
        saveConfig();

        // Caching players
        JSONParser jsonParser = new JSONParser();

        try {
            Utilities.loadPlayers();
        } catch (Exception e) {
            getLogger().severe("Failed to load players data: " + e);
        }

        try {
            Utilities.loadShop();
        } catch (Exception e) {
            getLogger().severe("Failed to load shop data: " + e);
        }

//        new Bot();

//        Utilities.runScienceTask();

        getServer().getPluginManager().registerEvents(new AuthModule(), this);
        getServer().getPluginManager().registerEvents(new ClaimModule(), this);
        getServer().getPluginManager().registerEvents(new HomeModule(), this);
        getServer().getPluginManager().registerEvents(new ShopModule(), this);
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

        getCommand("lcoins").setExecutor(new MoneyModule());
        getCommand("transfer").setExecutor(new MoneyModule());
        getCommand("setlcoins").setExecutor(new MoneyModule());

        getCommand("shop").setExecutor(new ShopModule());
        getCommand("sell").setExecutor(new ShopModule());

        getCommand("safechest").setExecutor(new PlayerModule());
    }

    @Override
    public void onDisable() {
        if (scienceTimer != null) {
            scienceTimer.cancel();
        }
//        JDA bot = Bot.getInstance();
//        if (bot != null) {
//            Bot.activityUpdateTimer.cancel();
//            bot.shutdownNow();
//        };
    }
}
