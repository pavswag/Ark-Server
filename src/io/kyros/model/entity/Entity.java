package io.kyros.model.entity;

import com.google.common.base.Preconditions;
import io.kyros.content.WeaponGames.WGManager;
import io.kyros.content.combat.Damage;
import io.kyros.content.combat.HitMask;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.raids.Raids;
import io.kyros.content.minigames.xeric.Xeric;
import io.kyros.content.tournaments.TourneyManager;
import io.kyros.model.*;
import io.kyros.model.collisionmap.RegionProvider;
import io.kyros.model.collisionmap.doors.Location;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCClipping;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.queue.EntityQueue;
import io.kyros.model.entity.queue.OneTimeEntityTask;
import io.kyros.model.timers.TickTimer;
import io.kyros.util.Misc;
import io.kyros.util.Stream;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static io.kyros.model.entity.npc.NPCClipping.DIR;

/**
 * Represents a game-world presence that exists among others of the same nature.
 * The objective is to allow multiple entities to share common similarities and
 * allow simple but effective reference in doing so.
 *
 * @author Jason MacKeigan
 * @date Mar 27, 2015, 2015, 8:00:45 PM
 */
public abstract class Entity {
    public int pidOrderIndex;
    /**
     * The index in the list that the player resides
     */
    protected int index;
    /**
     * A mapping of all damage that has been taken by other entities in the game
     */
    protected Map<Entity, List<Damage>> damageTaken = new HashMap<>();
    /**
     * The {@link Entity} that has been determined the killer of this {@link Entity}
     */
    protected Entity killer;

    public HealthBar healthBar;
    /**
     * The health of the entity
     */
    protected Health health;
    private InstancedArea instance;
    private Raids raidsInstance;
    private Xeric xeric;
    protected int hitMask1;
    protected int hitMask2;

    public int playerHitIndex, playerHitIndex2;
    public EntityReference frozenBy;
    public int freezeTimer;
    public boolean isDead;
    public int attackTimer;
    public int hitDiff2;
    public int hitDiff;
    public boolean hitUpdateRequired2;
    public boolean hitUpdateRequired;
    private boolean animationUpdateRequired;
    private boolean updateRequired = true;
    private Animation animation;
    private boolean gfxUpdateRequired;
    private Graphic graphic;
    private boolean invisible;

    /**
     * The timer associated with the animation of the Entity
     */
    private final TickTimer animationTimer = new TickTimer();

    /**
     * Attribute class for easy caching of variables.
     */
    private final Attributes attributes = new Attributes();

    /**
     * Sends some information to the Stream regarding a possible new hit on the
     * entity.
     *
     * @param str
     *            the stream for the entity
     */
    protected abstract void appendHitUpdate(Player player, Stream str);


    /**
     * Used to append some amount of damage to the entity and inflict on their total
     * amount of health. The method will also display a new hitmark on that entity.
     *
     * @param entity
     * @param damage
     *            the damage dealt
     * @param hitMask
     */
    public abstract void appendDamage(Entity entity, int damage, HitMask hitMask);

    public int lastMaxHit = -1;

    public abstract boolean isFreezable();

    /**
     * Determines if the entity is susceptible to a status based on their nature.
     * For example some players when wearing certain equipment are exempt from venom
     * or poison status. In other situations, NPC's are susceptible to venom.
     *
     * @param status
     *            the status the entity may not be susceptible to
     * @return {code true} if the entity is not susceptible to a particular status
     */
    public abstract boolean susceptibleTo(HealthStatus status);

    /**
     * Remove from current instance.
     */
    public abstract void removeFromInstance();

    /**
     * Get the entity size (x by x)
     */
    public abstract int getEntitySize();

    /**
     * The x-position on the map where the entity exists. This is on the x-axis
     *
     * @return the x
     */
    public abstract int getX();

    /**
     * Modifies the x-position of the entity.
     *
     * @param x
     *            the new position
     */
    public abstract void setX(int x);

    /**
     * The y-position on the map where the entity exists. This is on the y-axis.
     *
     * @return the y-position
     */
    public abstract int getY();

