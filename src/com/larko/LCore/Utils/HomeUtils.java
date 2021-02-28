package com.larko.LCore.Utils;

import com.larko.LCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class HomeUtils {
    static public boolean addHomeDB(UUID playerUuid, Location playerLoc, String name) {
        JSONParser jsonParser = new JSONParser();
        boolean createdHome = false;
        try {
            Object obj = jsonParser.parse(new FileReader(new File(Utilities.dataFolder, "players.json")));
            JSONArray players = (JSONArray) obj;
            // JSON Iterator
            Iterator<JSONObject> iterator = players.iterator();
            // Cached players Iterator
            Iterator<JSONObject> cachedIterator = Main.cachedPlayersData.iterator();

            while (iterator.hasNext() && cachedIterator.hasNext()){
                JSONObject playerObj = iterator.next();
                JSONObject cachedPlayer = cachedIterator.next();
                if(playerUuid.toString().equals((String) playerObj.get("uuid"))) {
                    JSONObject homes = (JSONObject) playerObj.get("homes");
                    JSONObject cachedHomes = (JSONObject) cachedPlayer.get("homes");

                    // Put to json
                    homes.put(name, playerLoc.getX() + " " + playerLoc.getY() + " " + playerLoc.getZ());
                    // Put to cache
                    cachedHomes.put(name, playerLoc.getX() + " " + playerLoc.getY() + " " + playerLoc.getZ());

                    FileWriter playersFile = new FileWriter(new File(Utilities.dataFolder, "players.json"));
                    playersFile.write(players.toJSONString());
                    playersFile.flush();
                    playersFile.close();
                    createdHome = true;
                    break;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return createdHome;
    }

    static public boolean delHomeDB(UUID uuid, String name) {
        JSONParser jsonParser = new JSONParser();
        boolean deletedHome = false;
        try {
            Object obj = jsonParser.parse(new FileReader(new File(Utilities.dataFolder, "players.json")));
            JSONArray players = (JSONArray) obj;
            // JSON Iterator
            Iterator<JSONObject> iterator = players.iterator();
            // Cached players Iterator
            Iterator<JSONObject> cachedIterator = Main.cachedPlayersData.iterator();
            while (iterator.hasNext() && cachedIterator.hasNext()){
                JSONObject playerObj = iterator.next();
                JSONObject cachedPlayer = cachedIterator.next();
                if(uuid.toString().equals((String) playerObj.get("uuid"))) {
                    JSONObject homes = (JSONObject) playerObj.get("homes");
                    JSONObject cachedHomes = (JSONObject) cachedPlayer.get("homes");
                    // Remove from json
                    homes.remove(name);
                    // Remove from cache
                    cachedHomes.remove(name);


                    FileWriter playersFile = new FileWriter(new File(Utilities.dataFolder, "players.json"));
                    playersFile.write(players.toJSONString());
                    playersFile.flush();
                    playersFile.close();
                    deletedHome = true;
                    break;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return deletedHome;
    }
}
