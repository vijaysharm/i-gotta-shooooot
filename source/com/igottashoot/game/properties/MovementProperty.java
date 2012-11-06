package com.igottashoot.game.properties;

import com.igottashoot.game.managers.GameObject;
import com.igottashoot.game.math.Vector2D;
import com.igottashoot.game.primitives.BaseObject;

public class MovementProperty extends BaseObject implements GameObjectProperty
{
    private static final float DEFAULT_AIR_DRAG = 0.0f;
    private static final float MAX_HORIZONTAL_SPEED = 500.0f;
    private static final float MAX_VERTICAL_SPEED = 500.0f;

    
    private final Vector2D mMaxVelocity;
    private final Vector2D mDrag;
    
    public MovementProperty()
    {
        mMaxVelocity = new Vector2D();
        mDrag = new Vector2D();
    }
    
    @Override
    public void reset()
    {
        mMaxVelocity.set( MAX_HORIZONTAL_SPEED, MAX_VERTICAL_SPEED );
        mDrag.set( DEFAULT_AIR_DRAG, DEFAULT_AIR_DRAG );
    }
    
    @Override
    public void update( float timeDelta, GameObject parent )
    {
        float velocityX = parent.getVelocityX();
        float velocityY = parent.getVelocityY();
        float x = parent.getPositionX();
        float y = parent.getPositionY();        
        float accelerationX = parent.gettAccelerationX();
        float accelerationY = parent.gettAccelerationY();
        
        float newVelocityX = ( computeVelocity( velocityX,
                                                accelerationX,
                                                mDrag.getX(),
                                                mMaxVelocity.getX(),
                                                timeDelta ) - velocityX ) / 2.0f;
        float newVelocityY = ( computeVelocity( velocityY,
                                                accelerationY,
                                                mDrag.getY(),
                                                mMaxVelocity.getY(),
                                                timeDelta ) - velocityY ) / 2.0f;
        
        velocityX += newVelocityX;
        velocityY += newVelocityY;
        
        float yd = velocityY * timeDelta;
        velocityY += newVelocityY;
        
        float xd = velocityX * timeDelta;
        velocityX += newVelocityX;
        
        y += yd;    
        x += xd;
        
        parent.setPositionX( x );
        parent.setPositionY( y );
        parent.setVelocityX( velocityX );
        parent.setVelocityY( velocityY );
    }

    @Override
    public int getPriority()
    {
        return PropertyExecutionPhase.MOVEMENT.getPhase();
    }
    
    public void setMaxVelocity( float x, float y )
    {
        mMaxVelocity.set( x, y );
    }
    
    public void setDrag( float x, float y )
    {
        mDrag.set( x, y );
    }

    private static float computeVelocity( float velocity,
                                          float acceleration, 
                                          float drag,
                                          float maxVelocity,
                                          float elapsedTime )
    {
        if ( acceleration != 0 )
            velocity += acceleration * elapsedTime;
        else if ( drag != 0 )
        {
            float d = drag * elapsedTime;
            if ( velocity - d > 0 )
                velocity -= d;
            else if ( velocity + d < 0 )
                velocity += d;
            else
                velocity = 0;
        }

        if ( ( velocity != 0 ) && ( Math.abs( velocity ) > Math.abs( maxVelocity ) ) )
        {
            if ( velocity > maxVelocity )
                velocity = maxVelocity;
            else if ( velocity < -maxVelocity )
                velocity = -maxVelocity;
        }
        
        return velocity;
    }    
}
