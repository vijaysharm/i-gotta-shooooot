package com.igottashoot.game.rendering;

public interface DrawThreadInterface
{
    public void onSurfaceCreated( DrawInitializer initializer );

    public void onSurfaceChanged( DrawResizer resizer, int width, int height );

    public void onDraw( DrawingCanvas canvas );
}
