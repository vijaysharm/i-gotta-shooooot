package com.igottashoot.game.core;

import com.igottashoot.game.input.TouchScreenLocation;
import com.igottashoot.game.primitives.RenderElement;
import com.igottashoot.game.rendering.RendererInfo;

public interface SystemRegistry
{
    void scheduleForDraw( RenderElement element );
    RenderElement allocateRenderElement();
    TimeSystem getTimeSystem();
    RendererInfo getRenderInfo();
    TouchScreenLocation getTouchScreenLocation();
    void load( GameEvent event );
    GameCollisionHandler getCollisionHandler();
}
