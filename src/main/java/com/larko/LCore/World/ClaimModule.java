package com.larko.LCore.World;

import com.larko.LCore.Structures.Claim;
import com.larko.LCore.Structures.LPlayer;
import com.larko.LCore.Structures.Position;
import com.larko.LCore.Utils.Utilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;
import java.util.stream.Collectors;

import com.larko.LCore.Utils.ClaimUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ClaimModule implements CommandExecutor, Listener {

    //static LinkedList inClaimPlayers = new LinkedList();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;
        if(label.equalsIgnoreCase("claim")) {
            LPlayer lPlayer = LPlayer.findByUUID(player.getUniqueId());
            if(args.length < 1) {
                player.sendMessage(ChatColor.RED + "Please provide a radius");
                return false;
            }
            if(!(Utilities.tryParseInt(args[0]))) {
                player.sendMessage(ChatColor.RED + "Could not parse radius, are you providing an integer?");
                return false;
            }
            int radius = Integer.parseInt(args[0]);
            if(radius > 100) {
                player.sendMessage(ChatColor.RED + "Can't claim more than 100 blocks");
                return false;
            }

            if(lPlayer.getClaims().size() > 5) {
                player.sendMessage(ChatColor.RED + "You cannot have more than 5 claims");
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
                player.sendMessage(ChatColor.RED + "Could not claim this zone");
                return false;
            }
        } else if(label.equalsIgnoreCase("unclaim")) {
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
            if(args.length < 1) {
                player.sendMessage(ChatColor.RED + "Player name missing");
                return false;
            }
            Player scopedPlayer = Bukkit.getPlayer(args[0]);
            if(scopedPlayer == null) {
                player.sendMessage(ChatColor.RED + "Invalid player");
                return false;
            }
            boolean addedToClaim = ClaimUtils.addPlayerToClaim(player.getUniqueId(), player.getLocation(), scopedPlayer.getUniqueId());
            if(!addedToClaim) {
                player.sendMessage(ChatColor.RED + "Could not add this player to this claim");
                return false;
            } else {
                player.sendMessage(ChatColor.ITALIC + "Added " + ChatColor.BLUE + scopedPlayer.getName() + ChatColor.WHITE + " to your claim");
                scopedPlayer.sendMessage(ChatColor.ITALIC + "" + ChatColor.BLUE + player.getName() + ChatColor.WHITE + " added you to his claim");
                return true;
            }
        } else if(label.equalsIgnoreCase("removefromclaim")) {
            if(args.length == 0) {
                player.sendMessage(ChatColor.RED + "Player name missing");
                return false;
            }
            Player scopedPlayer = Bukkit.getPlayer(args[0]);
            if(scopedPlayer == null) {
                player.sendMessage(ChatColor.RED + "Invalid player");
                return false;
            }
            boolean removedFromClaim = ClaimUtils.removePlayerFromClaim(player.getUniqueId(), player.getLocation(), scopedPlayer.getUniqueId());
            if(!removedFromClaim) {
                player.sendMessage(ChatColor.RED + "Could not remove this player from this claim");
                return false;
            } else {
                player.sendMessage(ChatColor.ITALIC + "Removed " + ChatColor.BLUE + scopedPlayer.getName() + ChatColor.WHITE + " from your claim");
                scopedPlayer.sendMessage(ChatColor.ITALIC + "" + ChatColor.BLUE + player.getName() + ChatColor.WHITE + " removed you from his claim");
                return true;
            }
        } else if(label.equalsIgnoreCase("claims")) {
            LPlayer lPlayer = LPlayer.findByUUID(player.getUniqueId());
            // minimum inv size is 9
            Inventory claimsInventory = Bukkit.createInventory(null, 9, Component.text("Claims"));
            for (Claim claim : lPlayer.getClaims()) {
                // random block
                List<Material> filteredMaterials = Arrays.stream(Material.values()).filter(material -> material.isBlock()).collect(Collectors.toList());
                ItemStack claimItem = new ItemStack(filteredMaterials.get(new Random().nextInt(filteredMaterials.size())));
                ItemMeta itemMeta = claimItem.getItemMeta();
                itemMeta.displayName(Component.text(claim.getPosition().toString(), TextColor.color(52, 137, 235)));
                itemMeta.lore(claim.getAuthorizedPlayers().stream().map(uuid -> Component.text(Bukkit.getOfflinePlayer(uuid).getName())).collect(Collectors.toList()));
                claimItem.setItemMeta(itemMeta);
                claimsInventory.addItem(claimItem);
            }
            InventoryView claimsView = player.openInventory(claimsInventory);
            /*ArrayList<Claim> claims = LPlayer.findByUUID(player.getUniqueId()).getClaims();
            if(claims.size() > 0) {
                String claimsString = "";
                for(Claim claim : claims) {
                    Position pos = claim.getPosition();
                    claimsString += "Coords : " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " Radius : " + claim.getRadius() + "\n";
                }
                player.sendMessage("Claims : " + claims.size() + "\n" + claimsString);
            } else {
                player.sendMessage("You don't have any claims");
            }*/
        }
        return false;
    }

    /*
       Prevent player from interacting in someone's claim, unless he's authorized
     */
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

    /*
       Checking where the player is to know if he's in a claim or no
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        OfflinePlayer claimOwner = ClaimUtils.checkPlayerClaim(player.getUniqueId(), player.getLocation());
        LPlayer lPlayer = LPlayer.findByUUID(player.getUniqueId());
        // show titles only if player was in claim/not in claim
        // was not in claim && is in claim actually = show title
        // was in claim && is not in claim = show title
        // that way we dont show the title when not needed
        if(claimOwner != null && !lPlayer.isInClaim()) {
            player.sendTitle("", ChatColor.BLUE + "Entered " + claimOwner.getName() + "'s claim", 1, 50, 3);
            lPlayer.setInClaim(true);
        } else if(claimOwner == null && lPlayer.isInClaim()) {
            lPlayer.setInClaim(false);
            player.sendTitle("", ChatColor.GREEN + "Wilderness", 1, 50, 3);
        }
    }

    /*
       Prevent player from abusing explosions to grief a claim
     */
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
            // Cancelling explosion only if owner is not found near the entity that exploded
            event.setCancelled(!foundClaimOwnerInRadius);
        }
    }

    /*
        Claims inventory events
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!(((TextComponent) event.getView().title()).content().equals("Claims"))) return;
        HumanEntity clickAuthor = event.getWhoClicked();
        Bukkit.getPlayer(clickAuthor.getUniqueId()).playSound(clickAuthor.getLocation(), Sound.ENTITY_VILLAGER_NO, 50, 5);
        event.setCancelled(true);
    }
}
