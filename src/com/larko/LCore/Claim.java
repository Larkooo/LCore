package com.larko.LCore;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Claim implements CommandExecutor, Listener {

    static LinkedList inClaimPlayers = new LinkedList();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("claim")) {
            if(!(commandSender instanceof Player)) return false;
            Player player = (Player) commandSender;
            if(args.length < 1) {
                player.sendMessage("Please provide a radius");
                return false;
            }
            if(!(Utils.tryParseInt(args[0]))) {
                player.sendMessage("Could not parse radius, are you providing an integer?");
                return false;
            }
            int radius = Integer.parseInt(args[0]);
            if(radius > 100) {
                player.sendMessage("Can't claim more than 100 blocks");
                return false;
            }

            if(Utils.getClaims(player.getUniqueId()).size() > 5) {
                player.sendMessage("You cannot have more than 5 claims");
                return false;
            }

            if(!(Utils.checkClaim(player.getUniqueId(), player.getLocation()))) {
                player.sendMessage("This zone has already been claimed");
                return false;
            }

            if(Utils.addClaim(player.getUniqueId(), player.getLocation(), radius)) {
                player.sendMessage("Claimed this zone");
                return true;
            } else {
                player.sendMessage("Could not claim this zone");
                return false;
            }
        } else if(label.equalsIgnoreCase("unclaim")) {
            if(!(commandSender instanceof Player)) return false;
            Player player = (Player) commandSender;
            boolean unclaimed = Utils.unClaim(player.getUniqueId(), player.getLocation());
            if(unclaimed) {
                player.sendMessage("Unclaimed zone");
                return true;
            } else {
                player.sendMessage("Can't unclaim something you haven't claimed yet");
                return false;
            }
        } else if(label.equalsIgnoreCase("claims")) {
            if(!(commandSender instanceof Player)) return false;
            Player player = (Player) commandSender;
            JSONArray claims = Utils.getClaims(player.getUniqueId());
            if(claims != null) {
                String claimsString = "";
                Iterator<JSONObject> claimsIterator = claims.iterator();
                while(claimsIterator.hasNext()) {
                    JSONObject claim = claimsIterator.next();
                    claimsString += "Coords : " + (String) claim.get("pos") + " Radius : " + claim.get("radius") + "\n";
                }
                player.sendMessage("Claims : " + claims.size() + "\n" + claimsString);
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(player.getInventory().getItemInMainHand().getType().isEdible() && (event.getAction() == Action.RIGHT_CLICK_AIR)) return;
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Location blockLocation = event.getClickedBlock().getLocation();
            if(!(Utils.checkClaim(player.getUniqueId(), blockLocation))) {
                event.setCancelled(true);
            }
        } else if(!(Utils.checkClaim(player.getUniqueId(), player.getLocation())))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        OfflinePlayer claimOwner = Utils.checkPlayerClaim(player.getUniqueId(), player.getLocation());
        if(claimOwner != null && !(inClaimPlayers.contains(player.getUniqueId()))) {
            player.sendTitle("", ChatColor.BLUE + "Entered " + claimOwner.getName() + "'s claim", 1, 50, 3);
            inClaimPlayers.add(player.getUniqueId());
            System.out.println("lol");
        } else if(claimOwner == null && inClaimPlayers.contains(player.getUniqueId())) {
            inClaimPlayers.remove(player.getUniqueId());
            player.sendTitle("", ChatColor.GREEN + "Wilderness", 1, 50, 3);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        // The entity id here is basically useless but I'm lazy to do another function so
        if(!(Utils.checkClaim(entity.getUniqueId(), entity.getLocation()))) {
            // Also useless lol
            OfflinePlayer claimOwner = Utils.checkPlayerClaim(entity.getUniqueId(), entity.getLocation());
            List<Entity> nearbyEntities = entity.getNearbyEntities(5, 5, 5);
            boolean foundClaimOwnerInRadius = false;
            for(Entity nearbyEntity : nearbyEntities) {
                if(nearbyEntity == claimOwner.getPlayer()) {
                    foundClaimOwnerInRadius = true;
                    break;
                }
            }
            event.setCancelled(!foundClaimOwnerInRadius);
        }
    }
}
