package com.larko.LCore;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class Claim implements CommandExecutor, Listener {

    static HashMap inClaimPlayers = new HashMap();

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
        if(!(Utils.checkClaim(player.getUniqueId(), player.getLocation())))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Player claimOwner = Utils.checkPlayerClaim(player.getUniqueId(), player.getLocation());
        if(claimOwner != null && inClaimPlayers.containsKey(player.getUniqueId())) {
            player.sendTitle(null, ChatColor.BLUE + "Entered " + claimOwner.getDisplayName() + "'s claim", 1, 50, 3);
            inClaimPlayers.put(player.getUniqueId(), claimOwner.getUniqueId());
        } else if(claimOwner == null && inClaimPlayers.containsKey(player.getUniqueId())) {
            inClaimPlayers.remove(player.getUniqueId());
        }
    }
}
