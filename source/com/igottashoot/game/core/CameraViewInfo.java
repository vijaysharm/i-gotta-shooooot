package com.igottashoot.game.core;

import com.igottashoot.game.primitives.BaseObject;

/**
 * Defines the world box that we're rendering 
 */
public class CameraViewInfo extends BaseObject
{
    private int mCameraWorldLeft;
    private int mCameraWorldBottom;
    private int mCameraWorldRight;
    private int mCameraWorldTop;
    
    public CameraViewInfo()
    {
        reset();
    }
    
    @Override
    public void reset()
    {
        mCameraWorldTop = 0;
        mCameraWorldRight = 0;
        mCameraWorldLeft = 0;
        mCameraWorldBottom = 0;
    }
    
    public void updateInfo( CameraViewInfo cameraViewInfo )
    {
        mCameraWorldTop = cameraViewInfo.mCameraWorldTop;
        mCameraWorldRight = cameraViewInfo.mCameraWorldRight;
        mCameraWorldLeft = cameraViewInfo.mCameraWorldLeft;
        mCameraWorldBottom = cameraViewInfo.mCameraWorldBottom;
    }

    public int getWidth()
    {
        return ( mCameraWorldRight - mCameraWorldLeft );
    }
    
    public int getHeight()
    {
        return ( mCameraWorldTop - mCameraWorldBottom );
    }
    
    public int getCameraWorldLeft()
    {
        return mCameraWorldLeft;
    }

    public int getCameraWorldBottom()
    {
        return mCameraWorldBottom;
    }

    
    public int getCameraWorldRight()
    {
        return mCameraWorldRight;
    }
    
    public int getCameraWorldTop()
    {
        return mCameraWorldTop;
    }
    
    public void setWorldSize( int left, int right, int bottom, int top )
    {
        mCameraWorldRight = right;
        mCameraWorldTop = top;
        mCameraWorldLeft = left;
        mCameraWorldBottom = bottom;
    }
}
