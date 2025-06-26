package io.kyros.model.definitions;

public class ItemEquipmentStatsBuilder {

    private int slot;

    private int astab;

    private int aslash;

    private int acrush;

    private int amagic;

    private int arange;

    private int dstab;

    private int dslash;

    private int dcrush;

    private int dmagic;

    private int drange;

    private int str;

    private int rstr;

    private int mdmg;

    private int prayer;

    private int aspeed;

    ItemEquipmentStatsBuilder() {
    }

    public ItemEquipmentStatsBuilder slot(final int slot) {
        this.slot = slot;
        return this;
    }

    public ItemEquipmentStatsBuilder astab(final int astab) {
        this.astab = astab;
        return this;
    }

    public ItemEquipmentStatsBuilder aslash(final int aslash) {
        this.aslash = aslash;
        return this;
    }

    public ItemEquipmentStatsBuilder acrush(final int acrush) {
        this.acrush = acrush;
        return this;
    }

    public ItemEquipmentStatsBuilder amagic(final int amagic) {
        this.amagic = amagic;
        return this;
    }

    public ItemEquipmentStatsBuilder arange(final int arange) {
        this.arange = arange;
        return this;
    }

    public ItemEquipmentStatsBuilder dstab(final int dstab) {
        this.dstab = dstab;
        return this;
    }

    public ItemEquipmentStatsBuilder dslash(final int dslash) {
        this.dslash = dslash;
        return this;
    }

    public ItemEquipmentStatsBuilder dcrush(final int dcrush) {
        this.dcrush = dcrush;
        return this;
    }

    public ItemEquipmentStatsBuilder dmagic(final int dmagic) {
        this.dmagic = dmagic;
        return this;
    }

    public ItemEquipmentStatsBuilder drange(final int drange) {
        this.drange = drange;
        return this;
    }

    public ItemEquipmentStatsBuilder str(final int str) {
        this.str = str;
        return this;
    }

    public ItemEquipmentStatsBuilder rstr(final int rstr) {
        this.rstr = rstr;
        return this;
    }

    public ItemEquipmentStatsBuilder mdmg(final int mdmg) {
        this.mdmg = mdmg;
        return this;
    }

    public ItemEquipmentStatsBuilder prayer(final int prayer) {
        this.prayer = prayer;
        return this;
    }

    public ItemEquipmentStatsBuilder aspeed(final int aspeed) {
        this.aspeed = aspeed;
        return this;
    }

    public ItemEquipmentStats build() {
        return new ItemEquipmentStats(this.slot, this.astab, this.aslash, this.acrush, this.amagic, this.arange, this.dstab, this.dslash, this.dcrush, this.dmagic, this.drange, this.str, this.rstr, this.mdmg, this.prayer, this.aspeed);
    }

    @Override
    public String toString() {
        return "ItemEquipmentStatsBuilder(slot=" + this.slot + ", astab=" + this.astab + ", aslash=" + this.aslash + ", acrush=" + this.acrush + ", amagic=" + this.amagic + ", arange=" + this.arange + ", dstab=" + this.dstab + ", dslash=" + this.dslash + ", dcrush=" + this.dcrush + ", dmagic=" + this.dmagic + ", drange=" + this.drange + ", str=" + this.str + ", rstr=" + this.rstr + ", mdmg=" + this.mdmg + ", prayer=" + this.prayer + ", aspeed=" + this.aspeed + ")";
    }
}
