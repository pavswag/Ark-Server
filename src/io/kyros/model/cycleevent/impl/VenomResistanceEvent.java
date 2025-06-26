package io.kyros.model.cycleevent.impl;

import io.kyros.model.cycleevent.Event;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.Health;
import io.kyros.model.entity.HealthStatus;

public class VenomResistanceEvent extends Event<Entity> {

	public VenomResistanceEvent(Entity attachment, int ticks) {
		super("venom_resistance_event", attachment, ticks);
	}

	@Override
	public void execute() {
		super.stop();
		if (attachment == null) {
			return;
		}
		Health health = attachment.getHealth();
		health.removeNonsusceptible(HealthStatus.VENOM);
	}

}