    /**
     * Modifies the y-position of the entity.
     *
     * @param y
     *            the new position
     */
    public abstract void setY(int y);

    /**
     * The height level of the entity.
     *
     * @return the height
     */
    public abstract int getHeight();

    /**
     * Modifies the height of the entity.
     *
     * @param height
     *            the new height
     */
    public abstract void setHeight(int height);

    public abstract void resetWalkingQueue();

    public abstract int getDefenceLevel();

    public abstract int getDefenceBonus(CombatType combatType, Entity attacker);

    public abstract boolean hasBlockAnimation();

    public abstract Animation getBlockAnimation();

    public abstract boolean isAutoRetaliate();

    public abstract void attackEntity(Entity entity);

    private Position[] dangerousArea;

    public void setDangerousArea(Position[] dangerousArea) {
        this.dangerousArea = dangerousArea;
    }

    /**
     * Creates a new {@link Entity}.
     */
    public Entity() { }

    public boolean isRegistered() {
        return getIndex() > 0;
    }

    public abstract void appendHeal(int amount, HitMask mark);

    public void appendDamage(int damage, HitMask hitMask) {
        appendDamage(null, damage, hitMask);
    }

    public int lastDamageTaken;

    public void startAnimation(Animation animation) {
        if (this.animation == null ||
                animation.getAnimationPriority().compareTo(this.animation.getAnimationPriority()) > 0) {
            this.animation = animation;
            setUpdateRequired(true);
            animationUpdateRequired = true;
        }
    }
    public List<EntityProperties> entityProperties = new ArrayList<>();
    public void addEntityProperty(EntityProperties property) {
        if(!entityProperties.contains(property)) {
            entityProperties.add(property);
            if(this.isNPC()) {
                this.asNPC().requestTransform(this.asNPC().getNpcId());
            }
            if(this.isPlayer()) {
                this.asPlayer().updateAppearance();
            }
            property.apply(this);
        }
    }
    public Queue<Graphic> graphics = new ArrayBlockingQueue<>(500);

    public void startGraphic(Graphic graphic) {
        Entity entity = this;
        if (entity.graphics.offer(graphic)) {
            setUpdateRequired(true);
            gfxUpdateRequired = true;
        }
    }

    public void resetAfterUpdate() {
        animation = null;
        animationUpdateRequired = false;
        graphic = null;
        gfxUpdateRequired = false;

        hitDiff = -1;
        hitDiff2 = -1;
        hitMask1 = -1;
        hitMask2 = -1;
        playerHitIndex = -1;
        playerHitIndex2 = -1;
        setUpdateRequired(false);
    }

    public double getDistance(Position source, int x, int y) {
        double low = 9999;
        if (insideOf(x, y)) return 0;
        for (Position p : getBorder(source)) {
            double dist = Misc.distance(x, y, p.getX(), p.getY());
            if (dist < low) {
                low = dist;
            }
        }
        return low;
    }

    /**
     * Get the distance between the provided and coordinates and the
     * closest tile within this entity's {@link Entity#getBorder()}.
     */
    public double getDistance(int x, int y) {
        double low = 9999;
        if (insideOf(x, y)) return 0;
        for (Position p : getBorder()) {
            double dist = Misc.distance(x, y, p.getX(), p.getY());
            if (dist < low) {
                low = dist;
            }
        }
        return low;
    }

    public double distance(Position position) {
        double low = 9999;
        if (insideOf(position)) return 0;
        for (Position p : getBorder()) {
            double dist = Misc.distance(position.getX(), position.getY(), p.getX(), p.getY());
            if (dist < low) {
                low = dist;
            }
        }
        return low;
    }

    public boolean insideOf(int x, int y) {
        return insideOf(new Position(x, y));
    }

    public boolean insideOf(Position position) {
        return Arrays.stream(getTiles()).anyMatch(p -> p.getX() == position.getX() && p.getY() == position.getY());
    }

    public Position[] getTiles() {
        return getTiles(getPosition(), getEntitySize());
    }

