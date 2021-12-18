package com.larko.LCore.World;

import com.larko.LCore.Structures.Claim;
import com.larko.LCore.Structures.Home;
import com.larko.LCore.Structures.LPlayer;
import com.larko.LCore.Structures.Position;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import com.larko.LCore.Utils.HomeUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HomeModule implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;

        if(label.equalsIgnoreCase("sethome")){
            if((args.length < 1)) return false;
            String homeName = args[0];
            LPlayer lPlayer = LPlayer.findByUUID(player.getUniqueId());

            if(!(player.getWorld().getEnvironment().equals(World.Environment.NORMAL))) {
                player.sendMessage("You can only set a home in the overworld");
                return false;
            };

            if(lPlayer.getHomes().size() == 10) {
                player.sendMessage("You cannot have more than 10 homes");
                return false;
            }
            boolean home = lPlayer.addHome(homeName, player.getLocation());
           // System.out.println(home);
            if(home) {
                player.sendMessage("Home has been set");
                return true;
            } else {
                player.sendMessage("Home could not be set");
                return false;
            }
        } else if(label.equalsIgnoreCase("home")) {
            if((args.length < 1)) return false;
            String homeName = args[0];
            LPlayer lPlayer = LPlayer.findByUUID(player.getUniqueId());
            Home home = lPlayer.getHome(homeName);
            if(home != null) {
                player.teleport(home.getPosition().toLocation());
                return true;
            } else {
                player.sendMessage("This home does not exist");
                return false;
            }
        } else if(label.equalsIgnoreCase("delhome")) {
            if((args.length < 1)) return false;
            String homeName = args[0];
            LPlayer lPlayer = LPlayer.findByUUID(player.getUniqueId());
            boolean deletedHome = lPlayer.removeHome(homeName);
            if(deletedHome){
                player.sendMessage("Deleted home");
                return true;
            } else {
                player.sendMessage("This home does not exist");
                return false;
            }
        } else if(label.equalsIgnoreCase("homes")) {
            LPlayer lPlayer = LPlayer.findByUUID(player.getUniqueId());
            Inventory homesInventory = Bukkit.createInventory(null, 18, Component.text("Homes"));
            for (Home home : lPlayer.getHomes()) {
                // random item
                List<Material> filteredMaterials = Arrays.stream(Material.values()).filter(material -> material.isItem()).collect(Collectors.toList());
                ItemStack homeItem = new ItemStack(filteredMaterials.get(new Random().nextInt(filteredMaterials.size())));
                ItemMeta itemMeta = homeItem.getItemMeta();
                itemMeta.displayName(Component.text(home.getName(), TextColor.color(52, 137, 235)));
                itemMeta.lore(Arrays.asList(Component.text(home.getPosition().toString())));
                homeItem.setItemMeta(itemMeta);
                homesInventory.addItem(homeItem);
            }
            InventoryView claimsView = player.openInventory(homesInventory);
        }
        return false;
    }

    /*
       Cancel click event if on homes inventory
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!((TextComponent) event.getView().title()).content().equals("Homes")) return;
        event.setCancelled(true);
        HumanEntity player = event.getWhoClicked();
        player.teleport(Position.fromString(((TextComponent) event.getCurrentItem().getItemMeta().lore().get(0)).content()).toLocation());
        Bukkit.getPlayer(player.getUniqueId()).playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 50, 5);
    }
}
