package com.igottashoot.game.animation;

import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.OrderedObject;
import com.igottashoot.game.utilities.FixedSizeArray;

public class SpriteAnimation extends BaseObject implements OrderedObject
{
    private final int mId;
    private final FixedSizeArray<AnimationFrame> mAnimationFrames;
    
    private float mTotalLength;
    private boolean mLoopAnimation;
    
    public SpriteAnimation( int id, int frames )
    {
        mId = id;
        mAnimationFrames = FixedSizeArray.newArray( frames );
        
        reset();
    }
    
    @Override
    public void reset()
    {
        mAnimationFrames.clear();
        mTotalLength = 0.0f;
        mLoopAnimation = false;
    }
    
    @Override
    public int getPriority()
    {
        return mId;
    }
    
    public void addAnimation( AnimationFrame animationFrame )
    {
        mTotalLength += animationFrame.getAnitmationFrameTime();
        mAnimationFrames.add( animationFrame );
    }
    
    public AnimationFrame getAnimationFrame( float animationTime )
    {
        final float length = getTotalLength();
        if ( length <= 0.0f )
            return null;
        
        final int frameCount = mAnimationFrames.getCount();
        
        AnimationFrame result = mAnimationFrames.get( frameCount - 1 );
        if ( frameCount <= 1 )
            return result;

        float currentTime = 0.0f;
        float cycleTime = animationTime;
        
        if ( mLoopAnimation )
            cycleTime = animationTime % length;

        if ( cycleTime >= length )
            return result;
        
        for ( int index = 0; index < frameCount; index++ )
        {
            AnimationFrame frame = mAnimationFrames.get( index );
            currentTime += frame.getAnitmationFrameTime();
            if ( currentTime > cycleTime )
                return frame;
        }
        
        return null;
    }
    
    public float getTotalLength()
    {
        return mTotalLength;
    }
    
    public void setAnimationToLoop( boolean loop )
    {
        mLoopAnimation = loop;
    }
}
