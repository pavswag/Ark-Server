package io.kyros.model.cycleevent.impl;

import io.kyros.model.cycleevent.Event;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.Health;
import io.kyros.model.entity.HealthStatus;

public class PoisonResistanceEvent extends Event<Entity> {

	public PoisonResistanceEvent(Entity attachment, int ticks) {
		super("poison_resistance_event", attachment, ticks);
	}

	@Override
	public void execute() {
		if (attachment == null) {
			super.stop();
			return;
		}
		super.stop();

		Health health = attachment.getHealth();
		health.removeNonsusceptible(HealthStatus.POISON);
	}

}
