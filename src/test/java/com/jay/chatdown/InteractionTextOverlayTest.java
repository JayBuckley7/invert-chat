package com.jay.chatdown;
import java.util.concurrent.atomic.AtomicBoolean;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
public class InteractionTextOverlayTest
{
    @Test public void restoresNativeSettingAcrossRepeatedUpdatesAndStop()
    {
        Client client=mock(Client.class);AtomicBoolean nativeText=new AtomicBoolean(true);
        when(client.isMouseoverTextEnabled()).thenAnswer(call->nativeText.get());
        doAnswer(call->{nativeText.set(call.getArgument(0));return null;}).when(client).setMouseoverTextEnabled(anyBoolean());
        InteractionTextOverlay overlay=new InteractionTextOverlay(client);
        overlay.update(true);overlay.update(true);assertFalse(nativeText.get());
        overlay.update(false);assertTrue(nativeText.get());
        nativeText.set(false);overlay.update(true);overlay.restore();assertFalse(nativeText.get());
    }
    @Test public void keepsNativeTargetColorsAndAdditionalOptionCount()
    {
        MenuEntry entry=mock(MenuEntry.class);when(entry.getOption()).thenReturn("Attack");when(entry.getTarget()).thenReturn("<col=ffff00>Gemstone Crab");
        assertEquals("Attack <col=ffff00>Gemstone Crab<col=ffffff> / 2 more options",InteractionTextOverlay.actionText(new MenuEntry[]{null,null,null,entry}));
        when(entry.getOption()).thenReturn("Cancel");assertEquals("",InteractionTextOverlay.actionText(new MenuEntry[]{null,entry}));
        assertEquals("",InteractionTextOverlay.actionText(new MenuEntry[0]));
    }
}
