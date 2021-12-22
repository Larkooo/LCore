package com.larko.LCore.Economy;

import com.larko.LCore.Structures.LPlayer;
import com.larko.LCore.Structures.Shop;
import com.larko.LCore.Structures.ShopItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.StyleBuilderApplicable;
import net.kyori.adventure.text.format.TextDecoration;
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
import org.bukkit.inventory.meta.BookMeta;
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
            Inventory shopInventory = Bukkit.createInventory(null, 54, Component.text(shop.getTitle()));
            shop.getItems().forEach(item -> {
                ItemStack itemStack = item.getItem();
                ItemMeta itemMeta = itemStack.getItemMeta();

                ArrayList<Component> lore = new ArrayList<>();
                lore.add(Component.text("Description : ").append(Component.text(item.getDescription()).color(NamedTextColor.BLUE)));
                lore.add(Component.text("Price : ").append(Component.text(item.getPrice() + " LCoins").color(NamedTextColor.GOLD)));
                lore.add(Component.text("Vendor : ").append(Component.text(Bukkit.getOfflinePlayer(item.getVendor().getUuid()).getName()).color(NamedTextColor.WHITE)));
                lore.add(Component.text(""));
                lore.add(Component.text(""));
                lore.add(Component.text(item.getUuid().toString()).color(NamedTextColor.LIGHT_PURPLE));
                itemMeta.lore(lore);

                itemStack.setItemMeta(itemMeta);

                shopInventory.addItem(itemStack);
            });
            player.openInventory(shopInventory);
            return true;
        } else if (label.equalsIgnoreCase("sell")) {
            ItemStack selectedItem = player.getInventory().getItemInMainHand();
            if (selectedItem.getType().equals(Material.AIR)) {
                player.sendMessage("You cannot sell " + ChatColor.ITALIC + selectedItem.getType());
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
                            selectedItem.getI18NDisplayName() +
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
        Shop shop = Shop.getInstance();
        if (!(event.getView().getTitle().equals(shop.getTitle()))) return;
        event.setCancelled(true);

        HumanEntity clickAuthor = event.getWhoClicked();
        LPlayer lPlayer = LPlayer.findByUUID(clickAuthor.getUniqueId());
        ItemStack itemStack = event.getCurrentItem();
        ItemMeta itemMeta = itemStack.getItemMeta();

        // The information about the item is contained in the lore of the item
        // just some string manipulation to retrieve the price and vendor
        double price = Double.parseDouble(((TextComponent)itemMeta.lore().get(1).children().get(0)).content().replace(" LCoins", ""));
        LPlayer vendor = LPlayer.findByUUID(Bukkit.getOfflinePlayerIfCached(((TextComponent) itemMeta.lore().get(2).children().get(0)).content()).getUniqueId());
        String uuid = ((TextComponent) itemMeta.lore().get(5)).content();

        // if client wants to buy only 1 item at a time, divide the price and reduce amount of item in inv
        //if (event.isLeftClick()) {
        //    price /= itemStack.getAmount();
        //}

        if (price > lPlayer.getLCoins()) {
            clickAuthor.sendMessage(ChatColor.RED + "You do not have enough LCoins to buy this item");
            return;
        }

        // just some checks to avoid loss of money
        if (lPlayer.setLCoins(lPlayer.getLCoins() - price)) {
            if(!vendor.setLCoins(vendor.getLCoins() + price)) {
                clickAuthor.sendMessage(ChatColor.RED + "An error occurred, refunding you...");
                lPlayer.setLCoins(lPlayer.getLCoins() + price);
                return;
            }

            // if want to buy only 1 unity of item, reduce amount in shop
            //if (event.isLeftClick()) {
            //    itemStack.setAmount(itemStack.getAmount() - 1);
            //    shop.findItemByUuid(UUID.fromString(uuid)).setItem(itemStack);
            //    event.getInventory().setItem(event.getRawSlot(), itemStack);
//
            //    // now set to 1 so we give the player only 1 unity of the items
            //    itemStack.setAmount(1);
            //}
            // if wants to buy everything, just remove the item directly from the shop
            //else {
                // we have to do this check because we are directly interacting with the json
                if (!shop.removeItem(UUID.fromString(uuid))) {
                    // if we cant seem to remove the item from the shop, refund the client and vendor
                    clickAuthor.sendMessage(ChatColor.RED + "An error occurred, refunding in progress...");
                    // give back money to client
                    lPlayer.setLCoins(lPlayer.getLCoins() + price);
                    // remove money from vendor
                    vendor.setLCoins(vendor.getLCoins() - price);
                    return;
                };

                event.getInventory().removeItem(itemStack);
            //}

            // The bill that the player will receive when he buys the item
            // a written book
            ItemStack billBook = new ItemStack(Material.WRITTEN_BOOK, 1);
            BookMeta billMeta = (BookMeta) billBook.getItemMeta();
            billMeta.setAuthor(shop.getTitle());
            billMeta.title(Component.text("Purchase of " + itemStack.getType().name()).style(Style.style(TextDecoration.BOLD)));
            billMeta.pages(
                    Component.text().content("\n")
                            .append(Component.text( "Purchase of " + itemStack.getType().name() + "\n\n")
                                    .style(Style.style(TextDecoration.ITALIC, TextDecoration.BOLD)))
                            .append(Component.text("Item : " + itemStack.getType().name() + "\n"))
                            .append(Component.text("Amount : " + itemStack.getAmount() + "\n"))
                            // vendor
                            .append(itemMeta.lore().get(2))
                            .append(Component.text("\nTotal Price : "))
                            .append(Component.text(price + " LCoins").color(NamedTextColor.GOLD))
                            .append(Component.text("\n\nShopItem Reference : "))
                            .append(Component.text(uuid).color(NamedTextColor.LIGHT_PURPLE))
                            .build()
            );
            billBook.setItemMeta(billMeta);

            // Remove the shop lore, so the player doesnt receive the item with
            // the tags that have been put for the shop
            itemMeta.lore(new ArrayList<>());
            itemStack.setItemMeta(itemMeta);

            clickAuthor.getInventory().addItem(itemStack);
            clickAuthor.getInventory().addItem(billBook);
            clickAuthor.sendMessage(ChatColor.GREEN +
                    "You bought " +
                            ChatColor.BLUE +
                            itemStack.getI18NDisplayName() +
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
                        itemStack.getI18NDisplayName() +
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
