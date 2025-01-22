/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.statemanagement;

import com.questhelper.config.ConfigKeys;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.RequirementValidator;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import net.runelite.api.Client;
import net.runelite.api.widgets.ComponentID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CrackTheClueIIStateTracker
{
	@Inject
	Client client;

	Requirement ornateglovesbootscollected, ornatelegscollected, ornatetopcollected, ornatecapecollected, ornatehelmcollected;

	RequirementValidator reqs;


	public void startUp(ConfigManager configManager, EventBus eventBus)
	{
		// Mid-conditions
		ornateglovesbootscollected = new RuneliteRequirement(
			configManager, ConfigKeys.CRACK_THE_CLUE_II_WEEK_ONE_ITEM.getKey(),
			new Conditions(true, LogicType.OR,
				new WidgetTextRequirement(ComponentID.DIARY_TEXT, true, "You find some beautifully ornate gloves and boots.")
			)
		);

		ornatelegscollected = new RuneliteRequirement(
			configManager, ConfigKeys.CRACK_THE_CLUE_II_WEEK_TWO_ITEM.getKey(),
			new Conditions(true, LogicType.OR,
				new WidgetTextRequirement(ComponentID.DIARY_TEXT, true, "You find some beautifully ornate leg armour.")
			)
		);

		ornatetopcollected = new RuneliteRequirement(
			configManager, ConfigKeys.CRACK_THE_CLUE_II_WEEK_THREE_ITEM.getKey(),
			new Conditions(true, LogicType.OR,
				new ChatMessageRequirement("Some beautifully ornate armour mysteriously appears.")
			)
		);

		ornatecapecollected = new RuneliteRequirement(
			configManager, ConfigKeys.CRACK_THE_CLUE_II_WEEK_FOUR_ITEM.getKey(),
			new Conditions(true, LogicType.OR,
				new WidgetTextRequirement(ComponentID.DIARY_TEXT, true, "You find a beautifully ornate cape.")
			)
		);

		ornatehelmcollected = new RuneliteRequirement(
			configManager, ConfigKeys.CRACK_THE_CLUE_II_WEEK_FINAL_ITEM.getKey(),
			new Conditions(true, LogicType.OR,
				new WidgetTextRequirement(ComponentID.DIARY_TEXT, true, "Here, take this. But tell no one I was here.")
			)
		);


		reqs = new RequirementValidator(client, eventBus,
			ornateglovesbootscollected, ornatelegscollected, ornatetopcollected, ornatecapecollected, ornatehelmcollected
		);

		eventBus.register(reqs);
		reqs.startUp();
	}


	public void shutDown(EventBus eventBus)
	{
		eventBus.unregister(reqs);
	}
}
