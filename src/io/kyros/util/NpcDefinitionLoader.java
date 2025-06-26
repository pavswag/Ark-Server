package io.kyros.util;

import io.kyros.Server;
import io.kyros.model.definitions.NpcDef;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;

public class NpcDefinitionLoader {

    public static void generateNpcIdsLua() {
        int totalNpcs = NpcDef.getDefinitions().size();
        Map<String, Integer> npcIds = new HashMap<>();
        FieldGenerator fieldGenerator = new FieldGenerator(generated -> {
            System.out.println(generated);
        });

        for(int id = 0; id < totalNpcs; id++) {
            String name = null;
            try {
                name = NpcDef.forId(id).getName();
            } catch (Exception e) {
                continue;
            }
            fieldGenerator.add(name, id);
            String sanitizedName = sanitizeName(name);
            if (sanitizedName != null) {
                npcIds.put(sanitizedName, id);
            }
        }

        writeNpcIdsToLuaFile(npcIds);
    }

    private static String sanitizeName(String definitionName) {
        if (definitionName != null && definitionName.length() > 0
                && !definitionName.equalsIgnoreCase("null")
                && !definitionName.contains("col=")
                && !definitionName.equals("Dwarf remains")
                && !definitionName.contains("shad=")
                && !definitionName.contains("@")) {
            String name = definitionName.toUpperCase().replaceAll(" ", "_");
            name = name.replaceAll("\\(", "");
            name = name.replaceAll("\\)", "");
            name = name.replaceAll("'", "");
            name = name.replaceAll("-", "_");
            name = name.replaceAll("\\.", "");
            name = name.replaceAll(",", "");
            name = name.replaceAll("!", "");
            name = name.replaceAll("\\�", "");
            name = name.replaceAll("\\?", "");
            name = name.replaceAll("%", "");
            name = name.replaceAll(":", "");
            name = name.replaceAll("\"", "");
            name = name.replaceAll("&", "AND");
            name = name.replaceAll("1/2", "HALF");
            name = name.replaceAll("2/3", "TWO_THIRDS");
            name = name.replaceAll("1/3", "ONE_THIRD");
            name = name.replaceAll("1/5", "ONE_FIFTH");
            name = name.replaceAll("2/5", "TWO_FIFTHS");
            name = name.replaceAll("3/5", "THREE_FIFTHS");
            name = name.replaceAll("4/5", "FOUR_FIFTHS");
            name = name.replaceAll("5/5", "FIVE_FIFTHS");
            name = name.replaceAll("3RD", "THIRD");
            name = name.replaceAll("4TH", "FOURTH");
            name = name.replaceAll("/", "_OF_");
            name = name.replaceAll("\\+", "PLUS");

            name = name.replaceAll("__", "_");
            if (Character.isDigit(name.charAt(0))) {
                name = "_" + name;
            }

            return name;
        }
        return null;
    }

    private static void writeNpcIdsToLuaFile(Map<String, Integer> npcIds) {
        try (FileWriter writer = new FileWriter("./data/scripts/api/definitions/npc/npc_ids.lua")) {
            writer.write("-- npc_ids.lua\n\n");
            writer.write("return {\n");

            for (Map.Entry<String, Integer> entry : npcIds.entrySet()) {
                writer.write(String.format("    %s = %d,\n", entry.getKey(), entry.getValue()));
            }

            writer.write("}\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

