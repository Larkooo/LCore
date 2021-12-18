package com.larko.LCore.Utils;

import com.larko.LCore.Auth.AuthModule;
import com.larko.LCore.Structures.LPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import com.larko.LCore.Main;
import org.mindrot.jbcrypt.BCrypt;

public class AuthUtils {
    public enum AuthState {
        AWAITING_REGISTER,
        AWAITING_LOGIN
    }

    static public void askAuth(Player player) {
        boolean hasAlreadyAccount = isPlayerRegistered(player.getUniqueId());

        if(hasAlreadyAccount) {
            player.sendTitle(ChatColor.RED + "Login", ChatColor.GRAY + "Please type your password in the chat", 1,100,3);
            HashMap<String, Object> data = new HashMap<String, Object>(){{
                put("authState", AuthState.AWAITING_LOGIN);
                put("tries", 0);
            }};
            AuthModule.awaitingLoginPlayers.put(player.getUniqueId(), data);
        } else {
            player.sendTitle(ChatColor.RED +"Create an account",  ChatColor.GRAY + "Please choose a password and type it in the chat", 1,100,3);
            HashMap<String, Object> data = new HashMap<String, Object>(){{
                put("authState", AuthState.AWAITING_REGISTER);
                put("tries", 0);
            }};
            AuthModule.awaitingLoginPlayers.put(player.getUniqueId(), data);
        }
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("LCore"), () -> {
            // Give the players some attributes / effects while he's not logged in.
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100000, 100, true, false, false));
            player.setCanPickupItems(false);
            // Make him invulnerable to prevent other players from killing him while logging him
            player.setInvulnerable(true);
        });
    }

    static public boolean isPlayerRegistered(UUID uuid) {
        return LPlayer.findByUUID(uuid) != null;
    }

    static public boolean registerPlayer(UUID uuid, String password) {
        JSONParser jsonParser = new JSONParser();
        try {
            Object obj = jsonParser.parse(new FileReader(new File(Utilities.dataFolder, "players.json")));

            JSONArray players = (JSONArray) obj;
            JSONObject player = new JSONObject();
            player.put("uuid", uuid.toString());
            // double
            player.put("lCoins", 5000.d);
            player.put("password", BCrypt.hashpw(password, BCrypt.gensalt()));
            player.put("homes", new JSONObject());
            player.put("claims", new JSONArray());

            // Add to json
            players.add(player);

            // just add to the players list
            LPlayer.fromJSON(player);


            FileWriter playersFile = new FileWriter(new File(Utilities.dataFolder, "players.json"));
            playersFile.write(players.toJSONString());
            playersFile.flush();
            playersFile.close();

            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static public boolean linkDiscordAccount(UUID uuid, String discordId) {
        JSONParser jsonParser = new JSONParser();
        try {
            Object obj = jsonParser.parse(new FileReader(new File(Utilities.dataFolder, "players.json")));

            JSONArray players = (JSONArray) obj;
            Iterator<JSONObject> iterator = players.iterator();
            int count = 0;
            while (iterator.hasNext()){
                JSONObject playerObj = iterator.next();
                if(uuid.toString().equals((String) playerObj.get("uuid"))) {
                    playerObj.put("discordId", discordId);

                    FileWriter playersFile = new FileWriter(new File(Utilities.dataFolder, "players.json"));
                    playersFile.write(players.toJSONString());
                    playersFile.flush();
                    playersFile.close();
                    return true;
                }
                count++;
            }

            return false;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static public boolean unlinkDiscordAccount(UUID uuid) {
        JSONParser jsonParser = new JSONParser();
        try {
            Object obj = jsonParser.parse(new FileReader(new File(Utilities.dataFolder, "players.json")));

            JSONArray players = (JSONArray) obj;
            Iterator<JSONObject> iterator = players.iterator();
            int count = 0;
            while (iterator.hasNext()){
                JSONObject playerObj = iterator.next();
                if(uuid.toString().equals((String) playerObj.get("uuid"))) {
                    playerObj.put("discordId", null);

                    FileWriter playersFile = new FileWriter(new File(Utilities.dataFolder, "players.json"));
                    playersFile.write(players.toJSONString());
                    playersFile.flush();
                    playersFile.close();
                    return true;
                }
                count++;
            }

            return false;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
