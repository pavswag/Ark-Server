package io.kyros.content.combat.formula.rework;


import io.kyros.content.bonus.BoostScrolls;
import io.kyros.content.combat.effects.damageeffect.impl.amuletofthedamned.impl.ToragsEffect;
import io.kyros.content.combat.melee.CombatPrayer;
import io.kyros.content.combat.melee.Prayer;
import io.kyros.content.combat.range.RangeData;
import io.kyros.content.commands.owner.SetAccuracyBonus;
import io.kyros.content.items.PvpWeapons;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.content.skills.Skill;
import io.kyros.content.skills.slayer.NewInterface;
import io.kyros.model.Bonus;
import io.kyros.model.CombatType;
import io.kyros.model.Items;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.definitions.ItemStats;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.npc.stats.NpcCombatDefinition;
import io.kyros.model.entity.npc.stats.NpcCombatSkill;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.CosmeticBoostsHandler;
import io.kyros.model.items.EquipmentSet;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

/**
 * @author Arthur Behesnilian 12:09 PM
 */
public class RangeCombatFormula implements CombatFormula {

    /**
     * The singleton instance for our standard Range combat formula
     */
    public static RangeCombatFormula STANDARD = new RangeCombatFormula();

    private int maxhit;

    public RangeCombatFormula() {
        this(-1);
    }

    public RangeCombatFormula(int maxHit) {
        this.maxhit = maxHit;
    }

    @Override
    public double getAccuracy(Entity attacker, Entity defender, double specialAttackMultiplier,
                              double defenceMultiplier) {
        double attack = getAttackRoll(attacker, defender, specialAttackMultiplier);
        int defence = (int) ((double) getDefenceRoll(attacker, defender) * defenceMultiplier);

        double accuracy;
        if (attack > defence) {
            accuracy = 1.0 - (defence + 2.0) / (2.0 * (attack + 1.0));
        } else {
            accuracy = attack / (2.0 * (defence + 1));
        }

        return accuracy;
    }

    private int getAttackRoll(Entity attacker, Entity defender, double specialAttackMultiplier) {
        double effectiveRangeLevel = attacker.isPlayer() ?
                getEffectiveRangeAttackLevel(attacker.asPlayer()) : getEffectiveRangeAttackLevel(attacker.asNPC());

        if (attacker.isPlayer() && defender.isNPC()) {
            effectiveRangeLevel += SetAccuracyBonus.RANGE_ATTACK - 8;
        }

        double equipmentRangeBonus = getEquipmentAttackRangeBonus(attacker);

        double maxRoll = effectiveRangeLevel * (equipmentRangeBonus + 64.0);
        if (attacker.isPlayer()) {
            maxRoll = applyAttackSpecials(attacker.asPlayer(), defender, maxRoll, specialAttackMultiplier);
            maxRoll = Math.floor(maxRoll);

            if (defender.isNPC()) {
                maxRoll *= getNpcAttackMultiplier(attacker.asPlayer(), defender.asNPC());
                maxRoll = Math.floor(maxRoll);
            }
        }

        return (int) (maxRoll);
    }

