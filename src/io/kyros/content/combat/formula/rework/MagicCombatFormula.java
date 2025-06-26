package io.kyros.content.combat.formula.rework;

import io.kyros.Configuration;
import io.kyros.content.bonus.BoostScrolls;
import io.kyros.content.combat.effects.damageeffect.impl.amuletofthedamned.impl.AhrimEffect;
import io.kyros.content.combat.formula.MagicMaxHit;
import io.kyros.content.combat.magic.CombatSpellData;
import io.kyros.content.combat.melee.CombatPrayer;
import io.kyros.content.combat.melee.Prayer;
import io.kyros.content.commands.owner.SetAccuracyBonus;
import io.kyros.content.commands.owner.SetDefenceBonus;
import io.kyros.content.items.PvpWeapons;
import io.kyros.content.minigames.castlewars.CastleWarsLobby;
import io.kyros.content.pet.PetPerk;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.content.skills.Skill;
import io.kyros.content.skills.slayer.NewInterface;
import io.kyros.model.Bonus;
import io.kyros.model.CombatType;
import io.kyros.model.Items;
import io.kyros.model.SpellBook;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.npc.stats.NpcCombatDefinition;
import io.kyros.model.entity.npc.stats.NpcCombatSkill;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.CosmeticBoostsHandler;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.Arrays;

/**
 * @author Arthur Behesnilian 2:42 PM
 */
public class MagicCombatFormula implements CombatFormula {

    public static MagicCombatFormula STANDARD = new MagicCombatFormula();

    private int maxHit;

    public MagicCombatFormula() {
        this.maxHit = -1;
    }

    public MagicCombatFormula(int maxHit) {
        this.maxHit = maxHit;
    }

    @Override
    public double getAccuracy(Entity attacker, Entity defender, double specialAttackMultiplier,
                              double defenceMultiplier) {

        int attack = getAttackRoll(attacker, defender);
        int defence = (int) ((defender.isPlayer() ? getDefenceRoll(attacker, defender.asPlayer()) :
                getDefenceRoll(defender.asNPC())) * defenceMultiplier);

        double accuracy = 0;

        if (attack > defence) {
            accuracy = 1.0 - (defence + 2.0) / (2.0 * (attack + 1.0));
        } else {
            accuracy = attack / (2.0 * (defence + 1));
        }

        return accuracy;
    }

    private int getAttackRoll(Entity attacker, Entity defender) {
        double effectiveMagicLevel = attacker.isPlayer() ?
                getEffectiveAttackLevel(attacker.asPlayer()) : getEffectiveAttackLevel(attacker.asNPC());

        if (attacker.isPlayer() && defender.isNPC()) {
            effectiveMagicLevel += SetAccuracyBonus.MAGE_ATTACK - 8;
        }

        double equipmentAttackBonus = getEquipmentAttackBonus(attacker);
        if (attacker.isPlayer() && attacker.asPlayer().wildLevel > 0 && equipmentAttackBonus > 150) {
            equipmentAttackBonus = 150;
        }

        if (attacker.isPlayer() && attacker.asPlayer().playerEquipment[Player.playerWeapon] == 84 && !attacker.asPlayer().getPosition().inWild()) {
            equipmentAttackBonus *= 2;
            effectiveMagicLevel *= 2;
        }
        if (attacker.isPlayer() && attacker.asPlayer().playerEquipment[Player.playerWeapon] == 33446 && !attacker.asPlayer().getPosition().inWild()) {
            equipmentAttackBonus *= 2;
            effectiveMagicLevel *= 2;
        }
        if (attacker.isPlayer() && attacker.asPlayer().playerEquipment[Player.playerWeapon] == 33341 && !attacker.asPlayer().getPosition().inWild()) {
            equipmentAttackBonus *= 2;
        }

        double maxRoll = effectiveMagicLevel * (equipmentAttackBonus + 64);
        if (attacker.isPlayer()) {
            maxRoll = applyAttackSpecials(attacker.asPlayer(), maxRoll);
            maxRoll = Math.floor(maxRoll);

            if (defender.isNPC()) {
                maxRoll *= getNpcAttackMultiplier(attacker.asPlayer(), defender.asNPC());
                maxRoll = Math.floor(maxRoll);
            }
        }
        return (int) maxRoll;
    }

