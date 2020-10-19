package com.noahhusby.terrabungee.api;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class TerraBungeeUtil {
    /**
     * Converts list of intents to a JSONArray
     * @param intents List of active intents
     * @return JSONArray of active intents
     */
    public static JSONArray intentsToArray(List<ServiceIntent> intents) {
        JSONArray a = new JSONArray();
        for(ServiceIntent intent : intents)
            a.add(intent.name());
        return a;
    }

    /**
     * Converts JSONArray to a list of intents
     * @param a JSONArray of active intents
     * @return List of active intents
     */
    public static List<ServiceIntent> arrayToIntents(JSONArray a) {
        List<ServiceIntent> intents = new ArrayList<>();
        for(Object o : a)
            intents.add(ServiceIntent.valueOf((String) o));
        return intents;
    }

    public static Object stringToJSON(String s) {
        try {
            return new JSONParser().parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
