package com.igottashoot.game.rendering;

import com.igottashoot.game.core.CameraViewInfo;
import com.igottashoot.game.managers.OrderedObjectManager;
import com.igottashoot.game.primitives.RenderElement;

public interface GameThreadInterface
{
    public void waitForDrawingToComplete();
    public void setDrawQueue( OrderedObjectManager<RenderElement> renderQueue, CameraViewInfo cameraViewInfo );
    public void scheduleTextureLoad( TextureLoader textureLoader );
}
