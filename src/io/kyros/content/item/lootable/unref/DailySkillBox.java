package io.kyros.content.item.lootable.unref;

import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.ItemAssistant;
import io.kyros.util.Misc;

import java.util.*;

/**
 * Revamped a simple means of receiving a random item based on chance.
 * 
 * @author Jason MacKeigan
 * @date Oct 29, 2014, 1:43:44 PM
 */
public class DailySkillBox extends CycleEvent {

	/**
	 * The item id of the mystery box required to trigger the event
	 */
	public static final int MYSTERY_BOX = 20791;

	/**
	 * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
	 */
	private static final Map<Rarity, List<GameItem>> items = new HashMap<>();

	/**
	 * Stores an array of items into each map with the corresponding rarity to the list
	 */
	static {
		items.put(Rarity.COMMON, 
			Arrays.asList(
				new GameItem(11849, 15 + Misc.random(15)),//marks of grace
				new GameItem(1518, 50 + Misc.random(25)),//maple logs
				new GameItem(450, 50 + Misc.random(50)),//adamantite ore
				new GameItem(2360, 50 + Misc.random(50)),//mithril bar
				new GameItem(450, 50 + Misc.random(50)),//adamantite ore
				new GameItem(2362, 50 + Misc.random(50)),//adamantite bar
				new GameItem(264, 25 + Misc.random(25)),//kwuarm
				new GameItem(3001, 25 + Misc.random(25)),//snapdragon
				new GameItem(266, 25 + Misc.random(25)),//cadantine
				new GameItem(2506, 50 + Misc.random(50)),//blue d-leather
				new GameItem(2508, 50 + Misc.random(50)),//red dragon leather
				new GameItem(1620, 25 + Misc.random(25)),//uncut ruby
				new GameItem(1618, 25 + Misc.random(25)),//uncut diamond
					new GameItem(7936, 50 + Misc.random(100)),//pure ess
				new GameItem(20718, 5)// burnt page
		));
		
	items.put(Rarity.UNCOMMON,
			Arrays.asList(
					new GameItem(11849, 15 + Misc.random(30)), //marks of grace
					new GameItem(1518, 50 + Misc.random(50)),//maple logs
					new GameItem(450, 50 + Misc.random(100)),//adamantite ore
					new GameItem(2360, 50 + Misc.random(100)),//mithril bar
					new GameItem(450, 50 + Misc.random(100)),//adamantite ore
					new GameItem(2362, 50 + Misc.random(100)),//adamantite bar
					new GameItem(264, 25 + Misc.random(50)),//kwuarm
					new GameItem(3001, 25 + Misc.random(50)),//snapdragon
					new GameItem(266, 25 + Misc.random(50)),//cadantine
					new GameItem(2506, 50 + Misc.random(100)),//blue d-leather
					new GameItem(2508, 50 + Misc.random(100)),//red dragon leather
					new GameItem(1620, 25 + Misc.random(50)),//uncut ruby
					new GameItem(1618, 25 + Misc.random(50)),//uncut diamond
					new GameItem(1514, 50 + Misc.random(100)),//magic logs
					new GameItem(452, 50 + Misc.random(50)),//runite ore
					new GameItem(2364, 50 + Misc.random(50)),//runite bar
					new GameItem(1624, 25 + Misc.random(100)),//un cut sapphire
					new GameItem(2482, 25 + Misc.random(100)),//clean lantadyme
					new GameItem(268, 25 + Misc.random(100)),//clean dwarf weed
					new GameItem(270, 25 + Misc.random(100)),//clean torstol
					new GameItem(2510, 50 + Misc.random(100)),//black d-leather
					new GameItem(7936, 50 + Misc.random(100)),//pure ess
					new GameItem(20718, 30)// burnt page
	));
		
		items.put(Rarity.RARE,
				Arrays.asList(
						new GameItem(20704, 1),//pyro garb
						new GameItem(20706, 1),//pyro robe
						new GameItem(20708, 1),//pyro hood
						new GameItem(20710, 1),//pyro boots
						new GameItem(20720, 1),//bruma torch
						new GameItem(20716, 1),//tome of fire
						new GameItem(20718, 25 + Misc.random(1000))// burnt page
		));
	}

	/**
	 * The player object that will be triggering this event
	 */
	private final Player player;

	/**
	 * Constructs a new myster box to handle item receiving for this player and this player alone
	 * 
	 * @param player the player
	 */
	public DailySkillBox(Player player) {
		this.player = player;
	}

	/**
	 * Opens a mystery box if possible, and ultimately triggers and event, if possible.
	 */
	public void open() {
		if (player.lastMysteryBox > System.currentTimeMillis()) {
			return;
		}
		if (player.getItems().freeSlots() < 2) {
			player.sendMessage("You need at least two free slots to open a mystery box.");
			return;
		}
		if (!player.getItems().playerHasItem(MYSTERY_BOX)) {
			player.sendMessage("You need a Wintertodt crate to do this.");
			return;
		}
		player.getItems().deleteItem(MYSTERY_BOX, 1);
		player.lastMysteryBox = System.currentTimeMillis();

		int random = Misc.random(10);
		List<GameItem> itemList = random < 5 ? items.get(Rarity.COMMON) : random >= 5 && random <= 8 ? items.get(Rarity.UNCOMMON) : items.get(Rarity.RARE);
		GameItem item = Misc.getRandomItem(itemList);

		if (items.get(Rarity.RARE).contains(item)) {
			PlayerHandler.executeGlobalMessage("[<col=CC0000>Wintertodt Crate</col>] <col=255>"
					+ player.getDisplayName()
					+ "</col> hit the jackpot and got a <col=CC0000>"+item.getDef().getName()+"</col>!");
		}

		player.getItems().addItemUnderAnyCircumstance(item.getId(), item.getAmount());
		player.sendMessage("You receive <col=255>" + item.getAmount() + " x " + ItemAssistant.getItemName(item.getId()) + "</col>.");

	}

	/**
	 * Executes the event for receiving the mystery box
	 */
	@Override
	public void execute(CycleEventContainer container) {
		if (player.isDisconnected() || Objects.isNull(player)) {
			container.stop();
			return;
		}


		container.stop();
	}

	/**
	 * Represents the rarity of a certain list of items
	 */
	enum Rarity {
		UNCOMMON, COMMON, RARE
	}

}