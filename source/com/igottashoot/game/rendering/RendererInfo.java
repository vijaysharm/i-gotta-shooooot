package com.igottashoot.game.rendering;

import com.igottashoot.game.core.CameraViewInfo;

public interface RendererInfo
{
    public float getScaleX();
    public float getInverseScaleX();
    
    public float getScaleY();
    public float getInverseScaleY();
    
    public int getRenderHeight();
    public int getRenderWidth();
    
    public int getHalfHeight();
    public int getHalfWidth();
    
    public CameraViewInfo getCameraViewInfo();
}
