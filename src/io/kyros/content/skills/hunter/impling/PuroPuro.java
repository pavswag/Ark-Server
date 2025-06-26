package io.kyros.content.skills.hunter.impling;

import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.player.Player;

public class PuroPuro {
	
	/**
	 * Impling data
	 * id -> The id of the impling
	 * x -> The x coordinate of the impling
	 * y -> The y coordinate of the impling
	 */
	public static final int[][] IMPLINGS = {
			/**
			 * Baby imps
			 */
			{1635, 2612, 4318},
			{1635, 2602, 4314},
			{1635, 2610, 4338},
			{1635, 2582, 4344},
			{1635, 2578, 4344},
			{1635, 2568, 4311},
			{1635, 2583, 4295},
			{1635, 2582, 4330},
			{1635, 2600, 4303},
			{1635, 2611, 4301},
			{1635, 2618, 4329},

			/**
			 * Young imps
			 */
			{1636, 2591, 4332},
			{1636, 2600, 4338},
			{1636, 2595, 4345},
			{1636, 2610, 4327},
			{1636, 2617, 4314},
			{1636, 2619, 4294},
			{1636, 2599, 4294},
			{1636, 2575, 4303},
			{1636, 2570, 4299},

			/**
			 * Gourment imps
			 */
			{1637, 2573, 4339},
			{1637, 2567, 4328},
			{1637, 2593, 4297},
			{1637, 2618, 4305},
			{1637, 2605, 4316},
			{1637, 2596, 4333},

			/**
			 * Earth imps
			 */
			{1638, 2592, 4338},
			{1638, 2611, 4345},
			{1638, 2617, 4339},
			{1638, 2614, 4301},
			{1638, 2606, 4295},
			{1638, 2581, 4299},

			/**
			 * Essence imps
			 */
			{1639, 2602, 4328},
			{1639, 2608, 4333},
			{1639, 2609, 4296},
			{1639, 2581, 4304},
			{1639, 2570, 4318},

			/**
			 * Eclectic imps
			 */
			{1640, 2611, 4310},
			{1640, 2617, 4319},
			{1640, 2600, 4347},
			{1640, 2570, 4326},
			{1640, 2579, 4310},

			/**
			 * Spirit imps
			 */

			/**
			 * Nature imps
			 */
			{1641, 2581, 4310},
			{1641, 2581, 4310},
			{1641, 2603, 4333},
			{1641, 2576, 4335},
			{1641, 2588, 4345},

			/**
			 * Magpie imps
			 */
			{1642, 2612, 4324},
			{1642, 2602, 4323},
			{1642, 2587, 4348},
			{1642, 2564, 4320},
			{1642, 2566, 4295},

			/**
			 * Ninja imps
			 */
			{1643, 2570, 4347},
			{1643, 2572, 4327},
			{1643, 2578, 4318},
			{1643, 2610, 4312},
			{1643, 2594, 4341},

			/**
			 * Dragon imps
			 */
			{1654, 2613, 4341},
			{1654, 2585, 4337},
			{1654, 2576, 4319},
			{1654, 2576, 4294},
			{1654, 2592, 4305},
	};
	
	public static void magicalWheat(Player player, WorldObject globalObject) {
		if (System.currentTimeMillis() - player.lastWheatPass < 2000) {
			return;
		}
		player.sendMessage("You use your strength to push through the wheat.");

		player.facePosition(globalObject.getPosition());
		System.out.println(globalObject.getPosition().toString());
		int goX = 0, goY = 0;
		if (player.getY() > globalObject.getPosition().getY() && player.getX() == globalObject.getX()) {
			goX = 0;
			goY = -2;
		} else if (player.getY() < globalObject.getPosition().getY() && player.getX() == globalObject.getX()) {
			goX = 0;
			goY = 2;
		} else if (player.getY() == globalObject.getPosition().getY() && player.getX() > globalObject.getX()) {
			goX = -2;
			goY = 0;
		} else if (player.getY() == globalObject.getPosition().getY() && player.getX() < globalObject.getX()) {
			goX = 2;
			goY = 0;
		}

		player.getAgilityHandler().move(player, goX, goY, 6594, -1);
		player.lastWheatPass = System.currentTimeMillis();
	}
}
