package io.kyros.content.bosses.nex;

import io.kyros.Server;
import io.kyros.content.bosses.hydra.CombatProjectile;
import io.kyros.content.bosses.nex.attacks.*;
import io.kyros.content.combat.HitMask;
import io.kyros.model.CombatType;
import io.kyros.model.Graphic;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author ArkCane#1489 @ ArkCane.net
 **/

public class NexNPC {

    public static final Position SPAWN_POSITION = new Position(2924, 5202);
    public static final Boundary BOUNDARY = new Boundary(2909, 5187, 2943, 5220);
    static final int MELEE_ANIM = 9181;
    static final int MAGE_ANIM = 9179;
    static final int RANGE_ANIM = 9180;
    static final int SIPHON_ANIM = 9183;
    private static int attackSpeed = 4;
    private static boolean canAttack;
    private boolean healthIncreased;

    final int CRUOR = 11285;
    final int FUMUS = 11283;
    final int UMBRA = 11284;
    final int GLACIES = 11286;
    public boolean fumusStarted = false;
    public boolean cruorStarted = false;
    public boolean umbraStarted = false;
    public boolean glaciesStarted = false;
    NPC glaciesNPC;
    NPC cruorNPC;
    NPC umbraNPC;
    NPC fumusNPC;
    Position fumusSpawnPoint = new Position(2915, 5213);
    Position umbraSpawnPoint = new Position(2935, 5213);
    Position cruorSpawnPoint = new Position(2915, 5193);
    Position glaciesSpawnPoint = new Position(2935, 5193);
    int spawnMageAnimation = 9189;
    public static List<Player> targets;
    static Phase currentPhase = Phase.SMOKE;
    static Player currenTarget;
    static int specialTimer = 75;
    static boolean canBeAttacked;
    boolean started = false;
    public static boolean siphoning = false;


    static List<NPC> reavers = new ArrayList<>();

    /*
     Gets the targets in the area
      */
    public static void updateTargets() {
        targets = Server.getPlayers().stream().filter(plr -> !plr.isDead && Boundary.isIn(plr, Boundary.NEX)).collect(Collectors.toList());
    }


    /*
    Method is called after every hit nex takes
     */
    public static void postHitDefend(NPC npc) {
        switch (currentPhase) {
            case SMOKE:
                if (npc.getHealth().getCurrentHealth() <= (npc.getHealth().getMaximumHealth() * 0.80)) {
                    startNewPhase(npc, Phase.SHADOW);
                }
            case SHADOW:
                if (npc.getHealth().getCurrentHealth() <= (npc.getHealth().getMaximumHealth() * 0.60)) {
                    startNewPhase(npc, Phase.BLOOD);
                }
            case BLOOD:
                if (npc.getHealth().getCurrentHealth() <= (npc.getHealth().getMaximumHealth() * 0.40)) {
                    startNewPhase(npc, Phase.ICE);
                }
            case ICE:
                if (npc.getHealth().getCurrentHealth() <= (npc.getHealth().getMaximumHealth() * 0.20)) {
                    startNewPhase(npc, Phase.ZAROS);
                }
                break;
        }
    }
    /*
    Nex is starting a new phase
     */
    public static void startNewPhase(NPC npc, Phase newPhase) {
        currentPhase = newPhase;
        canBeAttacked = true;
        canAttack = true;
//        setAttacks();
        switch (newPhase) {
            case SMOKE:
                npc.forceChat("Fill my soul with smoke!");
                break;
            case SHADOW:
                npc.forceChat("Darken my shadow!");
                npc.getHealth().setCurrentHealth((int) (npc.getHealth().getMaximumHealth() * 0.80));
                break;
            case BLOOD:
                npc.forceChat("Flood my lungs with blood!");
                npc.getHealth().setCurrentHealth((int) (npc.getHealth().getMaximumHealth() * 0.60));
                break;
            case ICE:
                npc.forceChat("Infuse me with the power of ice!");
                npc.getHealth().setCurrentHealth((int) (npc.getHealth().getMaximumHealth() * 0.40));
                break;
            case ZAROS:
                npc.forceChat("NOW, THE POWER OF ZAROS!");
                npc.getHealth().setCurrentHealth((int) (npc.getHealth().getMaximumHealth() * 0.20));
                npc.appendHeal(500, HitMask.NPC_HEAL);
                break;
        }
    }
    private static void meleeAttack(NPC npc, Player target) {
        npc.startAnimation(MELEE_ANIM);
        int dmg = Misc.random(0,15);
        if (target.protectingMelee()) {
            dmg = (dmg/2);
        }
        if (target.usingInfAgro) {
            target.attackEntity(npc);
        }
        target.appendDamage(dmg, (dmg > 0 ? HitMask.HIT : HitMask.BLOCK));

    }

