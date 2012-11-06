package com.igottashoot.game.math;

import com.igottashoot.game.primitives.BaseObject;

public class Vector2D extends BaseObject
{
    private final float[] mVector = new float[2];
    
    public Vector2D()
    {
        this( 0f, 0f );
    }
    
    public Vector2D( float x, float y )
    {
        setX( x );
        setY( y );
    }

    @Override
    public void reset()
    {
        zero();
    }
    
    public void set( float x, float y )
    {
        setX( x );
        setY( y );
    }
    
    public float getX()
    {
        return mVector[0];
    }
    
    public void setX( float x )
    {
        mVector[0] = x;
    }
    
    public float getY()
    {
        return mVector[1];
    }
    
    public void setY( float y )
    {
        mVector[1] = y;
    }

    public void zero()
    {
        mVector[0] = 0f;
        mVector[1] = 0f;
    }
    
    @Override
    public String toString()
    {
        return "[" + getX() + ", " + getY() + "]";
    }
}
