package io.kyros.model.entity.npc.stats;

import com.google.gson.Gson;
import io.kyros.Server;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.definitions.NpcStats;
import io.kyros.model.entity.npc.NPC;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class NpcCombatDefinition {

    /**
     * A Map of all Npc Combat Definitions
     */
    public static HashMap<Integer, NpcCombatDefinition> definitions = new HashMap<>();

    /**
     * The ID of the NPC
     */
    private int id;

    /**
     * The Attack speed of the NPC
     */
    private int attackSpeed;

    /**
     * The Attack style of the NPC
     */
    private String attackStyle;

    /**
     * A flag that determines if the NPC is aggressive
     */
    private boolean aggressive;

    /**
     * A flag that determines if the NPC is poisonous
     */
    private boolean isPoisonous;

    /**
     * A flag that determines if the NPC is immune to poison
     */
    private boolean isImmuneToPoison;

    /**
     * A flag that determines if the NPC is immune to venom
     */
    private boolean isImmuneToVenom;

    /**
     * A flag that determines if the NPC is immune to cannons
     */
    private boolean isImmuneToCannons;

    /**
     * A flag that determines if the NPC is immune to thralls
     */
    private boolean isImmuneToThralls;

    /**
     * All the NPC's combat levels
     */
    private Map<NpcCombatSkill, Integer> levels;

    /**
     * All the NPC's attack bonuses
     */
    private Map<NpcBonus, Integer> attackBonuses;

    /**
     * All the NPC's defensive bonuses
     */
    private Map<NpcBonus, Integer> defensiveBonuses;

    /**
     * Slayer level required to fight the NPC (optional)
     */
    private int slayerLevel = 0;  // Default to 0 if not provided

    public NpcCombatDefinition(NPC npc) {
        this.id = npc.getNpcId();
        this.levels = new HashMap<>();
        this.attackBonuses = new HashMap<>();
        this.defensiveBonuses = new HashMap<>();

        this.attackSpeed = 4;
        this.attackStyle = "Melee";
        this.aggressive = false;
        this.isPoisonous = false;
        this.isImmuneToPoison = false;
        this.isImmuneToVenom = false;
        this.isImmuneToCannons = false;
        this.isImmuneToThralls = false;
        for (NpcCombatSkill value : NpcCombatSkill.values()) {
            this.levels.put(value, 1);
        }
        for (NpcBonus value : NpcBonus.values()) {
            this.attackBonuses.put(value, 0);
            this.defensiveBonuses.put(value, 0);
        }
    }

    public NpcCombatDefinition(NpcCombatDefinition other) {
        this.id = other.id;
        this.levels = new HashMap<>();
        this.attackBonuses = new HashMap<>();
        this.defensiveBonuses = new HashMap<>();

        this.attackSpeed = other.attackSpeed;
        this.attackStyle = other.attackStyle;
        this.aggressive = other.aggressive;
        this.isPoisonous = other.isPoisonous;
        this.isImmuneToPoison = other.isImmuneToPoison;
        this.isImmuneToVenom = other.isImmuneToVenom;
        this.isImmuneToCannons = other.isImmuneToCannons;
        this.isImmuneToThralls = other.isImmuneToThralls;
        this.slayerLevel = other.slayerLevel;
        this.levels.putAll(other.levels);
        this.attackBonuses.putAll(other.attackBonuses);
        this.defensiveBonuses.putAll(other.defensiveBonuses);
    }

    public static void load() throws IOException {
        // Load the npc_combat_defs.json file
        Reader reader = Files.newBufferedReader(Paths.get(Server.getDataDirectory() + "/cfg/npc/npc_combat_defs.json"));
        NpcCombatDefinition[] npcCombatDefinitions = new Gson().fromJson(reader, NpcCombatDefinition[].class);

        for (NpcCombatDefinition npcCombatDefinition : npcCombatDefinitions) {
            if (npcCombatDefinition != null) {
                // Store the NpcCombatDefinition in the definitions map
                definitions.put(npcCombatDefinition.getId(), npcCombatDefinition);

                // Retrieve the NPCDef for the given NPC ID
                NpcDef npcDef = NpcDef.forId(npcCombatDefinition.getId());

                if (npcDef != null) {
                    // Create a new NpcStats object using data from NpcCombatDefinition and NPCDef
                    NpcStats npcStats = new NpcStats(
                            npcDef.getName(),
                            npcCombatDefinition.getLevel(NpcCombatSkill.HITPOINTS),
                            npcDef.getCombatLevel(),  // Combat level from NPCDef
                            npcCombatDefinition.getSlayerLevel(),  // Slayer level from NpcCombatDefinition
                            npcCombatDefinition.getAttackSpeed(),
                            npcCombatDefinition.getLevel(NpcCombatSkill.ATTACK),
                            npcCombatDefinition.getLevel(NpcCombatSkill.STRENGTH),
                            npcCombatDefinition.getLevel(NpcCombatSkill.DEFENCE),
                            npcCombatDefinition.getLevel(NpcCombatSkill.RANGE),  // Range level
                            npcCombatDefinition.getLevel(NpcCombatSkill.MAGIC),  // Magic level
                            npcCombatDefinition.getAttackBonus(NpcBonus.STAB_BONUS),
                            npcCombatDefinition.getAttackBonus(NpcBonus.SLASH_BONUS),
                            npcCombatDefinition.getAttackBonus(NpcBonus.CRUSH_BONUS),
                            npcCombatDefinition.getAttackBonus(NpcBonus.RANGE_BONUS),
                            npcCombatDefinition.getAttackBonus(NpcBonus.MAGIC_BONUS),
                            npcCombatDefinition.getDefenceBonus(NpcBonus.STAB_BONUS),
                            npcCombatDefinition.getDefenceBonus(NpcBonus.SLASH_BONUS),
                            npcCombatDefinition.getDefenceBonus(NpcBonus.CRUSH_BONUS),
                            npcCombatDefinition.getDefenceBonus(NpcBonus.RANGE_BONUS),
                            npcCombatDefinition.getDefenceBonus(NpcBonus.MAGIC_BONUS),
                            npcCombatDefinition.getAttackBonus(NpcBonus.ATTACK_BONUS),
                            npcCombatDefinition.getAttackBonus(NpcBonus.STRENGTH_BONUS),
                            npcCombatDefinition.getAttackBonus(NpcBonus.RANGE_STRENGTH_BONUS),
                            npcCombatDefinition.getAttackBonus(NpcBonus.MAGIC_STRENGTH_BONUS),
                            npcCombatDefinition.isImmuneToPoison(),
                            npcCombatDefinition.isImmuneToVenom(),
                            false,  // Dragon flag (set as needed)
                            false,  // Demon flag (set as needed)
                            false   // Undead flag (set as needed)
                    );

                    // Add the NpcStats object to the NpcStats map
                    NpcStats.addNPCStats(npcCombatDefinition.getId(), npcStats);
                }
            }
        }

        // Output the results of loading
        System.out.println("Loaded " + definitions.size() + " NPC Combat definitions...");
        System.out.println("Loaded " + NpcStats.getNpcStatsMap().size() + " NPC Stats entries...");
        reader.close();
    }

    public static void clearLoadedDefs() {
        definitions.clear();
    }

    public NpcCombatDefinition(int id) {
        this.id = id;
        this.levels = new HashMap<>();
        this.attackBonuses = new HashMap<>();
        this.defensiveBonuses = new HashMap<>();
    }

    public int getId() {
        return this.id;
    }

    public void setLevel(NpcCombatSkill npcCombatSkill, int level) {
        this.levels.put(npcCombatSkill, level);
    }

    public void setAttackBonus(NpcBonus npcAttackBonus, int bonus) {
        this.attackBonuses.put(npcAttackBonus, bonus);
    }

    public void setDefenceBonus(NpcBonus npcDefenceBonus, int bonus) {
        this.defensiveBonuses.put(npcDefenceBonus, bonus);
    }

    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public void setAttackStyle(String attackStyle) {
        this.attackStyle = attackStyle;
    }

    public void setAggressive(boolean aggressive) {
        this.aggressive = aggressive;
    }

    public void setPoisonous(boolean isPoisonous) {
        this.isPoisonous = isPoisonous;
    }

    public void setImmuneToPoison(boolean isImmuneToPoison) {
        this.isImmuneToPoison = isImmuneToPoison;
    }

    public void setImmuneToVenom(boolean isImmuneToVenom) {
        this.isImmuneToVenom = isImmuneToVenom;
    }

    public boolean isImmuneToPoison() {
        return this.isImmuneToPoison;
    }

    public boolean isImmuneToVenom() {
        return this.isImmuneToVenom;
    }

    public boolean isAggressive() {
        return this.aggressive;
    }

    public boolean isPoisonous() {
        return this.isPoisonous;
    }

    public String getAttackStyle() {
        return this.attackStyle;
    }

    public Map<NpcCombatSkill, Integer> getLevels() {
        return this.levels;
    }

    public Map<NpcBonus, Integer> getAttackBonuses() {
        return this.attackBonuses;
    }

    public Map<NpcBonus, Integer> getDefenceBonuses() {
        return this.defensiveBonuses;
    }

    public int getLevel(NpcCombatSkill npcCombatSkill) {
        return this.levels.getOrDefault(npcCombatSkill, 1);
    }

    public int getAttackBonus(NpcBonus npcBonus) {
        return this.attackBonuses.getOrDefault(npcBonus, 1);
    }

    public int getDefenceBonus(NpcBonus npcBonus) {
        return this.defensiveBonuses.getOrDefault(npcBonus, 1);
    }

    public int getAttackSpeed() {
        return this.attackSpeed;
    }

    public int getSlayerLevel() {
        return this.slayerLevel;
    }

    public void setSlayerLevel(int slayerLevel) {
        this.slayerLevel = slayerLevel;
    }

    public boolean isImmuneToCannons() {
        return isImmuneToCannons;
    }

    public void setImmuneToCannons(boolean immuneToCannons) {
        isImmuneToCannons = immuneToCannons;
    }

    public boolean isImmuneToThralls() {
        return isImmuneToThralls;
    }

    public void setImmuneToThralls(boolean immuneToThralls) {
        isImmuneToThralls = immuneToThralls;
    }

    public boolean isValid() {
        return levels.containsKey(NpcCombatSkill.HITPOINTS) && attackStyle != null;
    }
}
