package com.larko.LCore.Economy;

import com.larko.LCore.Structures.LPlayer;
import com.larko.LCore.Structures.Shop;
import com.larko.LCore.Structures.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ShopModule implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;
        if (label.equalsIgnoreCase("shop")) {
            Shop shop = Shop.getInstance();
            if (!shop.isOpen()) {
                player.sendMessage(ChatColor.DARK_GREEN + "The shop is currently " + ChatColor.BOLD + "closed");
                return false;
            }
            Inventory shopInventory = Bukkit.createInventory(null, 54, shop.getTitle());
            shop.getItems().forEach(item -> {
                ItemStack itemStack = item.getItem();
                ItemMeta itemMeta = itemStack.getItemMeta();

                ArrayList<String> lore = new ArrayList<>();
                lore.add("Description : " + item.getDescription());
                lore.add("Price : " + ChatColor.GOLD + "" + item.getPrice() + " LCoins");
                lore.add("Vendor : " + Bukkit.getOfflinePlayer(item.getVendor().getUuid()).getName());
                lore.add(item.getUuid().toString());
                itemMeta.setLore(lore);

                itemStack.setItemMeta(itemMeta);

                shopInventory.addItem(itemStack);
            });
            player.openInventory(shopInventory);
            return true;
        } else if (label.equalsIgnoreCase("sell")) {
            ItemStack selectedItem = player.getInventory().getItemInMainHand();
            if (selectedItem.getType().equals(Material.AIR)) {
                player.sendMessage("You cannot sell " + ChatColor.ITALIC + "air");
                return false;
            }
            if (args.length < 1) {
                player.sendMessage(ChatColor.DARK_RED + "You must set a price and a description for the item you're selling");
                return false;
            }
            if (args.length < 2) {
                player.sendMessage(ChatColor.DARK_RED + "You must set a description for the item you're selling");
                return false;
            }
            double price = Double.parseDouble(args[0]);
            if (price < 0) {
                player.sendMessage(ChatColor.DARK_RED + "Please enter a valid price");
                return false;
            }
            String description = args[1];

            player.getInventory().remove(selectedItem);
            if (!Shop.getInstance().addItem(new ShopItem(UUID.randomUUID(), selectedItem, description, price, LPlayer.findByUUID(player.getUniqueId())))) {
                player.sendMessage(ChatColor.RED + "An error occurred, could not sell item, refunding you...");
                player.getInventory().addItem(selectedItem);
                player.updateInventory();
                return false;
            }
            player.sendMessage(
                    ChatColor.GREEN +
                            "You've put " +
                            ChatColor.BLUE +
                            selectedItem.getType().name() +
                            ChatColor.GREEN +
                            " on the shop for sell for " +
                            ChatColor.GOLD + price + " LCoins"
            );
            return true;
        }
        return false;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!event.getView().getTitle().equals(Shop.getInstance().getTitle())) return;
        event.setCancelled(true);

        HumanEntity clickAuthor = event.getWhoClicked();
        LPlayer lPlayer = LPlayer.findByUUID(clickAuthor.getUniqueId());
        ItemStack itemStack = event.getCurrentItem();
        ItemMeta itemMeta = itemStack.getItemMeta();

        double price = Double.parseDouble(itemMeta.getLore().get(1).replace("Price : §6", "").replace(" LCoins", "")) * itemStack.getAmount();
        LPlayer vendor = LPlayer.findByUUID(Bukkit.getOfflinePlayer(itemMeta.getLore().get(2).replace("Vendor : ", "")).getUniqueId());
        String uuid = itemMeta.getLore().get(3);

        itemMeta.setLore(new ArrayList<>());
        itemStack.setItemMeta(itemMeta);

        if (price > lPlayer.getLCoins()) {
            clickAuthor.sendMessage(ChatColor.RED + "You do not have enough LCoins to buy this item");
            return;
        }

        if (lPlayer.setLCoins(lPlayer.getLCoins() - price)) {
            if(!vendor.setLCoins(vendor.getLCoins() + price)) {
                clickAuthor.sendMessage(ChatColor.RED + "An error occurred, refunding you...");
                lPlayer.setLCoins(lPlayer.getLCoins() + price);
                return;
            }

            if (!Shop.getInstance().removeItem(UUID.fromString(uuid))) return;
            event.getInventory().removeItem(itemStack);

            clickAuthor.getInventory().addItem(itemStack);
            clickAuthor.sendMessage(ChatColor.GREEN +
                    "You bought " +
                            ChatColor.BLUE +
                            itemStack.getType().name() +
                            ChatColor.GREEN + " for" +
                            ChatColor.GOLD + " " +
                            price + " LCoins"
            );
            if (vendor.isConnected()) {
                Bukkit.getPlayer(vendor.getUuid()).sendMessage(ChatColor.BLUE +
                        clickAuthor.getName() +
                        ChatColor.WHITE +
                        " bought your " +
                        ChatColor.ITALIC +
                        itemStack.getType().name() +
                        ChatColor.RESET +
                        " for " +
                        ChatColor.GOLD +
                        "" +
                        price +
                        " LCoins"
                );
            }
        }
    }
}
