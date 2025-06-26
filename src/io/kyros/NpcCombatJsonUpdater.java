package io.kyros;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NpcCombatJsonUpdater {

    public static void main(String[] args) {
        try {
            // Path to your JSON file
            String npcCombatDefsFile = Paths.get(Server.getDataDirectory(), "cfg/npc/npc_combat_defs.json").toString();

            // Load the existing JSON data
            FileReader reader = new FileReader(npcCombatDefsFile);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<JsonObject>>() {}.getType();
            List<JsonObject> npcCombatDefs = gson.fromJson(reader, listType);
            reader.close();

            // Create a new list to hold the updated JSON objects
            List<JsonObject> updatedNpcCombatDefs = new ArrayList<>();

            // Iterate over each NPC combat definition and reorder fields
            for (JsonObject npcCombatDef : npcCombatDefs) {
                // Create a new JsonObject to hold the reordered fields
                JsonObject reorderedNpcCombatDef = new JsonObject();

                // Copy the fields in the desired order
                reorderedNpcCombatDef.add("id", npcCombatDef.get("id"));
                reorderedNpcCombatDef.add("attackSpeed", npcCombatDef.get("attackSpeed"));

                // Move the slayerLevel field right after attackSpeed if it exists
                if (npcCombatDef.has("slayerLevel")) {
                    reorderedNpcCombatDef.add("slayerLevel", npcCombatDef.get("slayerLevel"));
                } else {
                    reorderedNpcCombatDef.addProperty("slayerLevel", 0);  // Default to 0 if not present
                }

                // Add the rest of the fields
                reorderedNpcCombatDef.add("attackStyle", npcCombatDef.get("attackStyle"));
                reorderedNpcCombatDef.add("aggressive", npcCombatDef.get("aggressive"));
                reorderedNpcCombatDef.add("isPoisonous", npcCombatDef.get("isPoisonous"));
                reorderedNpcCombatDef.add("isImmuneToPoison", npcCombatDef.get("isImmuneToPoison"));
                reorderedNpcCombatDef.add("isImmuneToVenom", npcCombatDef.get("isImmuneToVenom"));
                reorderedNpcCombatDef.add("isImmuneToCannons", npcCombatDef.get("isImmuneToCannons"));
                reorderedNpcCombatDef.add("isImmuneToThralls", npcCombatDef.get("isImmuneToThralls"));
                reorderedNpcCombatDef.add("levels", npcCombatDef.get("levels"));
                reorderedNpcCombatDef.add("attackBonuses", npcCombatDef.get("attackBonuses"));
                reorderedNpcCombatDef.add("defensiveBonuses", npcCombatDef.get("defensiveBonuses"));

                // Add the reordered object to the list
                updatedNpcCombatDefs.add(reorderedNpcCombatDef);
            }

            // Write the updated list back to the JSON file
            FileWriter writer = new FileWriter(npcCombatDefsFile);
            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
            prettyGson.toJson(updatedNpcCombatDefs, writer);
            writer.flush();
            writer.close();

            System.out.println("Reordered slayerLevel field in " + npcCombatDefsFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}