package io.kyros.model.cycleevent.impl;

import io.kyros.model.cycleevent.Event;
import io.kyros.util.Misc;

import java.util.concurrent.TimeUnit;

public class UpdateQuestTab extends Event<Object> {


	private static final int INTERVAL = Misc.toCycles(5, TimeUnit.SECONDS);

	
	public UpdateQuestTab() {
		super("", new Object(), INTERVAL);
	}	

	@Override
	public void execute() {
/*		Server.getPlayers().nonNullStream().forEach(player -> {
			player.getQuestTab().updateInformationTab();
		});*/
	}
} 