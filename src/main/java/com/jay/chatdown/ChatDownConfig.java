package com.jay.chatdown;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(ChatDownConfig.GROUP)
public interface ChatDownConfig extends Config
{
    String GROUP = "jay-chat-down";

    @ConfigItem(keyName = "followSplitChat", name = "Split chat below", description = "Keep split private messages below the open chatbox, or below the stones when collapsed", position = 0)
    default boolean followSplitChat() { return true; }

    @Range(min = 0, max = 30)
    @ConfigItem(keyName = "splitChatGap", name = "Split chat gap", description = "Space in pixels between the chatbox and split private messages", position = 1)
    default int splitChatGap() { return 4; }
}
