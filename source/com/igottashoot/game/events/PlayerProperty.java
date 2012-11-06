package com.igottashoot.game.events;

import com.igottashoot.game.collision.CollisionData;
import com.igottashoot.game.collision.CollisionHandler;
import com.igottashoot.game.managers.GameObject;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.properties.AnimatedSpriteProperty;
import com.igottashoot.game.properties.GameObjectProperty;

public class PlayerProperty extends BaseObject implements GameObjectProperty, CollisionHandler
{
    private static float MAX_VELOCITY_X = 1000f;
    private static float MAX_VELOCITY_Y = 360f;
    
    private AnimatedSpriteProperty mAnimatedSprite;
    private float mMaxJumpLimit;
    private float mJump;
    
    @Override
    public void reset()
    {
        mMaxJumpLimit = 0f;
        mJump = 0f;
    }
    
    @Override
    public void update( float timeDelta, GameObject parent )
    {
        if ( mAnimatedSprite == null )
            throw new IllegalStateException();
        
        float velocityX = parent.getVelocityX();
        float velocityY = parent.getVelocityY();
        
        // speed & acceleration
        if (velocityX < 0)
            velocityX = 0;
        else if ( velocityX < 100 )
            parent.setAccelerationX( 60 );
        else if ( velocityX < 250 )
            parent.setAccelerationX( 36 );
        else if ( velocityX < 400 )
            parent.setAccelerationX( 24 );
        else if ( velocityX < 600 )
            parent.setAccelerationX( 12 );
        else 
            parent.setAccelerationX( 4 );

        // jumping
        boolean isPressed = parent.getRegistry().getTouchScreenLocation().isPressed( 0, 0, 0, 0 );
        
        velocityX = parent.getVelocityX();
        velocityY = parent.getVelocityY();
        
        mMaxJumpLimit = velocityX / ( MAX_VELOCITY_X * 2.5f );
        if ( mMaxJumpLimit > 0.35 )
            mMaxJumpLimit = 0.35f;

        if ( mJump >= 0 && isPressed )
        {
            mJump += timeDelta;
            
            if ( mJump > mMaxJumpLimit )
                mJump = -1;
        }
        else
        {
            mJump = -1;
        }

        boolean onFloor = true;
        if ( mJump > 0 )
        {
            onFloor = false;
            
            if ( mJump < 0.08 )
                parent.setVelocityY( MAX_VELOCITY_Y * 0.65f );
            else
                parent.setVelocityY( MAX_VELOCITY_Y );
        }

        velocityX = parent.getVelocityX();
        velocityY = parent.getVelocityY();
        
        if ( onFloor )
        {
//            if (stumble && finished)
//                stumble = NO;
            
//            if (!stumble)
//            {
                if ( velocityX < 150 )
                    mAnimatedSprite.playAnimation( 0 );
                else if ( velocityX < 300 )
                    mAnimatedSprite.playAnimation( 0 );
                else if ( velocityX < 550 )
                    mAnimatedSprite.playAnimation( 0 );
                else
                    mAnimatedSprite.playAnimation( 0 );
//            }
        }
        else if ( velocityY < -140 )
        {
            mAnimatedSprite.playAnimation( 1 );
        }
        else if ( velocityY > -140 )
        {
            mAnimatedSprite.playAnimation( 2 );
//            stumble = NO;
        }
        
        if ( parent.getPositionX() > 1000 )
            parent.setPositionX( 0 );
    }

    @Override
    public int getPriority()
    {
        return PropertyExecutionPhase.THINK.getPhase();
    }
    
    @Override
    public void handleCollisionWith( GameObject parent, GameObject victim, CollisionData data )
    {
        if ( data.getCollisionNormalY() > 0 )
        {
            float newPosition = parent.getPositionY() + data.getPenetrationY();
            parent.setPositionY( newPosition );
            
            if ( ! parent.getRegistry().getTouchScreenLocation().isPressed( 0, 0, 0, 0 ) )
                mJump = 0f;
        }
    }
    
    public void setAnimatedSprite( AnimatedSpriteProperty animatedSprite )
    {
        mAnimatedSprite = animatedSprite;
    }
}
