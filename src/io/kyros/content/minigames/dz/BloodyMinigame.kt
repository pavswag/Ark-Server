package io.kyros.content.minigames.dz

import com.google.common.collect.Lists
import io.kyros.content.instances.InstanceConfiguration
import io.kyros.content.instances.impl.LegacySoloPlayerInstance
import io.kyros.content.item.lootable.LootRarity
import io.kyros.content.prestige.PrestigePerks
import io.kyros.model.cycleevent.CycleEvent
import io.kyros.model.cycleevent.CycleEventContainer
import io.kyros.model.cycleevent.CycleEventHandler
import io.kyros.model.definitions.NpcDef
import io.kyros.model.entity.npc.NPCHandler
import io.kyros.model.entity.npc.NPCSpawning
import io.kyros.model.entity.player.Boundary
import io.kyros.model.entity.player.Player
import io.kyros.model.entity.player.Position
import io.kyros.model.items.GameItem
import io.kyros.util.Misc
import java.util.*

class BloodyMinigame(player: Player?) :
    LegacySoloPlayerInstance(InstanceConfiguration.CLOSE_ON_EMPTY, player, Boundary.DONATOR_ZONE_BLOODY) {
    fun enter(player: Player, bm: BloodyMinigame) {
        player.bloody_wave = 0
        player.instance = bm
        player.moveTo(Position(1964, 5328, bm.height))
        bm.add(player)

        player.sendMessage("Welcome to the Bloody Battle. Your first wave will start soon.", 255)

        startSpawns(player)
    }

    private val type: Array<IntArray> = Wave.MAIN

    fun startSpawns(player: Player?) {
        CycleEventHandler.getSingleton().addEvent(player, object : CycleEvent() {
            override fun execute(event: CycleEventContainer) {
                if (player == null) {
                    event.stop()
                    return
                }

                if (!Boundary.isIn(player, Boundary.DONATOR_ZONE_BLOODY)) {
                    player.bloody_wave = 0
                    event.stop()
                    return
                }

                if (player.bloody_wave >= type.size && player.bloody_wave > 0) {
                    endGame(player)
                    event.stop()
                    return
                }

                if (player.bloody_wave_kills <= 0) {
                    player.bloody_wave_kills = (type[player.bloody_wave].size) //adjust KillReqCount
                    player.pa.sendString(
                        35428,
                        "Bloody Wave: " + (player.bloody_wave + 1) + ", Kills Remaining : " + player.bloody_wave_kills
                    )

                    val sb = StringBuilder()
                    for (i in type[player.bloody_wave].indices) {
                        if (!sb.toString().contains(NpcDef.forId(type[player.bloody_wave][i]).name)) {
                            sb.append(NpcDef.forId(type[player.bloody_wave][i]).name).append(", ")
                        }
                    }

                    player.pa.sendString(35429, "Next Wave: $sb")
                    if (player.bloody_wave == 20 && Misc.isLucky(20) || player.bloody_wave == 40 && Misc.isLucky(
                            30
                        ) || player.bloody_wave == 60 && Misc.isLucky(45)
                    ) {
                        giveExtraLoot(player)
                    }

                    for (i in 0 until player.bloody_wave_kills) {
                        val npcType = type[player.bloody_wave][i]
                        val x = 1965 + Misc.random(-4, 4)
                        val y = 5328 + Misc.random(-3, 3)
                        val n = NPCHandler.getNpc(npcType)
                        val maxhit = NPCHandler().getMaxHit(player, n)
                        val nerd = NPCSpawning.spawnNpc(player, npcType, x, y, player.instance.height, 1, maxhit, true, false)
                        nerd.needRespawn = false
                        nerd.behaviour.isRespawn = false
                        nerd.behaviour.isAggressive = true
                        nerd.combatDefinition.isAggressive = true
                    }
                }
                event.stop()
            }

            override fun onStopped() {
            }
        }, 3)
    }

    private fun endGame(player: Player) {
        giveExtraLoot(player)
        resetInstance(player, player.instance as BloodyMinigame, "Congratulations for finishing the Bloody Battle!")
    }

    fun leaveGame(player: Player) {
        resetInstance(player, player.instance as BloodyMinigame, "You have left the Bloody Battle.")
    }

    override fun handleDeath(player: Player): Boolean {
        resetInstance(
            player,
            player.instance as BloodyMinigame,
            "Unfortunately you died on wave " + player.bloody_wave + ". Better luck next time."
        )
        return true
    }

    fun resetInstance(player: Player, bm: BloodyMinigame, message: String?) {
        reward()
        bm.killNpcs()
        player.pa.movePlayer(1952, 5329, 0)
        player.instance = null
        player.bloody_wave = 0
        player.bloody_wave_kills = 0
        player.sendErrorMessage(message)
    }


    fun reward() {
        var amt = getPoints(player.bloody_wave)
        if (Misc.isLucky(5)) {
            amt += 150
        }
        if (PrestigePerks.hasRelic(player, PrestigePerks.BLOODY_MINIGAME)) {
            amt *= 2
        }
        player.bloody_points = (player.bloody_points + amt)
        player.sendMessage("You now have a total of " + (player.bloody_points) + " Bloody Points.")
    }

    fun getPoints(wave: Int): Int {
        if (wave >= 20) {
            return wave * 6
        }
        return wave * 3
    }

    fun giveExtraLoot(player: Player) {
        val rewards = randomItem
        for (item in rewards) {
            player.items.addItemUnderAnyCircumstance(item.id, item.amount)
            player.sendMessage("You've earned a reward in Bloody Minigame, " + item.def.name + "!")
        }
    }

    val loot: Map<LootRarity, List<GameItem>>
        get() = items

    val randomItem: List<GameItem>
        get() {
            val rewards: MutableList<GameItem> = Lists.newArrayList()

            rewards.add(Misc.getRandomItem(loot[LootRarity.COMMON]))

            return rewards
        }

    companion object {
        private val items: MutableMap<LootRarity, List<GameItem>> = HashMap()

        init {
            items[LootRarity.COMMON] = Arrays.asList(
                GameItem(26714),  //Arma (or) helm
                GameItem(26715),  //Arma (or) chest
                GameItem(26716),  //Arma (or) legs
                GameItem(26718),  //Bandos (or) Chest
                GameItem(26719),  //Bandos (or) Legs
                GameItem(26720),  //Bandos (or) Boots
                GameItem(691),  //10k foundry points
                GameItem(8167),  //foundry mystery chest
                GameItem(27112),  //barrows gloves
                GameItem(12873),  //guthan's set
                GameItem(12875),  //verac's set
                GameItem(12877),  //dharok's set
                GameItem(12879),  //torag set
                GameItem(12881),  //ahrims set
                GameItem(12883),  //karils set
                GameItem(2577),  //ranger boots
                GameItem(6920),  //infinity boots
                GameItem(11840),  //dragon boots
                GameItem(6199),  //m box
                GameItem(12873),  //guthan's set
                GameItem(12875),  //verac's set
                GameItem(12877),  //dharok's set
                GameItem(12879),  //torag set
                GameItem(12881),  //ahrims set
                GameItem(12883),  //karils set
                GameItem(2577),  //ranger boots
                GameItem(6920),  //infinity boots
                GameItem(11840),  //dragon boots
                GameItem(6199),  //m box
                GameItem(13615),  //graceful hood
                GameItem(13617),  //graceful cape
                GameItem(13619),  //graceful chest
                GameItem(13621),  //graceful legs
                GameItem(13623),  //graceful gloves
                GameItem(13625),  //graceful boots
                GameItem(4069),  //deco armor
                GameItem(4070),  //deco armor
                GameItem(4504),  //deco armor
                GameItem(4505),  //deco armor
                GameItem(4509),  //deco armor
                GameItem(4510),  //deco armor
                GameItem(11899),  //deco armor
                GameItem(11900),  //deco armor
                GameItem(24165),  //deco armor
                GameItem(24163),  //deco armor
                GameItem(24164),  //deco armor
                GameItem(6199),  //m box
                GameItem(13615),  //graceful hood
                GameItem(13617),  //graceful cape
                GameItem(13619),  //graceful chest
                GameItem(13621),  //graceful legs
                GameItem(13623),  //graceful gloves
                GameItem(13625),  //graceful boots
                GameItem(4069),  //deco armor
                GameItem(4070),  //deco armor
                GameItem(4504),  //deco armor
                GameItem(4505),  //deco armor
                GameItem(4509),  //deco armor
                GameItem(4510),  //deco armor
                GameItem(11899),  //deco armor
                GameItem(11900),  //deco armor
                GameItem(24165),  //deco armor
                GameItem(24163),  //deco armor
                GameItem(24164),  //deco armor
                GameItem(12873),  //guthan's set
                GameItem(12875),  //verac's set
                GameItem(12877),  //dharok's set
                GameItem(12879),  //torag set
                GameItem(12881),  //ahrims set
                GameItem(12883),  //karils set
                GameItem(2577),  //ranger boots
                GameItem(6920),  //infinity boots
                GameItem(11840),  //dragon boots
                GameItem(6199),  //m box
                GameItem(12873),  //guthan's set
                GameItem(12875),  //verac's set
                GameItem(12877),  //dharok's set
                GameItem(12879),  //torag set
                GameItem(12881),  //ahrims set
                GameItem(12883),  //karils set
                GameItem(2577),  //ranger boots
                GameItem(6920),  //infinity boots
                GameItem(11840),  //dragon boots
                GameItem(6199),  //m box
                GameItem(13615),  //graceful hood
                GameItem(13617),  //graceful cape
                GameItem(13619),  //graceful chest
                GameItem(13621),  //graceful legs
                GameItem(13623),  //graceful gloves
                GameItem(13625),  //graceful boots
                GameItem(4069),  //deco armor
                GameItem(4070),  //deco armor
                GameItem(4504),  //deco armor
                GameItem(4505),  //deco armor
                GameItem(4509),  //deco armor
                GameItem(4510),  //deco armor
                GameItem(11899),  //deco armor
                GameItem(11900),  //deco armor
                GameItem(24165),  //deco armor
                GameItem(24163),  //deco armor
                GameItem(24164),  //deco armor
                GameItem(6199),  //m box
                GameItem(13615),  //graceful hood
                GameItem(13617),  //graceful cape
                GameItem(13619),  //graceful chest
                GameItem(13621),  //graceful legs
                GameItem(13623),  //graceful gloves
                GameItem(13625),  //graceful boots
                GameItem(4069),  //deco armor
                GameItem(4070),  //deco armor
                GameItem(4504),  //deco armor
                GameItem(4505),  //deco armor
                GameItem(4509),  //deco armor
                GameItem(4510),  //deco armor
                GameItem(11899),  //deco armor
                GameItem(11900),  //deco armor
                GameItem(24165),  //deco armor
                GameItem(24163),  //deco armor
                GameItem(24164),  //deco armor
                GameItem(12873),  //guthan's set
                GameItem(12875),  //verac's set
                GameItem(12877),  //dharok's set
                GameItem(12879),  //torag set
                GameItem(12881),  //ahrims set
                GameItem(12883),  //karils set
                GameItem(2577),  //ranger boots
                GameItem(6920),  //infinity boots
                GameItem(11840),  //dragon boots
                GameItem(6199),  //m box
                GameItem(12873),  //guthan's set
                GameItem(12875),  //verac's set
                GameItem(12877),  //dharok's set
                GameItem(12879),  //torag set
                GameItem(12881),  //ahrims set
                GameItem(12883),  //karils set
                GameItem(2577),  //ranger boots
                GameItem(6920),  //infinity boots
                GameItem(11840),  //dragon boots
                GameItem(6199),  //m box
                GameItem(13615),  //graceful hood
                GameItem(13617),  //graceful cape
                GameItem(13619),  //graceful chest
                GameItem(13621),  //graceful legs
                GameItem(13623),  //graceful gloves
                GameItem(13625),  //graceful boots
                GameItem(4069),  //deco armor
                GameItem(4070),  //deco armor
                GameItem(4504),  //deco armor
                GameItem(4505),  //deco armor
                GameItem(4509),  //deco armor
                GameItem(4510),  //deco armor
                GameItem(11899),  //deco armor
                GameItem(11900),  //deco armor
                GameItem(24165),  //deco armor
                GameItem(24163),  //deco armor
                GameItem(24164),  //deco armor
                GameItem(6199),  //m box
                GameItem(13615),  //graceful hood
                GameItem(13617),  //graceful cape
                GameItem(13619),  //graceful chest
                GameItem(13621),  //graceful legs
                GameItem(13623),  //graceful gloves
                GameItem(13625),  //graceful boots
                GameItem(4069),  //deco armor
                GameItem(4070),  //deco armor
                GameItem(4504),  //deco armor
                GameItem(4505),  //deco armor
                GameItem(4509),  //deco armor
                GameItem(4510),  //deco armor
                GameItem(11899),  //deco armor
                GameItem(11900),  //deco armor
                GameItem(24165),  //deco armor
                GameItem(24163),  //deco armor
                GameItem(24164),  //deco armor
                GameItem(12873),  //guthan's set
                GameItem(12875),  //verac's set
                GameItem(12877),  //dharok's set
                GameItem(12879),  //torag set
                GameItem(12881),  //ahrims set
                GameItem(12883),  //karils set
                GameItem(2577),  //ranger boots
                GameItem(6920),  //infinity boots
                GameItem(11840),  //dragon boots
                GameItem(6199),  //m box
                GameItem(12873),  //guthan's set
                GameItem(12875),  //verac's set
                GameItem(12877),  //dharok's set
                GameItem(12879),  //torag set
                GameItem(12881),  //ahrims set
                GameItem(12883),  //karils set
                GameItem(2577),  //ranger boots
                GameItem(6920),  //infinity boots
                GameItem(11840),  //dragon boots
                GameItem(6199),  //m box
                GameItem(13615),  //graceful hood
                GameItem(13617),  //graceful cape
                GameItem(13619),  //graceful chest
                GameItem(13621),  //graceful legs
                GameItem(13623),  //graceful gloves
                GameItem(13625),  //graceful boots
                GameItem(4069),  //deco armor
                GameItem(4070),  //deco armor
                GameItem(4504),  //deco armor
                GameItem(4505),  //deco armor
                GameItem(4509),  //deco armor
                GameItem(4510),  //deco armor
                GameItem(11899),  //deco armor
                GameItem(11900),  //deco armor
                GameItem(24165),  //deco armor
                GameItem(24163),  //deco armor
                GameItem(24164),  //deco armor
                GameItem(6199),  //m box
                GameItem(13615),  //graceful hood
                GameItem(13617),  //graceful cape
                GameItem(13619),  //graceful chest
                GameItem(13621),  //graceful legs
                GameItem(13623),  //graceful gloves
                GameItem(13625),  //graceful boots
                GameItem(4069),  //deco armor
                GameItem(4070),  //deco armor
                GameItem(4504),  //deco armor
                GameItem(4505),  //deco armor
                GameItem(4509),  //deco armor
                GameItem(4510),  //deco armor
                GameItem(11899),  //deco armor
                GameItem(11900),  //deco armor
                GameItem(24165),  //deco armor
                GameItem(24163),  //deco armor
                GameItem(24164),  //deco armor
                GameItem(12873),  //guthan's set
                GameItem(12875),  //verac's set
                GameItem(12877),  //dharok's set
                GameItem(12879),  //torag set
                GameItem(12881),  //ahrims set
                GameItem(12883),  //karils set
                GameItem(2577),  //ranger boots
                GameItem(6920),  //infinity boots
                GameItem(11840),  //dragon boots
                GameItem(6199),  //m box
                GameItem(12873),  //guthan's set
                GameItem(12875),  //verac's set
                GameItem(12877),  //dharok's set
                GameItem(12879),  //torag set
                GameItem(12881),  //ahrims set
                GameItem(12883),  //karils set
                GameItem(2577),  //ranger boots
                GameItem(6920),  //infinity boots
                GameItem(11840),  //dragon boots
                GameItem(6199),  //m box
                GameItem(13615),  //graceful hood
                GameItem(13617),  //graceful cape
                GameItem(13619),  //graceful chest
                GameItem(13621),  //graceful legs
                GameItem(13623),  //graceful gloves
                GameItem(13625),  //graceful boots
                GameItem(4069),  //deco armor
                GameItem(4070),  //deco armor
                GameItem(4504),  //deco armor
                GameItem(4505),  //deco armor
                GameItem(4509),  //deco armor
                GameItem(4510),  //deco armor
                GameItem(11899),  //deco armor
                GameItem(11900),  //deco armor
                GameItem(24165),  //deco armor
                GameItem(24163),  //deco armor
                GameItem(24164),  //deco armor
                GameItem(6199),  //m box
                GameItem(13615),  //graceful hood
                GameItem(13617),  //graceful cape
                GameItem(13619),  //graceful chest
                GameItem(13621),  //graceful legs
                GameItem(13623),  //graceful gloves
                GameItem(13625),  //graceful boots
                GameItem(4069),  //deco armor
                GameItem(4070),  //deco armor
                GameItem(4504),  //deco armor
                GameItem(4505),  //deco armor
                GameItem(4509),  //deco armor
                GameItem(4510),  //deco armor
                GameItem(11899),  //deco armor
                GameItem(11900),  //deco armor
                GameItem(24165),  //deco armor
                GameItem(24163),  //deco armor
                GameItem(24164),  //deco armor
                GameItem(26526),  //Cannon piece
                GameItem(26522),  //Cannon piece
                GameItem(26520),  //Cannon piece
                GameItem(26524) //Cannon piece

            )
        }
    }
}