    public Position[] getTiles(Position position, int size) {
        Position[] tiles = new Position[getEntitySize() == 1 ? 1 : (int) Math.pow(size, 2)];
        int index = 0;
        for (int i = 1; i < size + 1; i++) {
            for (int k = 0; k < NPCClipping.SIZES[i].length; k++) {
                int x3 = position.getX() + NPCClipping.SIZES[i][k][0];
                int y3 = position.getY() + NPCClipping.SIZES[i][k][1];
                tiles[index] = new Position(x3, y3, getHeight());
                index++;
            }
        }
        return tiles;
    }

    public Position[] getBorder() {
        return getBorder(getPosition());
    }

    /**
     * The border is the outer most ring of an npc. For a size one npc it's one tile,
     * for a size two npcs it's every tile, for a size 3 npc it includes every tile on
     * the outside edge.
     * @param source The position of the entity.
     */
    public Position[] getBorder(Position source) {
        int x = source.getX();
        int y = source.getY();
        int size = getEntitySize();
        if (size <= 1) {
            return new Position[] {new Position(source.getX(), source.getY())};
        }
        Position[] border = new Position[(size) + (size - 1) + (size - 1) + (size - 2)];
        int j = 0;
        border[0] = new Position(x, y);
        for (int i = 0; i < 4; i++) {
            for (int k = 0; k < (i < 3 ? (i == 0 || i == 2 ? size : size) - 1 : (i == 0 || i == 2 ? size : size) - 2); k++) {
                if (i == 0) x++;
                 else if (i == 1) y++;
                 else if (i == 2) x--;
                 else if (i == 3) {
                    y--;
                }
                border[(++j)] = new Position(x, y);
            }
        }
        return border;
    }

    public Position getAdjacentPosition() {
        for (int index = 0; index < DIR.length; index++) {
            if (getRegionProvider().canMove(getX(), getY(), getHeight(), index, this.isNPC())) {
                return new Position(getX() + DIR[index][0], getY() + DIR[index][1], getHeight());
            }
        }
        return null;
    }

    public Position getAdjacentPosition(Position... except) {
        for (int index = 0; index < DIR.length; index++) {
            Position position = new Position(getX() + DIR[index][0], getY() + DIR[index][1], getHeight());
            if (getRegionProvider().canMove(getX(), getY(), getHeight(), index, this.isNPC()) && Arrays.stream(except).noneMatch(position::equals)) {
                return position;
            }
        }
        return null;
    }

    /**
     * Get a position along an entity's border that is closest to the provided position.
     */
    public Position getAdjacentBorderPosition(Position position) {
        Position[] tiles = getBorder();
        double lowDist = 999;
        Position lowTile = null;
        for (Position tile : tiles) {
            double dist = tile.getAbsDistance(position);
            if (lowTile == null || dist < lowDist) {
                lowDist = dist;
                lowTile = tile;
            }
        }

        return lowTile;
    }

    public Position getCenterPosition() {
        Position position = getPosition();
        if (getEntitySize() > 2) {
            int deltax = (int) Math.ceil((double) getEntitySize() / 3.0);
            int deltay = (int) Math.ceil((double) getEntitySize() / 3.0);
            position = new Position(getPosition().getX() + deltax, getPosition().getY() + deltay, getPosition().getHeight());
        }
        return position;
    }

    public int distanceToPoint(int pointX, int pointY) {
        return (int) Math.sqrt(Math.pow(getX() - pointX, 2) + Math.pow(getY() - pointY, 2));
    }

    public boolean sameInstance(Entity other) {
        if (this.instance == null) {
            return other.instance == null;
        } else if (other.instance != null) {
            return other.instance == this.instance;
        }
        return false;
    }

    public boolean collides(int x, int y, int size, int targetX, int targetY, int targetSize) {
        int distanceX = x - targetX;
        int distanceY = x - targetY;
        return distanceX < targetSize && distanceX > -size && distanceY < targetSize && distanceY > -size;
    }

