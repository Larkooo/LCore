package com.larko.LCore.Structures;

import com.larko.LCore.Utils.EconomyUtils;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class Shop {
    private String title;
    private boolean open;
    private ArrayList<ShopItem> items;
    private static Shop shop;

    Shop(String title, boolean open, ArrayList<ShopItem> items) {
        this.title = title;
        this.open = open;
        this.items = items;

        shop = this;
    }

    public static Shop getInstance() { return shop; }

    public String getTitle() { return title; }
    public boolean isOpen() { return open; }
    public ArrayList<ShopItem> getItems() { return items; }

    public boolean addItem(ShopItem item) {
        if (!EconomyUtils.addShopItem(item.getUuid(), item.getItem(), item.getDescription(), item.getPrice(), item.getVendor().getUuid())) return false;
        items.add(item);
        return true;
    }

    public boolean removeItem(UUID uuid) {
        if (!EconomyUtils.removeShopItem(uuid)) return false;
        for (ShopItem item : items) {
            if (item.getUuid().equals(uuid)) {
                items.remove(item);
                break;
            }
        }
        return true;
    }
    /*public boolean removeItem(int index) {
        if (!EconomyUtils.removeShopItem(index)) return false;
        items.remove(index);
        return true;
    }*/

    public static Shop fromJSON(JSONObject shop) {
        String title = (String) shop.get("title");
        boolean open = (boolean) shop.get("open");
        JSONArray items = (JSONArray) shop.get("items");

        ArrayList<ShopItem> deserializedItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            JSONObject item = (JSONObject) items.get(i);
            deserializedItems.add(new ShopItem(UUID.fromString((String) item.get("uuid")), ItemStack.deserialize(item), (String) item.get("description"), (double) item.get("price"), LPlayer.findByUUID(UUID.fromString((String) item.get("vendor")))));
        }
        return new Shop(title, open, deserializedItems);
    }

    public ShopItem findItemByUuid(UUID uuid) {
        for (ShopItem item : items) {
            if (item.getUuid().equals(uuid)) {
                return item;
            }
        }
        return null;
    }

}
