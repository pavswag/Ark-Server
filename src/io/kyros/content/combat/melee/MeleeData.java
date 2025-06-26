package io.kyros.content.combat.melee;

import io.kyros.content.combat.magic.CombatSpellData;
import io.kyros.content.combat.weapon.AttackStyle;
import io.kyros.model.CombatType;
import io.kyros.model.Items;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.ItemAssistant;

import static io.kyros.content.combat.weapon.CombatStyle.SLASH;

public class MeleeData {

	public static boolean usingSytheOfVitur(Player player) {
		if (player.attacking.getCombatType() == CombatType.MELEE && player.getItems().isWearingItem(25739)) {
			return true;
		} else if (player.attacking.getCombatType() == CombatType.MELEE && player.getItems().isWearingItem(25736)) {
			return true;
		} else if (player.attacking.getCombatType() == CombatType.MELEE && player.getItems().isWearingItem(33161)) {
			return true;
		} else if (player.attacking.getCombatType() == CombatType.MELEE && player.getItems().isWearingItem(33148)) {
			return true;
		} else if (player.attacking.getCombatType() == CombatType.MELEE && player.getItems().isWearingItem(33184)) {
			return true;
		} else if (player.attacking.getCombatType() == CombatType.MELEE && player.getItems().isWearingItem(33203)) {
			return true;
		} else if (player.attacking.getCombatType() == CombatType.MELEE && player.getItems().isWearingItem(33431)) {
			return true;
		} else if (player.attacking.getCombatType() == CombatType.MELEE && player.getItems().isWearingItem(27610)) {
			return true;
		} else if (player.attacking.getCombatType() == CombatType.MELEE && player.getItems().isWearingItem(33329)) {
			return true;
		}
		return player.attacking.getCombatType() == CombatType.MELEE && player.getItems().isWearingItem(Items.SCYTHE_OF_VITUR);
	}

	public static boolean usingHally(Player c) {
		switch (c.getEquipmentToShow(Player.playerWeapon)) {
			case 3190:
			case 3192:
			case 3194:
			case 3196:
			case 3198:
			case 2054:
			case 3202:
			case 3204:
			case 25979:
			case 27291:
			case 25981:
			case 27282:
			case 13092:
			case 29796:
				return true;

			default:
				return false;
		}
	}

