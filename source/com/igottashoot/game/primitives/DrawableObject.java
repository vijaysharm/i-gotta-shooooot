package com.igottashoot.game.primitives;

import com.igottashoot.game.rendering.DrawingCanvas;
import com.igottashoot.game.rendering.RendererInfo;

public interface DrawableObject
{
    public void draw( DrawingCanvas canvas, RendererInfo rendererInfo );
}
