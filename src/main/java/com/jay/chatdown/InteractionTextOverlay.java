package com.jay.chatdown;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TextComponent;

/** Displays the existing default action; never changes menu entries or sends input. */
final class InteractionTextOverlay extends Overlay
{
    private final Client client;
    private boolean owned, original, visible;

    @Inject InteractionTextOverlay(Client client)
    {
        this.client=client;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    void update(boolean enabled)
    {
        if (!enabled) { restore(); return; }
        if (!owned) { original=client.isMouseoverTextEnabled(); owned=true; }
        if (client.isMouseoverTextEnabled()) client.setMouseoverTextEnabled(false);
        visible=original;
    }

    void restore()
    {
        visible=false;
        if (owned && !client.isMouseoverTextEnabled()) client.setMouseoverTextEnabled(original);
        owned=false;
    }

    static String actionText(MenuEntry[] entries)
    {
        if (entries==null || entries.length<2) return "";
        MenuEntry entry=entries[entries.length-1];
        if (entry==null) return "";
        String option=entry.getOption(),target=entry.getTarget();
        if (option==null || option.isEmpty() || "Cancel".equals(option)) return "";
        String text=option+(target==null || target.isEmpty()?"":" "+target);
        if (entries.length>2) text+="<col=ffffff> / "+(entries.length-2)+" more options";
        return text;
    }

    @Override public Dimension render(Graphics2D graphics)
    {
        if (!visible || client.isMenuOpen()) return null;
        String text=actionText(client.getMenuEntries());
        int width=client.getCanvasWidth(),height=client.getCanvasHeight();
        if (text.isEmpty() || width<=8 || height<=8) return null;
        Graphics2D drawing=(Graphics2D)graphics.create();
        try
        {
            drawing.setFont(FontManager.getRunescapeFont());
            drawing.clipRect(4,0,width-8,height);
            TextComponent line=new TextComponent();
            line.setColor(Color.WHITE);
            line.setText(text);
            line.setPosition(new Point(4,height-4-drawing.getFontMetrics().getDescent()));
            line.render(drawing);
        }
        finally { drawing.dispose(); }
        return null;
    }
}
