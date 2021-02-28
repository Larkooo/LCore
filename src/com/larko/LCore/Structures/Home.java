package com.larko.LCore.Structures;

import org.bukkit.Location;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Home {
    private String name;
    private Position position;

    public Home(String name, Position position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return this.name;
    }

    public Position getPosition() {
        return this.position;
    }

    public static Home fromJSON(JSONObject jsonObject) {
        return new Home(
                jsonObject.toString(),
                Position.fromStrings(
                        jsonObject.get("pos").toString(),
                        jsonObject.get("dimension").toString()
                )
        );
    }
}