    private double getNpcAttackMultiplier(Player attacker, NPC defender) {
        double multiplier = 1.0;

        // Slayer Task boosts
        if (attacker.getSlayer().isTaskNpc(defender)) {
            // Slayer Helmet boost
            if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.MELEE)) {
                multiplier += 0.1666;
            }
            if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.RANGE) && attacker.getSlayer().getUnlocks().contains(NewInterface.Unlock.SUPER_SLAYER_HELM.getUnlock())) {
                multiplier += 0.2666;
            } else if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.RANGE)) {
                multiplier += 0.1666;
            }
            //Salve Amulet
            if (getSalveAmuletMultiplier(attacker, defender) != 0) {
                multiplier += getSalveAmuletMultiplier(attacker, defender);
            }

            if (BoostScrolls.checkSlayerBoost(attacker)) {
                multiplier += 0.10;
            }
        }

        if (BoostScrolls.checkDamageBoost(attacker)) {
            multiplier += 0.10;
        }

        //Amulet of avarice
        if (attacker.getItems().isWearingItem(22557) && attacker.isSkulled && Boundary.isIn(attacker, Boundary.REV_CAVE)) {
            multiplier += 0.20;
        }

        if (attacker.getPosition().inWild() && defender.getPosition().inWild()) {
            if (PvpWeapons.activateEffect(attacker, attacker.getItems().getWeapon())) {
                multiplier += 1.0;
            }
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

    private double applyAttackSpecials(Player attacker, double base) {
        double hit = base;

        hit *= getEquipmentMultiplier(attacker);
        hit = Math.floor(hit);

        if (attacker.getItems().isWearingItem(Items.MYSTIC_SMOKE_STAFF)) {
            hit *= 1.1;
            hit = Math.floor(hit);
        }

        return hit;
    }

    private double getEquipmentMultiplier(Player attacker) {
        return 1.0;
    }

    private double getEquipmentAttackBonus(Entity attacker) {
        return attacker.getBonus(Bonus.ATTACK_MAGIC);
    }

    private static int[] TRIDENTS = {
            Items.TRIDENT_OF_THE_SEAS, Items.TRIDENT_OF_THE_SEAS_E,
            Items.TRIDENT_OF_THE_SEAS_FULL, Items.TRIDENT_OF_THE_SEAS_FULL_2,
            Items.TRIDENT_OF_THE_SWAMP, Items.TRIDENT_OF_THE_SWAMP_E,
            Items.TRIDENT_OF_THE_SWAMP_E_2
    };

    private double getEffectiveAttackLevel(Player player) {
        double effectiveLevel = Math.floor(player.playerLevel[Skill.MAGIC.getId()] * getPrayerAttackMultiplier(player));

        //if (player.getItems().hasAnyItem(Player.playerWeapon, TRIDENTS)) {
        //    switch (player.getCombatConfigs().getWeaponMode().getAttackStyle()) {
        //        case ACCURATE:
        //            effectiveLevel += 3.0;
        //            break;
        //        case CONTROLLED:
        //            effectiveLevel += 1.0;
        //            break;
        //    }
        //}

        effectiveLevel += 8.0;

        if (Discord.jda != null && player.wildLevel <= 0) {
            Guild guild = Discord.jda.getGuildById(1001818107343556648L);

            if (guild != null) {
                for (Member booster : guild.getBoosters()) {
                    if (player.getDiscordUser() == booster.getUser().getIdLong()) {
                        effectiveLevel += effectiveLevel * 0.25;
                        effectiveLevel = Math.floor(effectiveLevel);
                    }
                }
            }
        }

        if (player.getCurrentPet().hasPerk("rare_olms_enemy") && player.wildLevel <= 0) {
            if (Boundary.isIn(player, Boundary.RAIDROOMS)) {
                effectiveLevel += effectiveLevel * (player.getCurrentPet().findPetPerk("rare_olms_enemy").getValue()/100);
                effectiveLevel = Math.floor(effectiveLevel);
            }
        }

        if (player.getCurrentPet().hasPerk("rare_divine_god") && player.wildLevel <= 0) {
            if (Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS) || Boundary.isIn(player, Boundary.GODWARS_OTHER_ROOMS)) {
                effectiveLevel += effectiveLevel * (player.getCurrentPet().findPetPerk("rare_divine_god").getValue()/100);
                effectiveLevel = Math.floor(effectiveLevel);
            }
        }

        if (player.fullVoidMage() && player.wildLevel <= 0) {
            effectiveLevel += effectiveLevel * 0.10;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullORVoidMage() && player.wildLevel <= 0) {
            effectiveLevel += effectiveLevel * 0.15;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullEliteORVoidMage() && !player.getPosition().inWild() && player.wildLevel <= 0) {
            effectiveLevel += effectiveLevel * 0.20;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullCeremonial() && !player.getPosition().inWild() && player.wildLevel <= 0) {
            effectiveLevel += effectiveLevel * 0.25;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullMalar() && !player.getPosition().inWild() && player.wildLevel <= 0) {
            effectiveLevel += effectiveLevel * 0.30;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullBloodbark() && !player.getPosition().inWild() && player.wildLevel <= 0) {
            effectiveLevel += effectiveLevel * 0.35;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if (player.fullReverie() && !player.getPosition().inWild() && player.wildLevel <= 0) {
            effectiveLevel += effectiveLevel * 0.40;
            effectiveLevel = Math.floor(effectiveLevel);
        } else if ((player.fullElderor() || player.fullArtorias()) && !player.getPosition().inWild() && player.wildLevel <= 0) {
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

        if (player.hasEquippedSomewhere(9068)) {
            effectiveLevel += effectiveLevel * 0.75;
            effectiveLevel = Math.floor(effectiveLevel);
        }

        if ((player.hasEquippedSomewhere(10556)
                || player.hasEquippedSomewhere(29489)
                || player.hasEquippedSomewhere(22954)
                || player.hasEquippedSomewhere(33403)
                || player.hasEquippedSomewhere(33408)
                || player.hasEquippedSomewhere(33420))
                && !player.getPosition().inWild()) { //Devout Boots
            effectiveLevel += effectiveLevel * 0.15;
        }

        if (player.getBoostHandler().getBoost(CosmeticBoostsHandler.CosmeticBoosts.ACCURACY_BONUS) > 0.0 && player.wildLevel <= 0) {
            effectiveLevel += effectiveLevel * player.getBoostHandler().getBoost(CosmeticBoostsHandler.CosmeticBoosts.ACCURACY_BONUS);
            effectiveLevel = Math.floor(effectiveLevel);
        }

        return Math.floor(effectiveLevel);
    }

    private double getPrayerAttackMultiplier(Player player) {
        CombatPrayer combatPrayer = player.getCombatPrayer();

        if (combatPrayer.isPrayerActive(Prayer.MYSTIC_WILL.getId())) {
            return 1.05;
        } else if (combatPrayer.isPrayerActive(Prayer.MYSTIC_LORE.getId())) {
            return 1.1;
        } else if (combatPrayer.isPrayerActive(Prayer.MYSTIC_MIGHT.getId())) {
            return 1.15;
        } else if (combatPrayer.isPrayerActive(Prayer.TRINITAS.getId())) {
            return 1.18;
        } else if (combatPrayer.isPrayerActive(Prayer.INTENSIFY.getId())) {
            return 1.25;
        } else if (combatPrayer.isPrayerActive(Prayer.ANCIENT_WILL.getId())) {
            return 1.20;
        } else if (combatPrayer.isPrayerActive(Prayer.AUGURY.getId())) {
            return 1.25;
        } else if (combatPrayer.isPrayerActive(Prayer.VAPORISE.getId())) {
            return 1.25;
        } else {
            return 1.0;
        }
    }

    private double getEffectiveAttackLevel(NPC npc) {
        NpcCombatDefinition definition = npc.getCombatDefinition();
        if (definition == null)
            return 8;

        return definition.getLevel(NpcCombatSkill.MAGIC) + 8;
    }

    private double getDefenceRoll(Entity attacker, Player player) {
        double effectiveDefenceLevel = getEffectiveDefenceLevel(player);

        if (attacker.isNPC()) {
            effectiveDefenceLevel += SetDefenceBonus.MAGE_DEFENCE - 8;
        }

        effectiveDefenceLevel *= 0.3;
        effectiveDefenceLevel = Math.floor(effectiveDefenceLevel);

        double magicLevel = player.playerLevel[Skill.MAGIC.getId()];
        magicLevel *= getPrayerAttackMultiplier(player);
        magicLevel = Math.floor(magicLevel);

        magicLevel *= 0.7;
        magicLevel = Math.floor(magicLevel);

        double effectiveMagicLevel = Math.floor(effectiveDefenceLevel + magicLevel + 8);
        double equipmentDefenceBonus = getEffectiveDefenceBonus(player);

        double maxRoll = effectiveMagicLevel * (equipmentDefenceBonus + 64);

        if (attacker.isNPC()) {
            maxRoll /= 2;
        }

        return maxRoll;
    }

    private double getEffectiveDefenceLevel(Player player) {
        double effectiveLevel = Math.floor(player.playerLevel[Skill.DEFENCE.getId()] *
                getPrayerDefenceMultiplier(player));

        switch (player.getCombatConfigs().getWeaponMode().getAttackStyle()) {
            case DEFENSIVE:
                effectiveLevel += 3.0;
                break;
            case CONTROLLED:
                effectiveLevel += 1.0;
                break;
        }

        effectiveLevel += 8.0;
        return Math.floor(effectiveLevel);
    }

    private double getPrayerDefenceMultiplier(Player player) {
        CombatPrayer combatPrayer = player.getCombatPrayer();

        if (combatPrayer.isPrayerActive(Prayer.THICK_SKIN.getId())) {
            return 1.05;
        } else if (combatPrayer.isPrayerActive(Prayer.ROCK_SKIN.getId())) {
            return 1.1;
        } else if (combatPrayer.isPrayerActive(Prayer.STEEL_SKIN.getId())) {
            return 1.15;
        } else if (combatPrayer.isPrayerActive(Prayer.CHIVALRY.getId())) {
            return 1.2;
        } else if (combatPrayer.isPrayerActive(Prayer.PIETY.getId())) {
            return 1.25;
        } else if (combatPrayer.isPrayerActive(Prayer.RIGOUR.getId())) {
            return 1.25;
        } else if (combatPrayer.isPrayerActive(Prayer.AUGURY.getId())) {
            return 1.25;
        } else if (combatPrayer.isPrayerActive(Prayer.VAPORISE.getId())) {
            return 1.30;
        } else if (combatPrayer.isPrayerActive(Prayer.ANNIHILATE.getId())) {
            return 1.30;
        } else if (combatPrayer.isPrayerActive(Prayer.CENTURION.getId())) {
            return 1.30;
        } else if (combatPrayer.isPrayerActive(Prayer.DECIMATE.getId())) {
            return 1.30;
        } else {
            return 1.0;
        }
    }


    private int getDefenceRoll(NPC npc) {
        double effectiveDefenceLevel = getEffectiveDefenceLevel(npc);
        double equipmentDefenceBonus = getEffectiveDefenceBonus(npc);

        double maxRoll = effectiveDefenceLevel * (equipmentDefenceBonus + 64);

        return (int) maxRoll;
    }

    public int getEffectiveDefenceLevel(NPC npc) {
        NpcCombatDefinition definition = npc.getCombatDefinition();
        if (definition == null)
            return 8;

        return definition.getLevel(NpcCombatSkill.MAGIC) + 9;
    }

    private double getEffectiveDefenceBonus(Entity entity) {
        return entity.getBonus(Bonus.DEFENCE_MAGIC);
    }

    @Override
    public int getMaxHit(Entity attacker, Entity defender, double specialAttackMultiplier,
                         double specialPassiveMultiplier) {
        int[] spellData = null;
        double hit = 0;
        if (attacker.isPlayer()) {
            Player player = attacker.asPlayer();
            if (player.getSpellId() > -1 && !player.usingSpecial) {
                spellData = CombatSpellData.MAGIC_SPELLS[player.getSpellId()];
            } else {
                if (this.hasSanguinestiCharges(player)) {
                    hit = this.getSanguinestuMaxHit(player);
                } else if (this.hasTridentCharges(player)) {
                    hit = this.getTridentMaxHit(player);
                }
            }
        }
        if (this.maxHit != -1) {
            hit = this.maxHit;
        } else if (spellData != null && hit == 0) {
            hit = CombatSpellData.getBaseDamage(spellData);
        } else if (attacker.isNPC()) {
            NPC npc = attacker.asNPC();
            hit = npc.maxHit;
        }

        if (attacker.isPlayer()) {
            Player player = attacker.asPlayer();

            // God Spell bonus
            if (CombatSpellData.godSpells(player)) {
                if (System.currentTimeMillis() - player.godSpellDelay < Configuration.GOD_SPELL_CHARGE) {
                    hit += 10;
                }
            }

            if (spellData != null) {
                if (player.getItems().isWearingItem(Player.playerHands, Items.CHAOS_GAUNTLETS)) {
                    boolean isBoltSpell = CombatSpellData.isBoltSpell(spellData[0]);
                    if (isBoltSpell)
                        hit += 3;
                }
            }

            int magicDamageBonus = attacker.getBonus(Bonus.MAGIC_DMG);

            if (!player.getCombatItems().usingNightmareStaffSpecial() && player.playerEquipment[Player.playerWeapon] == 24424) {
                if (magicDamageBonus > 100) {
                    magicDamageBonus = 50;
                }
            } else if(!player.getCombatItems().usingEldritchStaffSpecial() && player.playerEquipment[Player.playerWeapon] == 24425) {
                if (magicDamageBonus > 100) {
                    magicDamageBonus = 50;
                }
            } else if (player.wildLevel > 0) {
                magicDamageBonus = 75;
            }

            double multiplier = 1.0 + (magicDamageBonus / 100.0);

            // Tome of Fire bonus
            if (player.getCombatItems().hasTomeOfFire()) {
                boolean usingNightMareStaffSpecial = player.getCombatItems().usingNightmareStaffSpecial();
                if (CombatSpellData.fireSpells(player) && spellData != null) {
                    multiplier += 0.5;
                    player.getTomeOfFire().decrCharge();
                }
            }

            if (player.getCombatPrayer().isPrayerActive(Prayer.ANCIENT_WILL.getId())) {
                multiplier += 0.20;
            }
            if (player.getCombatPrayer().isPrayerActive(Prayer.AUGURY.getId())) {
                multiplier += 0.25;
            }
            if (player.getCombatPrayer().isPrayerActive(Prayer.VAPORISE.getId())) {
                multiplier += 0.25;
            }
            if (player.getCombatPrayer().isPrayerActive(Prayer.TRINITAS.getId())) {
                multiplier += 0.19;
            }
            if (player.getCombatPrayer().isPrayerActive(Prayer.INTENSIFY.getId())) {
                multiplier += 0.25;
            }

            if (player.getItems().hasAnyItem(Player.playerAmulet, Items.AMULET_OF_THE_DAMNED_FULL,
                    Items.AMULET_OF_THE_DAMNED)) {
                boolean hasExtra = AhrimEffect.INSTANCE.hasExtraRequirement(player);
                if (hasExtra && Misc.random(4) == 1) {
                    multiplier += 0.3;
                }
            }


            if (player.getItems().isWearingItem(27275, Player.playerWeapon) && !player.getPosition().inWild()) {
                multiplier += 1.000;
            }

            if (player.getItems().isWearingItem(33433, Player.playerWeapon) && !player.getPosition().inWild()) {
                multiplier += 1.725;
            }

            if (player.getItems().isWearingItem(84, Player.playerWeapon) && !player.getPosition().inWild()) {
                multiplier += 2.5;
            }

            if (player.getItems().isWearingItem(29594, Player.playerWeapon) && !player.getPosition().inWild()) {
                multiplier += 1.545;
            }
            if (player.getItems().isWearingItem(28796, Player.playerWeapon) && !player.getPosition().inWild()) {
                multiplier += 1.725;
            }

            if (player.getItems().isWearingItem(33149, Player.playerWeapon) && !player.getPosition().inWild()) {
                multiplier += 0.500;
            }



            if (player.getCombatItems().usingNightmareStaffSpecial())
                hit = MagicMaxHit.getNightmareSpecialMaxHit((25), 44);
            else if(player.getCombatItems().usingEldritchStaffSpecial())
                hit = MagicMaxHit.getNightmareSpecialMaxHit((21), 39);

            if (player.playerLevel[Skill.MAGIC.getId()] > 100) {
                int mageLevel = (player.playerLevel[Skill.MAGIC.getId()] - 100);
                multiplier += ((double) mageLevel / 10);
            }

            if (player.fullEliteVoidMage()) {
                multiplier += 0.025;
            } else if (player.fullEliteORVoidMage() && !player.getPosition().inWild()) {
                multiplier += 0.075;
            } else if (player.fullORVoidMage() && !player.getPosition().inWild()) {
                multiplier += 0.050;
            } else if (player.fullCeremonial() && !player.getPosition().inWild()) {
                multiplier += 0.100;
            } else if (player.fullMalar() && !player.getPosition().inWild()) {
                multiplier += 0.150;
            } else if (player.fullTectonic() && !player.getPosition().inWild()) {
                multiplier += 0.150;
            } else if (player.fullBloodbark() && !player.getPosition().inWild()) {
                multiplier += 0.162;
            } else if (player.fullReverie() && !player.getPosition().inWild()) {
                multiplier += 0.200;
            } else if ((player.fullElderor() || player.fullArtorias()) && !player.getPosition().inWild()) {
                multiplier += 0.230;
            }


            if (player.fullHereditSet() && !player.getPosition().inWild()) {
                multiplier += 0.075;
            }
            if (player.Hereditor() && !player.getPosition().inWild()) {
                multiplier += 0.12;
            }

            if (PrestigePerks.hasRelic(player, PrestigePerks.DAMAGE_BONUS1) && !player.getPosition().inWild()
                    && !Boundary.isIn(player, Boundary.WG_Boundary)
                    && !CastleWarsLobby.isInCw(player) && !CastleWarsLobby.isInCwWait(player)) {
                multiplier += 0.03;
            }

            if (PrestigePerks.hasRelic(player, PrestigePerks.DAMAGE_BONUS2) && !player.getPosition().inWild()
                    && !Boundary.isIn(player, Boundary.WG_Boundary)
                    && !CastleWarsLobby.isInCw(player) && !CastleWarsLobby.isInCwWait(player)) {
                multiplier += 0.03;
            }

            if (PrestigePerks.hasRelic(player, PrestigePerks.DAMAGE_BONUS3) && !player.getPosition().inWild()
                    && !Boundary.isIn(player, Boundary.WG_Boundary)
                    && !CastleWarsLobby.isInCw(player) && !CastleWarsLobby.isInCwWait(player)) {
                multiplier += 0.03;
            }

            if (PrestigePerks.hasRelic(player, PrestigePerks.EXPERIENCE_DAMAGE_BONUS1) && !player.getPosition().inWild()
                    && !Boundary.isIn(player, Boundary.WG_Boundary)
                    && !CastleWarsLobby.isInCw(player) && !CastleWarsLobby.isInCwWait(player)) {
                if (player.getMode().isOsrs() || player.getMode().is5x()) {
                    multiplier += 0.12;
                } else {
                    multiplier += 0.06;
                }
            }

            if (player.usingRage && !player.getPosition().inWild()) {
                multiplier += 0.75;
            }

            hit *= multiplier;
            hit = Math.floor(hit);

        } else if (attacker.isNPC()) {
            double multiplier = 1.0 + (attacker.getBonus(Bonus.MAGIC_DMG) / 100.0);
            hit += multiplier;
            hit = Math.floor(hit);
        }

        if (defender.isPlayer()) {
            Player player = defender.asPlayer();
            if (player.getCombatPrayer().isPrayerActive(Prayer.PROTECT_FROM_MAGIC.getId()) || player.getCombatPrayer().isPrayerActive(Prayer.DAMPEN_MAGIC.getId())) {
                hit *= 0.6D;  // Reduces hit by 40% when Protect from Magic is active
                hit = Math.floor(hit);  // Rounds the hit to a whole number
            }
        }

        hit *= getDamageDealMultiplier(attacker);
        hit = Math.floor(hit);

        hit *= getDamageTakenMultiplier(defender);
        hit = Math.floor(hit);

        if (attacker.isPlayer() && defender.isNPC()) {
            hit *= getNpcMultipliers(attacker.asPlayer(), defender.asNPC());
            hit = Math.floor(hit);
        }

        return (int) hit;
    }

    private double getNpcMultipliers(Player attacker, NPC defender) {
        double multiplier = 1.0;

        // Slayer Helmet boost
        if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.RANGE) && attacker.getSlayer().getUnlocks().contains(NewInterface.Unlock.SUPER_SLAYER_HELM.getUnlock())) {
            multiplier += 0.2666;
        } else if (attacker.getSlayer().hasSlayerHelmBoost(defender, CombatType.RANGE)) {
            multiplier += 0.1666;
        } else {
            // Salve Amulet boosts
            multiplier += getSalveAmuletMultiplier(attacker, defender);
        }

        if (attacker.getPosition().inWild() && defender.getPosition().inWild()) {
            if (PvpWeapons.activateEffect(attacker, attacker.getItems().getWeapon())) {
                multiplier += 0.25;
            }
        }


        double petBonus = attacker.getCurrentPet().findPetPerk("common_dmg_boost").getValue();

        if (petBonus > 0) {
            multiplier += (attacker.getCurrentPet().findPetPerk("common_dmg_boost").getValue() / 100);
        }

        // Pet boosts
        boolean hasDarkMagePet = PetHandler.hasDarkMagePet(attacker);
        boolean hasMagePet = PetHandler.hasMagePet(attacker);
        if (hasDarkMagePet)
            multiplier += 0.10;
        else if(hasMagePet && Misc.isLucky(50))
            multiplier += 0.10;

        if (attacker.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33103) && attacker.wildLevel <= 0) {
            multiplier += 0.05;
        } else if (attacker.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33106) && attacker.wildLevel <= 0) {
            multiplier += 0.10;
        } else if (PrestigePerks.hasRelic(attacker, PrestigePerks.ZERK_RANGE_MAGE) && attacker.wildLevel <= 0) {
            multiplier += 0.10;
        }

        return multiplier;
    }

    private double getDamageTakenMultiplier(Entity defender) {
        if (defender.isPlayer()) {
            Player player = defender.asPlayer();

            if (player.getCombatItems().elyProc())
                return 0.75;

        }
        return 1.0;
    }

    private double getDamageDealMultiplier(Entity attacker) {
        return 1.0;
    }

    public boolean hasTridentCharges(Player player) {
        if (player.getItems().hasAnyItem(Player.playerWeapon, TRIDENTS)) {
            return player.getToxicTridentCharge() > 0 || player.getTridentCharge() > 0;
        }
        return false;
    }

    private static int[] TOXIC_TRIDENTS = {
            Items.TRIDENT_OF_THE_SWAMP, Items.TRIDENT_OF_THE_SWAMP_E, Items.TRIDENT_OF_THE_SEAS_FULL
    };

    private boolean isToxic(int weaponId) {
        return Arrays.stream(TOXIC_TRIDENTS).anyMatch(id -> id == weaponId);
    }

    public int getTridentMaxHit(Player player) {
        int weaponId = player.getItems().getWeapon();
        if (weaponId == -1)
            return 0;

        boolean isToxic = isToxic(weaponId);
        int baseHit = isToxic ? 23 : 20;
        int magicLevel = Math.max(25, player.getLevelForXP(player.playerXP[Skill.MAGIC.getId()]));
        int extraHits = (int) ((double) (magicLevel - 75) / 3);
        return baseHit + extraHits;
    }

    public boolean hasSanguinestiCharges(Player player) {
        if (player.getItems().hasAnyItem(Player.playerWeapon, Items.SANGUINESTI_STAFF,
                Items.SANGUINESTI_STAFF_UNCHARGED)) {
            return player.getSangStaffCharge() > 0;
        }
        return false;
    }

    public int getSanguinestuMaxHit(Player player) {
        int baseHit = 12;
        int magicLevel = Math.max(player.playerLevel[Skill.MAGIC.getId()], 75);
        int extraHits = (int) ((double) (magicLevel - 75) / 3);
        return baseHit + extraHits;
    }

}
