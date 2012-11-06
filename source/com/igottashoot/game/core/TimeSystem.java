package com.igottashoot.game.core;

import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.UpdateableObject;

public class TimeSystem extends BaseObject  implements UpdateableObject<SystemRegistry>
{
    private float mGameTime;
    private float mRealTime;
    private float mGameFrameDelta;
    
    public TimeSystem()
    {
        reset();
    }
    
    @Override
    public void reset()
    {
        mGameTime = 0f;
        mGameFrameDelta = 0f;
        mRealTime = 0f;
    }
    
    @Override
    public void update( float timeDelta, SystemRegistry parent )
    {
        mRealTime += timeDelta;
        
        float scale = 1.0f;
        mGameTime += ( timeDelta * scale );
        mGameFrameDelta = ( timeDelta * scale );
    }

    public float getGameFrameDelta()
    {
        return mGameFrameDelta;
    }
    
    public float getGameTime()
    {
        return mGameTime;
    }
}
