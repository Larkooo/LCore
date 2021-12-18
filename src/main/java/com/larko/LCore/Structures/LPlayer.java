package com.larko.LCore.Structures;

import com.larko.LCore.Main;
import com.larko.LCore.Utils.AuthUtils;
import com.larko.LCore.Utils.ClaimUtils;
import com.larko.LCore.Utils.EconomyUtils;
import com.larko.LCore.Utils.HomeUtils;
import org.bukkit.Location;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class LPlayer {
    private static ArrayList<LPlayer> players = new ArrayList<>();
    private UUID uuid;
    private String hashedPassword;
    private ArrayList<Home> homes;
    private ArrayList<Claim> claims;
    private double lCoins;
    private String linkedDiscordId;
    private boolean connected;
    private boolean inClaim;
    //private org.bukkit.entity.Player entity;

    public LPlayer(UUID uuid, String hashedPassword, ArrayList<Home> homes, ArrayList<Claim> claims, double lCoins, @Nullable String linkedDiscordId /*, org.bukkit.entity.Player player */) {
        this.uuid = uuid;
        this.hashedPassword = hashedPassword;
        this.homes = homes;
        this.claims = claims;
        this.lCoins = lCoins;
        this.linkedDiscordId = linkedDiscordId;
        this.connected = false;
        //this.entity = player;
        players.add(this);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public boolean login(String password) {
        if (!BCrypt.checkpw(password, hashedPassword)) return false;
        connected = true;
        return true;
    }

    public boolean isConnected() { return this.connected; }
    public void setConnected(boolean connected) { this.connected = connected; }

    public double getLCoins() { return this.lCoins; }
    public boolean setLCoins(double lCoins) {
        if (!EconomyUtils.setLCoinsDB(uuid, lCoins)) return false;
        this.lCoins = lCoins;
        return true;
    }
    
    public boolean isInClaim() { return inClaim; }
    public void setInClaim(boolean inClaim) { this.inClaim = inClaim; }

    public String getHashedPassword() { return this.hashedPassword; }

    public ArrayList<Claim> getClaims() {
        return this.claims;
    }

    public boolean addClaim(Location location, int radius) {
        boolean claimAdded = ClaimUtils.addClaimDB(this.uuid, location, radius);
        if(claimAdded)
            return claims.add(new Claim(new Position(location.getX(), location.getY(), location.getZ(), location.getWorld().getEnvironment()), radius, new JSONArray()));
        return false;
    }

    public boolean removeClaim(Location location) {
        try {
            boolean claimRemoved = ClaimUtils.removeClaimDB(this.uuid, location);
            if(claimRemoved) {
                boolean removed = false;
                for(Claim claim : this.claims) {
                    Position claimPos = claim.getPosition();
                    if(claim.inRadius(location)) {
                        this.claims.remove(claim);
                        removed = true;
                    }
                }
                return removed;
            }
            return false;
        } catch(Exception err) {
            // ignore
            return true;
        }
    }

    public boolean addHome(String name, Location location) {
        if(HomeUtils.addHomeDB(this.uuid, location, name))
            return this.homes.add(new Home(name, new Position(location.getX(), location.getY(), location.getZ(), location.getWorld().getEnvironment())));
        return false;
    }

    public boolean removeHome(String name) {
        if(HomeUtils.delHomeDB(this.uuid, name))
            for(Home home : this.homes) {
                if(home.getName().equals(name)) {
                    return this.homes.remove(home);
                }
            }
        return false;
    }

    public Home getHome(String name) {
        for(Home home : this.homes) {
            if(home.getName().equals(name))
                return home;
        }
        return null;
    }

    public ArrayList<Home> getHomes() {
        return this.homes;
    }

    public Claim getClaim(Location location) {
        Claim found = null;
        for(Claim claim : this.claims) {
            if(claim.inRadius(location)) {
                found = claim;
            }
        }
        return found;
    }

    public String getLinkedDiscordId() {
        return linkedDiscordId;
    }

    public boolean setLinkedDiscordId(String discordId) {
        if (AuthUtils.linkDiscordAccount(uuid, discordId))
        {
            linkedDiscordId = discordId;
            return true;
        }
        return false;
    }

    public boolean unlinkDiscord() {
        if (linkedDiscordId == null) return true;
        if (AuthUtils.unlinkDiscordAccount(uuid)) {
            linkedDiscordId = null;
            return true;
        }
        return false;
    }

    public static LPlayer fromJSON(JSONObject jsonObject) {
        // claims
        Iterator<JSONObject> claimsIterator = ((JSONArray) jsonObject.get("claims")).iterator();
        ArrayList<Claim> claims = new ArrayList<>();
        while(claimsIterator.hasNext()) {
            JSONObject claimJson = claimsIterator.next();
            claims.add(
                    Claim.fromJSON(claimJson)
            );
        }

        // homes
        JSONObject homesJson = ((JSONObject) jsonObject.get("homes"));
        Iterator<String> homesKeys = homesJson.keySet().iterator();
        ArrayList<Home> homes = new ArrayList<>();
        while(homesKeys.hasNext()) {
            String key = homesKeys.next();
            homes.add(new Home(key, Position.fromStrings(homesJson.get(key).toString(), "overworld")));
        }

        LPlayer player = new LPlayer(UUID.fromString(jsonObject.get("uuid").toString()), jsonObject.get("password").toString(), homes, claims, jsonObject.containsKey("lCoins") ? (double) jsonObject.get("lCoins") : 0.0, jsonObject.containsKey("discordId") ? jsonObject.get("discordId").toString() : null);
        return player;
    }

    public static ArrayList<LPlayer> getPlayers() {
        return players;
    }

    public static LPlayer findByUUID(UUID uuid) {
        for (LPlayer player : players) {
            if(player.uuid.equals(uuid))
                return player;

        }
        return null;
    }

    public static LPlayer findByDiscord(String discordId) {
        for (LPlayer player : players) {
            if (player.getLinkedDiscordId() == null) continue;
            if (player.getLinkedDiscordId().equals(discordId)) return player;
        }
        return null;
    }

}
