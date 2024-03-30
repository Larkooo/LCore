package com.larko.LCore.Auth;

import com.larko.LCore.Structures.LPlayer;
import com.larko.LCore.Utils.Utilities;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static com.larko.LCore.Utils.AuthUtils.*;

public class AuthModule implements Listener {
    // Must contain AuthState
    public static Map awaitingLoginPlayers = new HashMap<UUID, HashMap<String, Object>>();

    public AuthModule() {
        // Ask the players to re-login if the plugin is restarted / auth module reinitialized
        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            askAuth(player);
        }
    }

    // Ask the player to login
    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        askAuth(player);
    }

    /*
       Teleport the player to his previous position if he's not logged in
       Aka Freezing him
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location before = event.getFrom();
        Player player = event.getPlayer();

        if (LPlayer.findByUUID(player.getUniqueId()).isConnected()) return;

        player.teleport(before);

        boolean hasAlreadyAccount = isPlayerRegistered(player.getUniqueId());

        if(hasAlreadyAccount) {
            player.sendTitle(ChatColor.RED + "Login", ChatColor.GRAY + "Please type your password in the chat", 1,100,3);
        } else {
            player.sendTitle(ChatColor.RED +"Create an account",  ChatColor.GRAY + "Please choose a password and type it in the chat", 1,100,3);
        }
    }

    /*
       Wait for input from user
       Input : password, for registering or logging in
     */
    @EventHandler
    public void onPlayerMessage(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if(!awaitingLoginPlayers.containsKey(player.getUniqueId())) return;
        AuthState method = (AuthState) ((HashMap<String, Object>) awaitingLoginPlayers.get(player.getUniqueId())).get("authState");
        String password = ((TextComponent) event.message()).content();
        // Delete message
        event.setCancelled(true);
        if(method == AuthState.AWAITING_REGISTER) {
            boolean registered = registerPlayer(player.getUniqueId(), password);
            if(!registered) {
                player.sendMessage(ChatColor.RED + "An error occurred. Please try registering again.");
                return;
            }
            ((HashMap<String, Object>) awaitingLoginPlayers.get(player.getUniqueId())).put("authState", AuthState.AWAITING_LOGIN);
            player.sendMessage("Successfully registered. Please type your password again to proceed.");
        } else {
            if(LPlayer.findByUUID(player.getUniqueId()).login(password)) {
                /*
                If player logged in, remove him from awaiting login list
                 */
                awaitingLoginPlayers.remove(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Successfully logged in.");
                // Remove effects / attributes
                Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("LCore"), () -> {
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                    player.setCanPickupItems(true);
                    player.setInvulnerable(false);
                    player.resetTitle();
                });
            } else {
                int tries = (int) ((HashMap<String, Object>) awaitingLoginPlayers.get(player.getUniqueId())).get("tries");
                // increment tries then add
                ((HashMap<String, Object>) awaitingLoginPlayers.get(player.getUniqueId())).put("tries", ++tries);
                if (tries == 3) {
                    Bukkit.getScheduler().runTask(
                            Bukkit.getPluginManager().getPlugin("LCore"),
                            () -> player.kick(Component.text("Too many failed tries").color(NamedTextColor.RED))
                    );
                    awaitingLoginPlayers.remove(player.getUniqueId());
                    return;
                }
                player.sendMessage(ChatColor.RED + "Bad password.");
            }
        }

    }

    /*
      Cancel player interaction event if the player is not logged in.
      To prevent the player from interacting with the world while being frozen, not logged in.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (LPlayer.findByUUID(player.getUniqueId()).isConnected()) return;

        event.setCancelled(true);

        boolean hasAlreadyAccount = isPlayerRegistered(player.getUniqueId());

        if(hasAlreadyAccount) {
            player.sendTitle(ChatColor.RED + "Login", ChatColor.GRAY + "Please type your password in the chat", 1,100,3);
        } else {
            player.sendTitle(ChatColor.RED +"Create an account",  ChatColor.GRAY + "Please choose a password and type it in the chat", 1,100,3);
        }
    }


    /*
       Destroy player instance if the player logout from the server
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        LPlayer player = LPlayer.findByUUID(event.getPlayer().getUniqueId());
        player.setConnected(false);
    }
}
