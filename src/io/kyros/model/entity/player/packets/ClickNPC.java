package io.kyros.model.entity.player.packets;

import io.kyros.Server;
import io.kyros.content.WeaponGames.WGManager;
import io.kyros.content.bosses.Tempoross;
import io.kyros.content.bosses.Vorkath;
import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.content.combat.magic.CombatSpellData;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.seasons.Christmas;
import io.kyros.content.skills.Minigame;
import io.kyros.content.skills.Skill;
import io.kyros.content.skills.hunter.impling.Impling;
import io.kyros.content.staff_skilling_bots.SkillingBots;
import io.kyros.content.staff_skilling_bots.StaffBot;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;
import io.kyros.script.event.impl.NpcOptionOne;
import io.kyros.util.discord.DiscordIntegration;

import static io.kyros.Server.getNpcs;
import static io.kyros.Server.playerHandler;

/**
 * Click NPC
 */
public class ClickNPC implements PacketType {

	public static final int ATTACK_NPC = 72,
			MAGE_NPC = 131,
			FIRST_CLICK = 155,
			SECOND_CLICK = 17,
			THIRD_CLICK = 21,
			FOURTH_CLICK = 18;

	@Override
	public void processPacket(final Player c, int packetType, int packetSize) {
		if (c.getMovementState().isLocked() || c.getLock().cannotInteract(c))
			return;
		if (c.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		c.interruptActions();
		c.npcAttackingIndex = 0;
		c.npcClickIndex = 0;
		c.playerAttackingIndex = 0;
		c.clickNpcType = 0;
		c.getPA().resetFollow();

		if (c.isForceMovementActive() || c.teleTimer > 0) {
			return;
		}

		if (c.getDisplayName().equalsIgnoreCase("luke")) {
			System.out.println("PacketType = " + packetType);
		}

		switch (packetType) {
		/**
		 * Attack npc melee or range
		 **/
		case ATTACK_NPC:
			if (c.morphed) {
				return;
			}

			if (c.playerEquipment[Player.playerWeapon] == 22547 || c.playerEquipment[Player.playerWeapon] == 22542 || c.playerEquipment[Player.playerWeapon] == 22552  ) {
				c.stopMovement();
				break;
			}

			int index = c.getInStream().readUnsignedWordA();

			NPC npc = getNpcs().get(index);
			if (npc == null) {
				c.stopMovement();
				return;
			}

			if (Minigame.handleSkillingNPC(c, npc)) {
				return;
			}

			c.stopMovement();
			c.npcAttackingIndex = index;
			c.usingClickCast = false;


			if (npc.getHealth().getMaximumHealth() == 0) {
				c.attacking.reset();
				c.getPA().resetHealthHud();
				return;
			}


			c.faceEntity(npc);

			if (c.getBankPin().requiresUnlock()) {
				c.attacking.reset();
				c.getBankPin().open(2);
				return;
			}

			if (c.attacking.attackEntityCheck(npc, true)) {
				c.attackEntity(npc);
			} else {
				c.attacking.reset();
			}
			break;

		/**
		 * Attack npc with magic
		 **/
		case MAGE_NPC:
			if (c.morphed) {
				return;
			}

			c.stopMovement();
			c.npcAttackingIndex = c.getInStream().readSignedWordBigEndianA();
			int castingSpellId = c.getInStream().readSignedWordA();
			c.usingClickCast = false;
			npc = getNpcs().get(c.npcAttackingIndex);

			if (npc == null) {
				c.attacking.reset();
				break;
			}

			if (Minigame.handleSkillingNPC(c, npc)) {
				return;
			}

			c.faceEntity(npc);

			if (npc.getHealth().getMaximumHealth() == 0) {
				c.sendMessage("You can't attack this npc.");
				c.attacking.reset();
				break;
			}

			for (int i = 0; i < CombatSpellData.MAGIC_SPELLS.length; i++) {
				if (castingSpellId == CombatSpellData.MAGIC_SPELLS[i][0]) {
					if (c.attacking.attackEntityCheck(npc, true)) {
						c.setSpellId(i);
						c.usingClickCast = true;
						c.attackEntity(npc);
					} else {
						c.attacking.reset();
					}
					break;
				}
			}
			break;

		case FIRST_CLICK:
			int npcIndex = c.inStream.readSignedWordBigEndian();
            NPC n = getNpcs().get(npcIndex);

			if (npcIndex >= Server.getNpcs().size() || npcIndex < 0 || n == null)
				return;

			if (Impling.isImp(n.getNpcId())) {
				if (c.lastImpling > System.currentTimeMillis()) {
					return;
				}
			}

			if (Minigame.handleSkillingNPC(c, n)) {
				return;
			};

			//Server.pluginManager.triggerEvent(new NpcOptionOne(n, c));

			if (c.debugMessage) {
				c.sendMessage("[DEBUG] NPC Option #1-> Click index: " + npcIndex + ", NPC Id: " + c.npcType);
			}



			if (Christmas.handleCandies(c, n.getNpcId()) && Christmas.isChristmas()) {
				return;
			}

			if (c.getInterfaceEvent().isActive()) {
				c.sendMessage("Please finish what you're doing.");
				return;
			}

			if (Hespori.clickNpc(c, n.getNpcId())) {
				return;
			}

			if (n.getNpcId() == 7316) {
				WGManager.getSingleton().join(c);
				return;
			}

			if (n.getNpcId() == 9715) {
				if (c.playerXP[Skill.DEMON_HUNTER.getId()] < 200_000_000) {
					c.start(new DialogueBuilder(c).npc(9715, "You need 200m Demon hunter", "before you can exchange your lamps!"));
					return;
				}
				int dreamy = c.getItems().getInventoryCount(11157);
				dreamy = (int) (5*(Math.floor(Math.abs(dreamy/5))));
				if (dreamy < 5) {
					c.start(new DialogueBuilder(c).npc(9715, "You don't have enough dreamy lamps to exchange."));
					return;
				}

				int finalDreamy = dreamy;
				c.start(new DialogueBuilder(c).option("Exchange " + dreamy + " Dreamylamps for " + (dreamy/5) + " xp lamps.",
						new DialogueOption("Yes", p -> {
							p.getItems().addItem(2528, (finalDreamy / 5));
							p.getItems().deleteItem2(11157, finalDreamy);
							p.getPA().closeAllWindows();
						}), new DialogueOption("No", p -> p.getPA().closeAllWindows())));
				return;
			}

			if (n.getNpcId() == 10571) {
				if (Tempoross.active) {
					Tempoross.handleFishing(c);
					return;
				}
				return;
			}
			if (n.getNpcId() == 6070) {
				c.rangingGuild.handleDialogue();
				return;
			}

			c.npcClickIndex = npcIndex;
			c.faceUpdate(npcIndex);
			c.stopMovement();
			c.npcType = n.getNpcId();
			c.getPA().followNPC(n, false);

			if(SkillingBots.onClick(c, n, 1))
				return;

			if (n.getNpcId() == 8523) {
				c.stopMovement();
				c.faceUpdate(0);
				c.getPA().resetFollow();
				if (c.playerEquipment[Player.playerWeapon] == 22817) {
					c.getActions().firstClickNpc(n);
				} else {
					c.sendMessage("You need a bird to reach this fishing spot.");
				}
				return;
			}

			if (n.getNpcId() == 9855) {
				c.stopMovement();
				c.faceUpdate(0);
				c.getPA().resetFollow();
				c.getPA().playerWalk(2272, 4061);
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						c.getDeathInterface().drawInterface(false);
							container.stop();
					}
					@Override
					public void onStopped() {
						c.clickNpcType = 0;
						c.npcType = 0;
					}
				}, 1);
				return;
			}

