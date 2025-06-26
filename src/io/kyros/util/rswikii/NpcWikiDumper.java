package io.kyros.util.rswikii;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.entity.npc.stats.NpcBonus;
import io.kyros.model.entity.npc.stats.NpcCombatSkill;
import io.kyros.model.entity.npc.stats.NpcCombatDefinition;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * NpcWikiDumper class for scraping and caching NPC combat stats from the Old School RuneScape Wiki.
 */
public class NpcWikiDumper {

    public static String RSWIKI_URL = "https://oldschoolrunescape.fandom.com/wiki/Old_School_RuneScape_Wiki";

    public static ArrayList<NpcCombatDefinition> statsList = new ArrayList<>();
    public static double completion = 0;

    public static void main(String[] args) {
        NpcWikiDumper.loadNpcList();
//        dumpNpcCache();
        dumpStats();
    }

    public static String formatNameForWiki(String entityName) {
        try {
            entityName = entityName.replace(" ", "_")
                    .replace("<.*>\b|<.*>", "")
                    .replace(" ", "_")
                    .replace("<.*>\b|<.*>", "");
            entityName = URLEncoder.encode(entityName, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entityName;
    }

    public static void dumpNpcCache() {
        ArrayList<String> npcsAlreadyDumped = new ArrayList<>();
        NpcDef.getDefinitions().forEach((id, def) -> {
            if (!npcsAlreadyDumped.contains(def.getName())) {
                if (cacheNpc(id, def.getName())) {
                    npcsAlreadyDumped.add(def.getName());
                }
            }
        });
    }

    public static void dumpStats() {

        long startTime = System.currentTimeMillis();

        int size = NpcDef.getDefinitions().size();
        NpcDef.getDefinitions().forEach((id, def) -> {
            dumpStats(id, def.getName());
            System.out.println("%(" + ((double) (++completion / size) * 100) + ") - (" + completion + "/" + size + ")");
            System.out.println("Time Elapsed: " + ((System.currentTimeMillis() - startTime) / 1000));
        });

        NpcWikiDumper.saveNpcStats();
        System.out.println("It took " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds to finish " +
                "dumping " + statsList.size() + " combat definitions.");
    }

    public static boolean cacheNpc(int npcId, String name) {
        String fileName = name;
        name = formatNameForWiki(name);
        String filePath = "./temp/npcs/" + fileName;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + ".html"))) {
            Document page = Jsoup.connect("https://oldschool.runescape.wiki/w/" + name).get();
            writer.write(page.html());
            System.out.println("Cached NPC: " + npcId + " - " + name);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void dumpStats(int npcId, String name) {
        try {
            File file = new File("./temp/npcs/" + name + ".html");
            if (!file.exists()) {
                System.out.println(name + " is not cached.");
                return;
            }

            Document page = Jsoup.parse(file, "UTF-8");

            Elements infoTable = page.select(".infobox-monster");

            NpcCombatDefinition stats;

            if (infoTable.size() > 0) {
                stats = new NpcCombatDefinition(npcId);

                // First, try to parse by data-attr-param attributes
                Elements dataRows = infoTable.select("[data-attr-param]");
                if (dataRows.isEmpty()) {
                    // Fallback to traditional parsing if no data-attr-param attributes are found
                    Elements tableRows = infoTable.select("tr");

                    for (Element tableRow : tableRows) {
                        String header = tableRow.select("th").text();
                        String value = tableRow.select("td").text();

                        switch (header) {
                            case "Combat level":
//                                stats.setCombatLevel(parseIntegerOrDefault(value, 0));
                                break;
                            case "Hitpoints":
                                stats.setLevel(NpcCombatSkill.HITPOINTS, parseIntegerOrDefault(value.replace(",", ""), 0));
                                break;
                            case "Attack":
                                stats.setLevel(NpcCombatSkill.ATTACK, parseIntegerOrDefault(value, 0));
                                break;
                            case "Strength":
                                stats.setLevel(NpcCombatSkill.STRENGTH, parseIntegerOrDefault(value, 0));
                                break;
                            case "Defence":
                                stats.setLevel(NpcCombatSkill.DEFENCE, parseIntegerOrDefault(value, 0));
                                break;
                            case "Magic":
                                stats.setLevel(NpcCombatSkill.MAGIC, parseIntegerOrDefault(value, 0));
                                break;
                            case "Ranged":
                                stats.setLevel(NpcCombatSkill.RANGE, parseIntegerOrDefault(value, 0));
                                break;
                            case "Aggressive":
                                stats.setAggressive(value.equalsIgnoreCase("Yes"));
                                break;
                            case "Poisonous":
                                stats.setPoisonous(value.contains("Yes"));
                                break;
                            case "Attack style":
                                stats.setAttackStyle(value);
                                break;
                            case "Attack speed":
                                String attackSpeedText = value.split(" ")[0];
                                stats.setAttackSpeed(parseIntegerOrDefault(attackSpeedText, -1)); // -1 for non-numeric attack speed
                                break;
                            case "Max hit":
                                // Handle max hit if needed
                                break;
                            case "Stab":
                                stats.setDefenceBonus(NpcBonus.STAB_BONUS, parseIntegerOrDefault(value.replace("+", "").replace("-", ""), 0));
                                break;
                            case "Slash":
                                stats.setDefenceBonus(NpcBonus.SLASH_BONUS, parseIntegerOrDefault(value.replace("+", "").replace("-", ""), 0));
                                break;
                            case "Crush":
                                stats.setDefenceBonus(NpcBonus.CRUSH_BONUS, parseIntegerOrDefault(value.replace("+", "").replace("-", ""), 0));
                                break;
                            case "Magic defence":
                                stats.setDefenceBonus(NpcBonus.MAGIC_BONUS, parseIntegerOrDefault(value.replace("+", "").replace("-", ""), 0));
                                break;
                            case "Ranged defence":
                                stats.setDefenceBonus(NpcBonus.RANGE_BONUS, parseIntegerOrDefault(value.replace("+", "").replace("-", ""), 0));
                                break;
                            case "Poison":
                                stats.setImmuneToPoison(value.equalsIgnoreCase("Immune"));
                                break;
                            case "Venom":
                                stats.setImmuneToVenom(value.equalsIgnoreCase("Immune"));
                                break;
                            case "Cannons":
                                stats.setImmuneToCannons(value.equalsIgnoreCase("Immune"));
                                break;
                            case "Thralls":
                                stats.setImmuneToThralls(value.equalsIgnoreCase("Immune"));
                                break;
                            // Add more cases as necessary
                            default:
                                // Handle any other potential fields
                                break;
                        }
                    }
                } else {
                    // If data-attr-param attributes are found, use the previously provided method
                    for (Element row : dataRows) {
                        String param = row.attr("data-attr-param");
                        String value = row.text();

                        switch (param) {
                            case "combat":
//                                stats.setCombatLevel(parseIntegerOrDefault(value, 0));
                                break;
                            case "hitpoints":
                                stats.setLevel(NpcCombatSkill.HITPOINTS, parseIntegerOrDefault(value, 0));
                                break;
                            case "att":
                                stats.setLevel(NpcCombatSkill.ATTACK, parseIntegerOrDefault(value, 0));
                                break;
                            case "str":
                                stats.setLevel(NpcCombatSkill.STRENGTH, parseIntegerOrDefault(value, 0));
                                break;
                            case "def":
                                stats.setLevel(NpcCombatSkill.DEFENCE, parseIntegerOrDefault(value, 0));
                                break;
                            case "mage":
                                stats.setLevel(NpcCombatSkill.MAGIC, parseIntegerOrDefault(value, 0));
                                break;
                            case "range":
                                stats.setLevel(NpcCombatSkill.RANGE, parseIntegerOrDefault(value, 0));
                                break;
                            case "attbns":
                                stats.setAttackBonus(NpcBonus.ATTACK_BONUS, parseIntegerOrDefault(value.replace("+", "").replace("-", ""), 0));
                                break;
                            case "strbns":
                                stats.setAttackBonus(NpcBonus.STRENGTH_BONUS, parseIntegerOrDefault(value.replace("+", "").replace("-", ""), 0));
                                break;
                            case "amagic":
                                stats.setAttackBonus(NpcBonus.ATTACK_MAGIC_BONUS, parseIntegerOrDefault(value.replace("+", "").replace("-", ""), 0));
                                break;
                            case "mbns":
                                stats.setAttackBonus(NpcBonus.MAGIC_STRENGTH_BONUS, parseIntegerOrDefault(value.replace("+", "").replace("-", ""), 0));
                                break;
                            case "arange":
                                stats.setAttackBonus(NpcBonus.ATTACK_RANGE_BONUS, parseIntegerOrDefault(value.replace("+", "").replace("-", ""), 0));
                                break;
                            case "rngbns":
                                stats.setAttackBonus(NpcBonus.RANGE_STRENGTH_BONUS, parseIntegerOrDefault(value.replace("+", "").replace("-", ""), 0));
                                break;
                            case "dstab":
                                stats.setDefenceBonus(NpcBonus.STAB_BONUS, parseIntegerOrDefault(value.replace("+", "").replace("-", ""), 0));
                                break;
                            case "dslash":
                                stats.setDefenceBonus(NpcBonus.SLASH_BONUS, parseIntegerOrDefault(value.replace("+", "").replace("-", ""), 0));
                                break;
                            case "dcrush":
                                stats.setDefenceBonus(NpcBonus.CRUSH_BONUS, parseIntegerOrDefault(value.replace("+", "").replace("-", ""), 0));
                                break;
                            case "dmagic":
                                stats.setDefenceBonus(NpcBonus.MAGIC_BONUS, parseIntegerOrDefault(value.replace("+", "").replace("-", ""), 0));
                                break;
                            case "dlight":
                                stats.setDefenceBonus(NpcBonus.RANGE_BONUS, parseIntegerOrDefault(value.replace("+", "").replace("-", ""), 0));
                                break;
                            case "poisonous":
                                stats.setPoisonous(value.equalsIgnoreCase("Yes"));
                                break;
                            case "aggressive":
                                stats.setAggressive(value.equalsIgnoreCase("Yes"));
                                break;
                            case "immunepoison":
                                stats.setImmuneToPoison(value.equalsIgnoreCase("Immune"));
                                break;
                            case "immunevenom":
                                stats.setImmuneToVenom(value.equalsIgnoreCase("Immune"));
                                break;
                            case "immunecannon":
                                stats.setImmuneToCannons(value.equalsIgnoreCase("Immune"));
                                break;
                            case "immunethrall":
                                stats.setImmuneToThralls(value.equalsIgnoreCase("Immune"));
                                break;
                            case "attack speed":
                                String attackSpeedText = value.split(" ")[0];
                                stats.setAttackSpeed(parseIntegerOrDefault(attackSpeedText, -1)); // -1 for non-numeric attack speed
                                break;
                            default:
                                // Handle other cases if necessary
                                break;
                        }
                    }
                }

                // Add the parsed stats to the statsList
                statsList.add(stats);

            } else {
                System.out.println("No info table for " + name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error occurred when dumping stats for " + name);
            e.printStackTrace();
        }
    }

    private static int parseIntegerOrDefault(String value, int defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }




    private static boolean getImmunity(Element element) {
        return element.parent().parent().select("td").first().text().equalsIgnoreCase("Not immune") ? false : true;
    }

    public static void saveNpcStats() {
        Path path = Paths.get("./temp/", "npc_combat_defs.json");
        File file = path.toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            for (NpcCombatDefinition stats : statsList) {

                Gson builder = new GsonBuilder().setPrettyPrinting().create();
                JsonObject object = new JsonObject();

                object.addProperty("id", stats.getId());
                object.addProperty("attackSpeed", stats.getAttackSpeed());
                object.addProperty("attackStyle", stats.getAttackStyle());
                object.addProperty("aggressive", stats.isAggressive());
                object.addProperty("isPoisonous", stats.isPoisonous());
                object.addProperty("isImmuneToPoison", stats.isImmuneToPoison());
                object.addProperty("isImmuneToVenom", stats.isImmuneToVenom());
                object.addProperty("isImmuneToCannons", stats.isImmuneToCannons());
                object.addProperty("isImmuneToThralls", stats.isImmuneToThralls());
                object.add("levels", builder.toJsonTree(stats.getLevels()));
                object.add("aggressiveBonuses", builder.toJsonTree(stats.getAttackBonuses()));
                object.add("defensiveBonuses", builder.toJsonTree(stats.getDefenceBonuses()));
                writer.write(builder.toJson(object) + ",");
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * LOADS OUR NPC LIST FOR EASY WIKI CHECKING
     */
    public static void loadNpcList() {
        try {
            NpcDef.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
