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
import java.util.concurrent.atomic.AtomicBoolean;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
public class InteractionTextOverlayTest
{
	@Test
	public void restoresNativeSettingAcrossRepeatedUpdatesAndStop()
	{
		Client client = mock(Client.class);
		AtomicBoolean nativeText = new AtomicBoolean(true);
		when(client.isMouseoverTextEnabled()).thenAnswer(call -> nativeText.get());
		doAnswer(call -> {
			nativeText.set(call.getArgument(0));
			return null;
		})
			.when(client)
			.setMouseoverTextEnabled(anyBoolean());
		InteractionTextOverlay overlay = new InteractionTextOverlay(client);
		overlay.update(true);
		overlay.update(true);
		assertFalse(nativeText.get());
		overlay.update(false);
		assertTrue(nativeText.get());
		nativeText.set(false);
		overlay.update(true);
		overlay.restore();
		assertFalse(nativeText.get());
	}

	@Test
	public void keepsNativeTargetColorsAndAdditionalOptionCount()
	{
		MenuEntry entry = mock(MenuEntry.class);
		when(entry.getOption()).thenReturn("Attack");
		when(entry.getTarget()).thenReturn("<col=ffff00>Gemstone Crab");
		assertEquals("Attack <col=ffff00>Gemstone Crab<col=ffffff> / 2 more options",
			         InteractionTextOverlay.actionText(new MenuEntry[] {null, null, null, entry}));
		when(entry.getOption()).thenReturn("Cancel");
		assertEquals("", InteractionTextOverlay.actionText(new MenuEntry[] {null, entry}));
		assertEquals("", InteractionTextOverlay.actionText(new MenuEntry[0]));
	}
}
