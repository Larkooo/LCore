package com.larko.LCore.World;

import com.larko.LCore.Structures.Claim;
import com.larko.LCore.Structures.LPlayer;
import com.larko.LCore.Structures.Position;
import com.larko.LCore.Utils.Utilities;
import org.bukkit.*;
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

import java.util.*;

import com.larko.LCore.Utils.ClaimUtils;

public class ClaimModule implements CommandExecutor, Listener {

    static LinkedList inClaimPlayers = new LinkedList();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        System.out.println(label);
        if(label.equalsIgnoreCase("claim") || label.equalsIgnoreCase("setclaim")) {
            if(!(commandSender instanceof Player)) return false;
            Player player = (Player) commandSender;
            LPlayer lPlayer = LPlayer.findByUUID(player.getUniqueId());
            if(args.length < 1) {
                player.sendMessage("Please provide a radius");
                return false;
            }
            if(!(Utilities.tryParseInt(args[0]))) {
                player.sendMessage("Could not parse radius, are you providing an integer?");
                return false;
            }
            int radius = Integer.parseInt(args[0]);
            if(radius > 100) {
                player.sendMessage("Can't claim more than 100 blocks");
                return false;
            }

            if(lPlayer.getClaims().size() > 5) {
                player.sendMessage("You cannot have more than 5 claims");
                return false;
            }

            if(!(ClaimUtils.checkClaim(player.getUniqueId(), player.getLocation()))) {
                player.sendMessage("This zone has already been claimed");
                return false;
            }

            if(lPlayer.addClaim(player.getLocation(), radius)) {
                player.sendMessage("Claimed this zone");
                return true;
            } else {
                player.sendMessage("Could not claim this zone");
                return false;
            }
        } else if(label.equalsIgnoreCase("unclaim")) {
            if(!(commandSender instanceof Player)) return false;
            Player player = (Player) commandSender;
            LPlayer lPlayer = LPlayer.findByUUID(player.getUniqueId());
            boolean unclaimed = lPlayer.removeClaim(player.getLocation());
            if(unclaimed) {
                player.sendMessage("Unclaimed zone");
                return true;
            } else {
                player.sendMessage("Can't unclaim something you haven't claimed yet");
                return false;
            }
        } else if(label.equalsIgnoreCase("addtoclaim")) {
            if(!(commandSender instanceof Player)) return false;
            Player player = (Player) commandSender;
            if(args.length < 1) {
                player.sendMessage("Player name missing");
                return false;
            }
            Player scopedPlayer = Bukkit.getPlayer(args[0]);
            if(scopedPlayer == null) {
                player.sendMessage("Invalid player");
                return false;
            }
            boolean addedToClaim = ClaimUtils.addPlayerToClaim(player.getUniqueId(), player.getLocation(), scopedPlayer.getUniqueId());
            if(!addedToClaim) {
                player.sendMessage("Could not add this player to this claim");
                return false;
            } else {
                player.sendMessage("Added " + args[0] + " to your claim");
                scopedPlayer.sendMessage(player.getName() + " added you to his claim");
                return true;
            }
        } else if(label.equalsIgnoreCase("removefromclaim")) {
            if(!(commandSender instanceof Player)) return false;
            Player player = (Player) commandSender;
            if(args.length < 1) {
                player.sendMessage("Player name missing");
                return false;
            }
            Player scopedPlayer = Bukkit.getPlayer(args[0]);
            if(scopedPlayer == null) {
                player.sendMessage("Invalid player");
                return false;
            }
            boolean removedFromClaim = ClaimUtils.removePlayerFromClaim(player.getUniqueId(), player.getLocation(), scopedPlayer.getUniqueId());
            if(!removedFromClaim) {
                player.sendMessage("Could not remove this player from this claim");
                return false;
            } else {
                player.sendMessage("Removed " + args[0] + " from your claim");
                scopedPlayer.sendMessage(player.getName() + " removed you from his claim");
                return true;
            }
        } else if(label.equalsIgnoreCase("claims")) {
            if(!(commandSender instanceof Player)) return false;
            Player player = (Player) commandSender;
            ArrayList<Claim> claims = LPlayer.findByUUID(player.getUniqueId()).getClaims();
            if(claims.size() > 0) {
                String claimsString = "";
                for(Claim claim : claims) {
                    Position pos = claim.getPosition();
                    claimsString += "Coords : " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " Radius : " + claim.getRadius() + "\n";
                }
                player.sendMessage("Claims : " + claims.size() + "\n" + claimsString);
            } else {
                player.sendMessage("You don't have any claims");
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(player.getInventory().getItemInMainHand().getType().isEdible() && (event.getAction() == Action.RIGHT_CLICK_AIR)) return;
        if(player.getInventory().getItemInMainHand().getType() == Material.SHIELD && (event.getAction() == Action.RIGHT_CLICK_AIR)) return;
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Location blockLocation = event.getClickedBlock().getLocation();
            if(!(ClaimUtils.checkClaim(player.getUniqueId(), blockLocation))) {
                event.setCancelled(true);
            }
        } else if(!(ClaimUtils.checkClaim(player.getUniqueId(), player.getLocation())))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        OfflinePlayer claimOwner = ClaimUtils.checkPlayerClaim(player.getUniqueId(), player.getLocation());
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
        if(!(ClaimUtils.checkClaim(entity.getUniqueId(), entity.getLocation()))) {
            // Also useless lol
            OfflinePlayer claimOwner = ClaimUtils.checkPlayerClaim(entity.getUniqueId(), entity.getLocation());
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