	public static void setWeaponAnimations(Player c) {
		c.playerStandIndex = 0x328;//808
		c.playerTurnIndex = 0x337;//823
		c.playerWalkIndex = 0x333;//819
		c.playerTurn180Index = 0x334;//820
		c.playerTurn90CWIndex = 0x335;//821
		c.playerTurn90CCWIndex = 0x336;//822
		c.playerRunIndex = 0x338;//824

		int arrowSlot = c.getEquipmentToShow(Player.playerArrows);

		if (arrowSlot == 22947) {  //Rada's blessing
			c.playerStandIndex = 1501;
			c.playerWalkIndex = 1851;
			c.playerRunIndex = 1851;
			return;
		}


		int weaponId = c.getEquipmentToShow(Player.playerWeapon);
		String name = ItemDef.forId(weaponId).getName();

		if (name == null) {
			return;
		}

		if (weaponId == 27871) {
			c.playerStandIndex = 9460;
			c.playerWalkIndex = 9461;
			c.playerRunIndex = 9459;
			return;
		}

		if (weaponId == 33446) {
			c.playerStandIndex = 2770;
			c.playerWalkIndex = 2769;
			c.playerRunIndex = 2769;
			return;
		}

		if (weaponId == 27586 || weaponId == 27580) {
			c.playerStandIndex = 1421;
			c.playerWalkIndex = 1422;
			c.playerRunIndex = 1427;
			return;
		}

		if (weaponId == 25500) {  //Cursed Banana
			c.playerStandIndex = 4646;
			c.playerWalkIndex = 4682;
			c.playerRunIndex = 6277;
			c.playerTurn90CWIndex = 6275;
			c.playerTurn90CCWIndex = 6276;
			c.playerTurnIndex = 6276;
			c.playerTurn180Index = 6276;
			return;
		}

		if (weaponId == 27645) {  //Mystic cards
			c.playerStandIndex = 9847;
			c.playerWalkIndex = 9849;
			c.playerRunIndex = 9850;
			c.playerTurnIndex = 820;
			c.playerTurn180Index = 820;
			c.playerTurn90CWIndex = 9851;
			c.playerTurn90CCWIndex = 9852;
			return;
		}

		if (weaponId == 21354) {  //Fan
			c.playerStandIndex = 6297;
			c.playerWalkIndex = 7629;
			c.playerRunIndex = 7633;
			c.playerTurn180Index = 7630;
			c.playerTurn90CWIndex = 7631;
			c.playerTurn90CCWIndex = 7632;
			return;
		}

		if (weaponId == 25314) {  //giant boulder
			c.playerStandIndex = 4193;
			c.playerWalkIndex = 4194;
			c.playerRunIndex = 7274;
			c.playerTurn180Index = 4194;
			c.playerTurn90CWIndex = 4194;
			c.playerTurn90CCWIndex = 4194;
			return;
		}

		if (weaponId == 27820) {  //Balloons
			c.playerStandIndex = 10032;
			c.playerWalkIndex = 10033;
			c.playerRunIndex = 10034;
			c.playerTurn180Index = 10033;
			c.playerTurn90CWIndex = 10033;
			c.playerTurn90CCWIndex = 10033;
			return;
		}

		if (weaponId == 21209) {  //Balloons
			c.playerStandIndex = 7538;
			c.playerWalkIndex = 7539;
			c.playerRunIndex = 7540;
			c.playerTurn180Index = 7539;
			c.playerTurn90CWIndex = 7539;
			c.playerTurn90CCWIndex = 7539;
			return;
		}

		if (weaponId == 26649) {  //Skis
			c.playerStandIndex = 9341;
			c.playerWalkIndex = 9342;
			c.playerRunIndex = 9346;
			return;
		}

		String weaponName = name.toLowerCase();

		if (weaponName.contains("c'bow") || weaponName.contains("crossbow")) {
			c.playerStandIndex = 4591;
			c.playerWalkIndex = 4226;
			c.playerRunIndex = 4228;
			return;
		}

		if (weaponName.contains("hunting knife")) {
			c.playerStandIndex = 7329;
			c.playerWalkIndex = 7327;
			c.playerRunIndex = 7327;
			return;
		}

		if (weaponName.contains("bulwark") || (weaponId == 25604)) {
			c.playerStandIndex = 7508;
			c.playerWalkIndex = 7510;
			c.playerRunIndex = 7509;
			return;
		}

		if (weaponName.contains("elder maul") || weaponName.contains("axe of araphel")) {
			c.playerStandIndex = 7518;
			c.playerWalkIndex = 7520;
			c.playerRunIndex = 7519;
			return;
		}
		if (weaponId == 26515 || weaponId == 27414 || weaponId == 20836 || weaponId == 23108
		    || weaponId == 23446 || weaponId == 8871 || weaponId == 24376 || weaponId == 24374
				|| weaponId == 24372){

			c.playerStandIndex = 9272;
			c.playerWalkIndex = 9273;
			c.playerRunIndex = 7274;
			return;
		}
		if (weaponId == 7671 || weaponId == 7673 || weaponId == 11705 || weaponId == 11706) {  //boxing
	        c.playerStandIndex = 3677;
			c.playerWalkIndex = 3680;
			c.playerRunIndex = 3680;
	        return;
		}

		if (weaponName.contains("ballista")) {
			c.playerStandIndex = 7220;
			c.playerWalkIndex = 7223;
			c.playerRunIndex = 7221;
			return;
		}

		if (weaponName.contains("tumeken's shadow")) {
			c.playerStandIndex = 9494;
			c.playerWalkIndex = 1660;
			c.playerRunIndex = 1664;
			return;
		}

		if (weaponId == 28574) { //Corrupted tumeken's shadow
			c.playerStandIndex = 9494;
			c.playerWalkIndex = 1660;
			c.playerRunIndex = 1664;
			return;
		}

		if (weaponName.contains("clueless")) {
			c.playerStandIndex = 7271;
			c.playerWalkIndex = 7272;
			c.playerRunIndex = 7273;
			return;
		}
		if (weaponName.contains("casket")) {
			c.playerStandIndex = 9272;
			c.playerWalkIndex = 9273;
			c.playerRunIndex = 7274;
			return;
		}
		if (weaponName.contains("halberd") || weaponName.contains("partisan") || weaponName.contains("hasta") || weaponName.contains("spear") || weaponName.contains("guthan")) {
			c.playerStandIndex = 809;
			c.playerWalkIndex = 1146;
			c.playerRunIndex = 1210;
			return;
		}

		if (weaponId == 25979 || weaponId == 27291 || weaponId == 25981 || weaponId == 27287) {
			c.playerStandIndex = 809;
			c.playerWalkIndex = 1146;
			c.playerRunIndex = 1210;
			return;
		}

		if (weaponName.contains("scythe")) {
			c.playerStandIndex = 8057;
			c.playerWalkIndex = 1146;
			c.playerRunIndex = 1210;
			return;
		}

		if (weaponName.contains("banner")) {
			c.playerStandIndex = 1421;
			c.playerWalkIndex = 1422;
			c.playerRunIndex = 1427;
			return;
		}
		if (weaponName.startsWith("basket")) {
			c.playerStandIndex = 1836;
			c.playerWalkIndex = 1836;
			c.playerRunIndex = 1836;
			return;
		}
		if (weaponName.contains("sled")) {
			c.playerStandIndex = 1461;
			c.playerWalkIndex = 1468;
			c.playerRunIndex = 1467;
			return;
		}
		if (weaponName.contains("dharok")) {
			c.playerStandIndex = 0x811;
			c.playerWalkIndex = 2064;
			return;
		}
		if (weaponName.contains("ale of the gods") || weaponName.contains("crystal grail") || weaponName.contains("ale of the scotty")) {
			c.playerStandIndex = 2770;
			c.playerWalkIndex = 2769;
			c.playerRunIndex = 2769;
			return;
		}
		if (weaponName.contains("soulreaper")) {
			c.playerStandIndex = 10169;
			c.playerWalkIndex = 10170;
			c.playerRunIndex = 10409;
			return;
		}
		if (weaponName.contains("ahrim")) {
			c.playerStandIndex = 809;
			c.playerWalkIndex = 1146;
			c.playerRunIndex = 1210;
			return;
		}
		if (weaponName.contains("verac")) {
			c.playerStandIndex = 1832;
			c.playerWalkIndex = 1830;
			c.playerRunIndex = 1831;
			return;
		}
		if (weaponName.contains("wand") || weaponName.contains("staff") || weaponName.contains("sceptre") || weaponName.contains("trident")) {
			c.playerStandIndex = 809;
			c.playerRunIndex = 1210;
			c.playerWalkIndex = 1146;
			return;
		}
		if (weaponName.contains("karil")) {
			c.playerStandIndex = 2074;
			c.playerWalkIndex = 2076;
			c.playerRunIndex = 2077;
			return;
		}
		if (weaponName.contains("2h sword") || weaponName.contains("godsword") || weaponName.contains("saradomin sw") || weaponName.contains("saradomin's bless") || weaponName.contains("large spade")) {
			if (weaponId != 7158) {
				c.playerStandIndex = 7053;
				c.playerWalkIndex = 7052;
				c.playerRunIndex = 7043;
				c.playerTurnIndex = 7044;
				c.playerTurn180Index = 7047;
				c.playerTurn90CWIndex = 7047;
				c.playerTurn90CCWIndex = 7048;
				return;
			}
		}
		if (weaponName.contains("bow")) {
			c.playerStandIndex = 808;
			c.playerWalkIndex = 819;
			c.playerRunIndex = 824;
			return;
		}
		if (weaponName.contains("godbow")) {
			c.playerStandIndex = 808;
			c.playerWalkIndex = 819;
			c.playerRunIndex = 824;
			return;
		}

		if (weaponName.contains("zamorakian")) {
			c.playerStandIndex = 1662;
			c.playerWalkIndex = 1663;
			c.playerRunIndex = 1664;
			return;
		}

		switch (weaponId) {
			case Items.DRAGON_HUNTER_LANCE:
				c.playerStandIndex = 813;
				c.playerWalkIndex = 1205;
				c.playerRunIndex = 2563;
				c.playerTurnIndex = 1209;
				c.playerTurn180Index = 1206;
				c.playerTurn90CWIndex = 1207;
				c.playerTurn90CCWIndex = 1208;
				break;
			case 7158:
				c.playerStandIndex = 2065;
				c.playerWalkIndex = 2064;
				break;
			case 27660:
			case 22545:
				c.playerStandIndex = 244;
				c.playerWalkIndex = 247;
				c.playerRunIndex = 248;
				break;
			case 4151:
			case 26484:
			case 26482:
			case 12773:
			case 12774:
			case 12006:
				c.playerWalkIndex = 1660;
				c.playerRunIndex = 1661;
				break;
			case 8004:
			case 7960:
				c.playerStandIndex = 2065;
				c.playerWalkIndex = 2064;
				break;
			case 6528:
			case Items.HILL_GIANT_CLUB:
				c.playerStandIndex = 0x811;
				c.playerWalkIndex = 2064;
				c.playerRunIndex = 1664;
				break;
			case 24227:
			case 24225:
			case 12848:
			case 4153:
			case 13263:
				c.playerStandIndex = 1662;
				c.playerWalkIndex = 1663;
				c.playerRunIndex = 1664;
				break;
			case 10887:
				c.playerStandIndex = 5869;
				c.playerWalkIndex = 5867;
				c.playerRunIndex = 5868;
				break;
			case 20368:
			case 20370:
			case 20374:
			case 20372:
			case 11802:
			case 20164:
			case 11804:
			case 11838:
			case 12809:
			case 11806:
			case 11808:
				c.playerStandIndex = 7053;
				c.playerWalkIndex = 7052;
				c.playerRunIndex = 7043;
				c.playerTurnIndex = 7049;
				c.playerTurn180Index = 7052;
				c.playerTurn90CWIndex = 7052;
				c.playerTurn90CCWIndex = 7052;
				break;
			case 1305:
                c.playerStandIndex = 809;
				break;
			case 28988:
				c.playerStandIndex = 1713;
				c.playerWalkIndex = 1703;
				c.playerRunIndex = 1707;
				c.playerTurnIndex = 1705;
				c.playerTurn180Index = 1705;
				c.playerTurn90CWIndex = 1705;
				c.playerTurn90CCWIndex = 1705;
				break;
			case 29000:
				c.playerStandIndex = 813;
				c.playerWalkIndex = 1205;
				c.playerRunIndex = 1210;
				c.playerTurnIndex = 1208;
				c.playerTurn180Index = 1208;
				c.playerTurn90CWIndex = 1208;
				c.playerTurn90CCWIndex = 1208;
				break;
			case 28997:
				c.playerStandIndex = 808;
				c.playerWalkIndex = 819;
				c.playerRunIndex = 824;
				c.playerTurnIndex = 822;
				c.playerTurn180Index = 822;
				c.playerTurn90CWIndex = 822;
				c.playerTurn90CCWIndex = 822;
				break;
			case 29084:
				c.playerStandIndex = 808;
				c.playerWalkIndex = 819;
				c.playerRunIndex = 824;
				c.playerTurnIndex = 822;
				c.playerTurn180Index = 822;
				c.playerTurn90CWIndex = 822;
				c.playerTurn90CCWIndex = 822;
				break;
			case 33417:
				c.playerStandIndex = 808;
				c.playerWalkIndex = 819;
				c.playerRunIndex = 824;
				c.playerTurnIndex = 822;
				c.playerTurn180Index = 822;
				c.playerTurn90CWIndex = 822;
				c.playerTurn90CCWIndex = 822;
				break;
			case 12727:
				c.playerStandIndex = 2316;
				c.playerWalkIndex = 2317;
				c.playerRunIndex = 2322;
				c.playerTurnIndex = 2320;
				c.playerTurn180Index = 2320;
				c.playerTurn90CWIndex = 2317;
				c.playerTurn90CCWIndex = 2319;
				break;
			case 27021:
				break;

            default:
				c.playerStandIndex = 0x328;
				c.playerTurnIndex = 0x337;
				c.playerWalkIndex = 0x333;
				c.playerTurn180Index = 0x334;
				c.playerTurn90CWIndex = 0x335;
				c.playerTurn90CCWIndex = 0x336;
				c.playerRunIndex = 0x338;
				break;
		}
	}

