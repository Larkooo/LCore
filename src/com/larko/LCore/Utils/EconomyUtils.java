package com.larko.LCore.Utils;

import com.larko.LCore.Structures.LPlayer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.UUID;

public class EconomyUtils {
    public static boolean setLCoinsDB(UUID playerUuid, double lCoins) {
        JSONParser jsonParser = new JSONParser();
        try {
            Object obj = jsonParser.parse(new FileReader(new File(Utilities.dataFolder, "players.json")));

            JSONArray players = (JSONArray) obj;
            Iterator<JSONObject> iterator = players.iterator();
            while (iterator.hasNext()){
                JSONObject playerObj = iterator.next();
                if(playerUuid.toString().equals((String) playerObj.get("uuid"))) {
                    playerObj.put("lCoins", lCoins);

                    FileWriter playersFile = new FileWriter(new File(Utilities.dataFolder, "players.json"));
                    playersFile.write(players.toJSONString());
                    playersFile.flush();
                    playersFile.close();
                    return true;
                }
            }

            return false;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
