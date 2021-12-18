package com.larko.LCore.Structures;

import com.larko.LCore.Utils.ClaimUtils;
import org.bukkit.Location;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class Claim {
    private Position position;
    private int radius;
    private ArrayList<UUID> authorizedPlayers;

    public Claim(Position position, int radius, ArrayList<UUID> authorizedPlayers) {
        this.position = position;
        this.radius = radius;
        this.authorizedPlayers = authorizedPlayers;
    }

    public Position getPosition() {
        return this.position;
    }

    public int getRadius() {
        return this.radius;
    }

    public boolean match(Location location) {
        Position pos = this.position;
        return pos.getX() == location.getX() && pos.getY() == location.getY() && pos.getZ() == location.getZ() && pos.getEnv() == location.getWorld().getEnvironment();
    }

    public boolean inRadius(Location location) {
        boolean isInRadius = false;
        double playerX = location.getX();
        double playerY = location.getY();
        double playerZ = location.getZ();


        double x1 = this.position.getX() - radius;
        double y1 = this.position.getY() - radius;
        double z1 = this.position.getZ() - radius;

        double x2 = this.position.getX() + radius;
        double y2 = this.position.getY() + radius;
        double z2 = this.position.getZ() + radius;

        if(((playerX > x1) && (playerX < x2)) && ((playerY > y1) && (playerY < y2)) && ((playerZ > z1) && (playerZ < z2))) {
            isInRadius = true;
        }
        return isInRadius;
    }

    public void addPlayer(UUID uuid) {
        if(!this.authorizedPlayers.contains(uuid))
            this.authorizedPlayers.add(uuid);
    }

    public void removePlayer(UUID uuid) {
        if(this.authorizedPlayers.contains(uuid))
            this.authorizedPlayers.remove(uuid);
    }

    public ArrayList<UUID> getAuthorizedPlayers()
    {
        return this.authorizedPlayers;
    }

    public static Claim fromJSON(JSONObject jsonObject) {
        // json array to arraylist with uuids
        JSONArray authorizedPlayers = (JSONArray) jsonObject.get("players");
        ArrayList<UUID> converted = new ArrayList<>();
        for (int i = 0; i < authorizedPlayers.size(); i++) {
            String uuid = (String) authorizedPlayers.get(i);
            converted.add(UUID.fromString(uuid));
        }
        Claim claim = new Claim(
                Position.fromStrings(
                        jsonObject.get("pos").toString(),
                        jsonObject.get("dimension").toString()
                ),
                Integer.parseInt(jsonObject.get("radius").toString()),
                converted
        );
    return claim;
    }
}
