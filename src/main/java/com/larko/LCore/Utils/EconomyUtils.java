package com.larko.LCore.Utils;

import com.google.gson.Gson;
import com.larko.LCore.Structures.LPlayer;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class EconomyUtils {
    public static boolean setLCoinsDB(UUID playerUuid, double lCoins) {
        JSONParser jsonParser = new JSONParser();
        try {
            Object obj = jsonParser.parse(new FileReader(new File(Utilities.dataFolder, "players.json")));

            JSONArray players = (JSONArray) obj;
            Iterator<JSONObject> iterator = players.iterator();
            while (iterator.hasNext()){
                JSONObject playerObj = iterator.next();
                if(playerUuid.toString().equals((String) playerObj.get("uuid"))) {
                    playerObj.put("lCoins", lCoins);

                    FileWriter playersFile = new FileWriter(new File(Utilities.dataFolder, "players.json"));
                    playersFile.write(players.toJSONString());
                    playersFile.flush();
                    playersFile.close();
                    return true;
                }
            }

            return false;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addShopItem(UUID uuid, ItemStack item, String description, double price, UUID vendorUuid) {
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject shop = (JSONObject) jsonParser.parse(new FileReader(new File(Utilities.dataFolder, "shop.json")));

            JSONArray items = (JSONArray) shop.get("items");

            Map<String, Object> data = item.serialize();
            data.put("uuid", uuid.toString());
            data.put("price", price);
            data.put("vendor", vendorUuid.toString());
            data.put("description", description);

            items.add(data);

            FileWriter shopWriter = new FileWriter(new File(Utilities.dataFolder, "shop.json"));
            shopWriter.write((new Gson()).toJson(shop));
            shopWriter.flush();
            shopWriter.close();

            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeShopItem(UUID uuid) {
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject shop = (JSONObject) jsonParser.parse(new FileReader(new File(Utilities.dataFolder, "shop.json")));

            JSONArray items = (JSONArray) shop.get("items");

            for (int i = 0; i < items.size(); i++) {
                JSONObject item = (JSONObject) items.get(i);
                if (uuid.toString().equals((String)item.get("uuid"))) {
                    items.remove(item);
                    break;
                }
            }

            FileWriter shopWriter = new FileWriter(new File(Utilities.dataFolder, "shop.json"));
            shopWriter.write(shop.toJSONString());
            shopWriter.flush();
            shopWriter.close();

            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
