package com.larko.LCore;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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

        JSONParser jsonParser = new JSONParser();
        try {
            Object obj = jsonParser.parse(new FileReader(new File(dataFolder, "players.json")));

            JSONArray players = (JSONArray) obj;

            Iterator<JSONObject> iterator = players.iterator();
            while (iterator.hasNext()){
                JSONObject playerObj = iterator.next();
                if(uuid.toString().equals((String) playerObj.get("UUID"))) {
                    registered = true;
                    break;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
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

            players.add(player);

            FileWriter playersFile = new FileWriter(new File(dataFolder, "players.json"));
            playersFile.write(players.toJSONString());
            playersFile.flush();
            playersFile.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    static public boolean loginPlayer(UUID uuid, String password) {
        JSONParser jsonParser = new JSONParser();
        String checkPassword = "";
        try {
            Object obj = jsonParser.parse(new FileReader(new File(dataFolder, "players.json")));
            JSONArray players = (JSONArray) obj;
            Iterator<JSONObject> iterator = players.iterator();
            while (iterator.hasNext()){
                JSONObject playerObj = iterator.next();
                if(uuid.toString().equals((String) playerObj.get("UUID"))) {
                    checkPassword = (String) playerObj.get("password");
                    break;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return BCrypt.checkpw(password, checkPassword);
    }

    static public boolean addHome(Player player, String name) {
        JSONParser jsonParser = new JSONParser();
        boolean createdHome = false;
        try {
            Object obj = jsonParser.parse(new FileReader(new File(dataFolder, "players.json")));
            JSONArray players = (JSONArray) obj;
            Iterator<JSONObject> iterator = players.iterator();
            while (iterator.hasNext()){
                JSONObject playerObj = iterator.next();
                if(player.getUniqueId().toString().equals((String) playerObj.get("UUID"))) {
                    Location playerLoc = player.getLocation();
                    JSONObject homes = (JSONObject) playerObj.get("homes");
                    homes.put(name, playerLoc.getX() + " " + playerLoc.getY() + " " + playerLoc.getZ());

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
            Iterator<JSONObject> iterator = players.iterator();
            while (iterator.hasNext()){
                JSONObject playerObj = iterator.next();
                if(uuid.toString().equals((String) playerObj.get("UUID"))) {
                    JSONObject homes = (JSONObject) playerObj.get("homes");
                    homes.remove(name);

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
        try {
            Object obj = jsonParser.parse(new FileReader(new File(dataFolder, "players.json")));
            JSONArray players = (JSONArray) obj;
            Iterator<JSONObject> iterator = players.iterator();
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
        } catch(Exception e) {
            e.printStackTrace();
        }
        return home;
    }

    static public Set<String> getHomes(UUID uuid) {
        JSONParser jsonParser = new JSONParser();
        Set<String> homes = null;
        try {
            Object obj = jsonParser.parse(new FileReader(new File(dataFolder, "players.json")));
            JSONArray players = (JSONArray) obj;
            Iterator<JSONObject> iterator = players.iterator();
            while (iterator.hasNext()){
                JSONObject playerObj = iterator.next();
                if(uuid.toString().equals((String)playerObj.get("UUID"))) {
                    JSONObject homesObj = (JSONObject) playerObj.get("homes");
                    homes = homesObj.keySet();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return homes;
    }

    static public boolean addClaim(UUID uuid, Location location, int radius){
        JSONParser jsonParser = new JSONParser();
        boolean createdClaim = false;
        try {
            Object obj = jsonParser.parse(new FileReader(new File(dataFolder, "players.json")));
            JSONArray players = (JSONArray) obj;
            Iterator<JSONObject> iterator = players.iterator();
            while (iterator.hasNext()){
                JSONObject playerObj = iterator.next();
                if(uuid.toString().equals((String) playerObj.get("UUID"))) {
                    JSONArray claims = (JSONArray) playerObj.get("claims");
                    JSONObject claimToAdd = new JSONObject();
                    claimToAdd.put("pos", location.getX() + " " + location.getY() + " " + location.getZ());
                    claimToAdd.put("radius", radius);
                    claims.add(claimToAdd);

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
            Iterator<JSONObject> iterator = players.iterator();
            while (iterator.hasNext()) {
                JSONObject playerObj = iterator.next();
                if (uuid.toString().equals((String) playerObj.get("UUID"))) {
                    JSONArray claims = (JSONArray) playerObj.get("claims");
                    Iterator<JSONObject> claimsIterator = claims.iterator();
                    int count = 0;
                    while(claimsIterator.hasNext()) {
                        JSONObject claim = claimsIterator.next();
                        boolean isInRadius = checkIfInRadius(playerLoc, (String)claim.get("pos"), (int)(long)claim.get("radius"));
                        if(isInRadius) {
                            claims.remove(count);
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
        try {
            Object obj = jsonParser.parse(new FileReader(new File(dataFolder, "players.json")));
            JSONArray players = (JSONArray) obj;
            Iterator<JSONObject> iterator = players.iterator();
            while (iterator.hasNext()) {
                JSONObject playerObj = iterator.next();
                if (uuid.toString().equals((String) playerObj.get("UUID"))) {
                    JSONArray claims = (JSONArray) playerObj.get("claims");
                    return claims;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static public boolean checkClaim(UUID uuid, Location location) {
        JSONParser jsonParser = new JSONParser();
        try {
            Object obj = jsonParser.parse(new FileReader(new File(dataFolder, "players.json")));
            JSONArray players = (JSONArray) obj;
            Iterator<JSONObject> iterator = players.iterator();
            while (iterator.hasNext()){
                JSONObject playerObj = iterator.next();
                JSONArray claims = (JSONArray) playerObj.get("claims");
                Iterator<JSONObject> claimsIterator = claims.iterator();

                while(claimsIterator.hasNext()) {
                    JSONObject claim = claimsIterator.next();
                    //System.out.println("lol");
                    boolean isInRadius = checkIfInRadius(location, (String)claim.get("pos"), (int)(long)claim.get("radius"));
                    //System.out.println(isInRadius);
                    if(!(playerObj.get("UUID").toString().equals(uuid.toString())) && isInRadius && location.getWorld() == Bukkit.getWorld("world")) {
                        return false;
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        //System.out.println(check);
        return true;
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
