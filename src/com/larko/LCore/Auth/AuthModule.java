package com.larko.LCore.Auth;

import com.larko.LCore.Structures.LPlayer;
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
    public static Map awaitingLoginPlayers = new HashMap<UUID, AuthState>();

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
        if(LPlayer.findByUUID(player.getUniqueId()) == null) {
            player.teleport(before);
        }
    }

    /*
       Wait for input from user
       Input : password, for registering or logging in
     */
    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!awaitingLoginPlayers.containsKey(player.getUniqueId())) return;
        AuthState method = (AuthState) awaitingLoginPlayers.get(player.getUniqueId());
        String message = event.getMessage();
        // Delete message
        event.setCancelled(true);
        if(method == AuthState.AWAITING_REGISTER) {
            boolean registered = registerPlayer(player.getUniqueId(), message);
            if(!registered) {
                player.sendMessage(ChatColor.RED + "An error occurred. Please try registering again.");
                return;
            }
            awaitingLoginPlayers.replace(player.getUniqueId(), AuthState.AWAITING_LOGIN);
            player.sendMessage("Successfully registered. Please type your password again to proceed.");
        } else {
            if(loginPlayer(player.getUniqueId(), message) != null) {
                /*
                If player logged in, remove him from awaiting login list
                 */
                awaitingLoginPlayers.remove(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Successfully logged in.");
                System.out.println(LPlayer.getPlayers().size());
                // Remove effects / attributes
                Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("LCore"), () -> {
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                    player.setCanPickupItems(true);
                    player.setInvulnerable(false);
                });
            } else {
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
        if(LPlayer.findByUUID(player.getUniqueId()) == null) {
            event.setCancelled(true);
        }
    }


    /*
       Destroy player instance if the player logout from the server
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        LPlayer player = LPlayer.findByUUID(event.getPlayer().getUniqueId());
        if(player != null)
            LPlayer.getPlayers().remove(player);
    }
}
