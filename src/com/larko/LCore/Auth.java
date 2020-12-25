package com.larko.LCore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.larko.LCore.Utils.*;

public class Auth implements Listener {
    static ArrayList loggedInPlayers = new ArrayList<UUID>();
    static HashMap awaitingLoginPlayers = new HashMap<UUID, String>();

    Auth() {
        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            boolean hasAlreadyAccount = isPlayerRegistered(player.getUniqueId());

            if(hasAlreadyAccount) {
                player.sendTitle(ChatColor.RED + "Login", ChatColor.GRAY + "Please type your password in the chat", 1,100,3);
                awaitingLoginPlayers.put(player.getUniqueId(), "login");
            } else {
                player.sendTitle(ChatColor.RED +"Create an account",  ChatColor.GRAY + "Please choose a password and type it in the chat", 1,100,3);
                awaitingLoginPlayers.put(player.getUniqueId(), "register");
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100000, 100, true, false, false));
            player.setCanPickupItems(false);
            player.setInvulnerable(true);
        }
    }

    // Ask the player to login
    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        boolean hasAlreadyAccount = isPlayerRegistered(player.getUniqueId());

        if(hasAlreadyAccount) {
            player.sendTitle(ChatColor.RED + "Login", ChatColor.GRAY + "Please type your password in the chat", 1,100,3);
            awaitingLoginPlayers.put(player.getUniqueId(), "login");
        } else {
            player.sendTitle(ChatColor.RED +"Create an account",  ChatColor.GRAY + "Please choose a password and type it in the chat", 1,100,3);
            awaitingLoginPlayers.put(player.getUniqueId(), "register");
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100000, 100, true, false, false));
        player.setCanPickupItems(false);
        player.setInvulnerable(true);


    }

    /* Teleport the player to his previous position if he's not logged in
    Aka Freezing him
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location before = event.getFrom();
        Player player = event.getPlayer();
        if(!loggedInPlayers.contains(player.getUniqueId())) {
            player.teleport(before);
        }
    }

    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!awaitingLoginPlayers.containsKey(player.getUniqueId())) return;
        String method = (String) awaitingLoginPlayers.get(player.getUniqueId());
        String message = event.getMessage();
        if(method == "register") {
            event.setCancelled(true);
            registerPlayer(player.getUniqueId(), message);
            awaitingLoginPlayers.replace(player.getUniqueId(), "login");
            player.sendMessage("Successfully registered. Please type your password again to proceed.");
        } else {
            event.setCancelled(true);
            if(loginPlayer(player.getUniqueId(), message)) {
                awaitingLoginPlayers.remove(player.getUniqueId());
                loggedInPlayers.add(player.getUniqueId());
                player.sendMessage("Successfully logged in");
                Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("LCore"), () -> {
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                    player.setCanPickupItems(true);
                    player.setInvulnerable(false);
                });
            } else {
                player.sendMessage(ChatColor.RED + "Bad password");
            }
        }

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(!loggedInPlayers.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if(loggedInPlayers.contains(event.getPlayer().getUniqueId()))
            loggedInPlayers.remove(event.getPlayer().getUniqueId());
    }
}