	public static int getWepAnim(Player c) {
		String weaponName = ItemAssistant.getItemName(c.getEquipmentToShow(Player.playerWeapon)).toLowerCase();
		if (c.getEquipmentToShow(Player.playerWeapon) <= 0) {
			switch (c.getCombatConfigs().getWeaponMode().getAttackStyle()) {
				case AGGRESSIVE:
					return 423;
				default:
					return 422;
			}
		}

		if (weaponName.equalsIgnoreCase("giant bronze dagger")) {
			return 11124;
		}

		if (weaponName.equalsIgnoreCase("BONK")) {
			return 377;
		}

		if (weaponName.equalsIgnoreCase("Corrupted dark bow") || weaponName.equalsIgnoreCase("Wraith Bow")) {
			return 426;
		}

		if (weaponName.equalsIgnoreCase("tonalztics of ralos (uncharged)")) {
			return 10922;
		}

		if (weaponName.contains("tonalztics")) {
			return 10923;
		}

		if (weaponName.contains("mystic cards")) {
			return 9848;
		}

		if (weaponName.contains("nutcracker")) {
			return 429;
		}
		if (weaponName.contains("bulwark") || weaponName.contains("eastdoor")) {
			return 7511;
		}
		if (weaponName.contains("elder maul") || weaponName.contains("axe of araphel")) {
			return 7516;
		}
		if (weaponName.contains("scythe") && !weaponName.contains("demonx") && !weaponName.contains("wraith") || weaponName.contains("candy cane scythe")) {
			switch (c.getCombatConfigs().getWeaponMode().getAttackStyle()) {
				case ACCURATE:
				case AGGRESSIVE:
				case CONTROLLED:
				case DEFENSIVE:
					return 8056;
			}
		}
		if (weaponName.contains("demonx scythe") || weaponName.contains("wraith scythe")) {
			return 8290;
		}
		if (weaponName.contains("osmumten") || weaponName.contains("demonx sword") || weaponName.contains("wraith sword")) {
			switch (c.getCombatConfigs().getWeaponMode().getIndex()) {
				case 0:  // attack
					return 9471;
				case 1:  // str
					return 390;
				case 2:  // crush
					return 9471;
				case 3:  // def
					return 9471;
			}
		}
		if (weaponName.contains("ghrazi rapier")) {
			return 8145;
		}
		if (weaponName.contains("zamorakian")) {
			return 2080;
		}
		if (weaponName.contains("hunting knife")) {
			return 7328;
		}
		if (weaponName.contains("ballista")) {
			return 7218;
		}
		if (weaponName.contains("blazing blowpipe")) {
			return 10656;
		}
		if (weaponName.contains("toxic blowpipe")) {
			return 5061;
		}
		if (weaponName.contains("warhammer")) {
			return 401;
		}
		if (weaponName.contains("dart")) {
			return c.getCombatConfigs().getWeaponMode().getAttackStyle() == AttackStyle.AGGRESSIVE ? 806 : 6600;
		}
		if (weaponName.contains("dragon 2h")) {
			return 407;
		}
		if (weaponName.contains("thrownaxe")) {
			return 7617;
		}
		if (weaponName.contains("knife") || weaponName.contains("javelin")) {
			return 8194;
		}
		if (weaponName.contains("cross") && !weaponName.contains("karil") || weaponName.contains("crossbow") && !weaponName.contains("karil") || weaponName.contains("c'bow") && !weaponName.contains("karil")) {
			return 4230;
		}
		if (weaponName.contains("halberd") || weaponName.contains("partisan")) {
			return 440;
		}
		if (weaponName.startsWith("dragon dagger")) {
			return 402;
		}
		if (weaponName.contains("abyssal dagger")) {
			return c.getCombatConfigs().getWeaponMode().getCombatStyle() == SLASH ? 3297 : 3294;
		}
		if (weaponName.contains("dagger") || weaponName.contains("arclight")) {
			return 412;
		}
		if (weaponName.contains("lance")) {
			switch (c.getCombatConfigs().getWeaponMode().getCombatStyle()) {
				case STAB:
					return 8288;
				case CRUSH:
				case SLASH: // 8289 is slash but it's broken
					return 8290;
			}
		}
		if (weaponName.contains("2h sword") || weaponName.contains("godsword") || weaponName.contains("aradomin sword") || weaponName.contains("blessed sword") || weaponName.contains("large spade")) {
			switch (c.getCombatConfigs().getWeaponMode().getIndex()) {
				case 0:// stab
				case 1:// str
					return 7045;
				case 2:// str
					return 7054;
				case 3:// def
					return 7055;
			}
		}
		if (weaponName.contains("dharok")) {
			switch (c.getCombatConfigs().getWeaponMode().getIndex()) {
				case 0:// attack
					return 2067;
				case 1:// str
					return 2067;
				case 2:// crush
					return 2066;
				case 3:// def
					return 2067;
			}
		}
		if (weaponName.contains("soulreaper")) {
			switch (c.getCombatConfigs().getWeaponMode().getIndex()) {
				case 0:// attack
				case 1:// str
				case 2:// crush
				case 3:// def
					return 7054;
			}
		}
		if (weaponName.contains("arclight")) {
			switch (c.getCombatConfigs().getWeaponMode().getAttackStyle()) {
				case ACCURATE:
					return 451;
				case AGGRESSIVE:
					return 2067;
				case DEFENSIVE:
					return 2067;
			}
		}
		if (weaponName.contains("sword") && !weaponName.contains("training")) {
			return 390;
		}
		if (weaponName.contains("voidwaker")) {
			return 390;
		}
		if (weaponName.contains("karil")) {
			return 2075;
		}
		if (weaponName.contains("bow") && !weaponName.contains("'bow") && !weaponName.contains("karil")) {
			return 426;
		}
		if (weaponName.contains("godbow") && !weaponName.contains("bow") && !weaponName.contains("'bow") && !weaponName.contains("karil")) {
			return 426;
		}
		if (weaponName.contains("'bow") && !weaponName.contains("karil")) {
			return 4230;
		}
		if (weaponName.contains("hasta") || weaponName.contains("spear")) {
			return 400;
		}
		if (weaponName.contains("snowball")) {
			return 5063;
		}
		if (weaponName.contains("scim")) {
			return 390;
		}
	    if (weaponName.contains("sulphur blades")) {
			return 2068;
        }
		if (weaponName.contains("macuahuitl")) {
		return 10989;
		}

		switch (c.getEquipmentToShow(Player.playerWeapon)) { // if you don't want to use strings

			case 33417:
				return 2068;
			case 9703:
				return 412;
			case 27660:
			case 22545:
				return 245;
			case 13263:
				return 3298;

			case 6522:
				return 2614;
			case 11959:
			case 10034:
			case 10033:
				return 2779;
			case 24425:
			case 24424:
			case 24423:
			case 24422:
				return 4505;
			case 11791:
			case 12904:
				return 440;
			case 8004:
			case 7960:
				return 2075;
			case 12848:
			case 24227:
			case 24225:
			case 4153: // granite maul
				return 1665;
			case 4726: // guthan
				return 2080;
			case 4747: // torag
				return 0x814;
			case 4710: // ahrim
				return 406;
			case 4755: // verac
				return 2062;
			case 4734: // karil
				return 2075;
			case 33162:
			case 4151:
			case 26484:
			case 26482:
			case 12773:
			case 12774:
			case 12006:
				return 1658;
			case 20727:
				return 2614;
			case 6528:
			case Items.HILL_GIANT_CLUB:
				return 2661;
			case 10887:
				return 5865;
			default:
				return 390;
		}
	}

