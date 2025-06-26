package io.kyros.content.combat;

import io.kyros.annotate.PostInit;
import io.kyros.content.combat.effects.damageeffect.DamageBoostingEffect;
import io.kyros.content.combat.weapon.CombatStyle;
import io.kyros.model.CombatType;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.stats.NpcCombatDefinition;
import io.kyros.model.entity.player.Player;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Arthur Behesnilian 1:56 PM
 */
@Slf4j
public class CombatConfigs {

    @Getter
    private static List<DamageBoostingEffect> damageBoostingEffects = new ArrayList<>();

    @PostInit
    public static void loadDamageBoostingEffects() {
        Reflections reflections = new Reflections("io.kyros.content.combat", new SubTypesScanner(false));
        Set<Class<? extends DamageBoostingEffect>> implementingClasses =
                reflections.getSubTypesOf(DamageBoostingEffect.class);
        for (Class<? extends DamageBoostingEffect> clazz : implementingClasses) {
            try {
                DamageBoostingEffect effect = clazz.getDeclaredConstructor().newInstance();
                damageBoostingEffects.add(effect);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("Loaded {} combat gear effects.", damageBoostingEffects.size());
    }

    /**
     * Retrieves the combat style for the Npc
     * @param definition The npc definition
     * @return The combat style for the Npc
     */
    private static CombatStyle getCombatStyleForNpc(NpcCombatDefinition definition) {
        switch(definition.getAttackStyle()) {
            case "Stab":
                return CombatStyle.STAB;
            case "Slash":
                return CombatStyle.SLASH;
            case "Crush":
                return CombatStyle.CRUSH;
            case "Ranged":
                return CombatStyle.RANGE;
            case "Magic":
                return CombatStyle.MAGIC;
            default:
                return CombatStyle.SPECIAL;
        }
    }

    /**
     * Retrieves the Combat style for the different entity types
     * @param entity The entity whose Combat style is being checked
     * @return The combat style for the entity type
     */
    public static CombatStyle getCombatStyle(Entity entity) {
        if (entity.isNPC()) {
            NPC n = entity.asNPC();
            NpcCombatDefinition definition = NpcCombatDefinition.definitions.get(n.getNpcId());
            if (definition != null) {
                return getCombatStyleForNpc(definition);
            }
            return CombatStyle.SPECIAL;
        } else if (entity.isPlayer()) {
            Player player = entity.asPlayer();

            switch (getCombatType(player)) {
                case MAGE:
                    return CombatStyle.MAGIC;
                case RANGE:
                    return CombatStyle.RANGE;
                default:
                    return player.getCombatConfigs().getWeaponMode().getCombatStyle();
            }

        } else {
            throw new IllegalArgumentException("You cannot use that entity type here. ");
        }
    }

    /**
     * Determines the combat type for the player
     * @param player The player
     * @return The combat type of the player
     */
    public static CombatType getCombatType(Player player) {
        if (player.usingMagic) {
            return CombatType.MAGE;
        } else if (player.usingBow || player.usingOtherRangeWeapons || player.usingCross || player.usingBallista) {
            return CombatType.RANGE;
        } else {
            return CombatType.MELEE;
        }
    }

}
