package io.kyros.model.definitions;

import io.kyros.model.Bonus;

public class ItemEquipmentStats {
    public int getBonus(Bonus bonus) {
        return switch (bonus) {
            case ATTACK_STAB -> astab;
            case ATTACK_SLASH -> aslash;
            case ATTACK_CRUSH -> acrush;
            case ATTACK_MAGIC -> amagic;
            case ATTACK_RANGED -> arange;
            case DEFENCE_STAB -> dstab;
            case DEFENCE_SLASH -> dslash;
            case DEFENCE_CRUSH -> dcrush;
            case DEFENCE_MAGIC -> dmagic;
            case DEFENCE_RANGED -> drange;
            case STRENGTH -> str;
            case RANGED_STRENGTH -> rstr;
            case MAGIC_DMG -> mdmg;
            case PRAYER -> prayer;
            default -> throw new IllegalArgumentException(bonus.toString());
        };
    }

    public int getAttackSpeed() {
        return aspeed;
    }

    public int getEquipmentSlot() {
        return slot;
    }

    private final int slot;
    private final int astab;
    private final int aslash;
    private final int acrush;
    private final int amagic;
    private final int arange;
    private final int dstab;
    private final int dslash;
    private final int dcrush;
    private final int dmagic;
    private final int drange;
    private final int str;
    private final int rstr;
    private final int mdmg;
    private final int prayer;
    private final int aspeed;

    public ItemEquipmentStats(final int slot, final int astab, final int aslash, final int acrush, final int amagic, final int arange, final int dstab, final int dslash, final int dcrush, final int dmagic, final int drange, final int str, final int rstr, final int mdmg, final int prayer, final int aspeed) {
        this.slot = slot;
        this.astab = astab;
        this.aslash = aslash;
        this.acrush = acrush;
        this.amagic = amagic;
        this.arange = arange;
        this.dstab = dstab;
        this.dslash = dslash;
        this.dcrush = dcrush;
        this.dmagic = dmagic;
        this.drange = drange;
        this.str = str;
        this.rstr = rstr;
        this.mdmg = mdmg;
        this.prayer = prayer;
        this.aspeed = aspeed;
    }


    public static ItemEquipmentStatsBuilder builder() {
        return new ItemEquipmentStatsBuilder();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ItemEquipmentStats)) return false;
        final ItemEquipmentStats other = (ItemEquipmentStats) o;
        if (this.getSlot() != other.getSlot()) return false;
        if (this.getAstab() != other.getAstab()) return false;
        if (this.getAslash() != other.getAslash()) return false;
        if (this.getAcrush() != other.getAcrush()) return false;
        if (this.getAmagic() != other.getAmagic()) return false;
        if (this.getArange() != other.getArange()) return false;
        if (this.getDstab() != other.getDstab()) return false;
        if (this.getDslash() != other.getDslash()) return false;
        if (this.getDcrush() != other.getDcrush()) return false;
        if (this.getDmagic() != other.getDmagic()) return false;
        if (this.getDrange() != other.getDrange()) return false;
        if (this.getStr() != other.getStr()) return false;
        if (this.getRstr() != other.getRstr()) return false;
        if (this.getMdmg() != other.getMdmg()) return false;
        if (this.getPrayer() != other.getPrayer()) return false;
        if (this.getAspeed() != other.getAspeed()) return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getSlot();
        result = result * PRIME + this.getAstab();
        result = result * PRIME + this.getAslash();
        result = result * PRIME + this.getAcrush();
        result = result * PRIME + this.getAmagic();
        result = result * PRIME + this.getArange();
        result = result * PRIME + this.getDstab();
        result = result * PRIME + this.getDslash();
        result = result * PRIME + this.getDcrush();
        result = result * PRIME + this.getDmagic();
        result = result * PRIME + this.getDrange();
        result = result * PRIME + this.getStr();
        result = result * PRIME + this.getRstr();
        result = result * PRIME + this.getMdmg();
        result = result * PRIME + this.getPrayer();
        result = result * PRIME + this.getAspeed();
        return result;
    }

    @Override
    public String toString() {
        return "ItemEquipmentStats(slot=" + this.getSlot() + ", astab=" + this.getAstab() + ", aslash=" + this.getAslash() + ", acrush=" + this.getAcrush() + ", amagic=" + this.getAmagic() + ", arange=" + this.getArange() + ", dstab=" + this.getDstab() + ", dslash=" + this.getDslash() + ", dcrush=" + this.getDcrush() + ", dmagic=" + this.getDmagic() + ", drange=" + this.getDrange() + ", str=" + this.getStr() + ", rstr=" + this.getRstr() + ", mdmg=" + this.getMdmg() + ", prayer=" + this.getPrayer() + ", aspeed=" + this.getAspeed() + ")";
    }

    public int getSlot() {
        return this.slot;
    }

    public int getAstab() {
        return this.astab;
    }

    public int getAslash() {
        return this.aslash;
    }

    public int getAcrush() {
        return this.acrush;
    }

    public int getAmagic() {
        return this.amagic;
    }

    public int getArange() {
        return this.arange;
    }

    public int getDstab() {
        return this.dstab;
    }

    public int getDslash() {
        return this.dslash;
    }

    public int getDcrush() {
        return this.dcrush;
    }

    public int getDmagic() {
        return this.dmagic;
    }

    public int getDrange() {
        return this.drange;
    }

    public int getStr() {
        return this.str;
    }

    public int getRstr() {
        return this.rstr;
    }

    public int getMdmg() {
        return this.mdmg;
    }

    public int getPrayer() {
        return this.prayer;
    }

    public int getAspeed() {
        return this.aspeed;
    }
}
