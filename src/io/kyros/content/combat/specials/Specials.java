package io.kyros.content.combat.specials;

import io.kyros.content.combat.specials.impl.*;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * Contains an enumeration of {@link Special} objects.
 *
 * @author Jason MacKeigan
 * @date Apr 4, 2015, 2015, 11:47:56 PM
 */
public enum Specials {
	GRANITE_HAMMER(new GraniteHammerSpecialAttack()),
	GRANITE_HANDLE(new GraniteMaulHandleSpecialAttack()),
	GRANITE_MAUL(new GraniteMaulSpecialAttack()),
	TOXIC_BLOWPIPE(new ToxicBlowpipe()),
	MAGIC_BOW(new MagicShortBow()),
	MAGIC_BOW_I(new MagicShortBow(5.0)),
	DRAGON_DAGGER(new DragonDagger()),
	BALLISTA(new Ballista()),
	DRAGON_LONGSWORD(new DragonLongsword()),
	SOUL_REAPER(new SoulReaper()),
	ARCLIGHT(new ArcLight()),
	ABYSSAL_BLUDGEON(new AbyssalBludgeon()),
	ARMADYL_CROSSBOW(new ArmadylCrossbow()),
	DRAGON_CROSSBOW(new DragonCrossbow()),
	ABYSSAL_WHIP(new AbyssalWhip()),
	ABYSSAL_DAGGER(new AbyssalDagger()),
	ANCIENT_MACE(new AncientMace()),
	ARMADYL_GODSWORD(new ArmadylGodsword()),
	BANDOS_GODSWORD(new BandosGodsword()),
	BARRELCHEST_ANCHOR(new BarrelchestAnchor()),
	CRYSTAL_HALBERD(new CrystalHalberd()),
	DARK_BOW(new DarkBow()),
	DRAGON_BATTLEAXE(new DragonBattleaxe()),
	DRAGON_CLAWS(new DragonClaws()),
	DRAGON_HALBERD(new DragonHalberd()),
	DRAGON_MACE(new DragonMace()),
	DRAGON_SCIMITAR(new DragonScimitar()),
	DRAGON_SWORD(new DragonSword()),
	DRAGON_SPEAR(new DragonSpear()),
	ZAMORAKIAN_HASTA(new ZamorakianHasta()),
	SARADOMIN_GODSWORD(new SaradominGodsword()),
	SARADOMIN_SWORD(new SaradominSword()),
	ANCIENT_GODSWORD(new AncientGodsword()),
	SARADOMIN_SWORD_BLESSED(new SaradominSwordBlessed()),
	STAFF_OF_THE_DEAD(new StaffOfTheDead()),
	STATIUS_WARHAMMER(new StatiusWarhammer()),
	TENTACLE_WHIP(new TentacleWhip()),
	VESTA_LONGSWORD(new VestaLongsword()),
	VESTA_SPEAR(new VestaSpear()),
	DRAGON_WARHAMMER(new DragonWarhammer()),
	ZAMORAK_GODSWORD(new ZamorakGodsword()),
	DRAGON_THROWNAXE(new DragonThrownaxe()),
	DRAGON_AXE(new DragonAxe()),
	DRAGON_PICKAXE(new DragonPickaxe()),
	DRAGON_HARPOON(new DragonHarpoon()),
	ELDRITCH_NIGHTMARE_STAFF(new EldritchNightmareStaff()),
	VOLATILE_NIGHTMARE_STAFF(new VolatileNightmareStaff()),
	DRAGON_KNIFE(new DragonKnife()),
	ZARYTE_CROSSBOW(new ZaryteCrossbow()),

	ASCENSION_CROSSBOW(new AscensionCrossbow()),
	SEREN_GODBOW(new SerenGodbow()),
	AXE_OF_ARAPHEL(new AxeOfAraphel()),
	CHRISTMAS_TWISTED_BOW(new ChristmasTbow()),
	OSMUMTEN_FANG(new OsmumtenFang()),
	DEMON_X(new DemonxSpear()),
	DEMON_X_BOW(new DemonXBow()),
	WEBWEAVER(new WebWeaver()),
	VOIDWAKER(new Voidwaker()),
	ACCURSED_SCEPTRE(new AccursedSceptre()),
	NOXIOUS_BLADE(new NoxiousBlade())

	;

	/**
	 * A {@link Set} of elements from the {@link Specials} enumeration.
	 */
	public static final Set<Specials> SPECIALS=EnumSet.allOf(Specials.class);
	private final Special special;

	Specials(Special special) {
		this.special = special;
	}

	/**
	 * The {@link Special} object that is associated with the weapon id specified in the parameter
	 *
	 * @param weaponId the weaponId that is associated with the special effect
	 * @return a {@link Special} object with the same weapon id as the parameter
	 */
	public static Special forWeaponId(int weaponId) {
		Optional<Specials> specials = SPECIALS.stream().filter(s -> Arrays.stream(s.special.getWeapon()).anyMatch(w -> w == weaponId)).findFirst();
		return specials.map(value -> value.special).orElse(null);
	}

	public Special getSpecial() {

		return special;
	}
}
