package com.igottashoot.game.collision;

import com.igottashoot.game.math.Vector2D;
import com.igottashoot.game.primitives.BaseObject;

public class AABoxBoundingVolume extends BaseObject implements BoundingVolume
{
    private final Vector2D mMin;
    private final Vector2D mMax;
    private final Vector2D mOffset;
    
    public AABoxBoundingVolume( float minX, float minY, float maxX, float maxY )
    {
        mMin = new Vector2D( minX, minY );
        mMax = new Vector2D( maxX, maxY );
        mOffset = new Vector2D();
        
        mOffset.zero();
    }

    @Override
    public void setOffset( float positionX, float positionY )
    {
        mOffset.set( positionX, positionY );
    }
    
    public float getMinimumX()
    {
        return mMin.getX() + mOffset.getX();
    }
    
    public float getMinimumY()
    {
        return mMin.getY() + mOffset.getY();
    }
    
    public float getMaximumX()
    {
        return mMax.getX() + mOffset.getX();
    }
    
    public float getMaximumY()
    {
        return mMax.getY() + mOffset.getY();
    }
}
