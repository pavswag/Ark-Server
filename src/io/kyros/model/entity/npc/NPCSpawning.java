package io.kyros.model.entity.npc;

import io.kyros.content.instances.InstancedArea;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.definitions.NpcStats;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.thrall.ThrallSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.kyros.Server.getNpcs;

public class NPCSpawning {

    private static final Logger logger = LoggerFactory.getLogger(NPCSpawning.class);

    private static void log(NPC npc) {
        logger.debug("Spawned {}.", npc);
    }

    public static NpcStats getStats(int hp, int attack, int defence) {
        return NpcStats.builder().setHitpoints(hp).setAttackLevel(attack).setRangeLevel(attack).setMagicLevel(attack).setDefenceLevel(defence).createNpcStats();
    }

    public static NPC spawnNpcOld(final Player c, int npcType, int x, int y, int heightLevel, int WalkingType, int HP, int maxHit, int attack, int defence, boolean attackPlayer, boolean headIcon) {
        return spawnNpc(c, npcType, x, y, heightLevel, WalkingType, maxHit, attackPlayer, headIcon, getStats(HP, attack, defence));
    }

    public static NPC spawnNpcOld(int npcType, int x, int y, int heightLevel, int WalkingType, int HP, int maxHit, int attack, int defence) {
        return spawnNpc(npcType, x, y, heightLevel, WalkingType, maxHit, getStats(HP, attack, defence));
    }

    public static NPC spawnNpc(final Player c, int npcType, int x, int y, int heightLevel, int WalkingType, int maxHit, boolean attackPlayer, boolean headIcon) {
        return spawnNpc(c, npcType, x, y, heightLevel, WalkingType, maxHit, attackPlayer, headIcon, null);
    }

    /**
     * Summon npc, barrows, etc
     */
    public static NPC spawnNpc(final Player c, int npcType, int x, int y, int heightLevel, int WalkingType, int maxHit, boolean attackPlayer, boolean headIcon, NpcStats npcStats) {


        NpcDef definition = NpcDef.forId(npcType);
        NPC newNPC;
        if (npcStats == null) {
            newNPC = new NPC(npcType, definition);
        } else {
            newNPC = new NPC(npcType, definition, npcStats);
        }
        newNPC.absX = x;
        newNPC.absY = y;
        newNPC.makeX = x;
        newNPC.makeY = y;
        newNPC.getSize();
        newNPC.heightLevel = heightLevel;

        newNPC.walkingType = WalkingType;
        newNPC.maxHit = maxHit;
        newNPC.spawnedBy = c.getIndex();
        getNpcs().add(newNPC);
        int slot = newNPC.getIndex();
        if (c.getInstance() != null) {
            c.getInstance().add(newNPC);
        }
        newNPC.getRegionProvider().addNpcClipping(newNPC);
        if (headIcon) c.getPA().drawHeadicon(1, slot);
        if (attackPlayer) {
            newNPC.underAttack = true;
            newNPC.setPlayerAttackingIndex(c.getIndex());
            c.underAttackByPlayer = slot;
            c.underAttackByNpc = slot;
        }
        if (newNPC.getNpcId() == 1605) {
            newNPC.forceChat("You must prove yourself... now!");
            newNPC.gfx100(86);
        }
        if (newNPC.getNpcId() == 1606) {
            newNPC.forceChat("This is only the beginning, you can\'t beat me!");
            newNPC.gfx100(86);
        }
        if (newNPC.getNpcId() == 1607) {
            newNPC.forceChat("Foolish mortal, I am unstoppable.");
        }
        if (newNPC.getNpcId() == 1608) {
            newNPC.forceChat("Now you feel it... The dark energy.");
        }
        if (newNPC.getNpcId() == 1609) {
            newNPC.forceChat("Aaaaaaaarrgghhhh! The power!");
        }
        log(newNPC);
        return newNPC;
    }

    public static NPC spawnNpc(int npcType, int x, int y, int heightLevel, int WalkingType, int maxHit) {
        return spawnNpc(npcType, x, y, heightLevel, WalkingType, maxHit, null);
    }

    public static NPC spawnNpc(int npcType, int x, int y, int heightLevel, int WalkingType, int maxHit, NpcStats npcStats) {
        // first, search for a free slot
        NpcDef definition = NpcDef.forId(npcType);
        NPC newNPC;
        if (npcStats == null) {
            newNPC = new NPC(npcType, definition);
        } else {
            newNPC = new NPC(npcType, definition, npcStats);
        }
        newNPC.absX = x;
        newNPC.absY = y;
        newNPC.makeX = x;
        newNPC.makeY = y;
        newNPC.heightLevel = heightLevel;
        newNPC.getRegionProvider().addNpcClipping(newNPC);
        newNPC.walkingType = WalkingType;
        newNPC.maxHit = maxHit;
        getNpcs().add(newNPC);
        log(newNPC);
        return newNPC;
    }

    public static NPC spawn(int npcType, int x, int y, int heightLevel, int WalkingType, int maxHit, boolean attackPlayer) {
        return spawn(npcType, x, y, heightLevel, WalkingType, maxHit, attackPlayer, null);
    }

    public static void spawn(NPC npc) {

        getNpcs().add(npc);
        log(npc);
    }

