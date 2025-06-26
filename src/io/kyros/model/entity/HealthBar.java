package io.kyros.model.entity;

import io.kyros.Server;
import io.kyros.cache.definitions.HealthBarDefinition;
import io.kyros.util.Stream;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HealthBar {

    public HealthBar(Entity owner) {
        this.owner = owner;
        this.barType = 0;//23
        this.healthBarDefinition = Server.definitionRepository.get(HealthBarDefinition.class, barType);
        this.barWidth = (double) healthBarDefinition.width;
        this.depleteSpeed = 10;
        this.delay = 0;
        this.maxHealth = owner.getHealth().getMaximumHealth();
        this.currentWidth = (int) this.barWidth;
        this.newWidth = this.currentWidth;
    }
    public HealthBar(Entity owner, int type) {
        this.owner = owner;
        this.barType = type;
        this.healthBarDefinition = Server.definitionRepository.get(HealthBarDefinition.class, barType);
        this.barWidth = (double) healthBarDefinition.width;
        this.depleteSpeed = 10;
        this.delay = 0;
        this.maxHealth = owner.getHealth().getMaximumHealth();
        this.currentWidth = (int) this.barWidth;
        this.newWidth = this.currentWidth;
    }
    public void prepare() {
        Health health = owner.getHealth();
        setNewWidth((int) (((double) health.getCurrentHealth() / (double) health.getMaximumHealth()) * getBarWidth()));
    }
    public void update(Stream str) {
        str.writeByte(1);
        str.writeShort(barType);
        str.writeShort(15);
        str.writeShort(0);
        str.writeShort(getCurrentWidth());
        str.writeShort(getNewWidth());
        setCurrentWidth(getNewWidth());
    }

    public void setBarType(int type) {
        this.barType = type;
        this.healthBarDefinition = Server.definitionRepository.get(HealthBarDefinition.class, barType);
        this.barWidth = (double) healthBarDefinition.width;
        prepare();
    }
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        prepare();
    }

    private int depleteSpeed;
    private int delay;
    private int currentWidth;
    private int newWidth;
    private double barWidth;
    private int maxHealth;
    private int barType;
    private final Entity owner;
    private HealthBarDefinition healthBarDefinition;
}
