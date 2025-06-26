package io.kyros.model.entity.npc;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.cache.definitions.NpcDefinition;
import io.kyros.content.bosses.ChaoticClone;
import io.kyros.content.bosses.Skotizo;
import io.kyros.content.bosses.araxxor.AraxxorBoss;
import io.kyros.content.bosses.araxxor.Araxyte;
import io.kyros.content.bosses.wildypursuit.FragmentOfSeren;
import io.kyros.content.combat.CombatHit;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.content.combat.npc.NPCCombatAttackHit;
import io.kyros.content.minigames.arbograve.ArbograveConstants;
import io.kyros.content.minigames.pest_control.PestControl;
import io.kyros.content.minigames.raids.Raids;
import io.kyros.content.skills.Skill;
import io.kyros.model.*;
import io.kyros.model.collisionmap.PathChecker;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.definitions.NpcStats;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.EntityProperties;
import io.kyros.model.entity.HealthBar;
import io.kyros.model.entity.HealthStatus;
import io.kyros.model.entity.npc.actions.NPCHitPlayer;
import io.kyros.model.entity.npc.data.BlockAnimation;
import io.kyros.model.entity.npc.data.DeathAnimation;
import io.kyros.model.entity.npc.stats.NpcBonus;
import io.kyros.model.entity.npc.stats.NpcCombatDefinition;
import io.kyros.model.entity.npc.stats.NpcCombatSkill;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.EquipmentSet;
import io.kyros.util.Location3D;
import io.kyros.util.Misc;
import io.kyros.util.Stream;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.kyros.Server.getNpcs;

public class NPC extends Entity {

	public List<Player> localPlayers = new ArrayList<>();

	private static final Logger logger = LoggerFactory.getLogger(NPC.class);
	public int parentIndex;

	public NPCAction[] actions;
	private int npcId;

	public boolean isGodmode;

	public int summonedBy;
	public int absX, absY;
	public int heightLevel;
	private boolean unregister;

	public boolean isPet;
	public boolean isThrall;

	@Getter
    @Setter
    private String phrase;

    public int makeX;
	public int makeY;
	public int maxHit;
	public Direction walkDirection = Direction.NONE;
	public Direction runDirection = Direction.NONE;
	public int walkingType;

	@Getter @Setter
	public int multiAttackDistance;

	public int lastX, lastY;
	public boolean summoner;
	public boolean ThrallSummoner;
	public boolean teleporting;

	public int lastForcedChat = 0;

	public int parentNpc = -1;
	public List<Integer> children;

	public long lastRandomlySelectedPlayer = 0;

	private boolean transformUpdateRequired;
	public int transformId;
	public Location3D targetedLocation;

	/**
	 * attackType: 0 = melee, 1 = range, 2 = mage
	 */
	public long lastSpecialAttack;

	public boolean spawnedMinions;

	private CombatType attackType;

	public int projectileId, endGfx, spawnedBy, hitDelayTimer, hitDiff, actionTimer;
	public boolean applyDead;
	public boolean needRespawn;
	public boolean walkingHome, underAttack;
	private int playerAttackingIndex;
	private int npcAttackingIndex;
	public int killedBy;
	public int oldIndex;
	public int underAttackBy;
	public long lastDamageTaken;
	public boolean randomWalk;
	public boolean faceEntityUpdateRequired;
	public boolean hitUpdateRequired;
	public boolean modelOverrideRequired;
	public boolean forcedChatRequired;
	private boolean forceMovementUpdateRequired;
	private ForceMovement forceMovement;
	public String forcedText;
	public int FocusPointX = -1, FocusPointY = -1;
	public int face;
	public int totalAttacks;
	private boolean facePlayer = true;
	private int projectileDelay;

	private NPCBehaviour behaviour = new NPCBehaviour();
	private final NpcDef definition;

	private long lastRandomWalk;
	private long lastRandomWalkHome;

	private long randomWalkDelay;
	private long randomStopDelay;

	private NpcStats defaultNpcStats;
	private NpcStats npcStats;
	private boolean Retaliate = true;

	private List<NPCAutoAttack> npcAutoAttacks = Lists.newArrayList();
	private NPCAutoAttack currentAttack;

	private NpcCombatDefinition npcCombatDefinition;


	public Boundary spawnBounds;

	public NPC(int npcId, Position position) {
		super();
		setNpcId(npcId);
		this.definition = NpcDef.forId(npcId);
		Preconditions.checkState(definition != null, "NPCDefinition cannot be null!");
		absX = makeX = position.getX();
		absY = makeY = position.getY();
		heightLevel = position.getHeight();
		setup();
		register();
		clearUpdateFlags();
		fetchDefaultNpcStats();
		setNpcCombatDefinition();
	}

	public NPC(int npcId, NpcDef definition, NpcStats defaultNpcStats) {
		super();
		this.definition = definition;
		setNpcId(npcId);
		walkDirection = Direction.NONE;
		runDirection = Direction.NONE;
		setup();
		setDefaultNpcStats(defaultNpcStats);
		setNpcCombatDefinition();
		if(health.getMaximumHealth() < 225) {
			healthBar = new HealthBar(this, 0);
		} else if(health.getMaximumHealth() < 500) {
			healthBar = new HealthBar(this, 3);
		} else {
			healthBar = new HealthBar(this, 6);
		}
	}

	public NPC(int npcId, NpcDef definition) {
		super();
		this.definition = definition;
		setNpcId(npcId);
		walkDirection = Direction.NONE;
		runDirection = Direction.NONE;
		setup();
		fetchDefaultNpcStats();
		setNpcCombatDefinition();
	}

	private void setup() {
		setDead(false);
		applyDead = false;
		actionTimer = 0;
		randomWalk = true;
		if (definition.isRunnable()) {
			getBehaviour().setRunnable(true);
		}
	}

	public SquareArea getAttackBounds() {
		return attackBounds;
	}

	public void setAttackBounds(SquareArea attackBounds) {
		this.attackBounds = attackBounds;
	}

	private SquareArea attackBounds = null;



