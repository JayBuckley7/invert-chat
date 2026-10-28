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

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ScriptID;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Chat Down",
	description = "Put chat stones above the message box and expand chat downward",
	tags = {"chatbox", "layout", "interface", "private"}
)
public class ChatDownPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ChatDownConfig config;

	@Inject
	private InteractionTextOverlay interactionText;

	@Inject
	private OverlayManager overlays;

	private final ChatDownLayout layout = new ChatDownLayout();
	private volatile boolean active;

	@Provides
	ChatDownConfig provideConfig(ConfigManager manager)
	{
		return manager.getConfig(ChatDownConfig.class);
	}

	@Override
	protected void startUp()
	{
		active = true;
		overlays.add(interactionText);
		clientThread.invokeLater(this::update);
	}

	@Override
	protected void shutDown()
	{
		active = false;
		overlays.remove(interactionText);
		clientThread.invokeLater(() ->
		{
			layout.restore();
			interactionText.restore();
		});
	}

	@Subscribe
	public void onBeforeRender(BeforeRender event)
	{
		update();
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		// Native scripts also relayout these widgets when opening, closing or changing UI mode.
		if (event.getScriptId() == ScriptID.BUILD_CHATBOX
			|| event.getScriptId() == ScriptID.SPLITPM_CHANGED
			|| event.getScriptId() == ScriptID.TOPLEVEL_REDRAW)
		{
			update();
		}
	}

	private void update()
	{
		if (!active)
		{
			return;
		}
		boolean available = client.getGameState() == GameState.LOGGED_IN && client.isResized();
		interactionText.update(available && config.interactionTextBottomLeft());
		if (!available)
		{
			layout.restore();
			return;
		}
		Widget root = client.getWidget(InterfaceID.Chatbox.UNIVERSE);
		Widget stones = client.getWidget(InterfaceID.Chatbox.CONTROLS);
		Widget area = client.getWidget(InterfaceID.Chatbox.CHATAREA);
		if (root == null || stones == null || area == null || root.isHidden() || stones.isHidden()
			|| stones.getHeight() <= 0)
		{
			layout.restore();
			return;
		}
		layout.apply(root, stones, area, client.getWidget(InterfaceID.PmChat.CONTAINER), config.followSplitChat(),
			config.splitChatGap());
	}
}
