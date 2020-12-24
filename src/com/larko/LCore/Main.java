package com.larko.LCore;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        Utils.dataFolder = getDataFolder();
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
