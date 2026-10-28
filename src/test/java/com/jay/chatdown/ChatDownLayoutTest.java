package com.jay.chatdown;

import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ChatDownLayoutTest
{
    private static final class Box
    {
        final Widget widget=mock(Widget.class);
        final int[] position={0,0,0,0};
        Box(int height)
        {
            when(widget.getHeight()).thenReturn(height);
            when(widget.getCanvasLocation()).thenReturn(new Point(0,0));
            when(widget.getOriginalX()).thenAnswer(call->position[0]);
            when(widget.getOriginalY()).thenAnswer(call->position[1]);
            when(widget.getXPositionMode()).thenAnswer(call->position[2]);
            when(widget.getYPositionMode()).thenAnswer(call->position[3]);
            when(widget.setOriginalX(anyInt())).thenAnswer(call->{position[0]=call.getArgument(0);return widget;});
            when(widget.setOriginalY(anyInt())).thenAnswer(call->{position[1]=call.getArgument(0);return widget;});
            when(widget.setXPositionMode(anyInt())).thenAnswer(call->{position[2]=call.getArgument(0);return widget;});
            when(widget.setYPositionMode(anyInt())).thenAnswer(call->{position[3]=call.getArgument(0);return widget;});
        }
    }
    @Test public void nativeOpenCloseKeepsStonesAtTopAndRestores()
    {
        Box root=new Box(165),stones=new Box(23),area=new Box(142);
        stones.position[3]=2;
        ChatDownLayout layout=new ChatDownLayout();
        layout.apply(root.widget,stones.widget,area.widget,null,true,4);
        assertEquals(0,stones.position[1]);assertEquals(0,stones.position[3]);assertEquals(23,area.position[1]);
        when(area.widget.isHidden()).thenReturn(true);
        layout.apply(root.widget,stones.widget,area.widget,null,true,4);
        verify(stones.widget,times(1)).revalidate();verify(area.widget,times(1)).revalidate();
        verify(area.widget,never()).setHidden(anyBoolean());
        layout.restore();assertEquals(2,stones.position[3]);assertEquals(0,area.position[1]);
    }
    @Test public void splitPrivateChatFollowsExpandedAndCollapsedBox()
    {
        Box root=new Box(165),stones=new Box(23),area=new Box(142),pm=new Box(1000);
        when(root.widget.getCanvasLocation()).thenReturn(new Point(50,10));
        Box older=new Box(12),newer=new Box(12);
        when(older.widget.getRelativeY()).thenReturn(900);when(newer.widget.getRelativeY()).thenReturn(920);
        when(pm.widget.getChildren()).thenReturn(new Widget[]{older.widget,newer.widget});
        ChatDownLayout layout=new ChatDownLayout();
        layout.apply(root.widget,stones.widget,area.widget,pm.widget,true,4);
        assertEquals(50,pm.position[0]);assertEquals(10+165+4-900,pm.position[1]);
        when(area.widget.isHidden()).thenReturn(true);
        layout.apply(root.widget,stones.widget,area.widget,pm.widget,true,4);
        assertEquals(10+23+4-900,pm.position[1]);
        layout.apply(root.widget,stones.widget,area.widget,pm.widget,false,4);
        assertEquals(0,pm.position[0]);assertEquals(0,pm.position[1]);
    }
    @Test public void insufficientSpaceKeepsPrivateMessagesInTheirNativePosition()
    {
        Box root=new Box(165),stones=new Box(23),area=new Box(142),pm=new Box(1000),line=new Box(12);
        when(root.widget.getCanvasLocation()).thenReturn(new Point(0,950));
        when(line.widget.getRelativeY()).thenReturn(900);
        when(pm.widget.getChildren()).thenReturn(new Widget[]{line.widget});
        ChatDownLayout layout=new ChatDownLayout();layout.apply(root.widget,stones.widget,area.widget,pm.widget,true,4);
        assertEquals(0,pm.position[1]);verify(pm.widget,never()).revalidate();
    }
    @Test public void nativeRelayoutBecomesTheNewRestorePoint()
    {
        Box root=new Box(165),stones=new Box(23),area=new Box(142);
        stones.position[3]=2;ChatDownLayout layout=new ChatDownLayout();
        layout.apply(root.widget,stones.widget,area.widget,null,true,4);
        stones.position[1]=7;stones.position[3]=2;
        layout.apply(root.widget,stones.widget,area.widget,null,true,4);
        layout.restore();assertEquals(7,stones.position[1]);assertEquals(2,stones.position[3]);
    }
    @Test public void disablingDoesNotUndoAnotherOwnersLaterEdit()
    {
        Box root=new Box(165),stones=new Box(23),area=new Box(142);
        ChatDownLayout layout=new ChatDownLayout();layout.apply(root.widget,stones.widget,area.widget,null,true,4);
        area.position[1]=80;layout.restore();assertEquals(80,area.position[1]);
    }
    @Test public void recreatedWidgetsDoNotKeepOldPlacements()
    {
        Box root=new Box(165),oldStones=new Box(23),oldArea=new Box(142),newStones=new Box(23),newArea=new Box(142);
        oldStones.position[3]=2;newStones.position[3]=2;ChatDownLayout layout=new ChatDownLayout();
        layout.apply(root.widget,oldStones.widget,oldArea.widget,null,true,4);
        layout.apply(root.widget,newStones.widget,newArea.widget,null,true,4);
        assertEquals(2,oldStones.position[3]);assertEquals(0,oldArea.position[1]);assertEquals(23,newArea.position[1]);
        layout.restore();assertEquals(2,newStones.position[3]);assertEquals(0,newArea.position[1]);
    }
}

