package com.igottashoot.game.properties;

import com.igottashoot.game.managers.GameObject;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.rendering.DrawableTiledBitmap;

public class AnimationProperty extends BaseObject implements GameObjectProperty
{
    private DrawableTiledBitmap mDrawableTiledBitmap;

    private int mCurrentFrame = 0;
    private float mNextFrameUpdate = 0;
    
    @Override
    public void reset()
    {
        mDrawableTiledBitmap.reset();
    }
    
    public void setDrawableTiledBitmap( DrawableTiledBitmap drawableTiledBitmap )
    {
        mDrawableTiledBitmap = drawableTiledBitmap;
    }
    
    @Override
    public void update( float timeDelta, GameObject parent )
    {
        if ( mDrawableTiledBitmap == null )
            return;

        if ( mCurrentFrame == 0 && mNextFrameUpdate == 0 )
        {
//            mDrawableTiledBitmap.setCurrentTileIndex( 0 );
            mNextFrameUpdate = parent.getRegistry().getTimeSystem().getGameTime() + 0.1f;
        }
        
        if ( mNextFrameUpdate < parent.getRegistry().getTimeSystem().getGameTime() )
        {
//            mDrawableTiledBitmap.setCurrentTileIndex( mCurrentFrame++ );
            mCurrentFrame = mCurrentFrame % 38;
            mNextFrameUpdate = parent.getRegistry().getTimeSystem().getGameTime() + 0.1f;
        }
    }

    @Override
    public int getPriority()
    {
        return PropertyExecutionPhase.ANIMATION.getPhase();
    }
}