    /**
     * When an entity dies it is paramount that we know who dealt the most damage to
     * that entity so that we can determine who will receive the drop.
     *
     * @return the {@link Entity} that dealt the most damage to this {@link Entity}.
     */
    public Entity calculateTourneyKiller() {
        final long VALID_TIMEFRAME = TimeUnit.SECONDS.toMillis(90);
        Entity killer = null;
        int totalDamage = 0;
        for (Entry<Entity, List<Damage>> entry : damageTaken.entrySet()) {
            Entity tempKiller = entry.getKey();
            List<Damage> damageList = entry.getValue();
            int damage = 0;
            if (tempKiller == null) {
                continue;
            }
            if (tempKiller instanceof Player) {
                if (!TourneyManager.getSingleton().isInArena((Player) tempKiller)) {
                    continue;
                }
                for (Damage d : damageList) {
                    if (System.currentTimeMillis() - d.getTimestamp() < VALID_TIMEFRAME) {
                        damage += d.getAmount();
                    }
                }
                if (tempKiller.isPlayer()) {
                    Player p = tempKiller.asPlayer();
                    if (p.isDisconnected() || p.getSession() == null || !p.getSession().isActive()) continue;
                    if (this.getRaidsInstance() != null) {
                        if (p.getRaidsInstance() == null || p.getRaidsInstance() != this.getRaidsInstance()) continue;
                    }
                }
                if (totalDamage == 0 || damage > totalDamage || killer == null) {
                    totalDamage = damage;
                    killer = tempKiller;
                }
            }
        }
        return killer;
    }

    public Entity calculateWGKiller() {
        final long VALID_TIMEFRAME = TimeUnit.SECONDS.toMillis(90);
        Entity killer = null;
        int totalDamage = 0;
        for (Entry<Entity, List<Damage>> entry : damageTaken.entrySet()) {
            Entity tempKiller = entry.getKey();
            List<Damage> damageList = entry.getValue();
            int damage = 0;
            if (tempKiller == null) {
                continue;
            }
            if (tempKiller instanceof Player) {
                if (!WGManager.getSingleton().isInArena((Player) tempKiller)) {
                    continue;
                }
                for (Damage d : damageList) {
                    if (System.currentTimeMillis() - d.getTimestamp() < VALID_TIMEFRAME) {
                        damage += d.getAmount();
                    }
                }
                if (tempKiller.isPlayer()) {
                    Player p = tempKiller.asPlayer();
                    if (p.isDisconnected() || p.getSession() == null || !p.getSession().isActive()) continue;
                    if (this.getRaidsInstance() != null) {
                        if (p.getRaidsInstance() == null || p.getRaidsInstance() != this.getRaidsInstance()) continue;
                    }
                }
                if (totalDamage == 0 || damage > totalDamage || killer == null) {
                    totalDamage = damage;
                    killer = tempKiller;
                }
            }
        }
        return killer;
    }

    /**
     * Adds some damage value to the entities list of taken damage
     *
     * @param entity
     *            the entity that dealt the damage
     * @param damage
     *            the total damage taken
     */
    public void addDamageTaken(Entity entity, int damage) {
        if (entity == null || damage <= 0) {
            return;
        }
        Damage combatDamage = new Damage(damage);
        if (damageTaken.containsKey(entity)) {
            damageTaken.get(entity).add(new Damage(damage));
        } else {
            damageTaken.put(entity, new ArrayList<>(Arrays.asList(combatDamage)));
        }
    }