			if (n.getNpcId() == 12256) {
				c.getShops().openShop(597);
				return;
			}

			if (n.getNpcId() == 7099) {
				c.getShops().openShop(193);
				return;
			}

			if (n.getNpcId() == 3599) {
				c.getShops().openShop(199);
				return;
			}

			if (n.getNpcId() == 10621) {
				c.getPA().resetFollow();
				c.getPA().playerWalk(n.getX(), n.getY());
				c.getActions().firstClickNpc(n);
				return;
			}

			if (n.getNpcId() == 8059) {
				c.stopMovement();
				c.faceUpdate(0);
				c.getPA().resetFollow();
				c.getPA().playerWalk(2272, 4061);
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (c.absX == 2272 && c.absY == 4061) {
							c.facePosition(2272, 4062);
							c.startAnimation(827);
							Vorkath.poke(c, n);
							container.stop();
						}
					}
					@Override
					public void onStopped() {
						c.clickNpcType = 0;
					}
				}, 1);
				return;
			}


			if (n.distance(c.getPosition()) <= 1) {
				c.facePosition(n.getX(), n.getY());
				n.facePlayer(c.getIndex());
				c.faceUpdate(0);
				c.getPA().resetFollow();
				c.getActions().firstClickNpc(n);
			} else {
				c.clickNpcType = 1;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if ((c.clickNpcType == 1) && n != null) {
							if (n.distance(c.getPosition()) <= (Boundary.isIn(c, Boundary.STAFF_ZONE) ? 3 : 1)) {
								c.facePosition(n.getX(), n.getY());
								n.facePlayer(c.getIndex());
								c.faceUpdate(0);
								c.getPA().resetFollow();
								c.getActions().firstClickNpc(n);
								container.stop();
							}
						}
						if (c.clickNpcType == 0 || c.clickNpcType > 1)
							container.stop();
					}

					@Override
					public void onStopped() {
						c.clickNpcType = 0;
					}
				}, 1);
			}
			break;

		case SECOND_CLICK:
			npcIndex = c.inStream.readUnsignedWordBigEndianA();
			if (getNpcs().get(npcIndex) == null) {
				break;
			}
			c.npcClickIndex = npcIndex;
			c.faceUpdate(c.npcClickIndex);
			if (c.debugMessage) {
				c.sendMessage("[DEBUG] NPC Option #2-> Click index: " + c.npcClickIndex + ", NPC Id: " + c.npcType);
			}

			if (getNpcs().get(c.npcClickIndex) != null) {
				c.npcType = getNpcs().get(c.npcClickIndex).getNpcId();
				c.getPA().followNPC(getNpcs().get(c.npcClickIndex), false);
			} else {
				return;
			}
			if(SkillingBots.onClick(c, getNpcs().get(npcIndex), 1))
				return;

			if (c.npcType == 1013) {
				c.start(new DialogueBuilder(c).option("You have " + c.getFortuneSpins() + " spins remaining!.",
						new DialogueOption("Quick-Spin! ( 50 spins at a time maximum! )", plr -> {
							if (c.getFortuneSpins() <= 0) {
								plr.start(new DialogueBuilder(plr).statement("You don't have any spins available!"));
								return;
							}
							plr.getPA().sendEnterAmount("How many spins would you like to use?", (plr1, amount) -> {
								if (plr1.getFortuneSpins() < amount) {
									plr1.sendMessage("You don't have enough spins to spin that many times!");
									return;
								}
								if (amount > 1000) {
									amount = 1000;
								}
								plr1.getWheelOfFortune().quickSpin(amount);
								plr1.getPA().closeAllWindows();
							});
						}), new DialogueOption("Never-mind.", plr -> plr.getPA().closeAllWindows())));
				return;
			}

			if (c.npcType == 2309) {
				DiscordIntegration.updateDiscordInterface(c);
				c.getPA().showInterface(37500);
				return;
			}

			if (c.npcType == 539) {
				c.getShops().openShop(196);
				return;
			}

			if (c.npcType == 7316) {
				c.getShops().openShop(189);
				return;
			}


			if (c.npcType == 9855) {
				c.stopMovement();
				c.faceUpdate(0);
				c.getPA().resetFollow();
				c.getPA().playerWalk(2272, 4061);
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						c.getDeathInterface().drawInterface(false);
						container.stop();

					}
					@Override
					public void onStopped() {
						c.clickNpcType = 0;
						c.npcType = 0;
					}
				}, 1);
				return;
			}
			if (getNpcs().get(c.npcClickIndex).distance(c.getPosition()) <= 1) {
				c.facePosition(getNpcs().get(c.npcClickIndex).getX(), getNpcs().get(c.npcClickIndex).getY());
                getNpcs().get(c.npcClickIndex).facePlayer(c.getIndex());
				if (c.npcType == 6637 || c.npcType == 6638) {
					PetHandler.metamorphosis(c, c.npcType);
				}
				c.faceUpdate(0);
				c.getPA().resetFollow();
				c.getActions().secondClickNpc(getNpcs().get(c.npcClickIndex));
			} else {
				c.clickNpcType = 2;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if ((c.clickNpcType == 2) && getNpcs().get(c.npcClickIndex) != null) {
							if (getNpcs().get(c.npcClickIndex).distance(c.getPosition()) <= 1) {
								c.facePosition(getNpcs().get(c.npcClickIndex).getX(),
                                        getNpcs().get(c.npcClickIndex).getY());
                                getNpcs().get(c.npcClickIndex).facePlayer(c.getIndex());
								c.faceUpdate(0);
								c.getPA().resetFollow();
								c.getActions().secondClickNpc(getNpcs().get(c.npcClickIndex));
								container.stop();
							}
						}
						if (c.clickNpcType < 2 || c.clickNpcType > 2)
							container.stop();
					}

					@Override
					public void onStopped() {
						c.clickNpcType = 0;
					}
				}, 1);
			}

			break;

		case THIRD_CLICK:
			npcIndex = c.inStream.readSignedWord();
			if (getNpcs().get(npcIndex) == null) {
				break;
			}
			c.npcClickIndex = npcIndex;
			c.faceUpdate(c.npcClickIndex);
			if (c.debugMessage) {
				c.sendMessage("[DEBUG] NPC Option #3-> Click index: " + c.npcClickIndex + ", NPC Id: " + c.npcType);
			}
			if (getNpcs().get(c.npcClickIndex) != null) {
				c.npcType = getNpcs().get(c.npcClickIndex).getNpcId();
				c.getPA().followNPC(getNpcs().get(c.npcClickIndex), false);
			} else {
				return;
			}
			if (getNpcs().get(c.npcClickIndex).distance(c.getPosition()) <= 1) {
				c.facePosition(getNpcs().get(c.npcClickIndex).getX(), getNpcs().get(c.npcClickIndex).getY());
				if (c.npcType == 2130 || c.npcType == 2131 || c.npcType == 2132 || c.npcType == 8492
						|| c.npcType == 8493 || c.npcType == 8494 || c.npcType == 8495
						|| c.npcType >= 7354 && c.npcType <= 7367 || c.npcType == 5559 || c.npcType == 5560
						|| c.npcType == 326 || c.npcType == 327 || c.npcType == 8737 || c.npcType == 8738
						|| c.npcType == 7674 || c.npcType == 8009 || c.npcType == 388 || c.npcType == 8010
						|| c.npcType == 762 || c.npcType == 763 || c.npcType == 30023) {
					PetHandler.metamorphosis(c, c.npcType);
					
				}
                getNpcs().get(c.npcClickIndex).facePlayer(c.getIndex());
				c.faceUpdate(0);
				c.getPA().resetFollow();
				c.getActions().thirdClickNpc(getNpcs().get(c.npcClickIndex));
			} else {
				c.clickNpcType = 3;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if ((c.clickNpcType == 3) && getNpcs().get(c.npcClickIndex) != null) {
							if (getNpcs().get(c.npcClickIndex).distance(c.getPosition()) <= 1) {
								c.facePosition(getNpcs().get(c.npcClickIndex).getX(),
                                        getNpcs().get(c.npcClickIndex).getY());
                                getNpcs().get(c.npcClickIndex).facePlayer(c.getIndex());
								c.faceUpdate(0);
								c.getPA().resetFollow();
								c.getActions().thirdClickNpc(getNpcs().get(c.npcClickIndex));

								container.stop();
							}
						}
						if (c.clickNpcType < 3)
							container.stop();
					}

					@Override
					public void onStopped() {
						c.clickNpcType = 0;
					}
				}, 1);
			}

			break;

		case FOURTH_CLICK:
			npcIndex = c.inStream.readSignedWordBigEndian();
			if (getNpcs().get(npcIndex) == null) {
				break;
			}
			c.npcClickIndex = npcIndex;
			c.faceUpdate(c.npcClickIndex);
			if (c.debugMessage) {
				c.sendMessage("[DEBUG] NPC Option #4-> Click index: " + c.npcClickIndex + ", NPC Id: " + c.npcType);
			}
			if (c.getInterfaceEvent().isActive()) {
				c.sendMessage("Please finish what you're doing.");
				return;
			}

			if (getNpcs().get(c.npcClickIndex) != null) {
				c.npcType = getNpcs().get(c.npcClickIndex).getNpcId();
				c.getPA().followNPC(getNpcs().get(c.npcClickIndex), false);
			} else {
				return;
			}

			if (getNpcs().get(c.npcClickIndex).distance(c.getPosition()) <= 1) {
				c.facePosition(getNpcs().get(c.npcClickIndex).getX(), getNpcs().get(c.npcClickIndex).getY());
                getNpcs().get(c.npcClickIndex).facePlayer(c.getIndex());
				c.faceUpdate(0);
				c.getPA().resetFollow();
				c.getActions().fourthClickNpc(getNpcs().get(c.npcClickIndex));
			} else {
				c.clickNpcType = 4;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if ((c.clickNpcType == 4) && getNpcs().get(c.npcClickIndex) != null) {
							if (getNpcs().get(c.npcClickIndex).distance(c.getPosition()) <= 1) {
								c.facePosition(getNpcs().get(c.npcClickIndex).getX(),
                                        getNpcs().get(c.npcClickIndex).getY());
                                getNpcs().get(c.npcClickIndex).facePlayer(c.getIndex());
								c.faceUpdate(0);
								c.getPA().resetFollow();
								c.getActions().fourthClickNpc(getNpcs().get(c.npcClickIndex));
								container.stop();
							}
						}
						if (c.clickNpcType < 4)
							container.stop();
					}

					@Override
					public void onStopped() {
						c.clickNpcType = 0;
					}
				}, 1);
			}

			break;
		}

	}
}