    public static NPC spawn(int npcType, int x, int y, int heightLevel, int WalkingType, int maxHit, boolean attackPlayer, NpcStats npcStats) {

        NpcDef definition = NpcDef.forId(npcType);
        NPC newNPC;
        if (npcStats != null) {
            newNPC = new NPC(npcType, definition, npcStats);
        } else {
            newNPC = new NPC(npcType, definition);
        }
        newNPC.absX = x;
        newNPC.absY = y;
        newNPC.makeX = x;
        newNPC.makeY = y;
        newNPC.heightLevel = heightLevel;
        newNPC.getRegionProvider().addNpcClipping(newNPC);
        newNPC.walkingType = WalkingType;
        newNPC.maxHit = maxHit;
        getNpcs().add(newNPC);
        log(newNPC);
        return newNPC;
    }

    public static NPC spawnPet(Player player, int npcId, int x, int y, int z, int maxHit, boolean attackPlayer, boolean headIcon, boolean summonFollow) {
        NpcDef definition = NpcDef.forId(npcId);
        NPC newNPC = new NPC(npcId, definition);
        newNPC.absX = x;
        newNPC.absY = y;
        newNPC.makeX = x;
        newNPC.makeY = y;
        newNPC.revokeWalkingPrivilege = false;
        newNPC.heightLevel = z;
        newNPC.walkingType = 0;
        newNPC.maxHit = maxHit;
        newNPC.spawnedBy = player.getIndex();
        newNPC.underAttack = true;
        newNPC.isPet = true;
        newNPC.facePlayer(player.getIndex());
        getNpcs().add(newNPC);
        int slot = newNPC.getIndex();
        if (headIcon)
            player.getPA().drawHeadicon(1, slot);
        if (summonFollow) {
            newNPC.summoner = true;
            newNPC.summonedBy = player.getIndex();
            player.hasFollower = true;
        }
        if (attackPlayer) {
            newNPC.underAttack = true;
            newNPC.setPlayerAttackingIndex(player.getIndex());
        }
        log(newNPC);
        return newNPC;
    }

    public static void spawnThrall(Player player, int npcId, int x, int y, int z, int maxHit, boolean attackPlayer, boolean headIcon, boolean summonFollow) {
        NpcDef definition = NpcDef.forId(npcId);
        NPC newNPC = new NPC(npcId, definition);
        newNPC.absX = x;
        newNPC.absY = y;
        newNPC.makeX = x;
        newNPC.makeY = y;
        newNPC.revokeWalkingPrivilege = false;
        newNPC.heightLevel = z;
        newNPC.walkingType = 0;
        newNPC.walkingHome = false;
        newNPC.maxHit = maxHit;
        newNPC.spawnedBy = player.getIndex();
        newNPC.underAttack = false;
        newNPC.isThrall = true;
        newNPC.facePlayer(player.getIndex());
        getNpcs().add(newNPC);
        int slot = newNPC.getIndex();
        if (headIcon)
            player.getPA().drawHeadicon(1, slot);
        if (summonFollow) {
            newNPC.ThrallSummoner = true;
            newNPC.summonedBy = player.getIndex();
            player.hasThrall = true;
        }
        if (attackPlayer) {
            newNPC.underAttack = true;
            newNPC.setPlayerAttackingIndex(player.getIndex());
        }
        log(newNPC);
        ThrallSystem.handleThrallAutoAttack(newNPC);
    }

    /**
     * Spawn a new npc on the world
     *  @param npcType
     *            the npcType were spawning
     * @param x
     *            the x coordinate were spawning on
     * @param y
     *            the y coordinate were spawning on
     * @param heightLevel
     *            the heightLevel were spawning on
     * @param WalkingType
     *            the WalkingType were setting
     * @param maxHit
     *            the maxHit were setting
     */
    public static NPC newNPC(int npcIndex, int npcType, int x, int y, int heightLevel, int WalkingType, int maxHit) {
        NpcDef definition = NpcDef.forId(npcType);
        NPC newNPC = new NPC(npcType, definition);
        newNPC.absX = x;
        newNPC.absY = y;
        newNPC.makeX = x;
        newNPC.makeY = y;
        newNPC.heightLevel = heightLevel;
        newNPC.getRegionProvider().addNpcClipping(newNPC);
        newNPC.walkingType = WalkingType;
        newNPC.maxHit = maxHit;
        newNPC.resetDamageTaken();
        getNpcs().add(newNPC);
        log(newNPC);
        return newNPC;
    }

    public static void finishNpcConstruction(NPC npc, int WalkingType, int maxHit) {
        npc.walkingType = WalkingType;
        npc.maxHit = maxHit;
        npc.resetDamageTaken();
    }

    public static NPC spawnNpc(InstancedArea instance, int npcType, int x, int y, int heightLevel, int WalkingType, int maxHit) {
        NpcDef definition = NpcDef.forId(npcType);
        NPC newNPC = new NPC(npcType, definition);
        newNPC.absX = x;
        newNPC.absY = y;
        newNPC.makeX = x;
        newNPC.makeY = y;
        newNPC.heightLevel = heightLevel;
        newNPC.getRegionProvider().addNpcClipping(newNPC);
        newNPC.walkingType = WalkingType;
        newNPC.maxHit = maxHit;
        if (instance != null) {
            instance.add(newNPC);
        }
        getNpcs().add(newNPC);
        log(newNPC);
        return newNPC;
    }
}
