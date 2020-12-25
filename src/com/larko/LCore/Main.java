package com.larko.LCore;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;

public class Main extends JavaPlugin {
    static JSONArray cachedPlayers;
    @Override
    public void onEnable() {
        Utils.dataFolder = getDataFolder();


        // Caching players
        JSONParser jsonParser = new JSONParser();

        try {
            Object players = jsonParser.parse(new FileReader(new File(Utils.dataFolder, "players.json")));
            cachedPlayers = (JSONArray) players;
        } catch (Exception e) {
            e.printStackTrace();
        }

        getServer().getPluginManager().registerEvents(new Auth(), this);
        getServer().getPluginManager().registerEvents(new Claim(), this);
        // Home
        getCommand("sethome").setExecutor(new Home());
        getCommand("home").setExecutor(new Home());
        getCommand("delhome").setExecutor(new Home());
        getCommand("homes").setExecutor(new Home());

        // Claim
        getCommand("claim").setExecutor(new Claim());
        getCommand("unclaim").setExecutor(new Claim());
        getCommand("claims").setExecutor(new Claim());
    }

    @Override
    public void onDisable() {

    }
}