    /**
     * When an entity dies it is paramount that we know who dealt the most damage to
     * that entity so that we can determine who will receive the drop.
     *
     * @return the {@link Entity} that dealt the most damage to this {@link Entity}.
     */
    public Entity calculateKiller() {
        final long VALID_TIMEFRAME = this instanceof NPC ? TimeUnit.MINUTES.toMillis(5) : TimeUnit.SECONDS.toMillis(90);
        Entity killer = null;
        int totalDamage = 0;
        for (Entry<Entity, List<Damage>> entry : damageTaken.entrySet()) {
            Entity tempKiller = entry.getKey();
            List<Damage> damageList = entry.getValue();
            int damage = 0;
            if (tempKiller == null) {
                continue;
            }
            for (Damage d : damageList) {
                if (System.currentTimeMillis() - d.getTimestamp() < VALID_TIMEFRAME) {
                    damage += d.getAmount();
                }
            }
            if (totalDamage == 0 || damage > totalDamage || killer == null) {
                totalDamage = damage;
                killer = tempKiller;
            }
            if (killer != null && killer instanceof Player && this instanceof NPC) {
                Player player = (Player) killer;
                NPC npc = (NPC) this;
                if (player.getMode().isIronmanType() && !Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS) && !Boundary.isIn(player, Boundary.CORPOREAL_BEAST_LAIR) && !Boundary.isIn(player, Boundary.DAGANNOTH_KINGS) && !Boundary.isIn(player, Boundary.TEKTON) && !Boundary.isIn(player, Boundary.SKELETAL_MYSTICS) && !Boundary.isIn(player, Boundary.RAID_MAIN)) {
                    double percentile = ((double) totalDamage / (double) npc.getHealth().getMaximumHealth()) * 100.0;
                    if (percentile < 75.0) {
                        killer = null;
                    }
                }
            }
        }
        return killer;
    }

    /**
     * The index value where the {@link Entity} resides along with other common
     * counterparts.
     *
     * @return the index of the array where this object resides
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set the index.
     * @param index the index of the array where this object resides
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Gets the current {@link Position}
     */
    public Position getPosition() {
        return new Position(getX(), getY(), getHeight());
    }

    /**
     * Clears any and all damage that has been taken by the entity
     */
    public void resetDamageTaken() {
        damageTaken.clear();
    }

    /**
     * The status of the entities health whether it's normal, poisoned, or some
     * other nature.
     *
     * @return the status of the entities health
     */
    public Health getHealth() {
        if (health == null) {
            health = new Health(this);
            healthBar = new HealthBar(this);
        }
        return health;
    }

    /**
     * Updates the {@link Entity} that has killed this {@link Entity
     *
     * @param killer
     *            the {@link Entity} killer
     */
    public void setKiller(Entity killer) {
        this.killer = killer;
    }

    /**
     * The {@link Entity} that has dealt the most damage in combat
     *
     * @return the killer
     */
    public Entity getKiller() {
        return killer;
    }



    public boolean isPlayer() {
        return this instanceof Player;
    }

    public boolean isNPC() {
        return this instanceof NPC;
    }

    public Player asPlayer() {
        return (Player) this;
    }

    public NPC asNPC() {
        return (NPC) this;
    }

    public RegionProvider getRegionProvider() {
        if (instance != null) {
            return instance;
        } else {
            return RegionProvider.getGlobal();
        }
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public Location getLocation() {
        return new Location(getX(), getY(), getHeight());
    }

    public Raids getRaidsInstance() {
        return raidsInstance;
    }

    public Entity setRaidsInstance(Raids raids) {
        this.raidsInstance = raids;
        return this;
    }

    public Animation getAnimation() {
        return animation;
    }

    public boolean isUpdateRequired() {
        return updateRequired;
    }

    public void setUpdateRequired(boolean updateRequired) {
        this.updateRequired = updateRequired;
    }

    public boolean isAnimationUpdateRequired() {
        return animationUpdateRequired;
    }

    public InstancedArea getInstance() {
        return instance;
    }

    /**
     * Set an instance. This is only used by an {@link InstancedArea}, do not call this method elsewhere.
     * Instead use {@link InstancedArea#add} and {@link Entity#removeFromInstance()}
     * @param instance the instance
     */
    public void setInstance(InstancedArea instance) {
        this.instance = instance;
    }

    public boolean isGfxUpdateRequired() {
        return gfxUpdateRequired;
    }

    public Graphic getGraphic() {
        return graphic;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
        /**
         * With NPC's, the invisible state is sent in the transform block, however we want to retain the current npc id
         */
        if(isNPC()) {
            this.asNPC().requestTransform(this.asNPC().getNpcId());
        }
    }

    public void setXeric(final Xeric xeric) {
        this.xeric = xeric;
    }

    public Xeric getXeric() {
        return this.xeric;
    }

    public TickTimer getAnimationTimer() {
        return this.animationTimer;
    }

    public int getBonus(Bonus bonus) {
        return 0;
    }


    public void queue(Runnable r) {
        queueAsBoolean(() -> {
            r.run();
            return true;
        });
    }
    public void queue(Runnable r, int delay) {
        queueAsBooleanWithDelay(() -> {
            r.run();
            return true;
        }, delay);
    }
    public void queueAsBoolean(BooleanSupplier r) {
        CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if(r.getAsBoolean())
                    container.stop();
            }
        }, 1);
    }
    public void queueAsBooleanWithDelay(BooleanSupplier r, int delay) {
        CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if(r.getAsBoolean())
                    container.stop();
            }
        }, delay);
    }

    protected Map<AttributeKey, Object> attribs;

    public boolean hasAttrib(AttributeKey key) {
        return attribs != null && attribs.containsKey(key);
    }

    /**
     * Gets an attribute without a default value.
     * Make sure to be careful using this, to avoid
     * NullPointerExceptions because of no default value.
     *
     * @param key
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttrib(AttributeKey key) {
        return attribs == null ? null : (T) attribs.get(key);
    }

    /**
     * Gets an attribute with a default value.
     *
     * @param key
     * @param defaultValue
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribOr(AttributeKey key, Object defaultValue) {
        return attribs == null ? (T) defaultValue : (T) attribs.getOrDefault(key, defaultValue);
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrT(AttributeKey key, T defaultValue) {
        return attribs == null ? (T) defaultValue : (T) attribs.getOrDefault(key, defaultValue);
    }

    public void clearAttrib(AttributeKey key) {
        if (attribs != null)
            attribs.remove(key);
    }

    public void clearAttribs() {
        attribs.clear();
    }

    public Object putAttrib(AttributeKey key, Object v) {
        if (attribs == null)
            attribs = new EnumMap<>(AttributeKey.class);
        return attribs.put(key, v);
    }

    /**
     * Modifies the current numerical value of an attribute.
     *
     * @param key          the key of the attribute to be changed.
     * @param modifier     the value that will be modifying the current value.
     * @param defaultValue the default value to be inserted if none exists.
     * @param <T>          the type of number being modified.
     * @throws IllegalArgumentException thrown when the current value returned from the key is not parsable to numerical value
     *                                  or if the modifier and defaultValue are not the same class.
     */
    public <T extends Number> void modifyNumericalAttribute(AttributeKey key, T modifier, T defaultValue) throws
            IllegalArgumentException {
        Preconditions.checkArgument(modifier.getClass() == defaultValue.getClass(),
                "Modifier and defaultValue must have same class.");

        Number current = getAttribOr(key, defaultValue);

        if (current.getClass() == Byte.class) {
            putAttrib(key, current.byteValue() + modifier.byteValue());
        } else if (current.getClass() == Short.class) {
            putAttrib(key, current.shortValue() + modifier.shortValue());
        } else if (current.getClass() == Integer.class) {
            putAttrib(key, current.intValue() + modifier.intValue());
        } else if (current.getClass() == Long.class) {
            putAttrib(key, current.longValue() + modifier.longValue());
        } else if (current.getClass() == Float.class) {
            putAttrib(key, current.floatValue() + modifier.floatValue());
        } else if (current.getClass() == Double.class) {
            putAttrib(key, current.doubleValue() + modifier.doubleValue());
        } else {
            throw new IllegalArgumentException("current value isn't a parsable number.");
        }
    }

    /**
     * If there isn't a method for your type, it's probably not a simple primitive. Use {@link Player#getAttribTypeOr(Entity, AttributeKey, Object, Class, Supplier)} instead.
     */
    public static int getAttribIntOr(Entity player, AttributeKey key, @Nullable Object defaultValue) {
        return getAttribTypeOr(player, key, defaultValue, Integer.class, () -> 0);
    }

    /**
     * If there isn't a method for your type, it's probably not a simple primitive. Use {@link Player#getAttribTypeOr(Entity, AttributeKey, Object, Class, Supplier)} instead.
     */
    public static long getAttribLongOr(Entity player, AttributeKey key, @Nullable Object defaultValue) {
        return getAttribTypeOr(player, key, defaultValue, Long.class, () -> 0L);
    }

    /**
     * If there isn't a method for your type, it's probably not a simple primitive. Use {@link Player#getAttribTypeOr(Entity, AttributeKey, Object, Class, Supplier)} instead.
     */
    public static boolean getAttribBooleanOr(Entity player, AttributeKey key, @Nullable Object defaultValue) {
        return getAttribTypeOr(player, key, defaultValue, Boolean.class, () -> false);
    }

    /**
     * If there isn't a method for your type, it's probably not a simple primitive. Use {@link Player#getAttribTypeOr(Entity, AttributeKey, Object, Class, Supplier)} instead.
     */
    public static String getAttribStringOr(Entity player, AttributeKey key, @Nullable Object defaultValue) {
        return getAttribTypeOr(player, key, defaultValue, String.class, () -> "");
    }

    /**
     * If there isn't a method for your type, it's probably not a simple primitive. Use {@link Player#getAttribTypeOr(Entity, AttributeKey, Object, Class, Supplier)} instead.
     */
    public static double getAttribDoubleOr(Entity player, AttributeKey key, @Nullable Object defaultValue) {
        return getAttribTypeOr(player, key, defaultValue, Double.class, () -> 0d);
    }

    static final Class<?>[] DISALLOWED = new Class[]{int.class, float.class, byte.class, double.class, long.class, short.class};

    /**
     * To avoid class cast exceptions when dealing with {@link AttributeKey}, since OSS' attributeMap doesn't enforce types,
     * and specifically for mission-critical player serialization (we don't care about runtime/gameplay errors.. yet)
     * do some hardcoded type checks so serialization never breaks and we don't lose players progress. Supplier is the concrete return type, will always be 100% correct matching the expected type of an attrib's values. defaultValue is an object, has no type checking, and can be totally wrong. The point of this method is to only use default if the type won't produce a CCE.
     *
     * @param defaultValue The offending generic -- no way to get runtime types from the <T> returned generic type..
     *                     so no way to compare this class to T
     * @param type         Enforces the type check to avoid serialization exceptions
     * @param supplier     The correct return type.
     * @author Shadowrs/Jak
     * @since 06/06/2020
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAttribTypeOr(@NotNull Entity player, @NotNull AttributeKey key, @Nullable Object defaultValue, @NotNull Class<T> type, @NotNull Supplier<T> supplier) {
        Preconditions.checkArgument(Arrays.stream(DISALLOWED).noneMatch(p -> type == p), "You cannot use type %s", type.getName());
        if (!player.hasAttrib(key)) {
            if (defaultValue == null && !type.isPrimitive() || defaultValue != null && defaultValue.getClass() == type) {
                return (T) defaultValue;
            } else {
                String msg = String.format("CRITICAL ERROR: wrong fallback Type associated with AttributeKey %s (expected: %s, but got: %s) when saving Player: %s. Using fallback value %s",
                        key, type, defaultValue == null ? "null" : defaultValue.getClass(), player.getMobName(), supplier.get());
                return supplier.get();
            }
        }
        @Nullable Object stored = player.getAttrib(key);
        if (stored == null && !type.isPrimitive() || stored != null && stored.getClass() == type) {
            return (T) stored;
        } else {
            String msg = String.format("CRITICAL ERROR: wrong stored Type associated with AttributeKey %s (expected: %s, but got: %s) when saving Player: %s. Data loss possible. Replacing value '%s' with %s",
                    key, type, stored == null ? "null" : stored.getClass(), player.getMobName(),
                    stored, supplier.get());
            return supplier.get();
        }
    }

    public abstract void onAdd();

    public abstract void onRemove();

    public String getMobName() {
        if (isNPC()) {
            return (asNPC().getDefinition().getName() != null ? asNPC().getDefinition().getName() : "N/A");
        } else {
            return asPlayer().getDisplayName();
        }
    }

    public void performTask(Runnable action) {
        action.run();
    }

    @Getter
    private final EntityQueue entityQueue = new EntityQueue(this);
}