	public static int getBlockEmote(Player c) {
		String shield = ItemAssistant.getItemName(c.getEquipmentToShow(Player.playerShield)).toLowerCase();
		String weapon = ItemAssistant.getItemName(c.getEquipmentToShow(Player.playerWeapon)).toLowerCase();
		if (shield.contains("nutcracker")) {
			return 410;
		}
		if (shield.contains("defender"))
			return 4177;
		if (shield.contains("2h") && c.getEquipmentToShow(Player.playerWeapon) != 7158)
			return 7050;
		if (shield.contains("book") || weapon.contains("wand") || weapon.contains("staff") || weapon.contains("sceptre") || weapon.contains("trident"))
			return 420;
		if (shield.contains("shield"))
			return 1156;
		if (shield.contains("warhammer"))
			return 397;
		if (shield.contains("bulwark") || shield.contains("eastdoor"))
			return 7512;
		if (shield.contains("elder maul") || shield.contains("axe of araphel"))
			return 7517;
		switch (c.getEquipmentToShow(Player.playerWeapon)) {
			case Items.SCYTHE_OF_VITUR:
				return 435;
			case 33161:
			case 33184:
			case 33203:
			case 33431:
				return 435;
			case 33148:
				return 435;
			case Items.DRAGON_HUNTER_LANCE:
				return 420;
			case 1734:
			case 411:

				return 3895;
			case 1724:
				return 3921;
			case 1709:
				return 3909;
			case 1704:
				return 3916;
			case 1699:
				return 3902;
			case 1689:
				return 3890;
			case 4755:
				return 2063;
			case 14484:
				return 397;
			case 12848:
			case 24227:
			case 24225:
			case 4153:
			case 13263:
				return 1666;
			case 13265:
			case 13267:
			case 13269:
			case 13271:
				return 3295;
			case 7158:
				return 410;
			case 4151:
			case 26484:
			case 26482:
			case 12773:
			case 12774:
			case 12006:
				return 1659;
			case 20727:
				return 2614;
			case 20368:
			case 20370:
			case 20374:
			case 20372:
			case 11802:
			case 20164:
			case 11806:
			case 11808:
			case 11804:
			case 11838:
			case 12809:
			case 11730:
				return 7056;
			case -1:
				return 424;
			default:
				return 424;
		}
	}

