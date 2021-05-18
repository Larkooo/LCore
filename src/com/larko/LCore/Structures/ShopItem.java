package com.larko.LCore.Structures;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.UUID;

public class ShopItem {
    private UUID uuid;
    private ItemStack item;
    private String description;
    private double price;
    private LPlayer vendor;

    public ShopItem(UUID uuid, ItemStack item, String description, double price, LPlayer vendor) {
        this.uuid = uuid;
        this.item = item;
        this.description = description;
        this.price = price;
        this.vendor = vendor;
    }

    public UUID getUuid() { return uuid; }
    public ItemStack getItem() { return item; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public LPlayer getVendor() { return vendor; }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public static ShopItem fromJSON(JSONObject data) {
        return new ShopItem(
                UUID.fromString((String) data.get("uuid")),
                ItemStack.deserialize(data),
                (String) data.get("description"),
                (double) data.get("price"),
                LPlayer.findByUUID(UUID.fromString((String) data.get("vendor")))
        );
    }

    public JSONObject toJSON() {
        Map<String, Object> data = item.serialize();
        data.put("uuid", uuid.toString());
        data.put("description", description);
        data.put("price", price);
        data.put("vendor", vendor.getUuid());
        return new JSONObject(data);
    }
}
