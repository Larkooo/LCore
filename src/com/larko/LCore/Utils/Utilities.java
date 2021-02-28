package com.larko.LCore.Utils;

import com.larko.LCore.Structures.Position;
import net.minecraft.server.v1_16_R3.ResourceKey;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;

public class Utilities {
    public static File dataFolder;

    public static boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean checkIfInRadius(Location playerLoc, String coords, int radius) {
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
