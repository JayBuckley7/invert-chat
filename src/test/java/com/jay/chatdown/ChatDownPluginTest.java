package com.jay.chatdown;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;
public class ChatDownPluginTest
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(ChatDownPlugin.class);
        RuneLite.main(args);
    }
}
