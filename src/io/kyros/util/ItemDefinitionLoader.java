package io.kyros.util;

import io.kyros.Server;
import io.kyros.cache.definitions.ItemDefinition;
import io.kyros.model.definitions.ItemDef;

import org.apache.commons.lang3.text.WordUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemDefinitionLoader {

    public static void generateItemIdsLua() {
        int totalItems = Server.getDefinitionRepository().total(ItemDefinition.class);
        Map<String, Integer> itemIds = new HashMap<>();
        FieldGenerator fieldGenerator = new FieldGenerator(name -> {

        });

        for(int id = 0; id < totalItems; id++) {
           ItemDefinition itemDef = Server.getDefinitionRepository().get(ItemDefinition.class, id);
            String name = null;
            try {
                name = itemDef.name;
                if(itemDef.noted())
                    name = name + "_NOTED";
            } catch (Exception e) {

            }
            fieldGenerator.add(name, id);
            String sanitizedName = sanitizeName(name);
            if (sanitizedName != null) {
                itemIds.put(sanitizedName, id);
            } else {
                System.out.println("Item [" + id + "] sanitizedName == null");
                try {
                    System.out.println("Item name = " + Server.getDefinitionRepository().get(ItemDefinition.class, id));
                } catch (Exception e) {
                    System.out.println("Item [" + id + "] name retrieved failed");
                }
            }
        }

        writeItemIdsToLuaFile(itemIds);
    }

    private static final Map<String, String> numberToWordMap = createNumberToWordMap();

    private static Map<String, String> createNumberToWordMap() {
        Map<String, String> map = new HashMap<>();
        map.put("0", "ZERO");
        map.put("1", "ONE");
        map.put("2", "TWO");
        map.put("3", "THREE");
        map.put("4", "FOUR");
        map.put("5", "FIVE");
        map.put("6", "SIX");
        map.put("7", "SEVEN");
        map.put("8", "EIGHT");
        map.put("9", "NINE");
        map.put("10", "TEN");
        map.put("11", "ELEVEN");
        map.put("12", "TWELVE");
        map.put("13", "THIRTEEN");
        map.put("14", "FOURTEEN");
        map.put("15", "FIFTEEN");
        map.put("16", "SIXTEEN");
        map.put("17", "SEVENTEEN");
        map.put("18", "EIGHTEEN");
        map.put("19", "NINETEEN");
        map.put("20", "TWENTY");
        map.put("30", "THIRTY");
        map.put("40", "FORTY");
        map.put("50", "FIFTY");
        map.put("60", "SIXTY");
        map.put("70", "SEVENTY");
        map.put("80", "EIGHTY");
        map.put("90", "NINETY");
        return map;
    }

    private static String convertNumberToWords(int number) {
        if (numberToWordMap.containsKey(String.valueOf(number))) {
            return numberToWordMap.get(String.valueOf(number));
        }

        StringBuilder words = new StringBuilder();
        if (number >= 100) {
            int hundreds = number / 100;
            words.append(numberToWordMap.get(String.valueOf(hundreds))).append("_HUNDRED_");
            number %= 100;
        }
        if (number > 0) {
            if (numberToWordMap.containsKey(String.valueOf(number))) {
                words.append(numberToWordMap.get(String.valueOf(number)));
            } else {
                int tens = number / 10 * 10;
                int units = number % 10;
                words.append(numberToWordMap.get(String.valueOf(tens))).append("_");
                if (units > 0) {
                    words.append(numberToWordMap.get(String.valueOf(units)));
                }
            }
        }

        return words.toString();
    }

    private static String sanitizeName(String definitionName) {
        if (definitionName != null && definitionName.length() > 0) {
            // Normalize to remove accents and special characters
            definitionName = Normalizer.normalize(definitionName, Normalizer.Form.NFD);
            definitionName = definitionName.replaceAll("[^\\p{ASCII}]", "");

            // Remove known effect patterns in different cases
            String[] effects = {"@gre@", "@blu@", "@red@", "@bla@", "@cya@", "@or3@", "@or2@", "@or@", "@whi@", "@pur@", "@mag@", "@yel@", "<col=", "<shad=", "</col>", "</shad>"};
            for (String effect : effects) {
                definitionName = definitionName.replaceAll("(?i)" + effect, "");
                // If effect has a closing tag, remove the closing part too
                if (effect.startsWith("<")) {
                    int startIndex = definitionName.toLowerCase().indexOf(effect.toLowerCase());
                    if (startIndex != -1) {
                        int endIndex = definitionName.indexOf(">", startIndex);
                        if (endIndex != -1) {
                            definitionName = definitionName.substring(0, startIndex) + definitionName.substring(endIndex + 1);
                        }
                    }
                }
            }

            // Replace $INTEGER with corresponding text
            Pattern pattern = Pattern.compile("\\$(\\d+)");
            Matcher matcher = pattern.matcher(definitionName);
            StringBuffer result = new StringBuffer();
            while (matcher.find()) {
                int number = Integer.parseInt(matcher.group(1));
                matcher.appendReplacement(result, convertNumberToWords(number));
            }
            matcher.appendTail(result);
            definitionName = result.toString();

            // Convert initial numbers to words
            pattern = Pattern.compile("^(\\d+)(.*)");
            matcher = pattern.matcher(definitionName);
            result = new StringBuffer();
            if (matcher.find()) {
                int number = Integer.parseInt(matcher.group(1));
                matcher.appendReplacement(result, convertNumberToWords(number) + matcher.group(2));
            }
            matcher.appendTail(result);
            definitionName = result.toString();

            // Remove other unwanted characters and sanitize the name
            String name = definitionName.toUpperCase().replaceAll(" ", "_");
            name = name.replaceAll("[\\(\\)'\"\\-\\.,!\\?%:;]", "");
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
            name = name.replaceAll("\\$", "");
            if(name.contains(">"))
                name = name.substring(name.indexOf(">") + 1);
            if (name.matches("^\\d+.*")) {
                String[] parts = name.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                if (parts.length > 1) {
                    name = convertNumberToWords(Integer.parseInt(parts[0])) + "_" + parts[1];
                } else {
                    name = convertNumberToWords(Integer.parseInt(parts[0]));
                }
            }

            while (name.startsWith("_")) {
                name = name.substring(1);
            }

            return name;
        }
        return null;
    }

    private static void writeItemIdsToLuaFile(Map<String, Integer> itemIds) {
        try (FileWriter writer = new FileWriter("./data/scripts/api/definitions/item/item_ids_test.lua")) {
            writer.write("-- item_ids.lua\n\n");
            writer.write("return {\n");

            for (Map.Entry<String, Integer> entry : itemIds.entrySet()) {
                writer.write(String.format("    %s = %d,\n", entry.getKey(), entry.getValue()));
            }

            writer.write("}\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

