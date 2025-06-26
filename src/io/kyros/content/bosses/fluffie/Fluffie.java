package io.kyros.content.bosses.fluffie;

import io.kyros.Server;
import io.kyros.content.bosses.hydra.CombatProjectile;
import io.kyros.content.combat.HitMask;
import io.kyros.model.CombatType;
import io.kyros.model.Graphic;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.definitions.NpcStats;
import io.kyros.model.entity.EntityReference;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.ClientGameTimer;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Fluffie {

    public static boolean spawned = false;
    public static double chance = 0;
    public static final Boundary location = new Boundary(3136, 10048, 3263, 10239);
    public static NPC fluffie = null;

    public static void handleSpawn() {
        if (!spawned) {
            int rng = Misc.random(0,11);
            int maxhit = 40;
            spawned = true;
            chance = 0;
            PlayerHandler.executeGlobalMessage("@red@[Fluffie]@blu@ has spawned in the Revenant caves!!");
            switch (rng) {
                case 0:
                    fluffie = NPCSpawning.spawn(1257, 3176, 10193, 0, 1, maxhit, false, NpcStats.forId(1257));
                    if (fluffie != null) {
                        fluffie.getBehaviour().setRespawn(false);
                    }
                    for (Player player : Server.getPlayers().toPlayerArray()) {
                        if (Boundary.isIn(player, location)) {
                            player.sendMessage("@red@[Fluffie]@blu@ is located near the cyclops/demon's.");
                        }
                    }
                    break;
                case 1:
                    fluffie = NPCSpawning.spawn(1257, 3170,10156, 0, 1, maxhit, false, NpcStats.forId(1257));
                    if (fluffie != null) {
                        fluffie.getBehaviour().setRespawn(false);
                    }
                    if (fluffie != null) {
                        fluffie.getBehaviour().setRespawn(false);
                    }
                    for (Player player : Server.getPlayers().toPlayerArray()) {
                        if (Boundary.isIn(player, location)) {
                            player.sendMessage("@red@[Fluffie]@blu@ is located near the Pyrefiend's to the west.");
                        }
                    }
                    break;
                case 2:
                    fluffie = NPCSpawning.spawn(1257, 3207,10165, 0, 1, maxhit, false, NpcStats.forId(1257));
                    if (fluffie != null) {
                        fluffie.getBehaviour().setRespawn(false);
                    }
                    for (Player player : Server.getPlayers().toPlayerArray()) {
                        if (Boundary.isIn(player, location)) {
                            player.sendMessage("@red@[Fluffie]@blu@ is located near the Dark Beats.");
                        }
                    }
                    break;
                case 3:
                    fluffie = NPCSpawning.spawn(1257, 3216,10195, 0, 1, maxhit, false, NpcStats.forId(1257));
                    if (fluffie != null) {
                        fluffie.getBehaviour().setRespawn(false);
                    }
                    for (Player player : Server.getPlayers().toPlayerArray()) {
                        if (Boundary.isIn(player, location)) {
                            player.sendMessage("@red@[Fluffie]@blu@ is located near the imp's to the north.");
                        }
                    }
                    break;
                case 4:
                    fluffie = NPCSpawning.spawn(1257, 3240,10203, 0, 1, maxhit, false, NpcStats.forId(1257));
                    if (fluffie != null) {
                        fluffie.getBehaviour().setRespawn(false);
                    }
                    for (Player player : Server.getPlayers().toPlayerArray()) {
                        if (Boundary.isIn(player, location)) {
                            player.sendMessage("@red@[Fluffie]@blu@ is located near the dragon's.");
                        }
                    }
                    break;
                case 5:
                    fluffie = NPCSpawning.spawn(1257, 3243,10174, 0, 1, maxhit, false, NpcStats.forId(1257));
                    if (fluffie != null) {
                        fluffie.getBehaviour().setRespawn(false);
                    }
                    for (Player player : Server.getPlayers().toPlayerArray()) {
                        if (Boundary.isIn(player, location)) {
                            player.sendMessage("@red@[Fluffie]@blu@ is located near the hellhound's.");
                        }
                    }
                    break;
                case 6:
                    fluffie = NPCSpawning.spawn(1257, 3225,10133, 0, 1, maxhit, false, NpcStats.forId(1257));
                    if (fluffie != null) {
                        fluffie.getBehaviour().setRespawn(false);
                    }
                    for (Player player : Server.getPlayers().toPlayerArray()) {
                        if (Boundary.isIn(player, location)) {
                            player.sendMessage("@red@[Fluffie]@blu@ is located near the middle ork's.");
                        }
                    }
                    break;
                case 7:
                    fluffie = NPCSpawning.spawn(1257, 3225,10070, 0, 1, maxhit, false, NpcStats.forId(1257));
                    if (fluffie != null) {
                        fluffie.getBehaviour().setRespawn(false);
                    }
                    for (Player player : Server.getPlayers().toPlayerArray()) {
                        if (Boundary.isIn(player, location)) {
                            player.sendMessage("@red@[Fluffie]@blu@ is located near the goblin's.");
                        }
                    }
                    break;
                case 8:
                    fluffie = NPCSpawning.spawn(1257, 3214,10095, 0, 1, maxhit, false, NpcStats.forId(1257));
                    if (fluffie != null) {
                        fluffie.getBehaviour().setRespawn(false);
                    }
                    for (Player player : Server.getPlayers().toPlayerArray()) {
                        if (Boundary.isIn(player, location)) {
                            player.sendMessage("@red@[Fluffie]@blu@ is located near the south ork's.");
                        }
                    }
                    break;
                case 9:
                    fluffie = NPCSpawning.spawn(1257, 3187,10119, 0, 1, maxhit, false, NpcStats.forId(1257));
                    if (fluffie != null) {
                        fluffie.getBehaviour().setRespawn(false);
                    }
                    for (Player player : Server.getPlayers().toPlayerArray()) {
                        if (Boundary.isIn(player, location)) {
                            player.sendMessage("@red@[Fluffie]@blu@ is located near the demon's & ork's.");
                        }
                    }
                    break;
                case 10:
                    fluffie = NPCSpawning.spawn(1257, 3165,10115, 0, 1, maxhit, false, NpcStats.forId(1257));
                    if (fluffie != null) {
                        fluffie.getBehaviour().setRespawn(false);
                    }
                    for (Player player : Server.getPlayers().toPlayerArray()) {
                        if (Boundary.isIn(player, location)) {
                            player.sendMessage("@red@[Fluffie]@blu@ is located near the demon's.");
                        }
                    }
                    break;
                case 11:
                    fluffie = NPCSpawning.spawn(1257, 3199,10071, 0, 1, maxhit, false, NpcStats.forId(1257));
                    if (fluffie != null) {
                        fluffie.getBehaviour().setRespawn(false);
                    }
                    for (Player player : Server.getPlayers().toPlayerArray()) {
                        if (Boundary.isIn(player, location)) {
                            player.sendMessage("@red@[Fluffie]@blu@ is located near the imp's to the south.");
                        }
                    }
                    break;
            }
        }
    }

    public static void attack() {
/*        if (fluffie == null || fluffie.isDead) {
            return;
        }*/
        updateTargets();

        if (targets.isEmpty()) {
            return;
        }

        int rng = Misc.random(0, 100);
        if (rng >= 0 && rng <= 59){
            //melee
            fluffie.startAnimation(4474);
            fluffie.setAttackType(CombatType.MELEE);
            meleeAttack();
        } else if (rng >= 60 && rng <= 80) {
            //range
            fluffie.startAnimation(4474);
            fluffie.setAttackType(CombatType.RANGE);
            fluffie.projectileId = -1;
            fluffie.endGfx = -1;
            fluffie.hitDelayTimer = 4;
            fluffie.attackTimer = 10;
            ShadowFire();
        } else if (rng >= 81 && rng <= 100) {
            //magic
            fluffie.startAnimation(4474);
            fluffie.setAttackType(CombatType.MAGE);
            fluffie.projectileId = 1711;
            fluffie.endGfx = -1;
            fluffie.hitDelayTimer = 4;
            fluffie.attackTimer = 10;
            fluffie.maxHit = 5;
            IceBarrage();
        }
    }

    public static List<Player> targets = new ArrayList<>();

    public static void updateTargets() {
        if (fluffie == null || fluffie.isDead) {
            return;
        }
        if (!targets.isEmpty()) {
            targets.clear();
        }

        targets = Server.getPlayers().stream().filter(plr -> !plr.isDead && plr.getPosition().getAbsDistance(fluffie.getPosition()) <= 25).collect(Collectors.toList());
    }

    public static void IceBarrage() {
/*        if (fluffie == null || fluffie.isDead) {
            return;
        }*/
        if (targets.isEmpty()) {
            return;
        }

        for (Player possibleTargets : targets) {
            possibleTargets.gfx0(369);
            int dam = Misc.random(45, 80);
            if (possibleTargets.protectingMagic())
                dam = (dam/2);

            possibleTargets.appendDamage(dam, (dam > 0 ? HitMask.HIT : HitMask.MISS));
            if (possibleTargets.isFreezable()) {
                int delay = 15;
                possibleTargets.frozenBy = EntityReference.getReference(fluffie);
                possibleTargets.freezeDelay = delay;
                possibleTargets.freezeTimer = delay;
                possibleTargets.resetWalkingQueue();
                possibleTargets.sendMessage("You have been frozen.");
                possibleTargets.getPA().sendGameTimer(ClientGameTimer.FREEZE, TimeUnit.MILLISECONDS, 600 * delay);
            }
        }
    }

    public static void meleeAttack() {
        for (Player target : targets) {
            int dmg = Misc.random(40,65);
            if (target.protectingMelee()) {
                dmg = (dmg/2);
            }
            target.appendDamage(dmg, (dmg > 0 ? HitMask.HIT : HitMask.MISS));
        }
    }

    public static void ShadowFire() {
        updateTargets();
        if (fluffie.isDead) {
            return;
        }
        if (targets.isEmpty()) {
            return;
        }
        sendProjectile(65);
    }
    private static final CombatProjectile SHADOW_RANGE_PROJECTILE = new CombatProjectile(2012, 50, 25, 4, 50, 0, 50);

    public static void sendProjectile(int maxDamage) {
        for (Player target : targets) {
            target.startGraphic(new Graphic(382, Graphic.GraphicHeight.HIGH));
            CombatProjectile projectile = SHADOW_RANGE_PROJECTILE;
            int size = (int) Math.ceil((double) fluffie.getSize() / 2.0);
            int centerX = fluffie.getX() + size;
            int centerY = fluffie.getY() + size;
            int offsetX = (centerY - target.getY()) * -1;
            int offsetY = (centerX - target.getX()) * -1;
            target.getPA().createPlayersProjectile(centerX, centerY, offsetX, offsetY, projectile.getAngle(), projectile.getSpeed(), projectile.getGfx(),
                    projectile.getStartHeight(), projectile.getEndHeight(), -1, 65, projectile.getDelay());
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if(container.getTotalTicks() == 3) {
                        int damage = Misc.random(40, maxDamage);

                        if(target.protectingRange())
                            damage = (damage / 2);


                        target.appendDamage(damage, (damage > 0 ? HitMask.HIT : HitMask.MISS));
                        container.stop();
                    }
                }
            }, 1);
        }
    }
}
