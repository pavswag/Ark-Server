package io.kyros.content.achievement_diary.impl;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public enum WesternDiaryEntry {
	//Easy
	PEST_CONTROL("Save the island of pest control: %totalstage", 25),
	GNOME_AGILITY("Complete the gnome agility course: %totalstage", 25),
	PEST_CONTROL_TELEPORT("Teleport to pest control using the minigame teleports"),
	FLETCH_OAK_SHORT_WEST("Fletch an oak shortbow in gnome stronghold"),
	
	//Medium
	SPIRIT_TREE_WEST("Travel to gnome stronghold via spirit tree"),
	CRYSTAL("Turn a crystal into a crystal bow"),
	HERB_PACK("Purchase 5 herb packs at the pest control: %totalstage", 5),
	SEED_PACK("Purchase 5 seed packs at the pest control: %totalstage", 5),
	
	//Hard
	KILL_ELVES("Kill some elves using a crystal bow: %totalstage", 50),
	COOK_MONK("Cook some monkfish: %totalstage", 250),
	KILL_ZULRAH("Kill Zulrah: %totalstage", 30),
	PICKPOCKET_GNOME("Pickpocket a theif in the area: %totalstage", 150),
	
	//Elite
	KILL_THERMO("Kill the thermonuclear smoke devil: %totalstage", 50),
	FULL_VOID("Get a task from nieve, wearing full void melee"),
	UPGRADE_VOID("Upgrade two void pieces to elite: %totalstage", 2);
	
	private final String description;
	
	private final int maximumStages;
	
	public static final Set<WesternDiaryEntry> SET = EnumSet.allOf(WesternDiaryEntry.class);
	
	WesternDiaryEntry(String description) {
		this(description, -1);
	}
	
	WesternDiaryEntry(String description, int maximumStages) {
		this.description = description;
		this.maximumStages = maximumStages;
	}
	
	public final String getDescription() {
		return description;
	}
	
	public static final Optional<WesternDiaryEntry> fromName(String name) {
		return SET.stream().filter(entry -> entry.name().equalsIgnoreCase(name)).findAny();
	}

	public int getMaximumStages() {
		return maximumStages;
	}
}
