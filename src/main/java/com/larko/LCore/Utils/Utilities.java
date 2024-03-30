package com.larko.LCore.Utils;

import com.larko.LCore.Main;
import com.larko.LCore.Structures.LPlayer;
import com.larko.LCore.Structures.Position;
import com.larko.LCore.Structures.Shop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.bukkit.Bukkit.getLogger;

public class Utilities {
    public static File dataFolder;
    public static FileConfiguration config;
    public static final String tokenConfigPlaceholder = "PUT_YOUR_BOT_TOKEN_HERE";

    public static void loadPlayers() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        File playersFile = new File(Utilities.dataFolder, "players.json");
        if (!playersFile.exists()) {
            boolean created = playersFile.createNewFile();
            if (!created) {
                getLogger().severe("Failed to create players.json file");
                return;
            }

            FileWriter playersWriter = new FileWriter(playersFile);
            playersWriter.write("[]");
            playersWriter.flush();
            playersWriter.close();
        }

        JSONArray players = (JSONArray) jsonParser.parse(new FileReader(playersFile));
        //cachedPlayersData = (JSONArray) players;
        for (int i = 0; i < players.size(); i++) {
            JSONObject player = (JSONObject) players.get(i);
            LPlayer.fromJSON(player);
        }
    }

    public static void loadShop() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        File shopFile = new File(Utilities.dataFolder, "shop.json");
        if (!shopFile.exists()) {
            boolean created = shopFile.createNewFile();
            if (!created) {
                getLogger().severe("Failed to create shop.json file");
                return;
            }

            FileWriter shopWriter = new FileWriter(shopFile);
            shopWriter.write("{\"title\": \"Shop\",\"open\": true,\"items\": []}");
            shopWriter.flush();
            shopWriter.close();
        }

        JSONObject shop = (JSONObject) jsonParser.parse(new FileReader(shopFile));
        Shop.fromJSON(shop);
    }

    public static void generateScienceGraph() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject science = (JSONObject) jsonParser.parse(new FileReader(new File(Utilities.dataFolder, "science.json")));

        final int range = 5;
        // timestamps
        double[] timestamps = new double[range];
        for (int i = 0; i < range; i++)
        {
            timestamps[(range - 1) - i] = Double.parseDouble((String) (science.keySet().toArray())[(science.size() - 1) - i]);
        }

        // tps & playercount
        double[] tps = new double[range];
        double[] playerCount = new double[range];
        for (int i = 0; i < range; i++)
        {
            tps[(range - 1) - i] = Double.parseDouble((String)((JSONObject)science.values().toArray()[(science.size() - 1) - i]).get("tps"));
            playerCount[(range - 1) - i] = new Double((long)((JSONObject)science.values().toArray()[(science.size() - 1) - i]).get("playerCount"));
        }

        // indices
        double[] indices = new double[range];
        for (int i = 0; i < range; i++)
            indices[(range - 1) - i] = -i * 5;

        XYChart chart = new XYChart(500, 400);

        chart.getStyler().setChartBackgroundColor(new Color(0x2f3136));
        chart.getStyler().setPlotBackgroundColor(Color.darkGray);

        chart.setTitle("Sample Chart");
        chart.setXAxisTitle("Time (min)");
        XYSeries tpsSeries = chart.addSeries("TPS", indices, tps);
        XYSeries playerCountSeries = chart.addSeries("Players", indices, playerCount);
        tpsSeries.setMarker(SeriesMarkers.CIRCLE);
        playerCountSeries.setMarker(SeriesMarkers.CIRCLE);

        BitmapEncoder.saveBitmap(chart, Utilities.dataFolder.getAbsolutePath() + "/science", BitmapEncoder.BitmapFormat.PNG);
    }

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
                    try {
                        if (TimeUnit.MILLISECONDS.toMinutes(now.getTime() - Long.parseLong(timestamps.get(timestamps.size() - 1))) < 5) return;
                    } catch (Exception e) {}

                    HashMap<String, Object> data = new HashMap<>();
                    data.put("playerCount", Bukkit.getOnlinePlayers().size());
                    data.put("tps",  Double.toString(Bukkit.getServer().getTPS()[0]));
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