	public void setNpcCombatDefinition() {
		NpcCombatDefinition definition = NpcCombatDefinition.definitions.get(this.npcId);
		if (definition == null) {
			this.npcCombatDefinition = new NpcCombatDefinition(this);
		} else {
			this.npcCombatDefinition = new NpcCombatDefinition(definition);
		}
	}

	public NPC provideRespawnInstance() {
		return null;
	}

	private void fetchDefaultNpcStats() {
		if (definition.getCombatLevel() > 0 || NpcStats.forId(npcId).getHitpoints() > 0) {
			setDefaultNpcStats(NpcStats.forId(npcId));
		} else {
			setDefaultNpcStats(NpcStats.builder().setAttackSpeed(4).createNpcStats());
		}
	}

	public void setDefaultNpcStats(NpcStats defaultNpcStats) {
		this.npcStats = defaultNpcStats;
		this.defaultNpcStats = defaultNpcStats;
		//this.defence = defaultNpcStats.getDefenceLevel();
		getHealth().setMaximumHealth(getNpcStats().getHitpoints());
		getHealth().reset();
	}

	public void setNpcStats(NpcStats npcStats) {
		this.npcStats = npcStats;
	}

	public boolean canBeAttacked(Entity entity) {
		return true;
	}

	public boolean canBeDamaged(Entity entity) {
		return true;
	}

	@Override
	public boolean isAutoRetaliate() {
		return Retaliate;
	}

	public void setAutoRetaliate(boolean bool) {
		Retaliate = bool;
	}
	public void onDeath() {}

	public void afterDeath() {}

	public int getDeathAnimation() {
		return DeathAnimation.handleEmote(getNpcId());
	}

	public int modifyDamage(Player player, int damage) {
		return damage;
	}

	public int getAttackDistanceModifier(Player player, CombatType combatType) {
		return 0;
	}

	@Override
	public boolean hasBlockAnimation() {
		return Arrays.stream(PestControl.PORTAL_DATA).noneMatch(data -> data[0] == getNpcId()) && getNpcId() != 2042 && getNpcId() != 2043 & getNpcId() != 2044
				&& getNpcId() != 3127 && getNpcId() != 319 && getNpcId() != 8_359 && getNpcId() != 12783 && getNpcId() != 12821;
	}

	@Override
	public Animation getBlockAnimation() {
		return new Animation(BlockAnimation.getAnimation(getNpcId()));
	}

	@Override
	public void attackEntity(Entity entity) {
		if (entity.isPlayer()) {
			setPlayerAttackingIndex(entity.getIndex());
		}
	}

	@Override
	public boolean isNPC() {
		return true;
	}

	@Override
	public String toString() {
		return "NPC{" +
				"npcId=" + npcId +
				", absX=" + absX +
				", absY=" + absY +
				", spawnX=" + makeX +
				", spawnY=" + makeY +
				", name='" + getName() + '\'' +
				", index=" + getIndex() +
				", instance=" + getInstance() +
				'}';
	}

	@Override
	public int getX() {
		return absX;
	}
	@Override
	public int getY() {
		return absY;
	}
	@Override
	public void setX(int x) {
		this.absX = x;
	}

	@Override
	public void setY(int y) {
		this.absY = y;
	}

	@Override
	public int getHeight() {
		return heightLevel;
	}

	@Override
	public void setHeight(int height) {
		this.heightLevel = height;
	}

	@Override
	public int getDefenceLevel() {
		return this.npcCombatDefinition.getLevel(NpcCombatSkill.DEFENCE);
	}

	@Override
	public int getDefenceBonus(CombatType type, Entity attacker) {
		// WARNING: the returned value can't be negative for magic combat formula
		// and other combat formulas if they ever get corrected to osrs
		if (type.equals(CombatType.MELEE)) {
			switch (getNpcId()) {
				case 965://kalphite queen
					return attacker.isPlayer() ? (EquipmentSet.VERAC.isWearing(attacker.asPlayer()) ? +500 : 5000) : 5000;
				case 9021://hunllef melee
				case 9022://hunllef range
				case 9023://hunllef mage
					return 100;
				case 6297:
					return 6503;
			}
		} else if (type.equals(CombatType.MAGE)) {
			switch (getNpcId()) {
				//case 2042://green zulrah
				//	return -150;
				case 319://corp
					return +80;
				//case 2044://blue zulrah
				//	return 1550;
				case 963://kalhpite queen
					return +7000;
				case 965://kalhpite queen part 2
				case 5890://abyssal sire
					return 100;
				case 6297:
					return 6503;
				case 7817:
					return +2500;
			}
		} else if (type.equals(CombatType.RANGE)) {
			switch (getNpcId()) {
				case 2042://green zulrah
				case 2043://red zulrah
				case 5890://abyssale sire
					return 0;
				case 2044://blue zulrah
					return -150;
				case 963://kalphite queen
					return +7000;
				case 965://kalphite queen part 2
					return 300;
				case 6297:
					return 6503;

			}
		}

		switch (type) {
			case MELEE:
				if (attacker.isPlayer()) {
					switch (attacker.asPlayer().getCombatConfigs().getWeaponMode().getCombatStyle()) {
						case STAB:
							return getNpcStats().getStabDef();
						case SLASH:
							return getNpcStats().getSlashDef();
						case CRUSH:
							return getNpcStats().getCrushDef();
					}
				}
				return getNpcStats().getSlashDef();
			case RANGE:
				return getNpcStats().getRangeDef();
			case MAGE:
				return getNpcStats().getMagicDef();
		}

		return 0;
	}

	@Override
	public void removeFromInstance() {
		if (getInstance() != null) {
			getInstance().remove(this);
		}
	}

	@Override
	public int getEntitySize() {
		return getSize();
	}

	public void register() {
		Preconditions.checkState(getIndex() == 0, "Already registered!");
		int index = Server.npcHandler.register(this);
		Preconditions.checkState(index != -1, "Cannot register npc!");
		this.getRegionProvider().addNpcClipping(this);
	}

	/**
	 * You should avoid using this unless you need instant deregistration.
	 * If deregistration deffered to the next npc process with due, you should simply
	 * call {@link NPC#unregister}. Calling this will cause issues, especially
	 * if you're calling from inside the npc process, as that will still complete
	 * after the npc has been unregistered.
	 */
	public void unregisterInstant() {
		unregister();
		processDeregistration();
	}

