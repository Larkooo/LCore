package com.larko.LCore.Utils;

import com.larko.LCore.Main;
import com.larko.LCore.Structures.Claim;
import com.larko.LCore.Structures.LPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.UUID;

public class ClaimUtils {
    static public boolean addClaimDB(UUID uuid, Location location, int radius){
        JSONParser jsonParser = new JSONParser();
        boolean createdClaim = false;
        try {
            Object obj = jsonParser.parse(new FileReader(new File(Utilities.dataFolder, "players.json")));
            JSONArray players = (JSONArray) obj;
            // JSON Iterator
            Iterator<JSONObject> iterator = players.iterator();
            while (iterator.hasNext()){
                JSONObject playerObj = iterator.next();
                if(uuid.toString().equals((String) playerObj.get("uuid"))) {
                    JSONArray claims = (JSONArray) playerObj.get("claims");
                    JSONObject claimToAdd = new JSONObject();
                    claimToAdd.put("dimension", location.getWorld().getEnvironment().name());
                    claimToAdd.put("pos", location.getX() + " " + location.getY() + " " + location.getZ());
                    claimToAdd.put("radius", String.valueOf(radius));
                    claimToAdd.put("players", new JSONArray());
                    // Add to JSON
                    claims.add(claimToAdd);

                    FileWriter playersFile = new FileWriter(new File(Utilities.dataFolder, "players.json"));
                    playersFile.write(players.toJSONString());
                    playersFile.flush();
                    playersFile.close();
                    createdClaim = true;
                    break;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return createdClaim;
    }

    static public boolean removeClaimDB(UUID uuid, Location playerLoc) {
        JSONParser jsonParser = new JSONParser();
        try {
            Object obj = jsonParser.parse(new FileReader(new File(Utilities.dataFolder, "players.json")));
            JSONArray players = (JSONArray) obj;
            // JSON Iterator
            Iterator<JSONObject> iterator = players.iterator();
            while (iterator.hasNext()) {
                JSONObject playerObj = iterator.next();
                if (uuid.toString().equals((String) playerObj.get("uuid"))) {
                    JSONArray claims = (JSONArray) playerObj.get("claims");
                    // JSON claims iterator
                    Iterator<JSONObject> claimsIterator = claims.iterator();
                    while(claimsIterator.hasNext()) {
                        JSONObject claim = claimsIterator.next();
                        boolean isInRadius = Utilities.checkIfInRadius(playerLoc, (String)claim.get("pos"),Integer.parseInt((String)claim.get("radius")));
                        if(isInRadius) {
                            // JSON remove
                            claimsIterator.remove();
                            FileWriter playersFile = new FileWriter(new File(Utilities.dataFolder, "players.json"));
                            playersFile.write(players.toJSONString());
                            playersFile.flush();
                            playersFile.close();
                            return true;
                        }


                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    static public boolean addPlayerToClaim(UUID authorUuid, Location loc, UUID playerUuid) {
        JSONParser jsonParser = new JSONParser();
        try {
            Object obj = jsonParser.parse(new FileReader(new File(Utilities.dataFolder, "players.json")));
            JSONArray players = (JSONArray) obj;
            // JSON Iterator
            Iterator<JSONObject> iterator = players.iterator();
            while (iterator.hasNext()) {
                JSONObject playerObj = iterator.next();
                if (authorUuid.toString().equals((String) playerObj.get("uuid"))) {
                    JSONArray claims = (JSONArray) playerObj.get("claims");
                    // JSON claims iterator
                    Iterator<JSONObject> claimsIterator = claims.iterator();
                    while(claimsIterator.hasNext()) {
                        JSONObject claim = claimsIterator.next();
                        boolean isInRadius = Utilities.checkIfInRadius(loc, (String)claim.get("pos"),Integer.parseInt((String)claim.get("radius")));
                        if(isInRadius) {
                            // JSON
                            JSONArray playersClaim = (JSONArray) claim.get("players");
                            playersClaim.add(playerUuid.toString());
                            // Cache
                            LPlayer.findByUUID(authorUuid).getClaim(loc).addPlayer(playerUuid);

                            FileWriter playersFile = new FileWriter(new File(Utilities.dataFolder, "players.json"));
                            playersFile.write(players.toJSONString());
                            playersFile.flush();
                            playersFile.close();
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    static public boolean removePlayerFromClaim(UUID authorUuid, Location loc, UUID playerUuid) {
        JSONParser jsonParser = new JSONParser();
        try {
            Object obj = jsonParser.parse(new FileReader(new File(Utilities.dataFolder, "players.json")));
            JSONArray players = (JSONArray) obj;
            // JSON Iterator
            Iterator<JSONObject> iterator = players.iterator();
            while (iterator.hasNext()) {
                JSONObject playerObj = iterator.next();
                if (authorUuid.toString().equals((String) playerObj.get("uuid"))) {
                    JSONArray claims = (JSONArray) playerObj.get("claims");
                    // JSON claims iterator
                    Iterator<JSONObject> claimsIterator = claims.iterator();
                    while(claimsIterator.hasNext()) {
                        JSONObject claim = claimsIterator.next();
                        boolean isInRadius = Utilities.checkIfInRadius(loc, (String)claim.get("pos"),Integer.parseInt((String)claim.get("radius")));
                        if(isInRadius) {
                            // JSON
                            JSONArray playersClaim = (JSONArray) claim.get("players");
                            if(playersClaim.contains(playerUuid.toString())) {
                                playersClaim.remove(playerUuid.toString());
                            }
                            LPlayer.findByUUID(authorUuid).getClaim(loc).removePlayer(playerUuid);

                            FileWriter playersFile = new FileWriter(new File(Utilities.dataFolder, "players.json"));
                            playersFile.write(players.toJSONString());
                            playersFile.flush();
                            playersFile.close();
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkClaim(UUID uuid, Location location) {
        for(LPlayer player : LPlayer.getPlayers()) {
            for (Claim claim : player.getClaims()) {
                if(claim.inRadius(location) && !(player.getUuid().equals(uuid)) && !(claim.getAuthorizedPlayers().contains(uuid))) {
                    return false;
                }
            }
        }
        return true;
    }
    

    static public OfflinePlayer checkPlayerClaim(UUID uuid, Location location) {
        for(LPlayer player : LPlayer.getPlayers()) {
            for (Claim claim : player.getClaims()) {
                if(claim.inRadius(location)) {
                    return Bukkit.getOfflinePlayer(player.getUuid());
                }
            }
        }
        return null;

        /*JSONParser jsonParser = new JSONParser();
        JSONArray cachedPlayersData = (JSONArray) Main.cachedPlayersData;
        for(int i = 0; i < cachedPlayersData.size(); i++){
            JSONObject playerObj = (JSONObject) cachedPlayersData.get(i);
            JSONArray claims = (JSONArray) playerObj.get("claims");
            for(int n = 0; n < claims.size(); n++) {
                JSONObject claim = (JSONObject) claims.get(n);
                boolean isInRadius = Utilities.checkIfInRadius(location, (String)claim.get("pos"), Integer.parseInt((String)claim.get("radius")));
                //!(playerObj.get("uuid").toString().equals(uuid.toString())) &&
                if(isInRadius && location.getWorld() == Bukkit.getWorld("world")) {
                    UUID claimPlayerUUID = UUID.fromString(playerObj.get("uuid").toString());
                    return Bukkit.getOfflinePlayer(claimPlayerUUID);
                }
            }
        }*/
    } 
}
