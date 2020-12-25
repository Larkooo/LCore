package com.larko.LCore;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class Utils {

    static File dataFolder;

    static public boolean isPlayerRegistered(UUID uuid) {
        boolean registered = false;


        Iterator<JSONObject> iterator = Main.cachedPlayers.iterator();
        while (iterator.hasNext()){
            JSONObject playerObj = iterator.next();
            if(uuid.toString().equals((String) playerObj.get("UUID"))) {
                registered = true;
                break;
            }
        }

        return registered;
    }

    static public void registerPlayer(UUID uuid, String password) {
        JSONParser jsonParser = new JSONParser();
        try {
            Object obj = jsonParser.parse(new FileReader(new File(dataFolder, "players.json")));

            JSONArray players = (JSONArray) obj;

            JSONObject player = new JSONObject();
            player.put("UUID", uuid.toString());
            player.put("password", BCrypt.hashpw(password, BCrypt.gensalt()));
            //player.put("password", password);
            player.put("homes", new JSONObject());
            player.put("claims", new JSONArray());

            // Add to json
            players.add(player);
            // Add to cache
            Main.cachedPlayers.add(player);


            FileWriter playersFile = new FileWriter(new File(dataFolder, "players.json"));
            playersFile.write(players.toJSONString());
            playersFile.flush();
            playersFile.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    static public boolean loginPlayer(UUID uuid, String password) {
        String checkPassword = "";
        Iterator<JSONObject> iterator = Main.cachedPlayers.iterator();
        while (iterator.hasNext()){
            JSONObject playerObj = iterator.next();
            if(uuid.toString().equals((String) playerObj.get("UUID"))) {
                checkPassword = (String) playerObj.get("password");
                break;
            }
        }
        return BCrypt.checkpw(password, checkPassword);
    }

    static public boolean addHome(Player player, String name) {
        JSONParser jsonParser = new JSONParser();
        boolean createdHome = false;
        try {
            Object obj = jsonParser.parse(new FileReader(new File(dataFolder, "players.json")));
            JSONArray players = (JSONArray) obj;
            // JSON Iterator
            Iterator<JSONObject> iterator = players.iterator();
            // Cached players Iterator
            Iterator<JSONObject> cachedIterator = Main.cachedPlayers.iterator();

            while (iterator.hasNext() && cachedIterator.hasNext()){
                JSONObject playerObj = iterator.next();
                JSONObject cachedPlayer = cachedIterator.next();
                if(player.getUniqueId().toString().equals((String) playerObj.get("UUID"))) {
                    Location playerLoc = player.getLocation();
                    JSONObject homes = (JSONObject) playerObj.get("homes");
                    JSONObject cachedHomes = (JSONObject) cachedPlayer.get("homes");

                    // Put to json
                    homes.put(name, playerLoc.getX() + " " + playerLoc.getY() + " " + playerLoc.getZ());
                    // Put to cache
                    cachedHomes.put(name, playerLoc.getX() + " " + playerLoc.getY() + " " + playerLoc.getZ());

                    FileWriter playersFile = new FileWriter(new File(dataFolder, "players.json"));
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

    static public boolean delHome(UUID uuid, String name) {
        JSONParser jsonParser = new JSONParser();
        boolean deletedHome = false;
        try {
            Object obj = jsonParser.parse(new FileReader(new File(dataFolder, "players.json")));
            JSONArray players = (JSONArray) obj;
            // JSON Iterator
            Iterator<JSONObject> iterator = players.iterator();
            // Cached players Iterator
            Iterator<JSONObject> cachedIterator = Main.cachedPlayers.iterator();
            while (iterator.hasNext() && cachedIterator.hasNext()){
                JSONObject playerObj = iterator.next();
                JSONObject cachedPlayer = cachedIterator.next();
                if(uuid.toString().equals((String) playerObj.get("UUID"))) {
                    JSONObject homes = (JSONObject) playerObj.get("homes");
                    JSONObject cachedHomes = (JSONObject) cachedPlayer.get("homes");
                    // Remove from json
                    homes.remove(name);
                    // Remove from cache
                    cachedHomes.remove(name);


                    FileWriter playersFile = new FileWriter(new File(dataFolder, "players.json"));
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

    static public Location getHome(UUID uuid, String name) {
        JSONParser jsonParser = new JSONParser();
        Location home = null;
        Iterator<JSONObject> iterator = Main.cachedPlayers.iterator();
        while (iterator.hasNext()){
            JSONObject playerObj = iterator.next();
            if(uuid.toString().equals((String) playerObj.get("UUID"))) {
                JSONObject homes = (JSONObject) playerObj.get("homes");
                String coordString = (String) homes.get(name);
                String[] splittedCoords = coordString.split("\\s+");
                home = new Location(Bukkit.getWorld("world"), Double.parseDouble(splittedCoords[0]), Double.parseDouble(splittedCoords[1]), Double.parseDouble(splittedCoords[2]));
                break;
            }
        }

        return home;
    }

    static public Set<String> getHomes(UUID uuid) {
        JSONParser jsonParser = new JSONParser();
        Set<String> homes = null;
        Iterator<JSONObject> iterator = Main.cachedPlayers.iterator();
        while (iterator.hasNext()){
            JSONObject playerObj = iterator.next();
            if(uuid.toString().equals((String)playerObj.get("UUID"))) {
                JSONObject homesObj = (JSONObject) playerObj.get("homes");
                homes = homesObj.keySet();
            }
        }
        return homes;
    }

    static public boolean addClaim(UUID uuid, Location location, int radius){
        JSONParser jsonParser = new JSONParser();
        boolean createdClaim = false;
        try {
            Object obj = jsonParser.parse(new FileReader(new File(dataFolder, "players.json")));
            JSONArray players = (JSONArray) obj;
            // JSON Iterator
            Iterator<JSONObject> iterator = players.iterator();
            // Cached players Iterator
            Iterator<JSONObject> cachedIterator = Main.cachedPlayers.iterator();
            while (iterator.hasNext() && cachedIterator.hasNext()){
                JSONObject playerObj = iterator.next();
                JSONObject cachedPlayer = cachedIterator.next();
                if(uuid.toString().equals((String) playerObj.get("UUID"))) {
                    JSONArray claims = (JSONArray) playerObj.get("claims");
                    JSONArray cachedClaims = (JSONArray) cachedPlayer.get("claims");
                    JSONObject claimToAdd = new JSONObject();
                    claimToAdd.put("pos", location.getX() + " " + location.getY() + " " + location.getZ());
                    claimToAdd.put("radius", String.valueOf(radius));
                    // Add to JSON
                    claims.add(claimToAdd);
                    // Add to cache
                    cachedClaims.add(claimToAdd);

                    FileWriter playersFile = new FileWriter(new File(dataFolder, "players.json"));
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

    static public boolean unClaim(UUID uuid, Location playerLoc) {
        JSONParser jsonParser = new JSONParser();
        try {
            Object obj = jsonParser.parse(new FileReader(new File(dataFolder, "players.json")));
            JSONArray players = (JSONArray) obj;
            // JSON Iterator
            Iterator<JSONObject> iterator = players.iterator();
            // Cached players Iterator
            Iterator<JSONObject> cachedIterator = Main.cachedPlayers.iterator();
            while (iterator.hasNext() && cachedIterator.hasNext()) {
                JSONObject playerObj = iterator.next();
                JSONObject cachedPlayer = cachedIterator.next();
                if (uuid.toString().equals((String) playerObj.get("UUID"))) {
                    JSONArray claims = (JSONArray) playerObj.get("claims");
                    JSONArray cachedClaims = (JSONArray) cachedPlayer.get("claims");
                    // JSON claims iterator
                    Iterator<JSONObject> claimsIterator = claims.iterator();
                    // Cached players claims iterator
                    Iterator<JSONObject> cachedClaimsIterator = cachedClaims.iterator();
                    int count = 0;
                    while(claimsIterator.hasNext()) {
                        JSONObject claim = claimsIterator.next();
                        JSONObject cachedClaim = cachedClaimsIterator.next();
                        boolean isInRadius = checkIfInRadius(playerLoc, (String)claim.get("pos"), (int)(long)claim.get("radius"));
                        if(isInRadius) {
                            // JSON remove
                            claims.remove(count);
                            // Cache remove
                            cachedClaim.remove(count);
                            FileWriter playersFile = new FileWriter(new File(dataFolder, "players.json"));
                            playersFile.write(players.toJSONString());
                            playersFile.flush();
                            playersFile.close();
                            return true;
                        }
                        count ++;


                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    static public JSONArray getClaims(UUID uuid) {
        JSONParser jsonParser = new JSONParser();
        Iterator<JSONObject> iterator = Main.cachedPlayers.iterator();
        while (iterator.hasNext()) {
            JSONObject playerObj = iterator.next();
            if (uuid.toString().equals((String) playerObj.get("UUID"))) {
                JSONArray claims = (JSONArray) playerObj.get("claims");
                return claims;
            }
        }
        return null;
    }

    static public boolean checkClaim(UUID uuid, Location location) {
        JSONParser jsonParser = new JSONParser();
        Iterator<JSONObject> iterator = Main.cachedPlayers.iterator();
        while (iterator.hasNext()){
            JSONObject playerObj = iterator.next();
            JSONArray claims = (JSONArray) playerObj.get("claims");
            Iterator<JSONObject> claimsIterator = claims.iterator();
            while(claimsIterator.hasNext()) {
                JSONObject claim = claimsIterator.next();
                boolean isInRadius = checkIfInRadius(location, (String)claim.get("pos"), Integer.parseInt((String)claim.get("radius")));
                if(!(playerObj.get("UUID").toString().equals(uuid.toString())) && isInRadius && location.getWorld() == Bukkit.getWorld("world")) {
                    return false;
                }
            }
        }
        //System.out.println(check);
        return true;
    }

    static public OfflinePlayer checkPlayerClaim(UUID uuid, Location location) {
        JSONParser jsonParser = new JSONParser();
        Iterator<JSONObject> iterator = Main.cachedPlayers.iterator();
        while (iterator.hasNext()){
            JSONObject playerObj = iterator.next();
            JSONArray claims = (JSONArray) playerObj.get("claims");
            Iterator<JSONObject> claimsIterator = claims.iterator();
            while(claimsIterator.hasNext()) {
                JSONObject claim = claimsIterator.next();
                boolean isInRadius = checkIfInRadius(location, (String)claim.get("pos"), Integer.parseInt((String)claim.get("radius")));
                //!(playerObj.get("UUID").toString().equals(uuid.toString())) &&
                if(isInRadius && location.getWorld() == Bukkit.getWorld("world")) {
                    UUID claimPlayerUUID = UUID.fromString(playerObj.get("UUID").toString());
                    return Bukkit.getOfflinePlayer(claimPlayerUUID);
                }
            }
        }
        //System.out.println(check);
        return null;
    }

    static public boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static public boolean checkIfInRadius(Location playerLoc, String coords, int radius) {
        boolean isInRadius = false;
        String[] splittedCoords = coords.split("\\s+");
        Double playerX = playerLoc.getX();
        Double playerY = playerLoc.getY();
        Double playerZ = playerLoc.getZ();

        Double x1 = Double.parseDouble(splittedCoords[0]) - radius;
        Double y1 = Double.parseDouble(splittedCoords[1]) - radius;
        Double z1 = Double.parseDouble(splittedCoords[2]) - radius;

        Double x2 = Double.parseDouble(splittedCoords[0]) + radius;
        Double y2 = Double.parseDouble(splittedCoords[1]) + radius;
        Double z2 = Double.parseDouble(splittedCoords[2]) + radius;

        if(((playerX > x1) && (playerX < x2)) && ((playerY > y1) && (playerY < y2)) && ((playerZ > z1) && (playerZ < z2))) {
            isInRadius = true;
        }
        return isInRadius;
    }
}