    public static void sendProjectile(NPC npc, Player target, CombatProjectile projectile, CombatType combatType, int maxDamage) {
        int size = (int) Math.ceil((double) npc.getSize() / 2.0);
        int centerX = npc.getX() + size;
        int centerY = npc.getY() + size;
        int offsetX = (centerY - target.getY()) * -1;
        int offsetY = (centerX - target.getX()) * -1;
        currenTarget.getPA().createPlayersProjectile(centerX, centerY, offsetX, offsetY, projectile.getAngle(), projectile.getSpeed(), projectile.getGfx(),
                projectile.getStartHeight(), projectile.getEndHeight(), -1, 65, projectile.getDelay());
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if(container.getTotalTicks() == 3) {
                    int damage = Misc.random(maxDamage);
                    if(combatType.equals(CombatType.MAGE) && target.protectingMagic())
                        damage = (damage / 2);
                    else if(combatType.equals(CombatType.RANGE) && target.protectingRange())
                        damage = (damage / 2);
                    else if(combatType.equals(CombatType.MELEE) && target.protectingMelee())
                        damage = (damage / 2);

                    target.appendDamage(damage, (damage > 0 ? HitMask.HIT : HitMask.MISS));
                    container.stop();
                }
            }
        }, 1);
    }

    private static final CombatProjectile SMOKE_RUSH_PROJECTILE = new CombatProjectile(384, 50, 25, 4, 50, 0, 50);
    private static final CombatProjectile SHADOW_RANGE_PROJECTILE = new CombatProjectile(2012, 50, 25, 4, 50, 0, 50);
    private static final CombatProjectile BASIC_MAGIC_PROJECTILE = new CombatProjectile(2004, 50, 25, 4, 50, 0, 50);

    private static void attack(NPC npc) {
        attackSpeed = 4;
        updateTargets();
        if (targets == null || targets.isEmpty()) {
            return;
        }

        currenTarget = getRandomTarget();
        if (currenTarget == null) {
            return;
        }
        npc.facePlayer(currenTarget.getIndex());
        switch (currentPhase) {
            case SMOKE:
                if (currenTarget.getPosition().getAbsDistance(npc.getPosition()) <= 2 && Misc.random(0, 1) == 0) {
                    meleeAttack(npc, currenTarget);
                    npc.startAnimation(MELEE_ANIM);
                } else if (Misc.random(0, 10) == 0 && specialTimer <= 0) {
                    specialTimer = 75;
                    npc.forceChat("Let the virus flow through you!");
                    npc.startAnimation(9188);
                    new ChokeAttack(getRandomTarget());
                } else {
                    for (Player player : targets) {
                        npc.startAnimation(MAGE_ANIM);
                        sendProjectile(npc, player, SMOKE_RUSH_PROJECTILE, CombatType.MAGE, 33);
                        player.startGraphic(new Graphic(387, Graphic.GraphicHeight.HIGH));
                        if (player.usingInfAgro) {
                            player.attackEntity(npc);
                        }
                    }
                }
                break;
            case SHADOW:
                if (currenTarget.getPosition().getAbsDistance(npc.getPosition()) <= 2 && Misc.random(0, 1) == 0) {
                    meleeAttack(npc, currenTarget);
                    npc.startAnimation(MELEE_ANIM);
                } else if (Misc.random(0, 10) == 0 && specialTimer <= 0) {
                    specialTimer = 75;
                    npc.forceChat("Fear the shadow!");
                    npc.startAnimation(9188);
                    new ShadowSmash(targets);
                } else if (currenTarget.getPosition().getAbsDistance(npc.getPosition()) > 2 && Misc.random(2) == 0) {
                    npc.startAnimation(9181);
                    meleeAttack(npc, currenTarget);
                } else {
                    for (Player player : targets) {
                        npc.forceChat("Shadow Fire!");
                        player.startGraphic(new Graphic(382, Graphic.GraphicHeight.HIGH));
                        sendProjectile(npc, player, SHADOW_RANGE_PROJECTILE, CombatType.RANGE, 35);
                        npc.startAnimation(RANGE_ANIM);
                        if (player.usingInfAgro) {
                            player.attackEntity(npc);
                        }
                    }
                }
                break;
            case BLOOD:
                if (Misc.random(0,10) == 0 && specialTimer <= 0) {
                    new BloodBarrage(npc, getRandomTarget(), targets);
                    npc.startAnimation(MAGE_ANIM);
                    specialTimer = 75;
                } else if (Misc.random(0, 10) == 0 && specialTimer <= 0) {
                    specialTimer = 75;
                    new BloodSacrifice(npc, getRandomTarget());
                    npc.forceChat("I demand a blood sacrifice!");
                } else if (Misc.random(0, 10) == 0 && specialTimer <= 0) {
                    specialTimer = 75;
                    npc.forceChat("A siphon will solve this!");
                    nexSiphon(npc);
                    npc.startAnimation(SIPHON_ANIM);
                } else if (currenTarget.getPosition().getAbsDistance(npc.getPosition()) <= 2) {
                    meleeAttack(npc, currenTarget);
                    npc.startAnimation(MELEE_ANIM);
                } else if (currenTarget.getPosition().getAbsDistance(npc.getPosition()) > 2 && Misc.random(2) == 0) {
                    npc.startAnimation(9181);
                    meleeAttack(npc,currenTarget);}
                break;
            case ICE:
                if (Misc.random(2) == 0 && specialTimer <= 0) {
                    npc.forceChat("Die now, in a prison of ice!");
                    updateTargets();
                    System.out.println("IcePrison");
                    npc.startAnimation(MAGE_ANIM);
                    specialTimer = 75;
                    for (Player target : targets) {
                        new IceBarrage(target, targets);
                        npc.startAnimation(MAGE_ANIM);
                        if (target.usingInfAgro) {
                            target.attackEntity(npc);
                        }
                    }
                } else if (Misc.random(0, 10) == 0 && specialTimer <= 0) {
                    npc.forceChat("Contain this!");
                    updateTargets();
                    for (Player target : targets) {
                        new IceBarrage(target, targets);
                        npc.startAnimation(MAGE_ANIM);
                        if (target.usingInfAgro) {
                            target.attackEntity(npc);
                        }
                    }
                    specialTimer = 75;
                } else if (Misc.random(0, 10) == 0 && specialTimer <= 0) {
                    npc.forceChat("Die now, in a prison of ice!");
                    updateTargets();
                    npc.startAnimation(MAGE_ANIM);
                    specialTimer = 75;
                    for (Player target : targets) {
                        new IceBarrage(target, targets);
                        npc.startAnimation(MAGE_ANIM);
                        if (target.usingInfAgro) {
                            target.attackEntity(npc);
                        }
                    }
                }
                break;
            case ZAROS:
                if (currenTarget.getPosition().getAbsDistance(npc.getPosition()) < 2 && Misc.random(2) == 0) {
                    meleeAttack(npc, currenTarget);
                    npc.startAnimation(9181);
                } else if (currenTarget.getPosition().getAbsDistance(npc.getPosition()) > 2 && Misc.random(2) == 0) {
                    npc.startAnimation(9181);
                    meleeAttack(npc, currenTarget);
//                    this.startAnimation(9181); // 9175 = walk / 9176 = speed walk / 9178 = Fly?
                } else {
                    for (Player player : targets) {
                        sendProjectile(npc, player, BASIC_MAGIC_PROJECTILE, CombatType.MAGE, 40);
                        npc.startAnimation(MAGE_ANIM);
                        if (player.usingInfAgro) {
                            player.attackEntity(npc);
                        }
                    }
                }
                break;
        }
    }
    /*
    Gets a random target
     */
    static Player getRandomTarget() {
        updateTargets();
        if (Boundary.getPlayersInBoundary(Boundary.NEX) <= 0) {
            return null;
        }
        if (Misc.random(targets.size() - 1) < 0) {
            return null;
        }
        return Misc.random(Server.getPlayers().stream().filter(plr -> !plr.isDead && Boundary.isIn(plr, Boundary.NEX)).collect(Collectors.toList()));
    }

    private static void nexSiphon(NPC npc) {
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                npc.startAnimation(SIPHON_ANIM);
                siphoning = true;
                if(container.getTotalTicks() == 8) {
                    npc.startAnimation(-1);
                    siphoning = false;
                }
            }
        }, 1);
    }
    
    public static void process(NPC npc) {
        if (npc.isDead() || npc.isDeadOrDying() || npc.isDead) {
            return;
        }
        postHitDefend(npc);

        if (attackSpeed > 0) {
            attackSpeed--;
        }

        if (attackSpeed <= 0) {
            attack(npc);
        }

        specialTimer--;
    }

    enum Phase {
        SMOKE,
        SHADOW,
        BLOOD,
        ICE,
        ZAROS
    }
}
