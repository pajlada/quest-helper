/*
 * Copyright (c) 2026, pajlada <rasmus.karlsson@pajlada.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.helpers.quests.thegraveyard;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.InInstanceRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.DigStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.questhelper.steps.TileStep;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;

/// Used the Wiki while making this guide! [https://oldschool.runescape.wiki/w/The_Graveyard](https://oldschool.runescape.wiki/w/The_Graveyard) Thanks Wiki :)
public class TheGraveyard extends BasicQuestHelper
{
	// Required items
	ItemRequirement sampleRequirement;
	ItemRequirement olafsParchment;

	// Recommended items
	ItemRequirement energyRestorePotion;

	// Mid-quest item requirements
	ItemRequirement itemAcquiredDuringQuest;

	// Zones
	Zone sampleZone;

	// Miscellaneous requirements
	VarbitRequirement hasFinishedCertainTask;

	// Steps
	DetailedQuestStep TODO;
	NpcStep talkToNpc;
	ObjectStep climbUpLadder;

	@Override
	protected void setupZones()
	{
		sampleZone = new Zone(new WorldPoint(10, 10, 0), new WorldPoint(20, 20, 0));
	}

	@Override
	protected void setupRequirements()
	{
		sampleRequirement = new ItemRequirement("Bucket", ItemID.BUCKET_EMPTY);
		var passedParchmentPoint = new VarbitRequirement(VarbitID.GRAVEYARD, 8, Operation.GREATER_EQUAL);
		olafsParchment = new ItemRequirement("Parchment", ItemID.OLAF2_ULFRIC_NOTE).alsoCheckBank().hideConditioned(passedParchmentPoint);

		energyRestorePotion = new ItemRequirement("Stamina potion", ItemCollections.STAMINA_POTIONS);

		itemAcquiredDuringQuest = new ItemRequirement("Book of Lore", ItemID.BOOK_OF_ASTROLOGY);

		hasFinishedCertainTask = new VarbitRequirement(VarbitID.PORT_TASK_SLOT_2_ID, 2);
	}

	void setupSteps()
	{
		TODO = new DetailedQuestStep(this, "BASE TODO");
		talkToNpc = new NpcStep(this, NpcID.OLAF, new WorldPoint(2724, 3729, 0), "Talk to Olaf Hradson near the entrance to Keldagrim to start the quest.", olafsParchment);
		talkToNpc.addDialogStep("Yes.");
		climbUpLadder = new ObjectStep(this, ObjectID.LADDER, "Climb up the ladder");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		for (var i = 0; i < 500; ++i) {
			var s = new DetailedQuestStep(this, "todo " + i);
			steps.put(i, s);
		}

		var spade = new ItemRequirement("Spade", ItemID.SPADE);

		var firstArea = new Zone(new WorldPoint(2689, 10116, 0), new WorldPoint(2707, 10141, 0));
		var firstArea2 = new Zone(new WorldPoint(2707, 10118, 0), new WorldPoint(2739, 10148, 0));
		var secondArea = new Zone(new WorldPoint(2691, 10143, 0), new WorldPoint(2706, 10170, 0));
		var secondArea2 = new Zone(new WorldPoint(2707, 10149, 0), new WorldPoint(2735, 10170, 0));
		var thirdArea = new Zone(new WorldPoint(2726, 10154, 0), new WorldPoint(2749, 10170, 0));

		var inFirstArea = new ZoneRequirement(firstArea, firstArea2);
		var inSecondArea = new ZoneRequirement(secondArea, secondArea2);
		var inThirdArea = new ZoneRequirement(thirdArea);

		var searchPainting = new ObjectStep(this, ObjectID.OLAF2_SKULL_PUZZLE_WALL, new WorldPoint(2707, 10147, 0), "Search the picture wall in the north room.");
		var openGate = new ObjectStep(this, ObjectID.OLAF2_RUSTY_GATE_PUZZLE, new WorldPoint(2725, 10168, 0), "Open the gate on the walkway.");
		var searchChest = new ObjectStep(this, ObjectID.OLAF2_CHEST_CLOSED, new WorldPoint(2740, 10164, 0), "Search the chest in the wreck for the parchment.");
		searchChest.addAlternateObjects(ObjectID.OLAF2_CHEST_OPEN);

		var digNextToWindsweptTree = DigStep.withCustomSpadeRequirement(this, new WorldPoint(2748, 3732, 0), "Dig next to the windswept tree near the entrance to Keldagrim and enter the Brine Rate Cavern.", spade);
		digNextToWindsweptTree.setWhenToHighlight(DigStep.WhenToHighlight.OnTile);

		var cGetParchment = new ConditionalStep(this, digNextToWindsweptTree, "Get the parchment!");
		cGetParchment.addStep(inThirdArea, searchChest);
		cGetParchment.addStep(inSecondArea, openGate);
		cGetParchment.addStep(inFirstArea, searchPainting);

		var cStartingOff = new ConditionalStep(this, cGetParchment);
		cStartingOff.addStep(olafsParchment, talkToNpc);
		steps.put(0, talkToNpc);

		steps.put(2, talkToNpc);

		steps.put(3, cStartingOff);

		steps.put(4, talkToNpc);

		// if talking to olaf without parchment, this varbit gets set
		// [2026-11-28T14:03:53Z 1010] varbit GRAVEYARD_RETRIEVE_PARCHMENT (16960) 0 -> 1

		// The parchment is necessary here
		var coins = new ItemRequirement("Coins", ItemID.COINS, 5000);
		var talkToVolf = new NpcStep(this, NpcID.OLAF_VOLF, new WorldPoint(2654, 3694, 0), "Talk to Volf Olafson in Relekka.", olafsParchment, coins);
		talkToVolf.addDialogStep("I don't think I have one that can, no."); // if paying to use Volf's boat
		talkToVolf.addDialogStep("Yes."); // if paying to use Volf's boat

		var cTalkToVolf = new ConditionalStep(this, cGetParchment);
		cTalkToVolf.addStep(olafsParchment, talkToVolf);

		steps.put(6, cTalkToVolf);

		steps.put(8, talkToVolf);
		steps.put(10, talkToVolf);
		steps.put(12, talkToVolf);
		// "I have one that should be able to." - bring your own boat
		// "I don't think I have one that can, no." - use Volf's boat
		// I, pajlada, will try to use Volf's boat for this first run through of the quest.


		var onBoat = new VarbitRequirement(VarbitID.SAILING_BOARDED_BOAT, 1);

		// If you agreed to use Volf's boat:
		// [2026-11-28T14:24:28Z 3068] varbit GRAVEYARD_VOLF_BOAT (16953) 0 -> 1
		// [2026-11-28T14:24:28Z 3068] varbit GRAVEYARD (16952) 12 -> 14
		// NEW SECTION: Sailing the seas?
		var talkToVolfAgain = new NpcStep(this, NpcID.OLAF_VOLF, new WorldPoint(2654, 3694, 0), "Talk to Volf Olafson in Relekka, ready to sail to Weissmere.");
		talkToVolfAgain.addDialogStep("Yes"); // when boarding the boat
		talkToVolfAgain.addDialogStep("Yes."); // when returning to the graveyard through the dialog
		steps.put(14, talkToVolfAgain);

		var anyAxe = new ItemRequirement("Any axe", ItemCollections.AXES);

		// after agreeing to hop on Volf's boat:
		// [2026-11-28T14:25:49Z 3203] varbit GRAVEYARD (16952) 14 -> 16
		// [2026-11-28T14:25:49Z 3203] varbit SAILING_BOAT_SPAWNED (19121) 3 -> 0
		var sailToWeissmere = new ObjectStep(this, ObjectID.SAILING_MOORING_DISEMBARK, new WorldPoint(2754, 3968, 0), "Sail to Weissmere. Avoid the Kraken!");
		var cSailToWeissmereWithVolf = new ConditionalStep(this, talkToVolfAgain);
		cSailToWeissmereWithVolf.addStep(onBoat, sailToWeissmere);
		steps.put(16, cSailToWeissmereWithVolf);

		var inWeissmereTopLevel = new ZoneRequirement(new Zone(11070));

		// 16 -> 17: just boarded weissmere for the first time with Volf's boat
		// [2026-11-28T14:35:41Z 4191] varbit SAILING_BOARDED_BOAT_LAST_MOORING_POINT (19147) 35 -> 61
		// [2026-11-28T14:35:42Z 4192] varbit GRAVEYARD (16952) 16 -> 17
		var getBronzeAxe = new ObjectStep(this, ObjectID.GY_STAGING_POI_1, new WorldPoint(2758, 3979, 0), "Get a bronze axe from the crate to the north.");
		getBronzeAxe.addDialogStep("Yes.");

		var chopProw = new ObjectStep(this, ObjectID.GY_STAGING_DOOR_BLOCKED, new WorldPoint(2762, 3970, 0), "Chop the prow to the east.", anyAxe);

		var needToUnblockProw = new VarbitRequirement(VarbitID.GRAVEYARD_STAGING_DOOR_UNBLOCKED, 0);

		var talkToVolfOnWeissmere1 = new NpcStep(this, NpcID.OLAF_VOLF, new WorldPoint(2758, 3975, 0), "Talk to Volf in Weissmere.");

		var cWeissmere1 = new ConditionalStep(this, cSailToWeissmereWithVolf);
		cWeissmere1.addStep(and(inWeissmereTopLevel, needToUnblockProw, not(anyAxe)), getBronzeAxe);
		cWeissmere1.addStep(and(inWeissmereTopLevel, needToUnblockProw), chopProw);
		cWeissmere1.addStep(and(inWeissmereTopLevel), talkToVolfOnWeissmere1);
		steps.put(17, cWeissmere1);

		// 17 -> 18 after talking to ovlf after landing in weissmere, you can talk to him in relekka and they'll talk as if htey're in weissmere still xD

		var captainsLogShut = new ItemRequirement("Captain's log", ItemID.GRAVEYARD_LOGBOOK_STORMBROW_SHUT);
		var climbOverProw = new ObjectStep(this, ObjectID.GY_STAGING_DOOR, new WorldPoint(2762, 3970, 0), "Pass through the prow.");
		var getCaptainsLogFromChest = new ObjectStep(this, ObjectID.GY_STAGING_LOGBOOK_CHEST_CLOSED, new WorldPoint(2766, 3970, 0), "Open the chest to the east.");
		var climbOverProw2 = new ObjectStep(this, ObjectID.GY_STAGING_DOOR, new WorldPoint(2762, 3970, 0), "Pass through the prow.");
		var talkToVolfOnWeissmere2 = new NpcStep(this, NpcID.OLAF_VOLF, new WorldPoint(2758, 3975, 0), "Give Volf the captain's log.", captainsLogShut);
		talkToVolfOnWeissmere2.addDialogStep("Talk about the Graveyard.");

		var weissmereStaging1 = ZoneRequirement.square(2752, 3994, 0, 2762, 3969, 0);

		var cWeissmere2 = new ConditionalStep(this, cSailToWeissmereWithVolf);
		cWeissmere2.addStep(and(inWeissmereTopLevel, captainsLogShut, weissmereStaging1), talkToVolfOnWeissmere2);
		cWeissmere2.addStep(and(inWeissmereTopLevel, captainsLogShut), climbOverProw2);

		cWeissmere2.addStep(and(inWeissmereTopLevel, needToUnblockProw, not(anyAxe)), getBronzeAxe);
		cWeissmere2.addStep(and(inWeissmereTopLevel, needToUnblockProw), chopProw);

		cWeissmere2.addStep(and(inWeissmereTopLevel, weissmereStaging1), climbOverProw);
		cWeissmere2.addStep(and(inWeissmereTopLevel), getCaptainsLogFromChest);

		steps.put(18, cWeissmere2);

		// 18 -> 19: handed book to volf

		var spyglass = new ItemRequirement("Spyglass", ItemID.SAILING_CHARTING_SPYGLASS);

		var talkToVolfOnWeissmere3 = new NpcStep(this, NpcID.OLAF_VOLF, new WorldPoint(2758, 3975, 0), "Talk to Volf on Weissmere about what to do next.");
		talkToVolfOnWeissmere3.addDialogStep("Talk about the Graveyard.");

		var climbTheMast = new ObjectStep(this, ObjectID.GY_STAGING_MAST_OP, new WorldPoint(2762, 3975, 0), "Climb the mast", spyglass);

		var cWeissmere3 = new ConditionalStep(this, cSailToWeissmereWithVolf);
		cWeissmere3.addStep(and(inWeissmereTopLevel, not(spyglass)), talkToVolfOnWeissmere3);
		cWeissmere3.addStep(and(inWeissmereTopLevel, spyglass), climbTheMast);

		steps.put(19, cWeissmere3);

		// 19 -> 20: after climbing the mast - skeletons are down there with volf!!!!


		var killSkeletonFremenniks = new NpcStep(this, new int[]{
			NpcID.GRAVEYARD_FREMENNIK_CAPTAINS_MINIONS_1,
			NpcID.GRAVEYARD_FREMENNIK_CAPTAINS_MINIONS_2,
			NpcID.GRAVEYARD_FREMENNIK_CAPTAINS_MINIONS_3,
			NpcID.GRAVEYARD_FREMENNIK_CAPTAINS_MINIONS_4,
			NpcID.GRAVEYARD_FREMENNIK_CAPTAINS_MINIONS_VOLF,
		}, new WorldPoint(2759, 3974, 0), "Kill the skeleton minions. Focus the rangers first. Protect from melee helps a lot.", true);

		var cWeissmere4 = new ConditionalStep(this, cSailToWeissmereWithVolf);
		var inInstance = new InInstanceRequirement();
		cWeissmere4.addStep(and(inWeissmereTopLevel, inInstance), killSkeletonFremenniks);
		cWeissmere4.addStep(and(inWeissmereTopLevel, not(spyglass)), talkToVolfOnWeissmere3);
		cWeissmere4.addStep(and(inWeissmereTopLevel, spyglass), climbTheMast);


		steps.put(20, cWeissmere4);

		// 20 -> 22 after finishing dialog with the skeletons, before the actual combat
		steps.put(22, cWeissmere4);
		steps.put(23, cWeissmere4);

		// 22 -> 23 after killing enough skeletons
		// TODO: can you break this cutscene?
		// 23 -> 26 after being teleported back

		var speakToBrundt = new NpcStep(this, NpcID.VIKING_BRUNDT, new WorldPoint(2659, 3670, 0), "Talk to Brundt the Chieftain in the longhall on Relekka.");
		speakToBrundt.addDialogStep("Ask about Weissmere.");
		steps.put(26, speakToBrundt);

		// 26 -> 28 after talking to Brundt and getting the logbook back
		var captainsLog = new ItemRequirement("Captain's log", ItemID.GRAVEYARD_LOGBOOK_STORMBROW);
		captainsLog.setTooltip("You can get another one from Brundt in Rellekka.");
		var captainsLogEnchanted = new ItemRequirement("Captain's log", ItemID.GRAVEYARD_LOGBOOK_STORMBROW_ENCHANTED).alsoCheckBank();
		captainsLogEnchanted.setTooltip("You can get an unenchanted version from Brundt in Rellekka, then get it enchanted by Fossegrimen");
		var readLogbook = new DetailedQuestStep(this, "Read the captain's log.", captainsLog.highlighted());
		readLogbook.addDialogStep("Ask about Weissmere.");
		steps.put(28, readLogbook);

		// 28 -> 30 after reading the captain's log
		var speakToBrundtAgain = new NpcStep(this, NpcID.VIKING_BRUNDT, new WorldPoint(2659, 3670, 0), "Talk to Brundt the Chieftain in the longhall on Relekka after reading the captain's log.", captainsLog);
		speakToBrundtAgain.addDialogStep("Ask about Weissmere.");
		steps.put(30, speakToBrundtAgain);

		// 30 -> 32: talked to brundt, he told us to talk to Fossegrimen.
		var talkToFossegrimen = new ObjectStep(this, ObjectID.VIKING_LAKE_SHRINE_ALTAR, new WorldPoint(2626, 3598, 0), "Talk to Fossegrimen about the binding stakes.", captainsLog);
		talkToFossegrimen.addDialogStep("So how do I stop Ræun?");
		talkToFossegrimen.addDialogStep("Can you enchant the logbook?");
		// talkToFossegrimen.addDialogStep("Ask about Weissmere.");
		steps.put(32, talkToFossegrimen);

		// 32 -> 34 after talking to fossegrimen and being told to subdue the spirit again
		steps.put(34, talkToFossegrimen);
		// 34 -> 36 after asking fossegrimen how to stop raeun and being informed about how to make the stakes

		var speakToBrundtAgainToGetANewCaptainsLog = new NpcStep(this, NpcID.VIKING_BRUNDT, new WorldPoint(2659, 3670, 0), "Talk to Brundt the Chieftain in the longhall on Relekka to get the captains log back.");
		speakToBrundtAgainToGetANewCaptainsLog.addDialogStep("Ask about Weissmere.");
		var talkToFossegrimenEnchantLog = new ObjectStep(this, ObjectID.VIKING_LAKE_SHRINE_ALTAR, new WorldPoint(2626, 3598, 0), "Ask Fossegrimen south of Rellekka to enchant your captain's log.", captainsLog);
		talkToFossegrimenEnchantLog.addDialogStep("Can you enchant the logbook?");
		var arcticPineLogs = new ItemRequirement("Arctic pine logs", ItemID.ARCTIC_PINE_LOG, 7);
		var arcticPineStake = new ItemRequirement("Arctic pine stake", ItemID.GRAVEYARD_BASIC_STAKE, 7);
		var arcticPineStakeInfused = new ItemRequirement("Infused arctic pine stake", ItemID.GRAVEYARD_INFUSED_STAKE, 7);
		var arcticPineStakeEnchanted = new ItemRequirement("Enchanted arctic pine stake", ItemID.GRAVEYARD_ENCHANTED_STAKE, 7).alsoCheckBank();
		var soulRunes = new ItemRequirement("Soul rune", ItemID.SOULRUNE, 21);
		var soulEssence = new ItemRequirement("Soul essence", ItemID.GRAVEYARD_SOUL_ESSENCE, 21);

		var chisel = new ItemRequirement("Chisel or hammer", ItemID.CHISEL);
		chisel.addAlternates(ItemID.HAMMER);

		var knife = new ItemRequirement("Knife", ItemID.KNIFE);

		var makeSoulEssence = new DetailedQuestStep(this, "Make soul essence by using your chisel or hammer on the soul runes. DON'T MAKE TOO MANY.", soulRunes.highlighted(), chisel.highlighted());

		var makeStakes = new DetailedQuestStep(this, "Make arctic pine stakes.", captainsLogEnchanted, arcticPineLogs.highlighted(), knife.highlighted());

		var xd = "Infuse the stakes with the soul essence at any astral except the Blood Altar, Soul Altar, or the Ourania Altar. I will guide you to the fire altar, but another altar is also fine.";

		var fireTalisman = new ItemRequirement("Fire talisman", ItemCollections.FIRE_ALTAR);
		var infuseStakes1 = new ObjectStep(this, ObjectID.FIRETEMPLE_RUINED_NEW, new WorldPoint(3312, 3254, 0), "Infuse the stakes with the soul essence at any astral except the Blood Altar, Soul Altar, or the Ourania Altar. I will guide you to the fire altar, but another altar is also fine.", captainsLogEnchanted, arcticPineStake.highlighted(), soulEssence, fireTalisman);
		var infuseStakes2 = new TileStep(this, new WorldPoint(2584, 4840, 0), xd, captainsLogEnchanted,    arcticPineStake.highlighted(), soulEssence, fireTalisman);
		var infuseStakes2_01 = new TileStep(this, new WorldPoint(2584, 4840, 0), xd, captainsLogEnchanted, arcticPineStake.quantity(6).highlighted(), soulEssence.quantity(18), fireTalisman);
		var infuseStakes2_02 = new TileStep(this, new WorldPoint(2584, 4840, 0), xd, captainsLogEnchanted, arcticPineStake.quantity(5).highlighted(), soulEssence.quantity(15), fireTalisman);
		var infuseStakes2_03 = new TileStep(this, new WorldPoint(2584, 4840, 0), xd, captainsLogEnchanted, arcticPineStake.quantity(4).highlighted(), soulEssence.quantity(12), fireTalisman);
		var infuseStakes2_04 = new TileStep(this, new WorldPoint(2584, 4840, 0), xd, captainsLogEnchanted, arcticPineStake.quantity(3).highlighted(), soulEssence.quantity(9) , fireTalisman);
		var infuseStakes2_05 = new TileStep(this, new WorldPoint(2584, 4840, 0), xd, captainsLogEnchanted, arcticPineStake.quantity(2).highlighted(), soulEssence.quantity(6) , fireTalisman);
		var infuseStakes2_06 = new TileStep(this, new WorldPoint(2584, 4840, 0), xd, captainsLogEnchanted, arcticPineStake.quantity(1).highlighted(), soulEssence.quantity(3) , fireTalisman);

		var enchantStakes = new DetailedQuestStep(this, "Enchant the stakes by clicking the infused arctic pine stakes. You will take some damage or be poisoned or other dangerous things. Keep food on you. :-).", captainsLogEnchanted, arcticPineStakeInfused.highlighted());
		enchantStakes.addDialogStep("Yes.");

		var enchantStakes_01 = new DetailedQuestStep(this, "Enchant the stakes by clicking the infused arctic pine stakes. You will take some damage or be poisoned or other dangerous things. Keep food on you. :-).", captainsLogEnchanted, arcticPineStakeInfused.quantity(6).highlighted());
		enchantStakes_01.addDialogStep("Yes.");
		var enchantStakes_02 = new DetailedQuestStep(this, "Enchant the stakes by clicking the infused arctic pine stakes. You will take some damage or be poisoned or other dangerous things. Keep food on you. :-).", captainsLogEnchanted, arcticPineStakeInfused.quantity(5).highlighted());
		enchantStakes_02.addDialogStep("Yes.");
		var enchantStakes_03 = new DetailedQuestStep(this, "Enchant the stakes by clicking the infused arctic pine stakes. You will take some damage or be poisoned or other dangerous things. Keep food on you. :-).", captainsLogEnchanted, arcticPineStakeInfused.quantity(4).highlighted());
		enchantStakes_03.addDialogStep("Yes.");
		var enchantStakes_04 = new DetailedQuestStep(this, "Enchant the stakes by clicking the infused arctic pine stakes. You will take some damage or be poisoned or other dangerous things. Keep food on you. :-).", captainsLogEnchanted, arcticPineStakeInfused.quantity(3).highlighted());
		enchantStakes_04.addDialogStep("Yes.");
		var enchantStakes_05 = new DetailedQuestStep(this, "Enchant the stakes by clicking the infused arctic pine stakes. You will take some damage or be poisoned or other dangerous things. Keep food on you. :-).", captainsLogEnchanted, arcticPineStakeInfused.quantity(2).highlighted());
		enchantStakes_05.addDialogStep("Yes.");
		var enchantStakes_06 = new DetailedQuestStep(this, "Enchant the stakes by clicking the infused arctic pine stakes. You will take some damage or be poisoned or other dangerous things. Keep food on you. :-).", captainsLogEnchanted, arcticPineStakeInfused.quantity(1).highlighted());
		enchantStakes_06.addDialogStep("Yes.");

		var inCutscene = new VarbitRequirement(VarbitID.CUTSCENE_STATUS, 1);

		var inFireAltarRegion = new ZoneRequirement(new Zone(10315));

		var talkToFossegrimenInCutscene = new DetailedQuestStep(this, "Talk to Fossegrimen in the cutscene.");

		var cMakeStakes = new ConditionalStep(this, speakToBrundtAgainToGetANewCaptainsLog);
		// NOTE: If a user makes one, infuses it, it can be a bit spaghetti. Assume user makes all stakes first. If a user deposits some stakes, it will be messed up. It should recover once the user has everything they need though

		cMakeStakes.addStep(inCutscene, talkToFossegrimenInCutscene);

		var talkToBrundtWithEnchantedStakes = new NpcStep(this, NpcID.VIKING_BRUNDT, new WorldPoint(2659, 3670, 0), "Talk to Brundt the Chieftain in the longhall on Relekka with the enchanted stakes.", arcticPineStakeEnchanted);
		talkToBrundtWithEnchantedStakes.addDialogStep("Ask about Weissmere.");
		talkToBrundtWithEnchantedStakes.addDialogStep("What do you mean?");

		var anyStakesEnchanted = new VarbitRequirement(VarbitID.GRAVEYARD_STAKES_ENCHANTED, 1, Operation.GREATER_EQUAL);

		cMakeStakes.addStep(arcticPineStakeEnchanted, talkToBrundtWithEnchantedStakes);

		cMakeStakes.addStep(and(captainsLogEnchanted, arcticPineStakeInfused), enchantStakes);
		cMakeStakes.addStep(and(anyStakesEnchanted, captainsLogEnchanted, arcticPineStakeInfused.quantity(6)), enchantStakes_01);
		cMakeStakes.addStep(and(anyStakesEnchanted, captainsLogEnchanted, arcticPineStakeInfused.quantity(5)), enchantStakes_02);
		cMakeStakes.addStep(and(anyStakesEnchanted, captainsLogEnchanted, arcticPineStakeInfused.quantity(4)), enchantStakes_03);
		cMakeStakes.addStep(and(anyStakesEnchanted, captainsLogEnchanted, arcticPineStakeInfused.quantity(3)), enchantStakes_04);
		cMakeStakes.addStep(and(anyStakesEnchanted, captainsLogEnchanted, arcticPineStakeInfused.quantity(2)), enchantStakes_05);
		cMakeStakes.addStep(and(anyStakesEnchanted, captainsLogEnchanted, arcticPineStakeInfused.quantity(1)), enchantStakes_06);


		cMakeStakes.addStep(and(captainsLogEnchanted, arcticPineStake, soulEssence, inFireAltarRegion), infuseStakes2);
		cMakeStakes.addStep(and(captainsLogEnchanted, arcticPineStake.quantity(6), soulEssence.quantity(18), inFireAltarRegion), infuseStakes2_01);
		cMakeStakes.addStep(and(captainsLogEnchanted, arcticPineStake.quantity(5), soulEssence.quantity(15), inFireAltarRegion), infuseStakes2_02);
		cMakeStakes.addStep(and(captainsLogEnchanted, arcticPineStake.quantity(4), soulEssence.quantity(12), inFireAltarRegion), infuseStakes2_03);
		cMakeStakes.addStep(and(captainsLogEnchanted, arcticPineStake.quantity(3), soulEssence.quantity(9) , inFireAltarRegion),  infuseStakes2_04);
		cMakeStakes.addStep(and(captainsLogEnchanted, arcticPineStake.quantity(2), soulEssence.quantity(6) , inFireAltarRegion),  infuseStakes2_05);
		cMakeStakes.addStep(and(captainsLogEnchanted, arcticPineStake.quantity(1), soulEssence.quantity(3) , inFireAltarRegion),  infuseStakes2_06);
		cMakeStakes.addStep(and(captainsLogEnchanted, arcticPineStake, soulEssence), infuseStakes1);



		cMakeStakes.addStep(and(captainsLogEnchanted, not(arcticPineStake)), makeStakes);
		cMakeStakes.addStep(and(captainsLogEnchanted, not(soulEssence)), makeSoulEssence);
		cMakeStakes.addStep(captainsLog, talkToFossegrimenEnchantLog);
		steps.put(36, cMakeStakes);
		steps.put(38, cMakeStakes);
		steps.put(40, cMakeStakes);
		steps.put(42, cMakeStakes);

		// 42 -> 43: shown Brundt the enchanted stakes. time to sail across the seas and die
		// TODO: New section, require combat equipment & food, and a teleport to scape

		// TODO: combat gear and food requirement
		var talkToWarriorInRellekkaToSailToWeissmere = new NpcStep(this, NpcID.GRAVEYARD_WARBAND_BOARDING_NPC, new WorldPoint(2632, 3704, 0), "Talk to the Fremennik warrior on the longest dock in Relekka to sail to Weissmere.");
		talkToWarriorInRellekkaToSailToWeissmere.addDialogStep("Yes.");

		steps.put(43, talkToWarriorInRellekkaToSailToWeissmere);

		// 43 -> 47: arrived in weissmere
		var cPlantStakes = new ConditionalStep(this, talkToWarriorInRellekkaToSailToWeissmere);
		cPlantStakes.addStep(inWeissmereTopLevel, TODO);
		steps.put(47, cPlantStakes);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.THE_FREMENNIK_EXILES, QuestState.FINISHED),
			new QuestRequirement(QuestHelperQuest.OLAFS_QUEST, QuestState.FINISHED),
			new QuestRequirement(QuestHelperQuest.PANDEMONIUM, QuestState.FINISHED),
			new SkillRequirement(Skill.SAILING, 65, false /* TODO: Confirm boostability */),
			new SkillRequirement(Skill.AGILITY, 65, false /* TODO: Confirm boostability */),
			new SkillRequirement(Skill.WOODCUTTING, 56, false /* TODO: Confirm boostability */),
			new SkillRequirement(Skill.FLETCHING, 54, false /* TODO: Confirm boostability */)
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			//
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			sampleRequirement
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			energyRestorePotion
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Big scary monster (lvl 200)"
		);
	}

	@Override
	public List<String> getNotes()
	{
		return List.of(
			"This is a note to appear in the sidebar"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.SAILING, 10000),
			new ExperienceReward(Skill.SLAYER, 8000)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of();
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to something grand")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			talkToNpc,
			climbUpLadder
		), List.of(
			olafsParchment
			// spade
		)));

		return sections;
	}
}