	public static int getHitDelay(Player c) {
		String weaponName = ItemAssistant.getItemName(c.getEquipmentToShow(Player.playerWeapon)).toLowerCase();
		if (c.usingMagic) {
			switch (CombatSpellData.getSpellId(c.getSpellId())) {
				case 12891:
					return 4;
				case 12871:
					return 6;
				default:
					return 4;
			}

		}
		if (weaponName.contains("dart") || weaponName.contains("snowball")) {
			return 3;
		}
		if (weaponName.contains("knife") || weaponName.contains("javelin") || weaponName.contains("thrownaxe") || weaponName.contains("throwing axe")) {
			return 3;
		}
		if (weaponName.contains("cross") || weaponName.contains("c'bow")) {
			return 4;
		}
		if (weaponName.contains("ballista")) {
			return 5;
		}
		if (weaponName.contains("bow") && !c.dbowSpec) {
			return 4;
		} else if (weaponName.contains("godbow") && !c.dbowSpec) {
			return 4;
		} else if (c.dbowSpec) {
			return 4;
		}

		switch (c.getEquipmentToShow(Player.playerWeapon)) {
			case 6522: // Toktz-xil-ul
				return 3;
			case 10887:
				return 3;
			case 10034:
			case 10033:
				return 3;
			default:
				return 2;
		}
	}
}