package com.igottashoot.game.rendering;

import com.igottashoot.game.core.CameraViewInfo;
import com.igottashoot.game.primitives.Texture;

public interface DrawingCanvas
{
    public void start( CameraViewInfo cameraViewInfo );
    public void end();
    public void clear();
    public void draw( Texture texture, DrawableObjectInfo drawableObjectInfo );
    public void handleLoadedTextures( Object[] loadTextures );
}
