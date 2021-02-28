package com.larko.LCore;

import com.larko.LCore.Auth.AuthModule;
import com.larko.LCore.World.ClaimModule;
import com.larko.LCore.World.HomeModule;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import com.larko.LCore.Utils.Utilities;

import java.io.File;
import java.io.FileReader;

public class Main extends JavaPlugin {
    public static JSONArray cachedPlayersData;
    @Override
    public void onEnable() {
        Utilities.dataFolder = getDataFolder();


        // Caching players
        JSONParser jsonParser = new JSONParser();

        try {
            Object players = jsonParser.parse(new FileReader(new File(Utilities.dataFolder, "players.json")));
            cachedPlayersData = (JSONArray) players;
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

    }
}
