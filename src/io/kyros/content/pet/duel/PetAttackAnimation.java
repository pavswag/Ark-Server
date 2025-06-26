package io.kyros.content.pet.duel;

public class PetAttackAnimation {
    private int npcId;
    private int attackAnimation;

    public PetAttackAnimation(int npcId, int attackAnimation) {
        this.npcId = npcId;
        this.attackAnimation = attackAnimation;
    }

    public int getNpcId() {
        return npcId;
    }

    public int getAttackAnimation() {
        return attackAnimation;
    }

    @Override
    public String toString() {
        return "NPC ID: " + npcId + ", Attack Animation: " + attackAnimation;
    }
}
