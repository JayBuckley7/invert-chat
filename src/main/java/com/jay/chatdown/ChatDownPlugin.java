package com.jay.chatdown;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(name = "Chat Down", description = "Put chat stones above the message box and expand chat downward", tags = {"chatbox", "layout", "interface", "private"})
public class ChatDownPlugin extends Plugin
{
    @Inject private Client client;
    @Inject private ClientThread clientThread;
    @Inject private ChatDownConfig config;
    private final ChatDownLayout layout = new ChatDownLayout();
    private volatile boolean active;

    @Provides ChatDownConfig provideConfig(ConfigManager manager) { return manager.getConfig(ChatDownConfig.class); }
    @Override protected void startUp() { active=true; clientThread.invokeLater(this::update); }
    @Override protected void shutDown() { active=false; clientThread.invokeLater(layout::restore); }
    @Subscribe public void onBeforeRender(BeforeRender event) { update(); }
    @Subscribe public void onScriptPostFired(ScriptPostFired event)
    {
        // Native scripts also relayout these widgets when opening, closing or changing UI mode.
        if (event.getScriptId() == net.runelite.api.ScriptID.BUILD_CHATBOX
                || event.getScriptId() == net.runelite.api.ScriptID.SPLITPM_CHANGED
                || event.getScriptId() == net.runelite.api.ScriptID.TOPLEVEL_REDRAW) update();
    }
    private void update()
    {
        if (!active) return;
        if (client.getGameState()!=GameState.LOGGED_IN || !client.isResized()) { layout.restore(); return; }
        Widget root=client.getWidget(InterfaceID.Chatbox.UNIVERSE);
        Widget stones=client.getWidget(InterfaceID.Chatbox.CONTROLS);
        Widget area=client.getWidget(InterfaceID.Chatbox.CHATAREA);
        if (root==null || stones==null || area==null || root.isHidden() || stones.isHidden() || stones.getHeight()<=0)
        { layout.restore(); return; }
        layout.apply(root, stones, area, client.getWidget(InterfaceID.PmChat.CONTAINER), config.followSplitChat(), config.splitChatGap());
    }
}

