/*
 * Copyright (c) 2023, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.quests.thepathofglouphrie;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
public class YewnocksPuzzle extends DetailedOwnerStep
{
	private static final int STOREROOM_REGION = 11074;
	private final int PUZZLE_SELECTED_DISC_VARP_ID = 856;
	private final int PUZZLE1_INSERTED_DISC_VARP_ID = 3994;
	private final int PUZZLE2_UPPER_INSERTED_DISC_VARP_ID = 3995;
	private final int PUZZLE2_LOWER_INSERTED_DISC_VARP_ID = 3996;
	private final int PUZZLE1_LEFT_VARP_ID = 3997;
	private final int PUZZLE1_RIGHT_VARP_ID = 3998;
	private final int PUZZLE2_VARP_ID = 3999;
	/**
	 * ItemID to ItemRequirement map
	 */
	private final HashMap<Integer, ItemRequirement> discs = new HashMap<>();
	/**
	 * Value to ItemRequirement map
	 */
	private final HashMap<Integer, ItemRequirement> valueToRequirement = new HashMap<>();
	/**
	 * ItemID to Value map
	 */
	private final HashMap<Integer, Integer> discToValue = new HashMap<>();
	/**
	 * Value to list of possible requirements using exactly 2 different
	 */
	private final HashMap<Integer, List<ItemRequirements>> valueToDoubleDiscRequirement = new HashMap<>();
	private ObjectStep clickMachine;
	private int puzzle1LeftItemID = -1;
	private int puzzle1RightItemID = -1;
	private int puzzle2ItemID = -1;
	private WidgetPresenceRequirement widgetOpen;
	private ItemStep selectDisc;
	private Solution solution;
	private ObjectStep getMoreDiscs;

	public YewnocksPuzzle(QuestHelper questHelper)
	{
		super(questHelper, "Operate Yewnock's machine & solve the puzzle");

		discs.put(ItemID.RED_CIRCLE, new ItemRequirement("Red circle", ItemID.RED_CIRCLE).highlighted());
		discs.put(ItemID.ORANGE_CIRCLE, new ItemRequirement("Orange circle", ItemID.ORANGE_CIRCLE).highlighted());
		discs.put(ItemID.YELLOW_CIRCLE, new ItemRequirement("Yellow circle", ItemID.YELLOW_CIRCLE).highlighted());
		discs.put(ItemID.GREEN_CIRCLE, new ItemRequirement("Green circle", ItemID.GREEN_CIRCLE).highlighted());
		discs.put(ItemID.BLUE_CIRCLE, new ItemRequirement("Blue circle", ItemID.BLUE_CIRCLE).highlighted());
		discs.put(ItemID.INDIGO_CIRCLE, new ItemRequirement("Indigo circle", ItemID.INDIGO_CIRCLE).highlighted());
		discs.put(ItemID.VIOLET_CIRCLE, new ItemRequirement("Violet circle", ItemID.VIOLET_CIRCLE).highlighted());
		discs.put(ItemID.RED_TRIANGLE, new ItemRequirement("Red triangle", ItemID.RED_TRIANGLE).highlighted());
		discs.put(ItemID.ORANGE_TRIANGLE, new ItemRequirement("Orange triangle", ItemID.ORANGE_TRIANGLE).highlighted());
		discs.put(ItemID.YELLOW_TRIANGLE, new ItemRequirement("Yellow triangle", ItemID.YELLOW_TRIANGLE).highlighted());
		discs.put(ItemID.GREEN_TRIANGLE, new ItemRequirement("Green triangle", ItemID.GREEN_TRIANGLE).highlighted());
		discs.put(ItemID.BLUE_TRIANGLE, new ItemRequirement("Blue triangle", ItemID.BLUE_TRIANGLE).highlighted());
		discs.put(ItemID.INDIGO_TRIANGLE, new ItemRequirement("Indigo triangle", ItemID.INDIGO_TRIANGLE).highlighted());
		discs.put(ItemID.VIOLET_TRIANGLE, new ItemRequirement("Violet triangle", ItemID.VIOLET_TRIANGLE).highlighted());
		discs.put(ItemID.RED_SQUARE, new ItemRequirement("Red square", ItemID.RED_SQUARE).highlighted());
		discs.put(ItemID.ORANGE_SQUARE, new ItemRequirement("Orange square", ItemID.ORANGE_SQUARE).highlighted());
		discs.put(ItemID.YELLOW_SQUARE, new ItemRequirement("Yellow square", ItemID.YELLOW_SQUARE).highlighted());
		discs.put(ItemID.GREEN_SQUARE, new ItemRequirement("Green square", ItemID.GREEN_SQUARE).highlighted());
		discs.put(ItemID.BLUE_SQUARE, new ItemRequirement("Blue square", ItemID.BLUE_SQUARE).highlighted());
		discs.put(ItemID.INDIGO_SQUARE, new ItemRequirement("Indigo square", ItemID.INDIGO_SQUARE).highlighted());
		discs.put(ItemID.VIOLET_SQUARE, new ItemRequirement("Violet square", ItemID.VIOLET_SQUARE).highlighted());
		discs.put(ItemID.RED_PENTAGON, new ItemRequirement("Red pentagon", ItemID.RED_PENTAGON).highlighted());
		discs.put(ItemID.ORANGE_PENTAGON, new ItemRequirement("Orange pentagon", ItemID.ORANGE_PENTAGON).highlighted());
		discs.put(ItemID.YELLOW_PENTAGON, new ItemRequirement("Yellow pentagon", ItemID.YELLOW_PENTAGON).highlighted());
		discs.put(ItemID.GREEN_PENTAGON, new ItemRequirement("Green pentagon", ItemID.GREEN_PENTAGON).highlighted());
		discs.put(ItemID.BLUE_PENTAGON, new ItemRequirement("Blue pentagon", ItemID.BLUE_PENTAGON).highlighted());
		discs.put(ItemID.INDIGO_PENTAGON, new ItemRequirement("Indigo pentagon", ItemID.INDIGO_PENTAGON).highlighted());
		discs.put(ItemID.VIOLET_PENTAGON, new ItemRequirement("Violet pentagon", ItemID.VIOLET_PENTAGON).highlighted());

		var yellowCircleRedTri = new ItemRequirement("Yellow circle/red triangle", ItemID.RED_TRIANGLE).highlighted();
		yellowCircleRedTri.addAlternates(ItemID.YELLOW_CIRCLE);
		var greenCircleRedSquare = new ItemRequirement("Green circle/red square", ItemID.GREEN_CIRCLE).highlighted();
		greenCircleRedSquare.addAlternates(ItemID.RED_SQUARE);
		var blueCircleRedPentagon = new ItemRequirement("Blue circle/red pentagon", ItemID.BLUE_CIRCLE).highlighted();
		blueCircleRedPentagon.addAlternates(ItemID.RED_PENTAGON);
		var indigoCircleOrangeTriangle = new ItemRequirement("Indigo circle/orange triangle", ItemID.INDIGO_CIRCLE).highlighted();
		indigoCircleOrangeTriangle.addAlternates(ItemID.ORANGE_TRIANGLE);
		var yellowSquareGreenTriangle = new ItemRequirement("Yellow square/green triangle", ItemID.YELLOW_SQUARE).highlighted();
		yellowSquareGreenTriangle.addAlternates(ItemID.GREEN_TRIANGLE);
		var yellowPentagonBlueTriangle = new ItemRequirement("Yellow pentagon/blue triangle", ItemID.YELLOW_PENTAGON).highlighted();
		yellowPentagonBlueTriangle.addAlternates(ItemID.BLUE_TRIANGLE);
		var blueSquareGreenPentagon = new ItemRequirement("Blue square/green pentagon", ItemID.BLUE_SQUARE).highlighted();
		blueSquareGreenPentagon.addAlternates(ItemID.GREEN_PENTAGON);

		valueToRequirement.put(1, discs.get(ItemID.RED_CIRCLE));
		valueToRequirement.put(2, discs.get(ItemID.ORANGE_CIRCLE));
		valueToRequirement.put(3, yellowCircleRedTri);
		valueToRequirement.put(4, greenCircleRedSquare);
		valueToRequirement.put(5, blueCircleRedPentagon);
		valueToRequirement.put(6, indigoCircleOrangeTriangle);
		valueToRequirement.put(7, discs.get(ItemID.VIOLET_CIRCLE));
		valueToRequirement.put(8, discs.get(ItemID.ORANGE_SQUARE));
		valueToRequirement.put(9, discs.get(ItemID.YELLOW_TRIANGLE));
		valueToRequirement.put(10, discs.get(ItemID.ORANGE_PENTAGON));
		valueToRequirement.put(12, yellowSquareGreenTriangle);
		valueToRequirement.put(15, yellowPentagonBlueTriangle);
		valueToRequirement.put(16, discs.get(ItemID.GREEN_SQUARE));
		valueToRequirement.put(18, discs.get(ItemID.INDIGO_TRIANGLE));
		valueToRequirement.put(20, blueSquareGreenPentagon);
		valueToRequirement.put(21, discs.get(ItemID.VIOLET_TRIANGLE));
		valueToRequirement.put(24, discs.get(ItemID.INDIGO_SQUARE));
		valueToRequirement.put(25, discs.get(ItemID.BLUE_PENTAGON));
		valueToRequirement.put(28, discs.get(ItemID.VIOLET_SQUARE));
		valueToRequirement.put(30, discs.get(ItemID.INDIGO_PENTAGON));
		valueToRequirement.put(35, discs.get(ItemID.VIOLET_PENTAGON));

		for (int i = 0; i < 35; i++)
		{
			var shape1 = valueToRequirement.get(i);
			for (int j = 0; j < 35; j++)
			{
				var shape2 = valueToRequirement.get(j);

				if (shape1 == null || shape2 == null)
				{
					continue;
				}
				valueToDoubleDiscRequirement.computeIfAbsent(i + j, sv2 -> new ArrayList<>());
				if (shape1.getId() == shape2.getId())
				{
					valueToDoubleDiscRequirement.get(i + j).add(new ItemRequirements(shape1.quantity(2)));
				}
				else
				{
					valueToDoubleDiscRequirement.get(i + j).add(new ItemRequirements(LogicType.AND, shape1, shape2));
				}
			}
		}

		discToValue.put(ItemID.RED_CIRCLE, 1);
		discToValue.put(ItemID.RED_TRIANGLE, 3);
		discToValue.put(ItemID.RED_SQUARE, 4);
		discToValue.put(ItemID.RED_PENTAGON, 5);
		discToValue.put(ItemID.ORANGE_CIRCLE, 2);
		discToValue.put(ItemID.ORANGE_TRIANGLE, 6);
		discToValue.put(ItemID.ORANGE_SQUARE, 8);
		discToValue.put(ItemID.ORANGE_PENTAGON, 10);
		discToValue.put(ItemID.YELLOW_CIRCLE, 3);
		discToValue.put(ItemID.YELLOW_TRIANGLE, 9);
		discToValue.put(ItemID.YELLOW_SQUARE, 12);
		discToValue.put(ItemID.YELLOW_PENTAGON, 15);
		discToValue.put(ItemID.GREEN_CIRCLE, 4);
		discToValue.put(ItemID.GREEN_TRIANGLE, 12);
		discToValue.put(ItemID.GREEN_SQUARE, 16);
		discToValue.put(ItemID.GREEN_PENTAGON, 20);
		discToValue.put(ItemID.BLUE_CIRCLE, 5);
		discToValue.put(ItemID.BLUE_TRIANGLE, 15);
		discToValue.put(ItemID.BLUE_SQUARE, 20);
		discToValue.put(ItemID.BLUE_PENTAGON, 25);
		discToValue.put(ItemID.INDIGO_CIRCLE, 6);
		discToValue.put(ItemID.INDIGO_TRIANGLE, 18);
		discToValue.put(ItemID.INDIGO_SQUARE, 24);
		discToValue.put(ItemID.INDIGO_PENTAGON, 30);
		discToValue.put(ItemID.VIOLET_CIRCLE, 7);
		discToValue.put(ItemID.VIOLET_TRIANGLE, 21);
		discToValue.put(ItemID.VIOLET_SQUARE, 28);
		discToValue.put(ItemID.VIOLET_PENTAGON, 35);
	}

	public static WorldPoint regionPoint(int regionX, int regionY)
	{
		return WorldPoint.fromRegion(STOREROOM_REGION, regionX, regionY, 0);
	}

	public static Solution makeSolution(List<Item> items)
	{
		Solution solution;
		if (puzzle1LeftItemID > 0 && puzzle1RightItemID > 0 && puzzle2ItemID > 0)
		{
			log.info("try to calculate shit");
			var puzzle1SlotState = client.getVarpValue(PUZZLE1_INSERTED_DISC_VARP_ID);
			if (puzzle1SlotState > 0)
			{
				if (puzzle2UpperRequirement == null || puzzle2LowerRequirement == null)
				{
					// solve puzzle 2
					var puzzle2SolutionValue = discToValue.get(puzzle2ItemID);
					var possiblePuzzle2Solutions = valueToDoubleDiscRequirement.get(puzzle2SolutionValue);
					var discContainer = client.getItemContainer(440);
					if (discContainer == null)
					{
						// No disc container found, can't try to auto solve things
						return;
					}
					var items = discContainer.getItems();
					for (var possiblePuzzle2Solution : possiblePuzzle2Solutions)
					{
						if (possiblePuzzle2Solution.check(client, false, List.of(items)))
						{
							// Found a valid puzzle2 solution
							puzzle2UpperRequirement = possiblePuzzle2Solution.getItemRequirements().get(0);
							puzzle2LowerRequirement = possiblePuzzle2Solution.getItemRequirements().get(1);
							break;
						}
					}
				}

				var puzzle2UpperSlotState = client.getVarpValue(PUZZLE2_UPPER_INSERTED_DISC_VARP_ID);
				var puzzle2LowerSlotState = client.getVarpValue(PUZZLE2_LOWER_INSERTED_DISC_VARP_ID);
				if (puzzle2UpperSlotState <= 0)
				{
					// Requirement: Any of the options in the upper slot
					selectDisc.setText("Insert the highlighted disc into the highlighted slot");
					selectDisc.setRequirements(List.of(puzzle2UpperRequirement.highlighted()));
					selectDisc.clearWidgetHighlights();
					// FOR PUZZLE 2 UPPER SOLUTION
					selectDisc.addWidgetHighlight(848, 20);
				}
				else if (puzzle2LowerSlotState <= 0)
				{
					selectDisc.setText("Insert the highlighted disc into the highlighted slot");
					selectDisc.setRequirements(List.of(puzzle2LowerRequirement.highlighted()));
					selectDisc.clearWidgetHighlights();
					// FOR PUZZLE 2 LOWER SOLUTION
					selectDisc.addWidgetHighlight(848, 21);
				}
				else
				{
					// CLICK CONFIRM
					selectDisc.setText("Click the submit button");
					selectDisc.setRequirements(List.of());
					selectDisc.clearWidgetHighlights();
					selectDisc.addWidgetHighlight(848, 12);
				}
			}
			else
			{
				if (puzzle1Requirement == null)
				{
					var discContainer = client.getItemContainer(440);
					if (discContainer == null)
					{
						// No disc container found, can't try to auto solve things
						return;
					}
					var items = discContainer.getItems();
					// solve puzzle 1
					var puzzle1SolutionValue = discToValue.get(puzzle1LeftItemID) + discToValue.get(puzzle1RightItemID);
					log.info("Puzzle 1 solution: {}", puzzle1SolutionValue);
					puzzle1Requirement = valueToRequirement.get(puzzle1SolutionValue);
					if (puzzle1Requirement != null)
					{
						if (puzzle1Requirement.check(client, false, List.of(items)))
						{
							selectDisc.setText("Insert the highlighted disc into the highlighted slot");
							selectDisc.setRequirements(List.of(puzzle1Requirement));
							selectDisc.clearWidgetHighlights();
							// FOR PUZZLE 1 SOLUTION
							selectDisc.addWidgetHighlight(848, 19);
						}
					}
				}

			}
			// FOR PUZZLE 2 UPPER SOLUTION
			// selectDisc.addWidgetHighlight(848, 20);
			// FOR PUZZLE 2 LOWER SOLUTION
			// selectDisc.addWidgetHighlight(848, 21);
			startUpStep(selectDisc);
		}
		else
		{
			puzzle1Requirement = null;
			puzzle2UpperRequirement = null;
			puzzle2LowerRequirement = null;
		}
		return solution;
	}

	@Override
	public void startUp()
	{
		puzzle1LeftItemID = client.getVarpValue(PUZZLE1_LEFT_VARP_ID);
		puzzle1RightItemID = client.getVarpValue(PUZZLE1_RIGHT_VARP_ID);
		puzzle2ItemID = client.getVarpValue(PUZZLE2_VARP_ID);

		updateSteps();
	}

	@Override
	protected void setupSteps()
	{
		getMoreDiscs = new ObjectStep(getQuestHelper(), ObjectID.CHEST_49617, "Get more discs from the chests outside. You can drop discs before you get more.", true);
		clickMachine = new ObjectStep(getQuestHelper(), ObjectID.YEWNOCKS_MACHINE_49662, regionPoint(22, 32), "Operate Yewnock's machine. If you run out of discs you can get new ones from the regular chests in the previous room.");
		widgetOpen = new WidgetPresenceRequirement(848, 0);

		selectDisc = new DiscInsertionStep(getQuestHelper(), "Select the highlighted disc in your inventory");
	}

	@Subscribe
	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		if (varbitChanged.getVarbitId() == -1)
		{
			if (varbitChanged.getVarpId() == PUZZLE1_LEFT_VARP_ID)
			{
				puzzle1LeftItemID = varbitChanged.getValue();
			}
			else if (varbitChanged.getVarpId() == PUZZLE1_RIGHT_VARP_ID)
			{
				puzzle1RightItemID = varbitChanged.getValue();
			}
			else if (varbitChanged.getVarpId() == PUZZLE2_VARP_ID)
			{
				puzzle2ItemID = varbitChanged.getValue();
			}
			else
			{
				// irrelevant value changed
				return;
			}

			updateSteps();
		}
	}

	@Subscribe
	public void onGameTick(final GameTick event)
	{
		// TODO: optimize
		updateSteps();
	}

	private int countShapes()
	{
		ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);
		if (itemContainer == null)
		{
			return 0;
		}

		int count = 0;

		for (var item : itemContainer.getItems())
		{
			var shape = discs.get(item.getId());
			if (shape != null)
			{
				count += item.getQuantity();
			}
		}

		return count;
	}

	@Override
	public void shutDown()
	{
		super.shutDown();
		puzzle1LeftItemID = -1;
		puzzle1RightItemID = -1;
		puzzle2ItemID = -1;
		solution = null;
	}

	protected void updateSteps()
	{
		if (!widgetOpen.check(client))
		{
			solution = null;
			startUpStep(clickMachine);
			return;
		}

		if (solution == null)
		{
			var discContainer = client.getItemContainer(440);
			if (discContainer == null)
			{
				// No disc container found, can't try to auto solve things
				return;
			}
			var items = discContainer.getItems();
			solution = makeSolution(List.of(items));

			if (!solution.isGood())
			{
				startUpStep(clickMachine);
				return;
				// FOUND SOLUTION
			}
		}
		else
		{
			if (!solution.isGood())
			{
				startUpStep(clickMachine);
				return;
				// FOUND SOLUTION
			}
		}

		// Widget is open
	}

	@Override
	public List<QuestStep> getSteps()
	{
		return List.of(clickMachine, selectDisc);
	}

	public class Solution
	{
		public ItemRequirement puzzle1Requirement;
		public ItemRequirement puzzle2UpperRequirement;
		public ItemRequirement puzzle2LowerRequirement;

		public boolean isGood()
		{
			return (puzzle1Requirement != null & puzzle2UpperRequirement != null & puzzle2LowerRequirement != null);
		}
	}
}
