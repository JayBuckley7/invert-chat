/*
 * Copyright (c) 2026, Jay Buckley
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
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jay.chatdown;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(ChatDownConfig.GROUP)
public interface ChatDownConfig extends Config
{
	String GROUP = "jay-chat-down";

	@ConfigItem(
		keyName = "followSplitChat",
		name = "Split chat below",
		description = "Keep split private messages below the open chatbox, or below the stones when collapsed",
		position = 0
	)
	default boolean followSplitChat()
	{
		return true;
	}

	@ConfigItem(
		keyName = "interactionTextBottomLeft",
		name = "Interaction text bottom-left",
		description = "Move the top-left hover-action text to the bottom-left of the game canvas",
		position = 2
	)
	default boolean interactionTextBottomLeft()
	{
		return true;
	}

	@Range(min = 0, max = 30)
	@ConfigItem(
		keyName = "splitChatGap",
		name = "Split chat gap",
		description = "Space in pixels between the chatbox and split private messages",
		position = 1
	)
	default int splitChatGap()
	{
		return 4;
	}
}