	/**
	 * Set the NPC to unregister on the next process.
	 * If you need instant deregistration call this and {@link NPC#processDeregistration()}.
	 */
	public void unregister() {
		setUnregister(true);
		this.getRegionProvider().removeNpcClipping(this);
	}

	/**
	 * Called by the internal npc processing to finish deregistration of an NPC.
	 * This is to prevent npcs from being unregistered while they are still processing.
	 * You shouldn't call this method to remove an npc, use {@link NPC#unregister()}.
	 */
	public boolean processDeregistration() {
		synchronized (this) {
			if (isUnregister() && getIndex() > 0) {
				// Synchronize on the CycleEventHandler instance or the event collection
				synchronized (CycleEventHandler.getSingleton()) {
					CycleEventHandler.getSingleton().stopEvents(this);
				}
				synchronized (Server.getNpcs()) {
					Server.getNpcs().remove(this);
				}
				if (getInstance() != null) {
					getInstance().remove(this);
				}
				logger.debug("Unregistered {}", this);
				return true;
			}
		}
		return false;
	}

	public void resetAttack() {
		setPlayerAttackingIndex(0);
		facePlayer(0);
		underAttack = false;
		randomWalk = true;
	}

	public void addChild(NPC npc) {
		Preconditions.checkState(npc.parentNpc == -1);
		npc.parentNpc = getIndex();
		if (children == null) {
			children = Lists.newArrayList(npc.getIndex());
		} else {
			children.add(npc.getIndex());
		}
	}

	public NPC getParent() {
		if (parentNpc != -1) {
			return getNpcs().get(parentNpc);
		} else {
			return null;
		}
	}

	public List<NPC> getChildren() {
		List<NPC> childrenNpcs = Lists.newArrayList();
		if (children != null) {
			children.forEach(child -> {
				NPC npc = getNpcs().get(child);
				Objects.requireNonNull(npc);
				childrenNpcs.add(npc);
			});
		}
		return childrenNpcs;
	}

	public void process() {
		Server.npcHandler.getNpcProcess().process(getIndex());
	}

	public void selectAutoAttack(Entity entity) {
		Preconditions.checkState(!npcAutoAttacks.isEmpty(), "No auto attacks present!");
		List<NPCAutoAttack> viable = npcAutoAttacks.stream().filter(autoAttack -> autoAttack.getSelectAutoAttack() == null
				|| autoAttack.getSelectAutoAttack().apply(new NPCCombatAttack(this, entity))).collect(Collectors.toList());
		Preconditions.checkState(!viable.isEmpty(), "No viable attacks can be found, npc: " + toString());
		currentAttack = viable.get(Misc.trueRand(viable.size()));
	}

	public void attack(Player c, NPCAutoAttack npcAutoAttack) {
		if (lastX != getX() || lastY != getY()) {
			return;
		} else if (c.isInvisible()  || isDead() || c.respawnTimer > 0) {
			return;
		} else if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		} else if (!getPosition().inMulti() && underAttackBy > 0 && underAttackBy != c.getIndex()) {
			resetAttack();
			return;
		} else if (!getPosition().inMulti() && ((c.underAttackByPlayer > 0 && c.underAttackByNpc != getIndex())
				|| (c.underAttackByNpc > 0 && c.underAttackByNpc != getIndex()))) {
			resetAttack();
			return;
		} else if (heightLevel != c.heightLevel) {
			resetAttack();
			return;
		}

		facePlayer(c.getIndex());

		double distanceRequired = npcAutoAttack.getDistanceRequiredForAttack();

		if (distanceRequired <= 1) {
			if(attackType == CombatType.MAGE)
				distanceRequired = 7.5;
			if(attackType == CombatType.RANGE)
				distanceRequired = 6.5;
		}


		if (getDistance(c.getX(), c.getY()) > distanceRequired + (getSize() > 1 ? 0.5 : 0.0)) {
//			System.out.println("Returning distance is fucked ? " + getDistance(c.getX(), c.getY()));
			return;
		}

		/*if (getAttackType() == CombatType.MELEE) { // This fixes attacking throough walls
			if (!PathChecker.raycast(this, c, false)) {
				return;
			}
		}*/
		if (NPCHandler.projectileClipping && !npcAutoAttack.isIgnoreProjectileClipping()) {
			if (getAttackType() == null || getAttackType() == CombatType.MAGE || getAttackType() == CombatType.RANGE) {
				int x1 = absX;
				int y1 = absY;
				int z = heightLevel;
				/*if (!PathChecker.isProjectilePathClear(this, c, x1, y1, z, c.absX, c.absY)
						&& !PathChecker.isProjectilePathClear(this, c, c.absX, c.absY, z, x1, y1)) {
					return;
				}*/
				if (!PathChecker.raycast(this, c, true)
						&& !PathChecker.raycast(c, this, true)) {
//					System.out.println("returning attack raycasting is fucked ?");
					return;
				}
			}
		}

		attackTimer = npcAutoAttack.getAttackDelay();
		setAttackType(npcAutoAttack.getCombatType());
		oldIndex = c.getIndex();

		if (npcAutoAttack.getAnimation() != null)
			startAnimation(npcAutoAttack.getAnimation());
		if (npcAutoAttack.getStartGraphic() != null)
			startGraphic(npcAutoAttack.getStartGraphic());

