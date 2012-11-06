package com.igottashoot.game.core;

import com.igottashoot.game.core.MainLoop.MainLoopOrder;
import com.igottashoot.game.managers.GameObject;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.OrderedUpdateableObject;

public class GameCamera extends BaseObject implements OrderedUpdateableObject<SystemRegistry>
{
    private final CameraViewInfo mCameraViewInfo;
    private float mLeft;
    private float mRight;
    private float mBottom;
    private float mTop;
    private GameObject mTarget;
    
    public GameCamera()
    {
        mCameraViewInfo = new CameraViewInfo();
        reset();
    }
    
    @Override
    public void reset()
    {
        mLeft = 0f;
        mRight = 0f;
        mTop = 0f;
        mBottom = 0f;
        mTarget = null;
    }
    
    @Override
    public int getPriority()
    {
        return MainLoopOrder.CAMERA.getPriority();
    }
    
    @Override
    public void update( float timeDelta, SystemRegistry parent )
    {
        mLeft = 0;
        mRight = parent.getRenderInfo().getRenderWidth();
        mBottom = 0;
        mTop = parent.getRenderInfo().getRenderHeight();
        
        if ( mTarget != null )
            setCenter( mTarget.getPositionX(), mTarget.getPositionY() );
        
        mCameraViewInfo.setWorldSize( (int)mLeft, (int)mRight, (int)mBottom, (int)mTop );
    }
    
    public CameraViewInfo getCameraViewInfo()
    {
        return mCameraViewInfo;
    }
    
    public float getWidth()
    {
        return mRight - mLeft;
    }

    public float getHeight()
    {
        return mTop - mBottom;
    }

    private float getCenterX()
    {
        final float minX = mLeft;
        return minX + ( mRight - minX ) * 0.5f;
    }

    private float getCenterY()
    {
        final float minY = mBottom;
        return minY + ( mTop - minY ) * 0.5f;
    }
    
    public void setCenter( final float centerX, final float centerY )
    {
        final float dX = centerX - getCenterX();
        final float dY = centerY - getCenterY();

        mLeft += dX;
        mRight += dX;
        mBottom += dY;
        mTop += dY;
    }

    public void setTarget( GameObject target )
    {
        mTarget = target;
    }
}
