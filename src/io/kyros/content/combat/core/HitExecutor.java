package io.kyros.content.combat.core;

import io.kyros.content.bosses.BossHealthHud;
import io.kyros.content.combat.Damage;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.effects.damageeffect.DamageEffect;
import io.kyros.content.combat.effects.damageeffect.impl.staffs.TridentOfTheSwampEffect;
import io.kyros.content.combat.magic.CombatSpellData;
import io.kyros.content.combat.magic.SanguinestiStaff;
import io.kyros.content.combat.magic.SoundData;
import io.kyros.content.combat.magic.TumekenShadow;
import io.kyros.content.combat.range.RangeData;
import io.kyros.content.skills.Minigame;
import io.kyros.content.skills.herblore.PoisonedWeapon;
import io.kyros.model.Graphic;
import io.kyros.model.SoundType;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.EntityReference;
import io.kyros.model.entity.HealthStatus;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;
import org.apache.commons.lang3.RandomUtils;

import java.util.Optional;

/**
 * This class class processes hits queued by the {@link HitDispatcher}.
 */
public abstract class HitExecutor {

    public static HitExecutor getDelayedHit(Player c, Entity defender, Damage damage) {
        if (defender.isNPC()) {
            return new HitExecutorNpc(c, defender, damage);
        } else {
            return new HitExecutorPlayer(c, defender, damage);
        }
    }

    protected final Player attacker;
    protected final Entity defender;
    protected final Damage damage;

    public abstract void onHit();

    public HitExecutor(Player attacker, Entity defender, Damage damage) {
        this.attacker = attacker;
        this.defender = defender;
        this.damage = damage;
    }

