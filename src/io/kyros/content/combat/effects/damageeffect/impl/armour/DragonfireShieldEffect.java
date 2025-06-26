package io.kyros.content.combat.effects.damageeffect.impl.armour;

import java.util.Objects;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.effects.damageeffect.DamageBoostingEffect;
import io.kyros.content.combat.effects.damageeffect.DamageEffect;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;
import io.kyros.util.task.TaskManager;

/**
 * @author Jason MacKeigan
 * @date Dec 11, 2014, 4:44:33 AM
 */
public class DragonfireShieldEffect implements DamageBoostingEffect {

	static final long ATTACK_DELAY_REQUIRED = 120_000;

	private int cycle;

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		CycleEventHandler.getSingleton().addEvent(attacker, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer eventContainer) {
				if (Objects.isNull(attacker) || Objects.isNull(defender)) {
					eventContainer.stop();
					return;
				}
				if (defender.getHealth().getCurrentHealth() <= 0 || defender.isDead) {
					eventContainer.stop();
					return;
				}
				cycle++;
				if (cycle == 1) {
					attacker.startAnimation(6696);
					attacker.gfx0(1165);
					attacker.setDragonfireShieldCharge(attacker.getDragonfireShieldCharge() - 1);
					attacker.getPA().sendConfig(6539, 200 / 8);//so ticks divide by what it multiplies does same thing  good to know
					TaskManager.submit(200, () -> attacker.getPA().sendConfig(6539, 0));
				} else if (cycle == 4) {
					attacker.getPA().createPlayersProjectile2(attacker.getX(), attacker.getY(), (attacker.getY() - defender.getY()) * -1, (attacker.getX() - defender.getX()) * -1,
							50, 50, 1166, 30, 30, -defender.getIndex() - 1, 30, 5);
				} else if (cycle >= 5) {
					if (defender.playerEquipment[Player.playerShield] == 11283 || defender.playerEquipment[Player.playerShield] == 11284) {
						defender.appendDamage(attacker, (damage.getAmount() / 2) + (Misc.random(damage.getAmount() / 2)), damage.getAmount() > 0 ? HitMask.HIT : HitMask.MISS);
						eventContainer.stop();
						return;
					}
					defender.appendDamage(attacker, damage.getAmount(), damage.getAmount() > 0 ? HitMask.MISS : damage.getAmount() == 25? HitMask.HIT_MAX : HitMask.HIT);
					eventContainer.stop();
				}
			}
		}, 1);
	}

	@Override
	public boolean isExecutable(Player operator, Entity victim) {
		if(!operator.getAttributes().getBoolean("using_dfs", false)) {
			return false;
		}
		if (operator.getDragonfireShieldCharge() <= 0) {
			operator.sendMessage("Your dragonfire shield is out of charges, you need to refill it.");
			return false;
		}
		if (System.currentTimeMillis() - operator.getLastDragonfireShieldAttack() < ATTACK_DELAY_REQUIRED) {
			operator.sendMessage("You must let your dragonfire shield cool down before using it again.");
			return false;
		}
		operator.getAttributes().setBoolean("using_dfs", false);
		return true;
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		CycleEventHandler.getSingleton().addEvent(attacker, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer eventContainer) {
				if (Objects.isNull(attacker) || Objects.isNull(defender)) {
					eventContainer.stop();
					return;
				}
				if (defender.getHealth().getCurrentHealth() <= 0 || defender.isDead() || attacker.teleTimer > 0) {
					eventContainer.stop();
					return;
				}
				if (Misc.distanceToPoint(attacker.getX(), attacker.getY(), defender.getX(), defender.getY()) > 12) {
					eventContainer.stop();
					return;
				}
				cycle++;
				if (cycle == 1) {
					attacker.startAnimation(6696);
					attacker.gfx0(1165);
				} else if (cycle == 4) {
					attacker.getPA().createPlayersProjectile2(attacker.getX(), attacker.getY(), (attacker.getY() - defender.getY()) * -1, (attacker.getX() - defender.getX()) * -1,
							50, 50, 1166, 30, 30, -attacker.oldNpcIndex - 1, 30, 5);
				} else if (cycle >= 5) {
					defender.underAttack = true;
					defender.hitDiff = damage.getAmount();
					defender.appendDamage(attacker, damage.getAmount(), HitMask.HIT);
					defender.hitUpdateRequired = true;
					defender.setUpdateRequired(true);
					eventContainer.stop();
				}
			}
		}, 1);
	}

	@Override
	public double getMaxHitBoost(Player attacker, Entity defender) {
		return 0;
	}

	@Override
	public double getAccuracyBoost(Player attacker, Entity defender) {
		return 0;
	}
}
