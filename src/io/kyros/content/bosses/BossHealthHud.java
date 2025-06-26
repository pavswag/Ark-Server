package io.kyros.content.bosses;

import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 24/03/2024
 */
public enum BossHealthHud {

    //Type 0 = Normal hood
    //Type 1 = Orange Shield
    //Type 2 = Cyan Shield
    //Type 3 = Purple Shield
    //Type 4 = Blue Shield
    //Type 5 = Yellow Shield
    //Type 6 = Rainbow Shield

    NEX(11278, 0),
    ALCHEMICAL_HYDRA(8622, 0),
    VORKATH(8060, 0),
    ABYSSAL_SIRE(5890, 0),
    CERBERUS(5862, 0),
    ZULRAH(2043, 0),
    CORPOREAL_BEAST(319, 0),
    KRAKEN(494, 0),
    THERMO(499, 0),
    SARACHNIS(8713, 0),
    KALPHITE_QUEEN(965, 0),
    LIZARDMAN_SHAMAN(6766, 0),
    GIANT_MOLE(5779, 0),
    OBOR(7416, 0),
    BRYOPHYTA(8195, 0),
    BARREL_CHEST(6342, 0),
    DUKE_SUCELLUS(12166, 0),
    DUKE_SUCELLUS_1(12191, 0),
    THE_LEVIATHAN(12214, 0),
    THE_WHISPERER(12205, 0),
    VARDORVIS(12223, 0),
    OLM_HEAD(7554, 0),
    OLM_LEFT_HAND(7553, 0),
    OLM_RIGHT_HAND(7555, 0),
    VESPULA(7531, 0),
    MUTTADILE(7563, 0),
    TEKTON(7544, 0),
    VASA(7566, 0),
    MAIDEN(8_360, 0),
    NYCLOCAS_MELEE(8355, 0),
    NYCLOCAS_MAGIC(8356, 0),
    NYCLOCAS_RANGE(8357, 0),
    XAARPUS(8_340, 0),
    VERZIK_1(8_372, 0),
    VERZIK_2(8_374, 0),
    SOTETSEG(8_388, 0),
    BLOAT(8_359, 0),
    DONOR_BOSS(8096, 4),
    VOTE_BOSS(5126, 5),
    GROOT(4923, 2),
    DURIAL(5169, 3),
    JUSTICAR(12449, 3),
    BABA(11775, 1),
    CHAOTIC_DEATH_SPAWN(7649, 4),
    SOL_HEREDIT(12821, 4),
    SUPERIOR_SOL_HEREDIT(12783, 4),
    Sharathteerk(12617, 2),
    Yama(10936, 2),
    Xamphur(10956, 2),
    ARAXXOR(13668, 0),
    DUMMY(7413, 0),
    QUEEN_LAT(8781, 0),
    ;

    private int npcId;
    private int type;

    BossHealthHud(int npcId, int type) {
        this.npcId = npcId;
        this.type = type;
    }

    public static Optional<BossHealthHud> getBossHealthHudForNpc(int id) {
        return Arrays.stream(values()).filter(it -> it.npcId == id).findAny();
    }


    public static void handleBossHud(Player player, NPC npc) {
        getBossHealthHudForNpc(npc.getNpcId()).ifPresent(bossId -> player.getPA().sendHealthHud(bossId.type, npc.getDefinition().getName(), npc.getHealth().getCurrentHealth(), npc.getHealth().getMaximumHealth()));

    }
}
