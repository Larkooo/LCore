package com.larko.LCore.Structures;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Position {
    private double x;
    private double y;
    private double z;
    private World.Environment environment;

    public Position(double x, double y, double z, World.Environment environment) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.environment = environment;
    }

    public static Position fromStrings(String coords, String worldStr) {
        String[] splittedCoords = coords.split("\\s+");
        Double x = Double.parseDouble(splittedCoords[0]);
        Double y = Double.parseDouble(splittedCoords[1]);
        Double z = Double.parseDouble(splittedCoords[2]);
        org.bukkit.World.Environment world;
        switch(worldStr) {
            case "NETHER":
                world = org.bukkit.World.Environment.NETHER;
                break;
            case "THE_END":
                world = World.Environment.THE_END;
                break;
            default:
                world = org.bukkit.World.Environment.NORMAL;
                break;
        }
        return new Position(x, y, z, world);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public World.Environment getEnv() {
        return this.environment;
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld("world"), this.x, this.y, this.z);
    }
}