    private double getNpcAttackMultiplier(Player attacker, NPC defender) {
        double multiplier = 1.0;

        // Slayer Helmet boost

        if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.RANGE) && attacker.getSlayer().getUnlocks().contains(NewInterface.Unlock.SUPER_SLAYER_HELM.getUnlock())) {
            multiplier += 0.2666;
        } else if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.RANGE)) {
            multiplier += 0.1666;
        }
        //Salve Amulet
        if (getSalveAmuletMultiplier(attacker, defender) != 0) {
            multiplier += getSalveAmuletMultiplier(attacker, defender);
        }
        //Amulet of avarice
        if (attacker.getItems().isWearingItem(22557) && attacker.isSkulled && Boundary.isIn(attacker, Boundary.REV_CAVE)) {
            multiplier += 0.20;
        }

        if ((attacker.getCombatItems().usingCrystalBow() && attacker.getCombatItems().usingCrystalGear()) || attacker.getCombatItems().usingBofa() && attacker.getCombatItems().usingCrystalGear()) {
            if (attacker.getCurrentPet().hasPerk("rare_crystallized")) {
                multiplier += 0.20;
            }
        }

        if (attacker.getCurrentPet().hasPerk("rare_kree's_wrath") || attacker.getCurrentPet().hasPerk("rare_boss_slayer")) {
            if (attacker.getSlayer() != null) {
                if (attacker.getSlayer().onTask("aviansie") || attacker.getSlayer().onTask("kree'arra")) {
                    multiplier += 0.10;
                } else {
                    multiplier += 0.20;
                }
            }
        }

        if (attacker.getCurrentPet().hasPerk("p2w_bullseye") && attacker.getCombatItems().usingBlowPipe()) {
            multiplier += 0.25;
        }


        // DHCB Multiplier
        if (attacker.getItems().isWearingItem(Items.DRAGON_HUNTER_CROSSBOW, Player.playerWeapon)
                || attacker.getItems().isWearingItem(25916, Player.playerWeapon)
                || attacker.getItems().isWearingItem(25918, Player.playerWeapon)
                || attacker.getItems().isWearingItem(33206, Player.playerWeapon)
                || attacker.getItems().isWearingItem(26269, Player.playerWeapon))
            if (defender.isDragon())
                multiplier += 0.3;

        if (attacker.getPosition().inWild() && defender.getPosition().inWild()) {
            if (PvpWeapons.activateEffect(attacker, attacker.getItems().getWeapon())) {
                multiplier += 0.5;
            }
        }

        return multiplier;
    }

    private double getEffectiveRangeAttackLevel(Player player) {
        double effectiveLevel = Math.floor(player.playerLevel[Skill.RANGED.getId()] *
                getPrayerAttackMultiplier(player));

        switch (player.getCombatConfigs().getWeaponMode().getAttackStyle()) {
            case ACCURATE:
                effectiveLevel += effectiveLevel * 0.20; // Add 10% more damage
                break;
        }


        if (Discord.jda != null) {
            Guild guild = Discord.jda.getGuildById(1001818107343556648L);

            if (guild != null) {
                for (Member booster : guild.getBoosters()) {
                    if (player.getDiscordUser() == booster.getUser().getIdLong()) {
                        effectiveLevel *= 0.25;
                        effectiveLevel = Math.floor(effectiveLevel);
                        break;
                    }
                }
            }
        }

        if (player.getCurrentPet().hasPerk("rare_olms_enemy")) {
            if (Boundary.isIn(player, Boundary.RAIDROOMS)) {
                effectiveLevel += effectiveLevel * (player.getCurrentPet().findPetPerk("rare_olms_enemy").getValue()/100);
                effectiveLevel = Math.floor(effectiveLevel);
            }
        }

        if (player.getCurrentPet().hasPerk("rare_divine_god")) {
            if (Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS) || Boundary.isIn(player, Boundary.GODWARS_OTHER_ROOMS)) {
                effectiveLevel += effectiveLevel * (player.getCurrentPet().findPetPerk("rare_divine_god").getValue()/100);
                effectiveLevel = Math.floor(effectiveLevel);
            }
        }

        if (player.fullEliteVoidRange()) {
            effectiveLevel += effectiveLevel * 0.20;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullVoidRange() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.1;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullEliteORVoidRange() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.25;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullORVoidRange() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.15;
            effectiveLevel = Math.floor(effectiveLevel);
        }else if (player.fullMasori() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.35;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullMasoriF() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.40;
        } else if (player.fullPernix() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.45;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullPlague() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.50;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if ((player.fullIce() || player.fullArtorias()) && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.55;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullMalar() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.45;
            effectiveLevel = Math.floor(effectiveLevel);
        }

        if (player.fullHereditSet() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.07;
            effectiveLevel = Math.floor(effectiveLevel);
        }

        if (player.Hereditor() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.12;
            effectiveLevel = Math.floor(effectiveLevel);
        }

        if (player.getItems().isWearingItem(33206)) {
            effectiveLevel += effectiveLevel * 0.2;
            effectiveLevel = Math.floor(effectiveLevel);
        }

        if (player.getItems().isWearingItem(26269)) {
            effectiveLevel += effectiveLevel * 0.4;
            effectiveLevel = Math.floor(effectiveLevel);
        }

        if (player.getCurrentPet().hasPerk("p2w_bullseye") && player.getCombatItems().usingBlowPipe()) {
            effectiveLevel += effectiveLevel * 0.25;
            effectiveLevel = Math.floor(effectiveLevel);
        }

        if (player.hasEquippedSomewhere(9068)) {
            effectiveLevel += effectiveLevel * 0.75;
            effectiveLevel = Math.floor(effectiveLevel);
        }

        if ((player.getEquipmentToShow(Player.playerAura) == 10556  || player.hasEquippedSomewhere(29489)) && !player.getPosition().inWild()) { //attacker icon
            effectiveLevel += effectiveLevel * 0.15;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if ((player.playerEquipment[Player.playerFeet] == 22954
                || player.hasEquippedSomewhere(33403)
                || player.hasEquippedSomewhere(33408)
                || player.hasEquippedSomewhere(33420))
                && !player.getPosition().inWild()) { //Devout Boots
            effectiveLevel += effectiveLevel * 0.15;
            effectiveLevel = Math.floor(effectiveLevel);
        }

        if (player.getBoostHandler().getBoost(CosmeticBoostsHandler.CosmeticBoosts.ACCURACY_BONUS) > 0.0 && player.wildLevel <= 0) {
            effectiveLevel += effectiveLevel * player.getBoostHandler().getBoost(CosmeticBoostsHandler.CosmeticBoosts.ACCURACY_BONUS);
            effectiveLevel = Math.floor(effectiveLevel);
        }


//        System.out.println("Effective Level = " + effectiveLevel);

        return Math.floor(effectiveLevel);
    }

    private double getPrayerAttackMultiplier(Player player) {
        CombatPrayer combatPrayer = player.getCombatPrayer();

        if (combatPrayer.isPrayerActive(Prayer.SHARP_EYE.getId())) {
            return 1.05;
        } else if (combatPrayer.isPrayerActive(Prayer.HAWK_EYE.getId())) {
            return 1.1;
        } else if (combatPrayer.isPrayerActive(Prayer.EAGLE_EYE.getId())) {
            return 1.15;
        } else if (combatPrayer.isPrayerActive(Prayer.RIGOUR.getId())) {
            return 1.2;
        } else if (combatPrayer.isPrayerActive(Prayer.ANCIENT_SIGHT.getId())) {
            return 1.2;
        } else if (combatPrayer.isPrayerActive(Prayer.ANNIHILATE.getId())) {
            return 1.25;
        } else if (combatPrayer.isPrayerActive(Prayer.CENTURION.getId())) {
            return 1.25;
        } else {
            return 1.0;
        }
    }

    private double getEffectiveRangeAttackLevel(NPC npc) {
        NpcCombatDefinition definition = npc.getCombatDefinition();
        if (definition == null) {
            return SetAccuracyBonus.RANGE_ATTACK;
        }

        double effectiveLevel = definition.getLevel(NpcCombatSkill.RANGE);
        return effectiveLevel + SetAccuracyBonus.RANGE_ATTACK;
    }

    private double getEquipmentAttackRangeBonus(Entity attacker) {
        return attacker.getBonus(Bonus.ATTACK_RANGED);
    }

    private double applyAttackSpecials(Player attacker, Entity defender, double base,
                                       double specialAttackMultiplier) {
        double hit = base;

        hit *= getEquipmentMultiplier(attacker);
        hit = Math.floor(hit);

        if (specialAttackMultiplier == 1.0) {
            double multiplier = 1.0;

            // Twisted Bow Effect
            if (attacker.getItems().isWearingItem(Items.TWISTED_BOW, Player.playerWeapon)) {
                if (defender.isNPC()) {
                    double damageCap = 140.0;
                    int magicLevel = 0;
                    if (defender.isPlayer())
                        magicLevel = defender.asPlayer().playerLevel[Skill.MAGIC.getId()];
                    else {
                        NpcCombatDefinition definition = defender.asNPC().getCombatDefinition();
                        if (definition != null) {
                            magicLevel = definition.getLevel(NpcCombatSkill.MAGIC);
                        }
                    }
                    multiplier += getTwistedBowAccuracyBoost(magicLevel, Boundary.isIn(attacker, Boundary.XERIC));
                }
            }

            hit *= multiplier;
            hit = Math.floor(hit);
        } else {
            hit *= specialAttackMultiplier;
            hit = Math.floor(hit);
        }

        return hit;
    }

    private double getEquipmentMultiplier(Player attacker) {
        return 1.0;
    }


    private int getDefenceRoll(Entity attacker, Entity defender) {
        double effectiveDefenceLevel = defender.isPlayer() ?
                getEffectiveDefenceLevel(defender.asPlayer()) : getEffectiveDefenceLevel(defender.asNPC());

        int equipmentDefenceBonus = getEquipmentDefenceBonus(defender);

        double maxRoll = effectiveDefenceLevel * (equipmentDefenceBonus + 64.0);
        maxRoll = applyDefenceSpecials(defender, maxRoll);

        if (attacker.isNPC()) {
            maxRoll /= 2;
        }

        return (int) maxRoll;
    }

    private double applyDefenceSpecials(Entity defender, double base) {
        double hit = base;

        if (defender.isPlayer()) {
            Player player = defender.asPlayer();
            if (EquipmentSet.TORAG.isWearingBarrows(player) && EquipmentSet.AMULET_OF_THE_DAMNED.isWearing(player)) {
                hit *= ToragsEffect.modifyDefenceLevel(player);
                hit = Math.floor(hit);
            }
        }

        return hit;
    }

    private int getEquipmentDefenceBonus(Entity defender) {
        return defender.getBonus(Bonus.DEFENCE_RANGED);
    }

    private double getEffectiveDefenceLevel(Player player) {
        double effectiveLevel = Math.floor(player.playerLevel[Skill.RANGED.getId()] *
                getPrayerDefenceMultiplier(player));

        switch (player.getCombatConfigs().getWeaponMode().getAttackStyle()) {
            case DEFENSIVE:
                effectiveLevel += 3.0;
                break;
            case CONTROLLED:
                effectiveLevel += 1.0;
                break;
        }
        effectiveLevel += 8;

        return Math.floor(effectiveLevel);
    }

    private double getPrayerDefenceMultiplier(Player player) {
        CombatPrayer combatPrayer = player.getCombatPrayer();

        if (combatPrayer.isPrayerActive(Prayer.THICK_SKIN.getId())) {
            return 1.05;
        } else if (combatPrayer.isPrayerActive(Prayer.ROCK_SKIN.getId())) {
            return 1.10;
        } else if (combatPrayer.isPrayerActive(Prayer.STEEL_SKIN.getId())) {
            return 1.15;
        } else if (combatPrayer.isPrayerActive(Prayer.CHIVALRY.getId())) {
            return 1.20;
        } else if (combatPrayer.isPrayerActive(Prayer.PIETY.getId()) ||
                combatPrayer.isPrayerActive(Prayer.RIGOUR.getId()) ||
                combatPrayer.isPrayerActive(Prayer.DECIMATE.getId()) ||
                combatPrayer.isPrayerActive(Prayer.ANNIHILATE.getId()) ||
                combatPrayer.isPrayerActive(Prayer.CENTURION.getId()) ||
                combatPrayer.isPrayerActive(Prayer.VAPORISE.getId()) ||
                combatPrayer.isPrayerActive(Prayer.AUGURY.getId())) {
            return 1.25;
        } else {
            return 1.0;
        }
    }

    private double getEffectiveDefenceLevel(NPC npc) {
        NpcCombatDefinition definition = npc.getCombatDefinition();
        if (definition == null)
            return 8;

        return definition.getLevel(NpcCombatSkill.DEFENCE) + 8.0;
    }

    @Override
    public int getMaxHit(Entity attacker, Entity defender, double specialAttackMultiplier,
                         double specialPassiveMultiplier) {

        int base = 0;

        if (maxhit != -1) {
            base = maxhit;
        } else {
            double effectiveRangedLevel = attacker.isPlayer() ?
                    this.getEffectiveRangeLevel(attacker.asPlayer(), defender) : getEffectiveRangeLevel(attacker.asNPC());
            double equipmentBonus = getEquipmentRangeBonus(attacker);

            if (attacker.isPlayer()) {
                Player player = attacker.asPlayer();

                for (int i : RangeData.NO_ARROW_DROP) {
                    if (player.getItems().isWearingItem(i, Player.playerWeapon)) {
                        if (player.playerEquipment[Player.playerArrows] != -1 && !ItemDef.forId(player.playerEquipment[Player.playerArrows]).getName().contains("arrow")
                        && !ItemDef.forId(player.playerEquipment[Player.playerArrows]).getName().contains("Heredit Quiver")
                        && !ItemDef.forId(player.playerEquipment[Player.playerArrows]).getName().contains("Heredit Quiver (or)")) {
                            equipmentBonus -= (ItemStats.forId(player.playerEquipment[Player.playerArrows]).getEquipment().getRstr());
                        }
                    }
                }

                // If wearing blowpipe take the stored dart range bonus instead
                if (player.getItems().isWearingItem(Items.TOXIC_BLOWPIPE, Player.playerWeapon) || player.getItems().isWearingItem(33177, Player.playerWeapon)) {
                    int dartId = player.getToxicBlowpipeAmmo();
                    ItemStats stats = ItemStats.forId(dartId);
                    equipmentBonus += stats.getEquipment().getRstr();
                }
            }
            base = (int) Math.floor(1.3 + (effectiveRangedLevel / 10d) + (equipmentBonus / 80d) + ((effectiveRangedLevel * equipmentBonus) / 640d));
        }

        base = applyRangedSpecials(attacker, defender, base, specialAttackMultiplier, specialPassiveMultiplier);



        return (int) Math.floor(base);
    }

    private double getEffectiveRangeLevel(Player player, Entity target) {
        double effectiveLevel = Math.floor(player.playerLevel[Skill.RANGED.getId()] * getPrayerRangedMultiplier(player));

        if (target.isNPC()) {
            effectiveLevel *= getNpcMultipliers(player, target.asNPC());
            effectiveLevel = Math.floor(effectiveLevel);
        }

        if (player.fullEliteVoidRange() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.20;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullVoidRange() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.1;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullEliteORVoidRange() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.25;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullORVoidRange() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.15;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullMasori() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.30;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullMasoriF() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.35;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullPernix() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.40;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullPlague() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.45;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if ((player.fullIce() || player.fullArtorias()) && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.50;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullMalar() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.35;
            effectiveLevel = Math.floor(effectiveLevel);
        } else  if (player.fullSirenic() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.50;
            effectiveLevel = Math.floor(effectiveLevel);
        }

        if (player.fullHereditSet() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.07;
            effectiveLevel = Math.floor(effectiveLevel);
        }

        if (player.Hereditor() && !player.getPosition().inWild()) {
            effectiveLevel += effectiveLevel * 0.12;
            effectiveLevel = Math.floor(effectiveLevel);
        }

        if (player.getItems().isWearingItem(33206)) {
            effectiveLevel += effectiveLevel * 0.2;
            effectiveLevel = Math.floor(effectiveLevel);
        }

        if (player.getCurrentPet().hasPerk("p2w_bullseye") && player.getCombatItems().usingBlowPipe()) {
            effectiveLevel += effectiveLevel * 0.25;
            effectiveLevel = Math.floor(effectiveLevel);
        }

        switch (player.getCombatConfigs().getWeaponMode().getAttackStyle()) {
            case ACCURATE:
                effectiveLevel += effectiveLevel * 0.20; // Add 10% more damage
                break;
            case AGGRESSIVE:
                effectiveLevel -= (effectiveLevel * 0.20); // Subtract 15% from the effective level
                break;
        }

        return Math.floor(effectiveLevel);
    }

    private double getPrayerRangedMultiplier(Player player) {
        CombatPrayer combatPrayer = player.getCombatPrayer();

        if (combatPrayer.isPrayerActive(Prayer.SHARP_EYE.getId())) {
            return 1.05;
        } else if (combatPrayer.isPrayerActive(Prayer.HAWK_EYE.getId())) {
            return 1.10;
        } else if (combatPrayer.isPrayerActive(Prayer.EAGLE_EYE.getId())) {
            return 1.15;
        } else if (combatPrayer.isPrayerActive(Prayer.ANCIENT_SIGHT.getId())) {
            return 1.20;
        } else if (combatPrayer.isPrayerActive(Prayer.RIGOUR.getId())) {
            return 1.23;
        } else if (combatPrayer.isPrayerActive(Prayer.ANNIHILATE.getId())) {
            return 1.30;
        } else if (combatPrayer.isPrayerActive(Prayer.CENTURION.getId())) {
            return 1.30;
        } else {
            return 1.0;
        }
    }

    private double getEffectiveRangeLevel(NPC npc) {
        NpcCombatDefinition definition = npc.getCombatDefinition();
        if (definition == null)
            return 8;

        return definition.getLevel(NpcCombatSkill.RANGE) + 8.0;
    }

    private double getEquipmentRangeBonus(Entity attacker) {
        return attacker.getBonus(Bonus.RANGED_STRENGTH);
    }

    private int applyRangedSpecials(Entity attacker, Entity defender, int base, double specialAttackMultiplier,
                                    double specialPassiveMultiplier) {
        double hit = base;

        Player player = attacker.isPlayer() ? attacker.asPlayer() : null;
        if (player != null) {
            hit *= getEquipmentMultiplier(player);
            hit = Math.floor(hit);
        }

        if (player != null && specialAttackMultiplier == 1.0) {
            double multiplier = 1.0;

            if (player.getItems().isWearingItem(Items.TWISTED_BOW, Player.playerWeapon)) {
                if (defender.isNPC()) {
                    int magicLevel = 1;
                    if (defender.isPlayer()) {
                        magicLevel = defender.asPlayer().playerLevel[Skill.DEFENCE.getId()];
                    } else {
                        NpcCombatDefinition definition = defender.asNPC().getCombatDefinition();
                        if (definition != null) {
                            magicLevel = definition.getLevel(NpcCombatSkill.MAGIC);
                        }
                    }
                    multiplier = getTwistedBowDamageBoost(magicLevel, Boundary.isIn(player, Boundary.XERIC));
                }
            }

            hit *= multiplier;
            hit = Math.floor(hit);
        } else {
            hit *= specialAttackMultiplier;
            hit = Math.floor(hit);
        }

        if (defender.isPlayer()) {
            Player target = defender.asPlayer();
            CombatPrayer combatPrayer = target.getCombatPrayer();

            if (combatPrayer.isPrayerActive(Prayer.PROTECT_FROM_MISSILES.getId()) || combatPrayer.isPrayerActive(Prayer.DAMPEN_RANGED.getId())) {
                hit *= 0.6D;  // Reduce hit by 40% if Protect from Ranged is active
                hit = Math.floor(hit);  // Floor the result to get a whole number
            }
        }


        if (specialAttackMultiplier == 1.0 && player != null) {
            hit = applyPassiveMultiplier(player, defender, hit);
            hit = Math.floor(hit);
        } else {
            hit *= specialPassiveMultiplier;
            hit = Math.floor(hit);
        }

        hit *= getDamageDealMultiplier(attacker);
        hit = Math.floor(hit);

        hit *= getDamageTakenMultiplier(defender);
        hit = Math.floor(hit);

        return (int) hit;
    }

    private double getNpcMultipliers(Player attacker, NPC defender) {
        double multiplier = 1.0;

        if (attacker.getSlayer().isTaskNpc(defender)) {
            // Slayer Helmet boost
            if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.RANGE) && attacker.getSlayer().getUnlocks().contains(NewInterface.Unlock.SUPER_SLAYER_HELM.getUnlock())) {
                multiplier += 0.2666;
            } else if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.RANGE)) {
                multiplier += 0.1666;
            } else {
                // Salve Amulet boosts
                multiplier += getSalveAmuletMultiplier(attacker, defender);
            }

            if (BoostScrolls.checkSlayerBoost(attacker)) {
                multiplier += 0.10;
            }
        }

        if (BoostScrolls.checkDamageBoost(attacker)) {
            multiplier += 0.10;
        }

        double petBonus = attacker.getCurrentPet().findPetPerk("common_dmg_boost").getValue();

        if (petBonus > 0) {
            multiplier += (attacker.getCurrentPet().findPetPerk("common_dmg_boost").getValue() / 100);
        }

        // DHCB Multiplier
        if (attacker.getItems().isWearingItem(Items.DRAGON_HUNTER_CROSSBOW, Player.playerWeapon)
                || attacker.getItems().isWearingItem(25916, Player.playerWeapon)
                || attacker.getItems().isWearingItem(25918, Player.playerWeapon)
                || attacker.getItems().isWearingItem(33206, Player.playerWeapon)
                || attacker.getItems().isWearingItem(26269, Player.playerWeapon))
            if (defender.isDragon())
                multiplier += 0.25;

        if (attacker.getPosition().inWild() && defender.getPosition().inWild()) {
            if (PvpWeapons.activateEffect(attacker, attacker.getItems().getWeapon())) {
                multiplier += 0.5;
            }
        }

        // Pet Boosts
        boolean hasDarkRangePet = PetHandler.hasDarkRangePet(attacker);
        boolean hasRangePet = PetHandler.hasRangePet(attacker);
        if (hasDarkRangePet)
            multiplier += 0.10;
        else if (hasRangePet && Misc.isLucky(50))
            multiplier += 0.10;


        if (attacker.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33104) && attacker.wildLevel <= 0) {
            multiplier += 0.05;
        } else if (attacker.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33107) && attacker.wildLevel <= 0) {
            multiplier += 0.10;
        } else if (PrestigePerks.hasRelic(attacker, PrestigePerks.ZERK_RANGE_MAGE) && attacker.wildLevel <= 0) {
            multiplier += 0.10;
        }

        if (PrestigePerks.hasRelic(attacker, PrestigePerks.DAMAGE_BONUS1) && !attacker.getPosition().inWild()
                && !Boundary.isIn(attacker, Boundary.WG_Boundary)) {
            multiplier += 0.03;
        }

        if (PrestigePerks.hasRelic(attacker, PrestigePerks.DAMAGE_BONUS2) && !attacker.getPosition().inWild()
                && !Boundary.isIn(attacker, Boundary.WG_Boundary)) {
            multiplier += 0.03;
        }

        if (PrestigePerks.hasRelic(attacker, PrestigePerks.DAMAGE_BONUS3) && !attacker.getPosition().inWild()
                && !Boundary.isIn(attacker, Boundary.WG_Boundary)) {
            multiplier += 0.03;
        }

        if (PrestigePerks.hasRelic(attacker, PrestigePerks.EXPERIENCE_DAMAGE_BONUS1) && !attacker.getPosition().inWild()
                && !Boundary.isIn(attacker, Boundary.WG_Boundary)) {
            if (attacker.getMode().isOsrs() || attacker.getMode().is5x()) {
                multiplier += 0.12;
            } else {
                multiplier += 0.06;
            }
        }

        if (attacker.usingRage && !attacker.getPosition().inWild()) {
            multiplier += 0.75;
        }
        if ((attacker.hasEquippedSomewhere(10556)
                || attacker.hasEquippedSomewhere(29489)
                || attacker.hasEquippedSomewhere(22954)
                || attacker.hasEquippedSomewhere(33403)
                || attacker.hasEquippedSomewhere(33408)
                || attacker.hasEquippedSomewhere(33420))
                && !attacker.getPosition().inWild()) { //Devout Boots
            multiplier += 0.15;
        }
        if (attacker.hasFollower && attacker.petSummonId == 25348) {
            multiplier += 0.20;
        }

        return multiplier;
    }

    private double applyPassiveMultiplier(Player player, Entity defender, double hit) {
        return hit;
    }

    private double getDamageDealMultiplier(Entity attacker) {
        return 1.0;
    }

    private double getDamageTakenMultiplier(Entity defender) {
        if (defender.isPlayer()) {
            Player player = defender.asPlayer();

            if (player.getCombatItems().elyProc())
                return 0.75;
        }
        return 1.0;
    }

    public static double getTwistedBowAccuracyBoost(int magicLevel, boolean cox) {
        if (magicLevel > (cox ? 350 : 250))
            magicLevel = (cox ? 350 : 250);
        double boost = 140 + ((3d * magicLevel - 10d) / 100d) - (Math.pow(3d * magicLevel / 10d - 100d, 2) / 100d);
        return (Math.min(boost, 140) / 100);
    }

    public static double getTwistedBowDamageBoost(int magicLevel, boolean cox) {
        if (magicLevel > (cox ? 350 : 250))
            magicLevel = (cox ? 350 : 250);
        double boost = 250 + ((3d * magicLevel - 14d) / 100d) - (Math.pow((3d * magicLevel / 10d) - 140d, 2) / 100d);
        return (Math.min(boost, cox ? 350 : 250) / 100);
    }


}