    public void hit() {
        if (defender.isDead || defender.getHealth().getCurrentHealth() <= 0) {
            return;
        }

        if (defender.isPlayer() && attacker.isNPC()) {
            if (Minigame.SpecialNPC(attacker.asNPC())) {
                return;
            }
        }

        if (defender.isAutoRetaliate()) {
            defender.attackEntity(attacker);
        }

        onHit();

        if (damage.getSpecial() != null) {
            if (attacker.getCurrentPet().hasPerk("rare_dethrone")) {
                damage.setAmount(damage.getAmount()*2);
            }
            damage.getSpecial().hit(attacker, defender, (damage));
        }

        attacker.lastAttackedEntity = EntityReference.getReference(defender);

        if (defender instanceof Player) {
            Player d = (Player) defender;
            d.lastDefend = EntityReference.getReference(attacker);
            d.lastDefendTime = System.currentTimeMillis();
        }
        if (damage.isSuccess()) {
            if (!defender.getHealth().isNotSusceptibleTo(HealthStatus.POISON)) {
                damage.getEquipment().ifPresent(equipment -> {
                    Optional<PoisonedWeapon.PoisonLevel> poison = Optional.empty();
                    for (int equipmentItem : equipment) {
                        if (equipmentItem == attacker.playerEquipment[Player.playerWeapon] || equipmentItem == attacker.playerEquipment[Player.playerArrows]) {
                            poison = PoisonedWeapon.getPoisonLevel(equipmentItem);
                            if (poison.isPresent()) {
                                break;
                            }
                        }
                    }
                    poison.ifPresent(pl -> {
                        if (RandomUtils.nextInt(0, pl.getPoisonProbability()) == 1) {
                            defender.getHealth().proposeStatus(HealthStatus.POISON, pl.getPoisonDamage(), Optional.of(attacker));
                        }
                    });
                });
            }
        }

        if (damage.getCombatType() != null) {
            if (defender.attackTimer > 3) {
                if (defender.hasBlockAnimation() && defender.getBlockAnimation().getId() != -1) {
                    defender.startAnimation(defender.getBlockAnimation());
                }
            }
            if(damage.getAmount() >= attacker.lastMaxHit && damage.getHitmark().getMax() != -1) {
                damage.setHitMask(HitMask.Companion.get(damage.getHitmark().getMax()));
            }
            switch (damage.getCombatType()) {
                case MELEE:
                    if(attacker.getItems().getWeapon() == 29084 || attacker.getItems().getWeapon() == 33417) {
                        if(damage.getAmount() > 0) {
                            damage.setAmount((int) Math.ceil(damage.getAmount() / 2.0));
                            defender.appendDamage(attacker, damage.getAmount(), damage.getHitmark());
                            defender.appendDamage(attacker, damage.getAmount(), damage.getHitmark());
                            return;
                        }
                    }
                    defender.appendDamage(attacker, damage.getAmount(), damage.getHitmark());
                    break;

                case RANGE:
                    if (attacker.dbowSpec) {
                        attacker.dbowSpec = false;
                    }

                    attacker.rangeEndGFX = RangeData.getRangeEndGFX(attacker, damage.getRangedWeaponType().noArrows());
                    attacker.ignoreDefence = false;
                    attacker.multiAttacking = false;

                    if (attacker.rangeEndGFX > 0) {
                        defender.startGraphic(new Graphic(attacker.rangeEndGFX, attacker.rangeEndGFXHeight ? Graphic.GraphicHeight.LOW : Graphic.GraphicHeight.MIDDLE));
                    }
                    defender.appendDamage(attacker, damage.getAmount(), damage.getHitmark());

                    if (attacker.playerEquipment[Player.playerWeapon] == 28922) {
                        if (defender.isPlayer()) {
                            RangeData.fireProjectilePlayer(defender.asPlayer(), attacker, 50, 70, 2729, 30, 12, 55, 10);
                        } else if (defender.isNPC()) {
                            RangeData.fireProjectileNPCtoPLAYER(defender.asNPC(), attacker, 50, 70, 2729, 30, 12, 55, 10);
                        }
                    } else if (attacker.playerEquipment[Player.playerWeapon] == 28919) {
                        if (defender.isPlayer()) {
                            RangeData.fireProjectilePlayer(defender.asPlayer(), attacker, 50, 70, 2728, 30, 12, 55, 10);
                        } else if (defender.isNPC()) {
                            RangeData.fireProjectileNPCtoPLAYER(defender.asNPC(), attacker, 50, 70, 2728, 30, 12, 55, 10);
                        }
                    }
                    if (attacker.playerEquipment[Player.playerWeapon] == 26269 && attacker.enhancerCrystal) {
                        defender.appendDamage(attacker, (damage.getAmount() > 0 ? (damage.getAmount()/2) : damage.getAmount()), damage.getHitmark());
                    }
                    if (attacker.playerEquipment[Player.playerWeapon] == 33435) {
                        defender.appendDamage(attacker, (damage.getAmount() > 0 ? (damage.getAmount()/2) : damage.getAmount()), damage.getHitmark());
                    }
                    break;

                case MAGE:
                    if (attacker.spellSwap) {
                        attacker.spellSwap = false;
                        attacker.setSidebarInterface(6, 16640);
                        attacker.playerMagicBook = 2;
                        attacker.gfx0(-1);
                    }

                    if (damage.isSuccess()) {
                        SoundData soundData = SoundData.forId(attacker.oldSpellId);
                        if (soundData != null) {
                            attacker.getPA().sendSound(soundData.forIdhit(), SoundType.SOUND);
                        }
                        defender.appendDamage(attacker, damage.getAmount(), damage.getHitmark());
                        if (attacker.oldSpellId > -1) {
                            defender.startGraphic(new Graphic(CombatSpellData.MAGIC_SPELLS[attacker.oldSpellId][5],
                                    CombatSpellData.getEndGfxHeight(attacker) == 100 ? Graphic.GraphicHeight.MIDDLE : Graphic.GraphicHeight.LOW));
                            switch (CombatSpellData.MAGIC_SPELLS[attacker.oldSpellId][0]) {
                                case 12939://smoke spells
                                case 12963:
                                    if (attacker.getItems().isWearingItem(27624)) {
                                        defender.getHealth().proposeStatus(HealthStatus.POISON, 11, Optional.of(attacker));
                                    } else {
                                        defender.getHealth().proposeStatus(HealthStatus.POISON, 10, Optional.of(attacker));
                                    }
                                    break;
                                case 12951:
                                case 12975:
                                    if (attacker.getItems().isWearingItem(27624)) {
                                        defender.getHealth().proposeStatus(HealthStatus.POISON, 22, Optional.of(attacker));
                                    } else {
                                        defender.getHealth().proposeStatus(HealthStatus.POISON, 20, Optional.of(attacker));
                                    }
                                    break;
                                case 12901:
                                case 12919: // blood spells
                                case 12911:
                                case 12929:
                                    if (attacker.getItems().isWearingItem(27624)) {
                                        int heal = Misc.random((int) (damage.getAmount() / 2.75));
                                        attacker.getHealth().increase(heal);
                                        attacker.getPA().refreshSkill(3);
                                    } else {
                                        int heal = Misc.random(damage.getAmount() / 3);
                                        attacker.getHealth().increase(heal);
                                        attacker.getPA().refreshSkill(3);
                                    }
                                    break;
                            }

                            if (attacker.getSpellId() == SanguinestiStaff.COMBAT_SPELL_INDEX && Misc.isLucky(17)) {
                                attacker.getHealth().increase(damage.getAmount() / 2);
                                attacker.startGraphic(new Graphic(1542));
                            }

                            if (attacker.getSpellId() == TumekenShadow.COMBAT_SPELL_INDEX) {
                                if (Misc.isLucky(85) && !defender.isPlayer() && !defender.isDead) {
                                    defender.appendDamage(attacker, Misc.random(1, 50), HitMask.HIT);
                                    defender.startGraphic(new Graphic(2127));
                                }
                            }

                            if (attacker.getSpellId() == 101) {            //Demon X Staff
                                if (Misc.random(0, 5) == 1 && !defender.isPlayer() && !defender.isDead) {
                                    attacker.getHealth().increase(damage.getAmount() / 3);
                                }
                                if (Misc.isLucky(24) && !defender.isPlayer() && !defender.isDead) {
                                    defender.appendDamage(attacker, Misc.random(35, 75), HitMask.HIT);
                                    defender.startGraphic(new Graphic(2200));
                                }
                            }

                            if (attacker.getSpellId() == 104) {            //Demon X Staff
                                if (Misc.random(0, 5) == 1 && !defender.isPlayer() && !defender.isDead) {
                                    attacker.getHealth().increase(damage.getAmount() / 3);
                                }
/*                                if (Misc.isLucky(24) && !defender.isPlayer() && !defender.isDead) {
                                    defender.appendDamage(attacker, Misc.random(250, 500), HitMask.HIT);
                                    defender.startGraphic(new Graphic(2200));
                                }*/
                            }

                            if (attacker.getSpellId() == 102) {            //Staff Of Ambition
                                if (Misc.random(0, 5) == 1 && !defender.isPlayer() && !defender.isDead) {
                                    attacker.getHealth().increase(damage.getAmount() / 3);
                                }
                                if (Misc.isLucky(24) && !defender.isPlayer() && !defender.isDead) {
                                    defender.appendDamage(attacker, Misc.random(75, 150), HitMask.HIT);
                                    defender.startGraphic(new Graphic(2200));
                                }
                            }
                        }

                    } else {
                        defender.startGraphic(new Graphic(85, Graphic.GraphicHeight.MIDDLE));
                    }
                    break;

                default:
                    break;
            }
            /**
             * Damage applied and maybe changed
             */
            if(defender.isNPC())
                BossHealthHud.handleBossHud(attacker, defender.asNPC());
        }

        attacker.multiAttacking = false;
        defender.setUpdateRequired(true);
        attacker.usingMagic = false;
        attacker.oldSpellId = 0;
        if (attacker.bowSpecShot <= 0) {
            attacker.oldNpcIndex = 0;
            attacker.weaponUsedOnAttack = 0;
            attacker.bowSpecShot = 0;
        }
        if (attacker.bowSpecShot >= 2) {
            attacker.bowSpecShot = 0;
        }
        if (attacker.bowSpecShot == 1) {
            attacker.hitDelay = 2;
            attacker.bowSpecShot = 0;
        }
    }

}
