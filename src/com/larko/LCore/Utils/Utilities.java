package com.larko.LCore.Utils;

import com.larko.LCore.Main;
import com.larko.LCore.Structures.Position;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.ResourceKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Utilities {
    public static File dataFolder;
    public static FileConfiguration config;
    public static final String tokenConfigPlaceholder = "PUT_YOUR_BOT_TOKEN_HERE";


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

    public static void runScienceTask() {
        // science task
        TimerTask task = new TimerTask(){
            public void run(){
                JSONParser scienceParser = new JSONParser();
                try {
                    JSONObject science = (JSONObject) scienceParser.parse(new FileReader(new File(Utilities.dataFolder, "science.json")));
                    Date now = new Date();
                    ArrayList<String> timestamps = new ArrayList<>(science.keySet());
                    // if diff between last stats and now is less than 5 minutes, dont bother saving
                    if (TimeUnit.MILLISECONDS.toMinutes(now.getTime() - Long.parseLong(timestamps.get(timestamps.size() - 1))) < 5) return;
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("playerCount", Bukkit.getOnlinePlayers().size());
                    data.put("tps",  Double.toString(MinecraftServer.getServer().recentTps[0]));
                    science.put(now.getTime(), data);

                    FileWriter scienceWriter = new FileWriter(new File(Utilities.dataFolder, "science.json"));
                    scienceWriter.write(science.toJSONString());
                    scienceWriter.flush();
                    scienceWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        Main.scienceTimer = new Timer();
        // every 5 min, write stats
        Main.scienceTimer.schedule(task, 0, (60*1000) * 5);
    }
}
