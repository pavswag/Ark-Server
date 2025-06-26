package io.kyros.content.bosses;

import com.google.common.collect.Lists;
import io.kyros.annotate.PostInit;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

import java.util.stream.Collectors;

public class Hayley extends NPC {

    public Hayley(int npcId, Position position) {
        super(npcId, position);
        this.getBehaviour().setRespawn(false);//Don't respawn
        this.getBehaviour().setAggressive(true);//Attack anyone
        this.getCombatDefinition().setAggressive(true);//Attack anyone
        setupAutoAttack(this);
    }

    public static Boundary Area = new Boundary(0,0,0,0); // Use this when doing things like "Multi"

@PostInit
    public static void handleSpawn() {
        NPC hayley = new Hayley(13028, new Position(1814,3602,0));
//        setupAutoAttack(hayley);
    }

    @Override
    public void onDeath() {
        //here we can override drops, and other elements such as for Global bosses.
    }

    @Override
    public void process() {
        //Don't really do anything here as we use AutoAttacks
    }

    @Override
    public int getDeathAnimation() {
        return 10928;
    }

    @Override
    public Animation getBlockAnimation() {
        return new Animation(6375);
    }


    @Override
    public boolean isAutoRetaliate() {
        return true;
    }

    private static void setupAutoAttack(NPC npc) {//Single Target
        npc.setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()//THIS WILL ONLY HIT THE PERSON ATTACKING IT!
                        .setAnimation(new Animation(6376))//Attack Animation
                        .setCombatType(CombatType.MELEE)//Attack Style
                        .setDistanceRequiredForAttack(3)//Distance npc is from the player when attacking
                        .setMaxHit(36)//Max Damage
                        .setAccuracyBonus(npcCombatAttack -> 10.0)//Accuracy boost
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.75)//Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                        .setPoisonDamage(4)//Poison
                        .setAttackDelay(4)//delay between attacks
                        .createNPCAutoAttack()
        ));
    }

    private static void setupAutoAttackMulti(NPC npc) {//Multi Target
        npc.setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(6376))//Attack Animation
                        .setCombatType(CombatType.MELEE)//Attack Style
                        .setMultiAttack(true)
                        .setSelectPlayersForMultiAttack(npcCombatAttack ->
                                npcCombatAttack.getNpc().getInstance().getPlayers().stream().filter(plr -> plr.distance(npcCombatAttack.getVictim().getPosition()) <= 3)//Handle multi combat distance
                                        .collect(Collectors.toList()))
                        .setDistanceRequiredForAttack(3)//Distance npc is from the player when attacking
                        .setMaxHit(36)//Max Damage
                        .setAccuracyBonus(npcCombatAttack -> 10.0)//Accuracy boost
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.75)//Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                        .setPoisonDamage(4)//Poison
                        .setAttackDelay(4)//delay between attacks
                        .createNPCAutoAttack()
        ));
    }

    private static void setupAutoAttackCombo(NPC npc) {
        npc.setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()//Default attack
                        .setSelectPlayersForMultiAttack(npcCombatAttack ->
                                npcCombatAttack.getNpc().getInstance().getPlayers().stream().filter(plr -> plr.distance(npcCombatAttack.getVictim().getPosition()) <= 3)//Handle multi combat distance
                                        .collect(Collectors.toList()))
                        .setMultiAttack(true)
                        .setAnimation(new Animation(6376))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(3)
                        .setMaxHit(52)
                        .setAccuracyBonus(npcCombatAttack -> 10.0)
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.75)
                        .setPoisonDamage(15)
                        .setAttackDelay(3)
                        .createNPCAutoAttack(),

                new NPCAutoAttackBuilder()//chance based attack
                        .setSelectPlayersForMultiAttack(npcCombatAttack ->
                                npcCombatAttack.getNpc().getInstance().getPlayers().stream().filter(plr -> plr.distance(npcCombatAttack.getVictim().getPosition()) <= 3)//Handle multi combat distance
                                        .collect(Collectors.toList()))
                        .setMultiAttack(true)
                        .setSelectAutoAttack(attack -> Misc.trueRand(10) == 0)
                        .setAnimation(new Animation(6376))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(3)
                        .setMaxHit(52)
                        .setAccuracyBonus(npcCombatAttack -> 15.0)
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.75)
                        .setPoisonDamage(15)
                        .setAttackDelay(3)
                        .createNPCAutoAttack()
        ));
    }
}