		if (npcAutoAttack.isMultiAttack()) {
			Preconditions.checkState(npcAutoAttack.getSelectPlayersForMultiAttack() != null, "You must define a NPCAutoAttack#selectPlayersForMultiAttack");
			for (Player player : npcAutoAttack.getSelectPlayersForMultiAttack().apply(new NPCCombatAttack(this, c))) {
				finishAutoAttack(player, npcAutoAttack);
			}
		} else {
			finishAutoAttack(c, npcAutoAttack);
		}
	}

	public void attack(NPC c, NPCAutoAttack npcAutoAttack) {
		if (c.isGodmode) {
			System.out.println("Return attack godmode");
			return;
		} else if (!getPosition().inMulti() && underAttackBy > 0 && underAttackBy != c.getIndex()) {
			resetAttack();
			System.out.println("Return attack non multi #1");
			return;
		} else if (!getPosition().inMulti()) {
			resetAttack();
			System.out.println("Return attack non multi #2");
			return;
		} else if (heightLevel != c.heightLevel) {
			resetAttack();
			System.out.println("Return attack Height");
			return;
		}

		faceNPC(c.getIndex());

		double distanceRequired = npcAutoAttack.getDistanceRequiredForAttack();

		if(attackType == CombatType.MAGE)
			distanceRequired = 7.5;
		if(attackType == CombatType.RANGE)
			distanceRequired = 6.5;


		if (getDistance(c.getX(), c.getY()) > distanceRequired + (getSize() > 1 ? 0.5 : 0.0)) {
			return;
		}

		if (NPCHandler.projectileClipping && !npcAutoAttack.isIgnoreProjectileClipping()) {
			if (getAttackType() == null || getAttackType() == CombatType.MAGE || getAttackType() == CombatType.RANGE) {
				if (!PathChecker.raycast(this, c, true)
						&& !PathChecker.raycast(c, this, true))
					return;
			}
		}

		attackTimer = npcAutoAttack.getAttackDelay();
		setAttackType(npcAutoAttack.getCombatType());
//		oldIndex = c.getIndex();

		if (npcAutoAttack.getAnimation() != null)
			startAnimation(npcAutoAttack.getAnimation());
		if (npcAutoAttack.getStartGraphic() != null)
			startGraphic(npcAutoAttack.getStartGraphic());

		finishAutoAttack(c, npcAutoAttack);
	}
	private void finishAutoAttack(NPC c, NPCAutoAttack npcAutoAttack) {
		NPCCombatAttack npcCombatAttack = new NPCCombatAttack(this, c);

		if (npcAutoAttack.getOnAttack() != null)
			npcAutoAttack.getOnAttack().accept(npcCombatAttack);
		if (npcAutoAttack.getProjectile() != null)
			npcAutoAttack.getProjectile().createTargetedProjectile(this, c).send(getInstance());

		Preconditions.checkState(npcAutoAttack.getHitDelay() > 0, "Hit delay is zero!");
		final NPC npc = this;
		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				CombatHit hit = NPCHitPlayer.applyAutoAttackDamage(npc, c, npcAutoAttack);
				if (npcAutoAttack.getOnHit() != null)
					npcAutoAttack.getOnHit().accept(NPCCombatAttackHit.of(npcCombatAttack, hit));
				container.stop();
			}
		}, npcAutoAttack.getHitDelay());
	}
	private void finishAutoAttack(Player c, NPCAutoAttack npcAutoAttack) {
		c.underAttackByNpc = getIndex();
		c.singleCombatDelay2 = System.currentTimeMillis();
//		c.getPA().removeAllWindows();

		NPCCombatAttack npcCombatAttack = new NPCCombatAttack(this, c);

		if (npcAutoAttack.getOnAttack() != null)
			npcAutoAttack.getOnAttack().accept(npcCombatAttack);
		if (npcAutoAttack.getProjectile() != null)
			npcAutoAttack.getProjectile().createTargetedProjectile(this, c).send(getInstance());

		if (getAttackType() == CombatType.DRAGON_FIRE) {
			hitDelayTimer += 2;
			c.getCombatItems().absorbDragonfireDamage();
		}

		Preconditions.checkState(npcAutoAttack.getHitDelay() > 0, "Hit delay is zero!");
		final NPC npc = this;
		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				CombatHit hit = NPCHitPlayer.applyAutoAttackDamage(npc, c, npcAutoAttack);
				if (npcAutoAttack.getOnHit() != null)
					npcAutoAttack.getOnHit().accept(NPCCombatAttackHit.of(npcCombatAttack, hit));
				container.stop();
			}
		}, npcAutoAttack.getHitDelay());
	}

	public Position getFollowPosition() {
		switch (getNpcId()) {
			case Skotizo.AWAKENED_ALTAR_EAST:
				return new Position(1713, 9888);
			case Skotizo.AWAKENED_ALTAR_NORTH:
				return new Position(1694, 9903);
			case Skotizo.AWAKENED_ALTAR_SOUTH:
				return new Position(1696, 9872);
			case Skotizo.AWAKENED_ALTAR_WEST:
				return new Position(1679, 9888);
		}
		return null;
	}

	public Position getPosition() {
		return new Position(absX, absY, heightLevel);
	}

	public Position getCenterPosition() {
		Position position = getPosition();
		Position position2 = getPosition().translate(getSize() - 1, getSize() - 1);
		return new Position((position.getX() + position2.getX()) / 2, (position.getY() + position2.getY()) / 2, position.getHeight());
	}

	public void startAnimation(int animationId) {
		startAnimation(new Animation(animationId));
	}

	public void startAnimation(int animationId, AnimationPriority animationPriority) {
		startAnimation(new Animation(animationId, 0, animationPriority));
	}

	public void sendForceMovement(ForceMovement forceMovement) {
		this.forceMovement = forceMovement;
		forceMovementUpdateRequired = true;
		setUpdateRequired(true);
	}

	/**
	 * Teleport
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	public void teleport(int x, int y, int z) {
		teleport(new Position(x, y, z));
	}

	public void teleport(Position position) {
		teleporting = true;
		setUpdateRequired(true);

		if (!this.isPet && !this.isThrall) {
			this.getRegionProvider().removeNpcClipping(this);
		}

		absX = position.getX();
		absY = position.getY();
		heightLevel = position.getHeight();

		if (!this.isPet && !this.isThrall) {
			this.getRegionProvider().addNpcClipping(this);
		}
	}

	/**
	 * Makes the npcs either able or unable to face other players
	 *
	 * @param facePlayer
	 *            {@code true} if the npc can face players
	 */
	public void setFacePlayer(boolean facePlayer) {
		this.facePlayer = facePlayer;
	}

	/**
	 * Sends the request to a client that the npc should be transformed into
	 * another.
	 */
	public void requestTransform(int id) {
		if (npcId != id) {
			transformId = id;
			setNpcId(id);
			transformUpdateRequired = true;
			setUpdateRequired(true);
			this.setNpcCombatDefinition();
		} else if(!entityProperties.isEmpty()) {
			transformId = id;
			transformUpdateRequired = true;
			setUpdateRequired(true);
		}
	}

	public void appendTransformUpdate(Stream str) {
		str.writeByte(isInvisible() ? 1 : 0);

		str.writeByte(entityProperties.size());
		if(!entityProperties.isEmpty()) {
			for (EntityProperties properties : entityProperties) {
				str.writeByte(properties.ordinal());
			}
		}
		str.writeWordBigEndianA(transformId);
	}

	public void updateNPCMovement(Stream str) {
		if (walkDirection == Direction.NONE) {
			if (isUpdateRequired()) {
				str.writeBits(1, 1);
				str.writeBits(2, 0);
			} else {
				str.writeBits(1, 0);
			}
		} else {
			str.writeBits(1, 1);

			if (runDirection == Direction.NONE) {
				str.writeBits(2, 1);
				str.writeBits(3, walkDirection.toInteger());
			} else {
				str.writeBits(2, 2);
				str.writeBits(3, walkDirection.toInteger());
				str.writeBits(3, runDirection.toInteger());
			}

			if (isUpdateRequired()) {
				str.writeBits(1, 1);
			} else {
				str.writeBits(1, 0);
			}
		}
	}

	/**
	 * Text update
	 **/

	public void forceChat(String text) {
		forcedText = text;
		forcedChatRequired = true;
		setUpdateRequired(true);
	}

	public void appendMask80Update(Stream str) {
		str.writeShort(graphics.size());
		Iterator<Graphic> iterator = graphics.iterator();
		while(iterator.hasNext()) {
			Graphic graphicObject = iterator.next();
			str.writeShort(graphicObject.getId());
			str.writeInt(graphicObject.getHeight() + (graphicObject.getDelay() & 0xffff));
			iterator.remove();
		}
		graphics.clear();
	}

	public void gfx100(int gfx) {
		startGraphic(new Graphic(gfx, 0, 6553600));
	}
	public void gfx100(int gfx, int height) {
		startGraphic(new Graphic(gfx, 0, 65536 * height));
	}
	public void gfx0(int gfx) {
		startGraphic(new Graphic(gfx, 0, 0));
	}

	@Override
	public boolean isFreezable() {
		switch (getNpcId()) {
			case 2042:
			case 2043:
			case 2044:
			case 7544:
			case 12818:
			case 12817:
			case 5129:
			case FragmentOfSeren.NPC_ID:
			case 2205:
			case 3129:
			case 2215:
			case 3162:
				return false;
		}
		return true;
	}

	public void appendAnimUpdate(Stream str) {
		str.writeWordBigEndian(getAnimation().getId());
		str.writeByte(getAnimation().getDelay());
	}

	private void appendSetFocusDestination(Stream str) {
		str.writeWordBigEndian(FocusPointX);
		str.writeWordBigEndian(FocusPointY);
	}

	public void facePosition(Position position) {
		facePosition(position.getX(), position.getY());
	}

	public void facePosition(int x, int y) {
		FocusPointX = 2 * x + 1;
		FocusPointY = 2 * y + 1;
		setUpdateRequired(true);
	}

	public void appendFaceEntity(Stream str) {
		str.writeShort(face);
	}

	public void facePlayer(int player) {
		if (getNpcId() == Npcs.MAX_DUMMY) {
			return;
		}

		if (!facePlayer) {
			if (face == -1) {
				return;
			}
			face = -1;
		} else {
			face = player + 32768;
		}
		faceEntityUpdateRequired = true;
		setUpdateRequired(true);
	}

	public void faceNPC(int index) {
		face = index;
		faceEntityUpdateRequired = true;
		setUpdateRequired(true);
	}

	public void appendForcedMovementUpdate(Player player, Stream str) {
		str.writeByte(forceMovement.getStart(player).getX());
		str.writeByte(forceMovement.getStart(player).getY());
		str.writeByte(forceMovement.getEnd(player).getX());
		str.writeByte(forceMovement.getEnd(player).getY());
		str.writeShort(forceMovement.getMoveCycleStart());
		str.writeShort(forceMovement.getMoveCycleEnd());
		str.writeByte(forceMovement.getMoveDirection());
	}
	private NpcOverrides npcOverrides;
	public void setModelOverride(NpcOverrides npcOverrides) {
		this.npcOverrides = npcOverrides;
		modelOverrideRequired = true;
		setUpdateRequired(true);
	}

	public void appendNPCUpdateBlock(Player player, Stream str) {
		if (!isUpdateRequired())
			return;
		int updateMask = 0;
		if(modelOverrideRequired)
			updateMask |= 0x4000;
		if (forceMovementUpdateRequired)
			updateMask |= 0x100;
		if (isAnimationUpdateRequired())
			updateMask |= 0x10;
		if (isGfxUpdateRequired())
			updateMask |= 0x80;
		if (faceEntityUpdateRequired)
			updateMask |= 0x20;
		if (forcedChatRequired)
			updateMask |= 1;
		if (hitUpdateRequired)
			updateMask |= 0x40;
		if (transformUpdateRequired)
			updateMask |= 2;
		if (FocusPointX != -1)
			updateMask |= 4;

		str.writeShort(updateMask);

		if(modelOverrideRequired)
			appendModelOverride(str);
		if (forceMovementUpdateRequired)
			appendForcedMovementUpdate(player, str);
		if (isAnimationUpdateRequired())
			appendAnimUpdate(str);
		if (isGfxUpdateRequired())
			appendMask80Update(str);
		if (faceEntityUpdateRequired)
			appendFaceEntity(str);
		if (forcedChatRequired) {
			str.writeString(forcedText);
		}
		if (hitUpdateRequired)
			appendHitUpdate(player, str);
		if (transformUpdateRequired) {
			appendTransformUpdate(str);
		}
		if (FocusPointX != -1)
			appendSetFocusDestination(str);
	}

	public void appendModelOverride(Stream stream) {
		int flags = 0;
		if (this.npcOverrides == null) {
			flags |= 1;
		} else {
			if(npcOverrides.modelIds != null) {
				flags |= 2;
			}
			if(npcOverrides.recolorTo != null) {
				flags |= 4;
			}
			if(npcOverrides.retextureTo != null) {
				flags |= 8;
			}
			flags |= 16;
		}
		stream.writeUnsignedByte(flags);
//		System.out.println("Flagged [" + flags + "]");


		if(npcOverrides != null) {
			if(npcOverrides.modelIds != null) {
				stream.writeUnsignedByte(npcOverrides.modelIds.length);
				for(int modelId : npcOverrides.modelIds) {
					stream.writeShort(modelId);
				}
			}
			if(npcOverrides.recolorTo != null) {
//				System.out.println("Flagged recolor");
				for(int recolor : npcOverrides.recolorTo) {
					stream.writeShort(recolor);
				}
			}
			if(npcOverrides.retextureTo != null) {
				for(int retexture : npcOverrides.retextureTo) {
					stream.writeShort(retexture);
				}
			}
			stream.writeUnsignedByte(npcOverrides.useLocalPlayer ? 1 : 0);
		}
	}


	public void clearUpdateFlags() {
		setUpdateRequired(false);
		forcedChatRequired = false;
		hitUpdateRequired = false;
		hitUpdateRequired2 = false;
		faceEntityUpdateRequired = false;
		if(entityProperties.isEmpty())
			transformUpdateRequired = false;
		forcedText = null;
		modelOverrideRequired = false;
		walkDirection = Direction.NONE;
		runDirection = Direction.NONE;
		FocusPointX = -1;
		FocusPointY = -1;
		teleporting = false;
		forceMovementUpdateRequired = false;
		resetAfterUpdate();
	}

	@Override
	public void resetWalkingQueue() {
		setMovement(Direction.NONE);
	}

	public void setMovement(Direction walkDirection) {
		setMovement(walkDirection, Direction.NONE);
	}

	public void setMovement(Direction walkDirection, Direction runDirection) {
		setWalkDirection(walkDirection);
		setRunDirection(runDirection);
	}

	public void moveTowards(int x, int y) {
		moveTowards(x, y, false, false);
	}

	public void moveTowards(int x, int y, boolean checkClipping) {
		moveTowards(x, y, false, checkClipping);
	}

	public void moveTowards(int x, int y, boolean run, boolean checkClipping) {
		resetWalkingQueue();
		Position moveTowards = new Position(x, y, heightLevel);
		Direction dir1 = Direction.fromDeltas(getPosition(), moveTowards);
		if (!checkClipping || NPCDumbPathFinder.canMoveTo(this, getPosition(), dir1)) {
			setWalkDirection(dir1);
		} else {
			resetWalkingQueue();
			return;
		}

		Position walked = new Position(absX + walkDirection.x(), absY + walkDirection.y());

		if (walked.equals(moveTowards)) {
			return;
		}

		if ((behaviour.isRunnable() || run) && (!checkClipping || NPCDumbPathFinder.canMoveTo(this, walked, runDirection))) {
			setRunDirection(Direction.fromDeltas(walked, moveTowards));
		}
	}

	public boolean revokeWalkingPrivilege;

	public void processMovement() {
		if (revokeWalkingPrivilege)
			return;
		if (walkDirection != Direction.NONE) {
			if (freezeTimer != 0 || (forceMovement != null && !forceMovement.isActive()) || teleporting) {
				resetWalkingQueue();
			} else {
				if (!this.isPet && !this.isThrall)
					this.getRegionProvider().removeNpcClipping(this);

				absX += walkDirection.x() + runDirection.x();
				absY += walkDirection.y() + runDirection.y();

				if (!this.isPet && !this.isThrall)
					this.getRegionProvider().addNpcClipping(this);

				setUpdateRequired(true);
			}
		} else {
			Preconditions.checkState(runDirection == Direction.NONE, "How can you walk before you run?");
		}
		//dir >>= 1; // TODO is this needed, commented out when adding npc running?
	}

	@Override
	public void appendHitUpdate(Player player, Stream str) {
		if (getHealth().getCurrentHealth() <= 0) {
			setDead(true);
		}
		HitMask hitMask = HitMask.Companion.get(hitMask1);
		if(hitMask != null) {
			if(hitMask.getId() != hitMask.getMax()) {
				if(hitMask.getTinted() != -1) {
					if(player.getIndex() != playerHitIndex) {
						hitMask1 = hitMask.getTinted();
					}
				}
			}
		}
		hitMask = HitMask.Companion.get(hitMask2);
		if(hitMask != null) {
			if(hitMask.getId() != hitMask.getMax()) {
				if(hitMask.getTinted() != -1) {
					if(player.getIndex() != playerHitIndex2) {
						hitMask2 = hitMask.getTinted();
					}
				}
			}
		}
		str.writeInt(hitDiff);
		str.writeByte(hitMask1);
		str.writeInt(hitDiff2);
		str.writeByte(hitMask2);
		str.writeShort(0);//delay
		boolean sendHitIndex = playerHitIndex >= 0 && playerHitIndex < 2048;
		str.writeInt(sendHitIndex ? playerHitIndex + 32_768 : 10_000);
		str.writeInt(health.getCurrentHealth());
		str.writeInt(health.getMaximumHealth());
		healthBar.update(str);
	}

	public int hitDiff2;
	public boolean hitUpdateRequired2;


	public void lowerDefence(double percent) {
		Preconditions.checkArgument(percent > 0 && percent <= 1, "Percent out of bounds.");
		int defence = getDefence();
		int loweredDefence = (int) (defence * (1.0 - percent));
		if (loweredDefence < 0) {
			loweredDefence = 0;
		}
		this.npcCombatDefinition.setLevel(NpcCombatSkill.DEFENCE, loweredDefence);
	}

	public void increaseDefence(int newDefence) {
		this.npcCombatDefinition.setLevel(NpcCombatSkill.DEFENCE, newDefence);
	}

	public int getDefence() {
		return this.npcCombatDefinition.getLevel(NpcCombatSkill.DEFENCE);
	}

	public boolean inRaids() {
		return (getX() > 3210 && getX() < 3368 && getY() > 5137 && getY() < 5759);
	}

	public boolean inXeric() {
		return (getX() > 3217 && getX() < 3247 && getY() > 4817 && getY() < 4851);
	}

	public int getSize() {
		if (definition == null)
			return 1;

		if (Boundary.isIn(this, ChaoticClone.boundary)) {
			return 1;
		}

		return definition.getSize();
	}



	/**
	 * An object containing specific information about the NPC such as the combat
	 * level, default maximum health, the name, etcetera.
	 *
	 * @return the {@link NpcDef} object associated with this NPC
	 */
	public NpcDef getDefinition() {
		return definition;
	}

	public String getName() {
		return getDefinition().getName();
	}

	public int getProjectileDelay() {
		return projectileDelay;
	}

	public void setProjectileDelay(int delay) {
		projectileDelay = delay;
	}

	@Override
	public void appendHeal(int amount, HitMask hitMask) {
		getHealth().increase(amount);
		if (!hitUpdateRequired) {
			hitUpdateRequired = true;
			hitDiff = amount;
			hitMask1 = hitMask.getId();
		}
		setUpdateRequired(true);
	}
	private static final List<Integer> avoidOneHit = List.of();

	@Override
	public void appendDamage(Entity entity, int damage, HitMask hitMask) {
		if (entity != null && entity.isPlayer()) {
			Player player = (Player) entity;
			if(player.getCurrentPet().findPetPerk("p2w_boosted").isHit()) {
				for(Skill combatSkill : Skill.getCombatSkills()) {
					if (combatSkill.getId() != 3 && combatSkill.getId() != 5) {
						if(player.playerLevel[combatSkill.getId()] < player.getLevelForXP(player.playerXP[combatSkill.getId()])) {
							player.playerLevel[combatSkill.getId()] = player.getLevelForXP(player.playerXP[combatSkill.getId()]);
						} else {
							if (player.playerLevel[combatSkill.getId()] < 135) {
								player.playerLevel[combatSkill.getId()] = Math.min(player.playerLevel[combatSkill.getId()] + 1, player.playerLevel[combatSkill.getId()] + 20);
							}
						}
					}
					player.getPA().refreshSkill(combatSkill.getId());
				}
			}
			/*if(player.getCurrentPet().findPetPerk("common_one_shot").isHit()) {
				if (npcId != 8096 && npcId != 5126 && npcId != 4923 && npcId != 5169 && npcId != 9425 && npcId != 319 && !Boundary.isIn(entity, ArbograveConstants.ALL_BOUNDARIES)) {
					damage = getHealth().getCurrentHealth();
				}
			}
			if(player.getCurrentPet().hasPerk("common_dmg_boost") && player.getCurrentPet().findPetPerk("common_dmg_boost").isHit()) {
				damage += (int) ((damage / 100) * player.getCurrentPet().findPetPerk("common_dmg_boost").getValue());
			}
			if(player.getCurrentPet().hasPerk("rare_kree'ara's_revenge") && player.getCurrentPet().findPetPerk("rare_kree'ara's_revenge").isHit() ) {
				damage += (int) ((damage / 100) * player.getCurrentPet().findPetPerk("rare_kree'ara's_revenge").getValue());
			}*/
			if (player.playerEquipment[Player.playerWeapon] != 84 && player.playerEquipment[Player.playerWeapon] != 33446) {
				// Mirrorback Araxyte Reflection Logic
/*				if (this instanceof Araxyte && ((Araxyte) this).getType() == Araxyte.AraxyteType.MIRRORBACK) {
					if (damage >= 50) {
						damage = 50;
					}
					int damageToReflect = (int) (damage * 0.5);
					player.appendDamage(damageToReflect, HitMask.HIT);
					player.sendMessage("The Mirrorback Araxyte reflects some of your damage back to you!",
							TimeUnit.MINUTES.toMillis(10));
				}*/

				if (this instanceof AraxxorBoss araxxor) {
					if (araxxor.getInstance() != null && !araxxor.getInstance().getNpcs().isEmpty()) {
						// Check if any Mirrorback Araxyte is alive in the instance
						boolean mirrorbackExists = araxxor.getInstance().getNpcs().stream()
								.anyMatch(npc -> npc instanceof Araxyte && ((Araxyte) npc).getType() == Araxyte.AraxyteType.MIRRORBACK && !npc.isDead());

						if (mirrorbackExists) {
							// Reflect damage if Mirrorback Araxyte is present
							if (damage >= 50) {
								damage = 50;  // Cap the reflected damage
							}
							int damageToReflect = (int) (damage * 0.5);  // Reflect 50% of the damage back to the player
							player.appendDamage(damageToReflect, HitMask.HIT);
							player.sendMessage("The Mirrorback Araxyte reflects some of your damage back to you!",
									TimeUnit.MINUTES.toMillis(10));
						}
					}
				}
			}
			damage = modifyDamage(player, damage);
			if (!canBeDamaged(player)) {
				damage = 0;
			}

			if (damage > 0 && hitMask == HitMask.MISS) {
				hitMask = HitMask.HIT;
			}

			addDamageTaken(player, damage);

			if (player.getRaidsInstance() != null && Boundary.isIn(player, Boundary.FULL_RAIDS)) {
				Raids.damage(player, damage);
			}
			playerHitIndex = player.getIndex();
		}

		if (damage <= 0) {
			damage = 0;
			hitMask = HitMask.MISS;
		}
		if (getHealth().getCurrentHealth() - damage < 0) {
			damage = getHealth().getCurrentHealth();
		}
		if (getNpcId() != Npcs.MAX_DUMMY) {
			getHealth().reduce(damage);
			if (getHealth().getCurrentHealth() <= 0) {
				setDead(true);
			}
		}
		if (!hitUpdateRequired) {
			if(entity != null && entity.isPlayer()) {
				playerHitIndex = entity.asPlayer().getIndex();
			}
			hitUpdateRequired = true;
			hitDiff = damage;
			hitMask1 = hitMask.getId();
		} else if (hitUpdateRequired && !hitUpdateRequired2) {
			if(entity != null && entity.isPlayer()) {
				playerHitIndex2 = entity.asPlayer().getIndex();
			}
			hitUpdateRequired2 = true;
			hitDiff2 = damage;
			hitMask2 = hitMask.getId();
		}
		setUpdateRequired(true);
	}

	@Override
	public boolean susceptibleTo(HealthStatus status) {
		switch (getNpcId()) {
			case 2042:
			case 2043:
			case 2044:
			case 6720:
			case 7413:
			case 7544:
			case 5129:
			case FragmentOfSeren.NPC_ID:
			case 7604:
			case 7605:
			case 7606:
				return false;
		}

		if (status == HealthStatus.POISON) {
			return !defaultNpcStats.isPoisonImmune();
		} else if (status == HealthStatus.VENOM) {
			return !defaultNpcStats.isVenomImmune();
		}

		return true;
	}

	public long getLastRandomWalk() {
		return lastRandomWalk;
	}

	public void setLastRandomWalk(long lastRandomWalk) {
		this.lastRandomWalk = lastRandomWalk;
	}

	public long getRandomWalkDelay() {
		return randomWalkDelay;
	}

	public void setRandomWalkDelay(long randomWalkDelay) {
		this.randomWalkDelay = randomWalkDelay;
	}

	public int getNpcId() {
		return npcId;
	}

	public void setNpcId(int npcId) {
		this.npcId = npcId;
	}

	public NPCAutoAttack getCurrentAttack() {
		return currentAttack;
	}
	public void setCurrentAttack(NPCAutoAttack attack) {
		currentAttack = attack;
	}

	public List<NPCAutoAttack> getNpcAutoAttacks() {
		return Collections.unmodifiableList(npcAutoAttacks);
	}

	public void setNpcAutoAttacks(List<NPCAutoAttack> npcAutoAttacks) {
		this.npcAutoAttacks = npcAutoAttacks;
	}

	public boolean isDeadOrDying() {
		return isDead() || needRespawn || getHealth().getCurrentHealth() == 0;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean dead) {
		isDead = dead;
	}

	public NPCBehaviour getBehaviour() {
		return behaviour;
	}

	public NpcStats getDefaultNpcStats() {
		return defaultNpcStats;
	}

	public NpcStats getNpcStats() {
		return npcStats;
	}

	public CombatType getAttackType() {
		return attackType;
	}

	public void setAttackType(CombatType attackType) {
		this.attackType = attackType;
	}

	public int getPlayerAttackingIndex() {
		return playerAttackingIndex;
	}

	public boolean isAggro() {
		return playerAttackingIndex > 1 || npcAttackingIndex > 1;
	}

	public void setPlayerAttackingIndex(int playerAttackingIndex) {
		if (this.playerAttackingIndex != playerAttackingIndex) {
			this.playerAttackingIndex = playerAttackingIndex;
		}
	}

	public int getNpcAttackingIndex() {
		return npcAttackingIndex;
	}

	public void setNpcAttackingIndex(int npcAttackingIndex) {
		if (this.npcAttackingIndex != npcAttackingIndex) {
			this.npcAttackingIndex = npcAttackingIndex;
		}
	}

	public Direction getWalkDirection() {
		return walkDirection;
	}

	public void setWalkDirection(Direction walkDirection) {
		this.walkDirection = walkDirection;
	}

	public Direction getRunDirection() {
		return runDirection;
	}

	public void setRunDirection(Direction runDirection) {
		this.runDirection = runDirection;
	}

	public boolean isUnregister() {
		return unregister;
	}

	public void setUnregister(boolean unregister) {
		this.unregister = unregister;
	}

	public NpcCombatDefinition getCombatDefinition() {
		return this.npcCombatDefinition;
	}

	public boolean isDemon() {
		return Misc.linearSearch(Configuration.DEMON_IDS, this.npcId) != -1;
	}

	public boolean isUndead() {
		return Misc.linearSearch(Configuration.UNDEAD_NPCS, this.npcId) != -1;
	}

	public boolean isDragon() {
		return Misc.linearSearch(Configuration.DRAG_IDS, this.npcId) != -1;
	}

	public boolean isLeafy() {
		return Misc.linearSearch(Configuration.LEAF_IDS, this.npcId) != -1;
	}

	@Override
	public int getBonus(Bonus bonus) {
		NpcCombatDefinition definition = this.getCombatDefinition();
		if (definition == null) {
			return 0;
		}

		switch (bonus) {
			case ATTACK_STAB:
			case ATTACK_SLASH:
			case ATTACK_CRUSH:
				return definition.getAttackBonus(NpcBonus.ATTACK_BONUS);
			case ATTACK_MAGIC:
				return definition.getAttackBonus(NpcBonus.MAGIC_BONUS);
			case ATTACK_RANGED:
				return definition.getAttackBonus(NpcBonus.RANGE_BONUS);

			case DEFENCE_STAB:
				return definition.getDefenceBonus(NpcBonus.STAB_BONUS);
			case DEFENCE_SLASH:
				return definition.getDefenceBonus(NpcBonus.SLASH_BONUS);
			case DEFENCE_CRUSH:
				return definition.getDefenceBonus(NpcBonus.CRUSH_BONUS);
			case DEFENCE_MAGIC:
				return definition.getDefenceBonus(NpcBonus.MAGIC_BONUS);
			case DEFENCE_RANGED:
				return definition.getDefenceBonus(NpcBonus.RANGE_BONUS);
			case MAGIC_DMG:
				return definition.getAttackBonus(NpcBonus.MAGIC_STRENGTH_BONUS);
		}

		return 0;
	}

	@Override
	public void onAdd() {

	}

	@Override
	public void onRemove() {

	}
	private NpcDefinition npcDefinition = null;
	public NpcDefinition def() {
		if(npcDefinition == null)
			npcDefinition = Server.definitionRepository.get(NpcDefinition.class, npcId);
		return npcDefinition;
	}

	public int getAttackDistanceMulti() {
		return (getMultiAttackDistance() > 0 ? getMultiAttackDistance() : 15);
	}
}
