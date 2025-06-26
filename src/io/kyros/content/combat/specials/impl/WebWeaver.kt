package io.kyros.content.combat.specials.impl

import io.kyros.content.WeaponGames.WGModes.max
import io.kyros.content.combat.Damage
import io.kyros.content.combat.HitMask
import io.kyros.content.combat.specials.Special
import io.kyros.content.skills.Skill
import io.kyros.model.CombatType
import io.kyros.model.entity.Entity
import io.kyros.model.entity.player.Player
import io.kyros.model.entity.player.PlayerAssistant
import io.kyros.util.Misc

class WebWeaver : Special(5.0, 4.0, 1.0, intArrayOf(27655)) {

    override fun activate(player: Player, target: Entity, damage: Damage) {
        player.startAnimation(9964)
        player.gfx0(2354)

        // Schedule a task to apply damage to the target after 2 seconds
        player.queue({
            when {
                target.isPlayer -> target.asPlayer().gfx0(2355)
                target.isNPC -> target.asNPC().gfx0(2355)
            }
        }, 1)

        if (damage.amount == 0) {
            val second = Misc.random(0, max)
            val maxHit = second == max
            if (second == 0) {
                doHit(player, target, 0, maxHit, 0)
                doHit(player, target, (max * 0.75).toInt(), maxHit, 1)
                doHit(player, target, (max * 0.75).toInt(), maxHit, 1)
            } else {
                doHit(player, target, second, maxHit, 0)
                doHit(player, target, second / 2, maxHit, 1)
                doHit(player, target, second / 2, maxHit, 1)
            }
        } else {
            val halvedHit = if (damage.amount == 0) 0 else damage.amount / 2
            val finalHit = if (halvedHit == 0) 0 else halvedHit / 2
            doHit(player, target, halvedHit, true, 0)
            doHit(player, target, finalHit, true, 1)
            doHit(player, target, finalHit, true, 1)
        }
    }

    private fun doHit(player: Player, target: Entity, damage: Int, maxDamage: Boolean, delay: Int) {
        player.damageQueue.add(
            Damage(
                target,
                damage,
                player.hitDelay + delay,
                player.playerEquipment,
                if (damage > 0) if (maxDamage) HitMask.HIT_MAX else HitMask.HIT else HitMask.MISS,
                CombatType.RANGE
            )
        )
        player.pa.addXpDrop(PlayerAssistant.XpDrop(damage, Skill.ATTACK.id))
    }

    override fun hit(player: Player, target: Entity, damage: Damage) {
    }
}